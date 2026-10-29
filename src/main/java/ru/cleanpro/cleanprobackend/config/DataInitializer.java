package ru.cleanpro.cleanprobackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.cleanpro.cleanprobackend.model.CategoryEntity;
import ru.cleanpro.cleanprobackend.model.FaqEntity;
import ru.cleanpro.cleanprobackend.model.ReviewEntity;
import ru.cleanpro.cleanprobackend.model.ServiceEntity;
import ru.cleanpro.cleanprobackend.model.UserEntity;
import ru.cleanpro.cleanprobackend.repository.CategoryRepository;
import ru.cleanpro.cleanprobackend.repository.FaqRepository;
import ru.cleanpro.cleanprobackend.repository.ReviewRepository;
import ru.cleanpro.cleanprobackend.repository.ServiceRepository;
import ru.cleanpro.cleanprobackend.repository.UserRepository;
import ru.cleanpro.cleanprobackend.model.CalculatorPriceEntity;
import ru.cleanpro.cleanprobackend.repository.CalculatorPriceRepository;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final ReviewRepository reviewRepository;
    private final CategoryRepository categoryRepository;
    private final FaqRepository faqRepository;
    private final PasswordEncoder passwordEncoder;
    private final CalculatorPriceRepository calculatorPriceRepository;

    @Override
    public void run(String... args) {
        initAdmin();
        initCategoriesAndServices();
        initReviews();
        initFaqs();
        initCalculatorPrices();
    }

    // ===== Админ =====

    private void initAdmin() {
        if (userRepository.findByEmail("admin@cleanpro48.ru") == null) {
            UserEntity admin = UserEntity.builder()
                    .firstName("Админ")
                    .lastName("CleanPro48")
                    .email("admin@cleanpro48.ru")
                    .password(passwordEncoder.encode("admin123"))
                    .role("ADMIN")
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Создан администратор: admin@cleanpro48.ru / admin123");
        }
    }

    // ===== Категории и услуги =====

    private void initCategoriesAndServices() {
        if (categoryRepository.count() > 0) return;

        // ===== Категория «Уборка» =====
        CategoryEntity cleaning = categoryRepository.save(CategoryEntity.builder()
                .slug("cleaning")
                .title("Уборка")
                .description("Профессиональная уборка квартир, домов и офисов в Липецке")
                .sortOrder(10)
                .active(true)
                .build());

        // ===== Категория «Химчистка» =====
        CategoryEntity dryCleaning = categoryRepository.save(CategoryEntity.builder()
                .slug("dry-cleaning")
                .title("Химчистка")
                .description("Профессиональная химчистка мебели, штор, ковров и матрасов")
                .sortOrder(20)
                .active(true)
                .build());

        // ===== Категория «Коммерческая уборка» =====
        CategoryEntity commercial = categoryRepository.save(CategoryEntity.builder()
                .slug("commercial")
                .title("Коммерческая уборка")
                .description("Профессиональная уборка коммерческих помещений в Липецке: " +
                        "магазины, кафе, рестораны, салоны красоты, фитнес-клубы и торговые центры. " +
                        "Работаем ночью и в выходные, чтобы не мешать вашему бизнесу.")
                .sortOrder(30)
                .active(true)
                .build());

        // ===== Услуги в категории «Уборка» =====

        serviceRepository.save(ServiceEntity.builder()
                .slug("general")
                .title("Генеральная уборка")
                .shortDescription("Полная уборка квартиры с мытьём окон и техники.")
                .fullDescription("Комплексная уборка: влажная уборка всех поверхностей, " +
                        "мытьё окон и подоконников, люстр, кухни (включая плиту и вытяжку), " +
                        "дезинфекция санузлов, мытьё полов и плинтусов.")
                .priceFrom(new BigDecimal("4500"))
                .sortOrder(10)
                .active(true)
                .category(cleaning)
                .build());

        serviceRepository.save(ServiceEntity.builder()
                .slug("regular")
                .title("Регулярная уборка")
                .shortDescription("Поддерживающая уборка на постоянной основе.")
                .fullDescription("Влажная уборка полов и поверхностей, уборка кухни и санузлов, " +
                        "вынос мусора. Подходит для поддержания чистоты в квартире или доме. " +
                        "При регулярном заказе — скидка 10%.")
                .priceFrom(new BigDecimal("2500"))
                .sortOrder(20)
                .active(true)
                .category(cleaning)
                .build());

        serviceRepository.save(ServiceEntity.builder()
                .slug("house")
                .title("Уборка домов")
                .shortDescription("Комплексная уборка частных домов и коттеджей.")
                .fullDescription("Уборка всех помещений дома, включая лестницы, веранды и террасы. " +
                        "Мытьё окон, влажная уборка поверхностей, дезинфекция санузлов, " +
                        "вынос мусора. Работаем с домами любой площади.")
                .priceFrom(new BigDecimal("6000"))
                .sortOrder(30)
                .active(true)
                .category(cleaning)
                .build());

        serviceRepository.save(ServiceEntity.builder()
                .slug("office")
                .title("Уборка офисов")
                .shortDescription("Регулярная или разовая уборка офисных помещений.")
                .fullDescription("Влажная уборка кабинетов и коридоров, уборка санузлов " +
                        "и кухонных зон, вынос мусора, мытьё окон и полив растений — по запросу.")
                .priceFrom(new BigDecimal("3000"))
                .sortOrder(40)
                .active(true)
                .category(cleaning)
                .build());

        serviceRepository.save(ServiceEntity.builder()
                .slug("windows")
                .title("Мытьё окон")
                .shortDescription("Профессиональная мойка окон любой сложности.")
                .fullDescription("Мойка стёкол с двух сторон, мытьё рам, подоконников и откосов. " +
                        "Удаление следов краски, клея, строительной пыли. Работаем на высоте.")
                .priceFrom(new BigDecimal("2000"))
                .sortOrder(50)
                .active(true)
                .category(cleaning)
                .build());

        serviceRepository.save(ServiceEntity.builder()
                .slug("renovation")
                .title("Уборка после ремонта")
                .shortDescription("Удаление строительной пыли, следов краски и клея.")
                .fullDescription("Сухая и влажная уборка всех поверхностей, удаление следов " +
                        "краски, клея и затирки, мытьё окон после ремонта, уборка после монтажа " +
                        "мебели, вынос строительного мусора — по запросу.")
                .priceFrom(new BigDecimal("7000"))
                .sortOrder(60)
                .active(true)
                .category(cleaning)
                .build());

        // ===== Услуги в категории «Химчистка» =====

        serviceRepository.save(ServiceEntity.builder()
                .slug("himchistka-furniture")
                .title("Химчистка мебели")
                .shortDescription("Чистка диванов, кресел и стульев на дому.")
                .fullDescription("Глубокая чистка мягкой мебели: диваны, кресла, стулья. " +
                        "Используем профессиональный экстрактор и безопасные моющие средства. " +
                        "Удаляем пятна, запахи, пыль, следы от животных. Мебель высыхает за 4–6 часов.")
                .priceFrom(new BigDecimal("1500"))
                .sortOrder(10)
                .active(true)
                .category(dryCleaning)
                .build());

        serviceRepository.save(ServiceEntity.builder()
                .slug("himchistka-curtains")
                .title("Химчистка штор и тюли")
                .shortDescription("Чистка штор и тюли прямо на карнизе, без снятия.")
                .fullDescription("Профессиональная чистка штор и тюли на месте — не нужно снимать " +
                        "и везти в химчистку. Удаляем пыль, пятна, запахи. Ткань сохраняет цвет " +
                        "и форму, не даёт усадку.")
                .priceFrom(new BigDecimal("1800"))
                .sortOrder(20)
                .active(true)
                .category(dryCleaning)
                .build());

        serviceRepository.save(ServiceEntity.builder()
                .slug("himchistka-carpets")
                .title("Химчистка ковров")
                .shortDescription("Глубокая чистка ковров и ковролина с выездом на дом.")
                .fullDescription("Профессиональная химчистка ковров, ковролина и паласов. " +
                        "Удаляем пятна, пыль, следы от животных. Обрабатываем " +
                        "антибактериальными средствами. Чистим на месте — вывозить не нужно.")
                .priceFrom(new BigDecimal("1200"))
                .sortOrder(30)
                .active(true)
                .category(dryCleaning)
                .build());

        serviceRepository.save(ServiceEntity.builder()
                .slug("himchistka-mattresses")
                .title("Химчистка матрасов")
                .shortDescription("Глубокая чистка матрасов с удалением пыли и клещей.")
                .fullDescription("Профессиональная химчистка матрасов с удалением пыли, " +
                        "пылевых клещей, следов пота и пятен. Обрабатываем антибактериальными " +
                        "и гипоаллергенными средствами. Матрас высыхает за 3–4 часа.")
                .priceFrom(new BigDecimal("1500"))
                .sortOrder(40)
                .active(true)
                .category(dryCleaning)
                .build());

        // ===== Услуги в категории «Коммерческая уборка» =====

        serviceRepository.save(ServiceEntity.builder()
                .slug("shops")
                .title("Уборка магазинов")
                .shortDescription("Ежедневная или периодическая уборка торговых помещений.")
                .fullDescription("Уборка торгового зала, склада, подсобных помещений. " +
                        "Мытьё полов, витрин, стекол, вынос мусора, уборка санузлов. " +
                        "Работаем до открытия или после закрытия магазина.")
                .priceFrom(new BigDecimal("5000"))
                .sortOrder(10)
                .active(true)
                .category(commercial)
                .build());

        serviceRepository.save(ServiceEntity.builder()
                .slug("cafes")
                .title("Уборка кафе и ресторанов")
                .shortDescription("Профессиональная уборка залов, кухонь и подсобных помещений.")
                .fullDescription("Уборка обеденного зала, барной стойки, кухни, санузлов. " +
                        "Удаление следов жира и нагара, мытьё посуды и техники — по запросу. " +
                        "Работаем ночью, чтобы не мешать работе заведения.")
                .priceFrom(new BigDecimal("6000"))
                .sortOrder(20)
                .active(true)
                .category(commercial)
                .build());

        serviceRepository.save(ServiceEntity.builder()
                .slug("beauty-salons")
                .title("Уборка салонов красоты")
                .shortDescription("Деликатная уборка с учётом специфики салонов.")
                .fullDescription("Уборка рабочих мест, зон ожидания, санузлов. " +
                        "Дезинфекция инструментов и поверхностей. Удаление следов краски, лака, волос. " +
                        "Используем средства, безопасные для клиентов и персонала.")
                .priceFrom(new BigDecimal("4000"))
                .sortOrder(30)
                .active(true)
                .category(commercial)
                .build());

        serviceRepository.save(ServiceEntity.builder()
                .slug("fitness")
                .title("Уборка фитнес-клубов")
                .shortDescription("Уборка тренажёрных залов, раздевалок и душевых.")
                .fullDescription("Уборка тренажёрных залов, групповых студий, раздевалок и душевых. " +
                        "Дезинфекция спортивного оборудования, мытьё зеркал и полов, " +
                        "удаление пота и следов от обуви.")
                .priceFrom(new BigDecimal("7000"))
                .sortOrder(40)
                .active(true)
                .category(commercial)
                .build());

        serviceRepository.save(ServiceEntity.builder()
                .slug("business-centers")
                .title("Уборка офисных центров")
                .shortDescription("Комплексное обслуживание бизнес-центров и офисных зданий.")
                .fullDescription("Уборка холлов, коридоров, лестниц, санузлов, лифтов. " +
                        "Мытьё окон и стеклянных перегородок, вынос мусора. " +
                        "Работаем по договору с юридическими лицами.")
                .priceFrom(new BigDecimal("10000"))
                .sortOrder(50)
                .active(true)
                .category(commercial)
                .build());

        System.out.println("✅ Созданы 3 категории и 15 услуг");
    }

    // ===== Отзывы =====

    private void initReviews() {
        if (reviewRepository.count() > 0) return;

        reviewRepository.save(ReviewEntity.builder()
                .authorName("Анна")
                .authorInfo("Липецк")
                .text("Заказывала генеральную уборку перед переездом. Всё блестит! " +
                        "Приехали вовремя, работали аккуратно.")
                .rating(5)
                .active(true)
                .build());

        reviewRepository.save(ReviewEntity.builder()
                .authorName("Сергей")
                .authorInfo("ООО «Ромашка»")
                .text("Убирают наш офис каждую неделю. Всегда чисто, персонал вежливый. Рекомендую.")
                .rating(5)
                .active(true)
                .build());

        System.out.println("✅ Созданы 2 стартовых отзыва");
    }

    // ===== FAQ ( Частые вопросы о химчистке )=====

    private void initFaqs() {
        if (faqRepository.count() > 0) return;

        faqRepository.save(FaqEntity.builder()
                .category("himchistka")
                .question("Сколько сохнет мебель после химчистки?")
                .answer("В среднем мебель высыхает за 4–6 часов. Точное время зависит от материала " +
                        "обивки, влажности в помещении и температуры. Мы рекомендуем не садиться " +
                        "на мебель в течение 4–5 часов после чистки, а также проветривать комнату.")
                .sortOrder(10)
                .active(true)
                .build());

        faqRepository.save(FaqEntity.builder()
                .category("himchistka")
                .question("Безопасна ли химчистка для детей и животных?")
                .answer("Да. Мы используем сертифицированные моющие средства с нейтральным pH, " +
                        "гипоаллергенные и безопасные для детей, животных и людей с аллергией. " +
                        "После высыхания мебель полностью готова к использованию.")
                .sortOrder(20)
                .active(true)
                .build());

        faqRepository.save(FaqEntity.builder()
                .category("himchistka")
                .question("Можно ли вывести застарелые пятна?")
                .answer("В большинстве случаев — да. Мы работаем с пятнами от кофе, вина, сока, " +
                        "жира, следов животных и косметики. Однако некоторые пятна (например, " +
                        "от отбеливателя или старые следы краски) могут быть необратимыми — " +
                        "мастер предупредит вас об этом заранее на осмотре.")
                .sortOrder(30)
                .active(true)
                .build());

        faqRepository.save(FaqEntity.builder()
                .category("himchistka")
                .question("Вы химчистите мебель на дому или забираете её?")
                .answer("Мы работаем на дому — приезжаем со своим оборудованием и средствами. " +
                        "Вам не нужно ничего вывозить и ждать несколько дней. Всё делаем на месте " +
                        "за 1–2 часа. Работаем в Липецке и области.")
                .sortOrder(40)
                .active(true)
                .build());

        faqRepository.save(FaqEntity.builder()
                .category("himchistka")
                .question("Что делать, если после химчистки остался запах?")
                .answer("Лёгкий запах моющего средства может сохраняться 2–4 часа — это нормально. " +
                        "Проветрите помещение, и он исчезнет. Если запах остался дольше или вас " +
                        "что-то смущает — свяжитесь с нами, мы бесплатно вернёмся и обработаем " +
                        "мебель нейтрализатором запаха.")
                .sortOrder(50)
                .active(true)
                .build());

        faqRepository.save(FaqEntity.builder()
                .category("himchistka")
                .question("Как часто нужно делать химчистку мебели?")
                .answer("Рекомендуем проводить профессиональную химчистку мягкой мебели " +
                        "1–2 раза в год. Если в доме есть животные, маленькие дети или кто-то " +
                        "из членов семьи страдает аллергией — лучше чистить каждые 4–6 месяцев. " +
                        "Это продлевает срок службы мебели и поддерживает здоровый микроклимат.")
                .sortOrder(60)
                .active(true)
                .build());

        System.out.println("✅ Созданы 6 стартовых FAQ");
    }

    // ===== КАЛЬКУЛЯТОР =====

    private void initCalculatorPrices() {
        if (calculatorPriceRepository.count() > 0) return;

        // ===== Услуги: цена за м² =====
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("service_regular").title("Регулярная — цена за м² (₽)")
                .value(new BigDecimal("40")).sortOrder(10).build());
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("service_general").title("Генеральная — цена за м² (₽)")
                .value(new BigDecimal("80")).sortOrder(20).build());
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("service_renovation").title("После ремонта — цена за м² (₽)")
                .value(new BigDecimal("120")).sortOrder(30).build());

        // ===== Тип объекта: множитель =====
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("type_flat").title("Квартира — множитель")
                .value(new BigDecimal("1.0")).sortOrder(40).build());
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("type_house").title("Дом — множитель")
                .value(new BigDecimal("1.3")).sortOrder(50).build());
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("type_office").title("Офис — множитель")
                .value(new BigDecimal("1.1")).sortOrder(60).build());

        // ===== Базовая цена =====
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("base_regular").title("Регулярная — базовая цена (₽)")
                .value(new BigDecimal("2500")).sortOrder(70).build());
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("base_general").title("Генеральная — базовая цена (₽)")
                .value(new BigDecimal("4500")).sortOrder(80).build());
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("base_renovation").title("После ремонта — базовая цена (₽)")
                .value(new BigDecimal("7000")).sortOrder(90).build());

        // ===== Комнаты =====
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("room_1").title("1 комната — надбавка (₽)")
                .value(new BigDecimal("0")).sortOrder(100).build());
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("room_2").title("2 комнаты — надбавка (₽)")
                .value(new BigDecimal("1500")).sortOrder(110).build());
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("room_3").title("3 комнаты — надбавка (₽)")
                .value(new BigDecimal("3000")).sortOrder(120).build());
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("room_4").title("4 комнаты — надбавка (₽)")
                .value(new BigDecimal("4500")).sortOrder(130).build());
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("room_5").title("5+ комнат — надбавка (₽)")
                .value(new BigDecimal("6000")).sortOrder(140).build());

        // ===== Доп. услуги =====
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("extra_windows").title("Мытьё окон (₽)")
                .value(new BigDecimal("2000")).sortOrder(150).build());
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("extra_furniture").title("Химчистка мебели (₽)")
                .value(new BigDecimal("1500")).sortOrder(160).build());
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("extra_appliances").title("Мытьё техники (₽)")
                .value(new BigDecimal("1000")).sortOrder(170).build());
        calculatorPriceRepository.save(CalculatorPriceEntity.builder()
                .code("extra_balcony").title("Уборка балкона (₽)")
                .value(new BigDecimal("800")).sortOrder(180).build());

        System.out.println("✅ Созданы стартовые цены калькулятора");
    }
}