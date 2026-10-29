// ===== Плавная прокрутка к форме заявки =====
document.addEventListener('DOMContentLoaded', () => {

    // Все ссылки с якорем #order — плавно скроллят к форме
    document.querySelectorAll('a[href*="#order"]').forEach(link => {
        link.addEventListener('click', (e) => {
            const target = document.getElementById('order');
            if (target) {
                e.preventDefault();
                target.scrollIntoView({ behavior: 'smooth', block: 'start' });

                // Если открыта другая страница — переходим на главную с якорем
                if (window.location.pathname !== '/') {
                    window.location.href = '/#order';
                }
            }
        });
    });

    // ===== Маска для телефона (простая) =====
    const phoneInputs = document.querySelectorAll('input[type="tel"]');
    phoneInputs.forEach(input => {
        input.addEventListener('input', (e) => {
            let value = e.target.value.replace(/\D/g, '');

            // Приводим к формату +7 (XXX) XXX-XX-XX
            if (value.startsWith('8')) {
                value = '7' + value.slice(1);
            }
            if (!value.startsWith('7')) {
                value = '7' + value;
            }
            value = value.slice(0, 11);

            let formatted = '+7';
            if (value.length > 1) formatted += ' (' + value.slice(1, 4);
            if (value.length >= 5) formatted += ') ' + value.slice(4, 7);
            if (value.length >= 8) formatted += '-' + value.slice(7, 9);
            if (value.length >= 10) formatted += '-' + value.slice(9, 11);

            e.target.value = formatted;
        });

        input.addEventListener('focus', (e) => {
            if (!e.target.value) {
                e.target.value = '+7 (';
            }
        });

        input.addEventListener('blur', (e) => {
            if (e.target.value === '+7 (' || e.target.value === '+7') {
                e.target.value = '';
            }
        });
    });

    // ===== Простая валидация формы перед отправкой =====
    const orderForms = document.querySelectorAll('.order-form');
    orderForms.forEach(form => {
        form.addEventListener('submit', (e) => {
            const nameInput = form.querySelector('input[name="name"]');
            if (nameInput && nameInput.disabled) {
                e.preventDefault();
                alert('Чтобы оставить заявку, войдите или зарегистрируйтесь');
                window.location.href = '/login';
                return;
            }
            const name = form.querySelector('input[name="name"]');
            const phone = form.querySelector('input[name="phone"]');

            if (name && name.value.trim().length < 2) {
                e.preventDefault();
                alert('Пожалуйста, укажите ваше имя');
                name.focus();
                return;
            }

            if (phone) {
                const digits = phone.value.replace(/\D/g, '');
                if (digits.length !== 11) {
                    e.preventDefault();
                    alert('Пожалуйста, укажите корректный номер телефона');
                    phone.focus();
                    return;
                }
            }
        });
    });

    // ===== Подсветка активного пункта меню =====
    const currentPath = window.location.pathname;
    document.querySelectorAll('.site-nav a').forEach(link => {
        const href = link.getAttribute('href');
        if (href === currentPath) {
            link.style.textDecoration = 'underline';
            link.style.fontWeight = '700';
        }
    });

    // ===== FAQ аккордеон =====
    document.querySelectorAll('.faq-question').forEach(button => {
        button.addEventListener('click', () => {
            const item = button.closest('.faq-item');
            const isOpen = item.classList.contains('open');

            // Переключаем текущий
            if (isOpen) {
                item.classList.remove('open');
            } else {
                item.classList.add('open');
            }
        });
    });

    // ===== Калькулятор стоимости уборки =====
    (function () {
        const totalEl = document.getElementById('calc-total');
        if (!totalEl) return;

        // Пустой объект — заполнится после загрузки цен с сервера
        let PRICES = {};

        // Состояние калькулятора
        const state = {
            typeMult: 1,
            base: 4500,
            serviceSlug: 'general',
            addRooms: 1500,
            area: 50,
            extras: 0
        };

        function formatNumber(num) {
            return Math.round(num).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
        }

        // Достать код услуги → слаг
        const SERVICE_MAP = {
            'regular': 'regular',
            'general': 'general',
            'renovation': 'renovation'
        };

        function recalc() {
            const pricePerSqm = PRICES['service_' + state.serviceSlug] || 80;
            const areaCost = state.area * pricePerSqm;

            const hintEl = document.getElementById('calc-area-hint-text');
            if (hintEl) {
                hintEl.textContent = '~ ' + formatNumber(areaCost) + ' ₽ за уборку площади';
            }

            const subtotal = (state.base + state.addRooms + areaCost) * state.typeMult;
            const total = subtotal + state.extras;
            totalEl.textContent = formatNumber(total);
        }

        // Загрузить цены с сервера и обновить UI
        async function loadPrices() {
            try {
                const response = await fetch('/api/calculator-prices');
                if (!response.ok) throw new Error('HTTP ' + response.status);
                PRICES = await response.json();
                console.log('✅ Цены калькулятора загружены:', PRICES);

                // Обновляем data-атрибуты кнопок из БД
                document.querySelectorAll('#calc-type .calc-option').forEach(btn => {
                    const type = btn.dataset.type;
                    const mult = PRICES['type_' + type];
                    if (mult != null) btn.dataset.mult = mult;
                });

                document.querySelectorAll('#calc-service .calc-option').forEach(btn => {
                    const svc = btn.dataset.service;
                    const base = PRICES['base_' + svc];
                    if (base != null) btn.dataset.base = base;
                });

                document.querySelectorAll('#calc-rooms .calc-option').forEach(btn => {
                    const r = btn.dataset.rooms;
                    const add = PRICES['room_' + r];
                    if (add != null) btn.dataset.add = add;
                });

                // Обновляем цены доп. услуг в чекбоксах
                document.querySelectorAll('.calc-checkbox input[type="checkbox"]').forEach(cb => {
                    const id = cb.id; // calc-windows → extra_windows
                    const key = id.replace('calc-', 'extra_');
                    if (PRICES[key] != null) {
                        cb.dataset.price = PRICES[key];
                        const span = cb.parentElement.querySelector('span');
                        if (span) {
                            span.textContent = span.textContent.replace(/\(\+[\d\s]+ ₽\)/, '(+' + PRICES[key] + ' ₽)');
                        }
                    }
                });

                // Первый расчёт с новыми ценами
                recalc();
            } catch (e) {
                console.warn('⚠ Не удалось загрузить цены, используем дефолтные', e);
                recalc();  // fallback на дефолтные значения
            }
        }

        // ===== Обработчики =====
        document.querySelectorAll('#calc-type, #calc-service, #calc-rooms').forEach(group => {
            group.querySelectorAll('.calc-option').forEach(btn => {
                btn.addEventListener('click', () => {
                    group.querySelectorAll('.calc-option').forEach(b => b.classList.remove('active'));
                    btn.classList.add('active');

                    if (group.id === 'calc-type') {
                        state.typeMult = parseFloat(btn.dataset.mult);
                    } else if (group.id === 'calc-service') {
                        state.base = parseFloat(btn.dataset.base);
                        state.serviceSlug = btn.dataset.service;
                    } else if (group.id === 'calc-rooms') {
                        state.addRooms = parseFloat(btn.dataset.add);
                    }
                    recalc();
                });
            });
        });

        const areaInput = document.getElementById('calc-area');
        if (areaInput) {
            areaInput.addEventListener('input', () => {
                let value = parseInt(areaInput.value, 10);
                if (isNaN(value) || value < 10) value = 10;
                if (value > 500) value = 500;
                state.area = value;
                recalc();
            });
            areaInput.addEventListener('blur', () => { areaInput.value = state.area; });
        }

        document.querySelectorAll('.calc-checkbox input[type="checkbox"]').forEach(cb => {
            cb.addEventListener('change', () => {
                let extras = 0;
                document.querySelectorAll('.calc-checkbox input[type="checkbox"]:checked').forEach(c => {
                    extras += parseFloat(c.dataset.price);
                });
                state.extras = extras;
                recalc();
            });
        });

        // Стартовая загрузка
        loadPrices();

        // Кнопка «Оставить заявку» — оставляем как было
        const orderBtn = document.getElementById('calc-order-btn');
        if (orderBtn) {
            orderBtn.addEventListener('click', () => {
                const form = document.querySelector('#order form');
                if (!form) return;
                const select = form.querySelector('select[name="service"]');
                if (select && state.serviceSlug) select.value = state.serviceSlug;

                const rooms = document.querySelector('#calc-rooms .calc-option.active')?.textContent || '—';
                const type = document.querySelector('#calc-type .calc-option.active')?.textContent || '—';
                const total = totalEl.textContent;

                const extras = [];
                document.querySelectorAll('.calc-checkbox input[type="checkbox"]:checked').forEach(c => {
                    extras.push(c.parentElement.querySelector('span').textContent.split(' (')[0]);
                });

                const comment = `Калькулятор: ${type}, ${rooms} комн., ${state.area} м²` +
                    (extras.length ? `, доп: ${extras.join(', ')}` : '') +
                    `. Предварительная стоимость: ${total} ₽.`;

                const commentField = form.querySelector('textarea[name="comment"]');
                if (commentField && !commentField.value) commentField.value = comment;
            });
        }
    })();

    // ===== Подсветка активного пункта админ-меню =====
    document.addEventListener('DOMContentLoaded', () => {
        const currentPath = window.location.pathname;
        document.querySelectorAll('.admin-nav a').forEach(link => {
            const href = link.getAttribute('href');
            if (href === currentPath) {
                link.classList.add('active');
            }
        });
    });

    // ===== Слайдер «до/после» в блоке «Результаты работы» =====
    (function () {
        const sliders = document.querySelectorAll('.ba-slider');
        if (!sliders.length) return;

        sliders.forEach(slider => {
            const afterWrap = slider.querySelector('.ba-after-wrap');
            const handle = slider.querySelector('.ba-handle');
            let isDragging = false;

            // Устанавливаем позицию разделителя в процентах (0-100)
            function setPosition(percent) {
                // Ограничиваем 5-95%, чтобы ползунок не уходил за края
                percent = Math.max(5, Math.min(95, percent));
                const inset = 100 - percent;
                afterWrap.style.clipPath = `inset(0 0 0 ${percent}%)`;
                handle.style.left = percent + '%';
            }

            // Получить позицию курсора в процентах относительно слайдера
            function getPercentFromEvent(e) {
                const rect = slider.getBoundingClientRect();
                const clientX = e.touches ? e.touches[0].clientX : e.clientX;
                return ((clientX - rect.left) / rect.width) * 100;
            }

            // Начало перетаскивания
            function startDrag(e) {
                isDragging = true;
                slider.style.cursor = 'grabbing';
                setPosition(getPercentFromEvent(e));
                e.preventDefault();
            }

            // Движение
            function moveDrag(e) {
                if (!isDragging) return;
                setPosition(getPercentFromEvent(e));
                e.preventDefault();
            }

            // Конец
            function endDrag() {
                isDragging = false;
                slider.style.cursor = 'ew-resize';
            }

            // Клик по слайдеру — сразу двигаем
            slider.addEventListener('click', (e) => {
                setPosition(getPercentFromEvent(e));
            });

            // Мышью
            slider.addEventListener('mousedown', startDrag);
            document.addEventListener('mousemove', moveDrag);
            document.addEventListener('mouseup', endDrag);

            // Касанием (мобильные)
            slider.addEventListener('touchstart', startDrag, { passive: false });
            slider.addEventListener('touchmove', moveDrag, { passive: false });
            slider.addEventListener('touchend', endDrag);

            // Инициализация: ползунок на 50%
            setPosition(50);
        });

        // Кнопка «Ещё фото»
        const showMoreBtn = document.getElementById('showMoreResults');
        if (showMoreBtn) {
            showMoreBtn.addEventListener('click', () => {
                const hidden = document.querySelectorAll('.hidden-result');
                let shownCount = 0;
                hidden.forEach(card => {
                    if (!card.classList.contains('visible') && shownCount < 3) {
                        card.classList.add('visible');
                        shownCount++;
                    }
                });

                // Если все раскрыты — скрыть кнопку
                const remaining = document.querySelectorAll('.hidden-result:not(.visible)');
                if (remaining.length === 0) {
                    showMoreBtn.classList.add('hidden');
                }
            });
        }
    })();

    // ===== Мобильное меню-бургер =====
    (function () {
        const burger = document.getElementById('navBurger');
        const wrap = document.getElementById('navMobileWrap');
        if (!burger || !wrap) return;

        burger.addEventListener('click', () => {
            burger.classList.toggle('open');
            wrap.classList.toggle('open');
        });
    })();
});