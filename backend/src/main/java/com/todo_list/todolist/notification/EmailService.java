package com.todo_list.todolist.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(EmailUserDto userDto) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@todolist.com"); // Apenas cosmético no Gmail
            message.setTo(userDto.email());
            message.setSubject("Bem-vindo ao ToDo List, " + userDto.name() + "!");
            message.setText("Olá " + userDto.name() + ",\n\n" +
                    "Seu cadastro foi realizado com sucesso!\n" +
                    "Username: " + userDto.username() + "\n\n" +
                    "Aproveite para organizar suas tarefas.");

            mailSender.send(message);
            System.out.println("E-mail enviado para: " + userDto.email());
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail: " + e.getMessage());
        }
    }
}