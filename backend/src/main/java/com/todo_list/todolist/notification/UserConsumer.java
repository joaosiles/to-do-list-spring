package com.todo_list.todolist.notification;

import com.todo_list.todolist.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserConsumer {

    @Autowired
    private EmailService emailService;

    // O RabbitListener converte o JSON automaticamente para o DTO se configurado,
    // mas por segurança e simplicidade inicial, vamos receber o objeto serializado.
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void listen(EmailUserDto userDto) {
        System.out.println("Mensagem recebida do RabbitMQ: " + userDto.username());

        // Chama o serviço de e-mail
        emailService.sendWelcomeEmail(userDto);
    }
}