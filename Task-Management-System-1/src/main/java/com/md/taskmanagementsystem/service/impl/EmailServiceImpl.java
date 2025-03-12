package com.md.taskmanagementsystem.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.md.taskmanagementsystem.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl  implements EmailService{

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * Sends an email using a Thymeleaf template.
     *
     * @param to           The recipient's email address.
     * @param subject      The subject of the email.
     * @param templateName The name of the Thymeleaf template.
     * @param model        The dynamic values to be passed into the template.
     */
    @Override
    public void sendEmail(String to, String subject, String templateName, Map<String, Object> model) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom("mohdakib8190@gmail.com");  
            helper.setTo(to);
            helper.setSubject(subject);

            // Process the Thymeleaf template with dynamic values
            Context context = new Context();
            context.setVariables(model);
            String htmlContent = templateEngine.process(templateName, context);

            helper.setText(htmlContent, true);  // Enable HTML format
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();  // Handle exception properly in a real application
        }
    }
}
