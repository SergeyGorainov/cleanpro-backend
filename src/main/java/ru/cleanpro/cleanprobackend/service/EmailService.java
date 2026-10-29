package ru.cleanpro.cleanprobackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.cleanpro.cleanprobackend.model.OrderEntity;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // Email администратора — кому отправлять уведомления о новых заявках
    @Value("${cleanpro.admin.email}")
    private String adminEmail;

    // ===== 1. Уведомление администратору о новой заявке =====

    public void sendNewOrderNotification(OrderEntity order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(adminEmail);
            helper.setSubject("🔔 Новая заявка №" + order.getId() + " — CleanPro48");

            String html = buildAdminEmail(order);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("✅ Уведомление о заявке №{} отправлено на {}", order.getId(), adminEmail);
        } catch (MessagingException e) {
            log.error("❌ Не удалось отправить уведомление о заявке №{}: {}",
                    order.getId(), e.getMessage());
        }
    }

    // ===== 2. Письмо-подтверждение клиенту =====

    public void sendClientConfirmation(OrderEntity order) {
        if (order.getUser() == null || order.getUser().getEmail() == null) {
            log.info("Заявка №{} — гость без email, подтверждение не отправляется", order.getId());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(order.getUser().getEmail());
            helper.setSubject("Спасибо за заявку! — CleanPro48");
            helper.setText(buildClientEmail(order), true);

            mailSender.send(message);
            log.info("✅ Подтверждение отправлено клиенту {}", order.getUser().getEmail());
        } catch (MessagingException e) {
            log.error("❌ Не удалось отправить подтверждение клиенту: {}", e.getMessage());
        }
    }

    // ===== Утилита: название услуги (берётся из БД через @ManyToOne) =====

    private String serviceTitle(OrderEntity order) {
        return (order.getService() != null) ? order.getService().getTitle() : "—";
    }

    // ===== HTML-шаблоны писем =====

    private String buildAdminEmail(OrderEntity order) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px;">
                <h2 style="color: #0077cc;">Новая заявка №%d</h2>
                <table style="border-collapse: collapse; width: 100%%;">
                    <tr><td style="padding: 8px; font-weight: bold;">Имя:</td>
                        <td style="padding: 8px;">%s</td></tr>
                    <tr><td style="padding: 8px; font-weight: bold;">Телефон:</td>
                        <td style="padding: 8px;"><a href="tel:%s">%s</a></td></tr>
                    <tr><td style="padding: 8px; font-weight: bold;">Адрес:</td>
                        <td style="padding: 8px;">%s</td></tr>
                    <tr><td style="padding: 8px; font-weight: bold;">Услуга:</td>
                        <td style="padding: 8px;">%s</td></tr>
                    <tr><td style="padding: 8px; font-weight: bold;">Комментарий:</td>
                        <td style="padding: 8px;">%s</td></tr>
                    <tr><td style="padding: 8px; font-weight: bold;">Дата:</td>
                        <td style="padding: 8px;">%s</td></tr>
                </table>
                <p style="margin-top: 20px;">
                    <a href="http://localhost:8080/admin/orders"
                       style="background: #ff6b00; color: #fff; padding: 10px 20px;
                              text-decoration: none; border-radius: 6px;">
                        Открыть в админке
                    </a>
                </p>
            </div>
            """.formatted(
                order.getId(),
                order.getName(),
                order.getPhone(), order.getPhone(),
                order.getAddress() != null ? order.getAddress() : "—",
                serviceTitle(order),
                order.getComment() != null ? order.getComment() : "—",
                order.getCreatedAt()
        );
    }

    private String buildClientEmail(OrderEntity order) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px;">
                <h2 style="color: #0077cc;">Спасибо за заявку!</h2>
                <p>Здравствуйте, <b>%s</b>!</p>
                <p>Мы получили вашу заявку №%d на услугу <b>%s</b>.</p>
                <p>Наш менеджер свяжется с вами в течение 15 минут.</p>
                <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;">
                <p style="color: #777; font-size: 0.9rem;">
                    CleanPro48 — профессиональный клининг в Липецке<br>
                    Телефон: +7 (900) 000-00-00
                </p>
            </div>
            """.formatted(
                order.getName(),
                order.getId(),
                serviceTitle(order)
        );
    }
}