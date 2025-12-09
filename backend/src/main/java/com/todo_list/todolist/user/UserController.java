package com.todo_list.todolist.user;

import com.todo_list.todolist.config.RabbitMQConfig;
import com.todo_list.todolist.notification.EmailUserDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- 1. Injeção do RabbitTemplate ---
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/") // Cadastro
    public ResponseEntity create(@RequestBody UserModel userModel) {
        var user = this.userRepository.findByUsername(userModel.getUsername());

        if (user != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe");
        }

        var passwordHashred = passwordEncoder.encode(userModel.getPassword());
        userModel.setPassword(passwordHashred);

        // Salva o usuário no banco de dados primeiro
        var userCreated = this.userRepository.save(userModel);

        // --- 2. Envio Assíncrono para o RabbitMQ ---
        try {
            // Prepara os dados para enviar (apenas o necessário)
            EmailUserDto emailDto = new EmailUserDto(
                    userCreated.getEmail(),
                    userCreated.getUsername(),
                    userCreated.getName()
            );

            // Envia a mensagem para a fila definida na Config
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, emailDto);

        } catch (Exception e) {
            // Opcional: Logar erro se o RabbitMQ falhar, mas não travar o cadastro
            System.err.println("Erro ao enviar notificação: " + e.getMessage());
        }
        // -------------------------------------------

        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    @PostMapping("/login") // Login
    public ResponseEntity login(@RequestBody UserModel userModel) {
        var user = this.userRepository.findByUsername(userModel.getUsername());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário ou senha incorretos");
        }

        var passwordVerify = passwordEncoder.matches(userModel.getPassword(), user.getPassword());

        if (!passwordVerify) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário ou senha incorretos");
        }

        // Gera token simples (Base64)
        String token = java.util.Base64.getEncoder().encodeToString(
                (userModel.getUsername() + ":" + userModel.getPassword()).getBytes());

        return ResponseEntity.status(HttpStatus.OK).body(java.util.Map.of("token", token));
    }
}