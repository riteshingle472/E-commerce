package org.riteshingle.ecommerce.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;
    private final JavaMailSender javaMailService;

    public void sendMail(String to,String subject,String body){

        SimpleMailMessage javaMail =  new SimpleMailMessage();

        try {
            javaMail.setFrom(fromEmail);
            javaMail.setTo(to);
            javaMail.setSubject(subject);
            javaMail.setText(body);

            javaMailService.send(javaMail);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
