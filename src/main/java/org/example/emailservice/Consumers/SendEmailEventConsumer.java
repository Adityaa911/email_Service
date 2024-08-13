package org.example.emailservice.Consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.Session;
import org.example.emailservice.Dtos.SendEmailEventsDtos;
import org.example.emailservice.Utils.EmailUtil;
import org.springframework.kafka.annotation.KafkaListener;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

public class SendEmailEventConsumer {

    private ObjectMapper objectMapper;

    SendEmailEventConsumer(ObjectMapper objectMapper){
        this.objectMapper=objectMapper;
    }
    @KafkaListener( topics = "sendEmail",groupId = "EmailService")
    public void HandleSendEmailEvent(String message) throws JsonProcessingException {
        SendEmailEventsDtos sendEmailEventsDtos =
                objectMapper.readValue(
                        message,SendEmailEventsDtos.class
                );
        String to = sendEmailEventsDtos.getTo();
        String from = sendEmailEventsDtos.getFrom();
        String subject = sendEmailEventsDtos.getSubject();
        String body = sendEmailEventsDtos.getBody();

        // now we have to send the email

        System.out.println("Email Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "password");
            }
        };
        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session,to,from,subject, body);





    }

}
