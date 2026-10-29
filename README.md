# CleanPro48 — Веб-платформа для клининговой компании

Дипломный проект. Разработка веб-платформы для автоматизации приема и обработки заявок клининговой компании CleanPro48 (г. Липецк).

## 🛠 Технологии
- **Backend:** Java 21, Spring Boot, Spring Security, Spring Data JPA (Hibernate)
- **База данных:** PostgreSQL
- **Frontend:** Thymeleaf, HTML5, CSS3, JavaScript (ES6), Chart.js
- **Сборка:** Maven

## 🚀 Функционал
- Публичный сайт с калькулятором стоимости уборки (динамические цены через REST API).
- Роли пользователей: Гость, Клиент (USER), Сотрудник (EMPLOYEE), Администратор (ADMIN).
- Админ-панель с полным CRUD (услуги, категории, отзывы, FAQ, цены, пользователи).
- Дашборд с аналитикой (графики Chart.js).
- Экспорт заявок в CSV.
- Многоуровневая безопасность (BCrypt, CSRF, Spring Security).

## ⚙️ Запуск проекта локально
1. Установите PostgreSQL и создайте базу данных `cleanpro_db`.
2. Создайте файл `src/main/resources/application-local.yaml` и укажите свои данные:
   ```yaml
   spring:
     datasource:
       password: ваш_пароль_от_БД
     mail:
       username: ваш_email@mail.ru
       password: ваш_пароль_приложения

Сриншот 1
<img width="1679" height="1306" alt="1" src="https://github.com/user-attachments/assets/9bd4d06a-da2d-4550-a893-299e59395eb7" />
Скриншот 2
<img width="1341" height="1306" alt="2" src="https://github.com/user-attachments/assets/69bcf5df-5e81-41a5-9b25-0976dcddaf19" />
