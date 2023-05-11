package net.javaguides.springboot.event.listener;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.javaguides.springboot.event.RegistrationCompleteEvent;
import net.javaguides.springboot.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

 @Autowired   
 private JavaMailSender mailSender;

 private User theUser;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // 1. Get the newly registered user
        theUser = event.getUser();
        // 2. Build the verification URL to be sent to the user
        String url = "http://localhost:4200/#/home";
        // 3. Send the email.
        try {
            sendRegistrationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendRegistrationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "FoodAlchemy: Registration Successful";
        String senderName = "FoodAlchemy - User Registration";
        String mailContent = "<p><img src='cid:logo' width='700' height='400''/></p>" +
                "<p><span style='color: #000000;'> Hi, " + theUser.getUsername() + ", </span></p>" +
                "<p><span style='color: #000000;'>Thank you for successfully registering with us. " +
                "Your account has been successfully created.</span></p>" +
                "<p><span style='color: #000000;'>Welcome to our community! We're thrilled to have you on board.</span></p>" +
                "<p><a href=\"" + url + "\">Click here to login</a></p>" +
                "<p><b style='color: #000000;'> Thank you, <br> FoodAlchemy - User Registration</b></p>";
                ;
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom("foodalchemytfip@gmail.com", senderName);
        messageHelper.setTo(theUser.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);

        ClassPathResource resource = new ClassPathResource("/static/images/emailbanner.png");
        messageHelper.addInline("logo", resource);

        mailSender.send(message);
    }
    
}
