package com.todo_list.todolist.filter;

import com.todo_list.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var servletPath = request.getServletPath();

        // Esse filtro só vai agir nas rotas de /tasks/
        if (servletPath.startsWith("/tasks/")) {

            // Pega a autenticação (Basic Auth) do cabeçalho
            var authorization = request.getHeader("Authorization");

            if (authorization == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // Limpa o texto "Basic " e decodifica o Base64 (user:senha)
            var authEncoded = authorization.substring("Basic".length()).trim();
            byte[] authDecode = Base64.getDecoder().decode(authEncoded);
            var authString = new String(authDecode);

            // Separa usuario e senha
            String[] crendentials = authString.split(":");
            String username = crendentials[0];
            String password = crendentials[1];

            // Busca o usuário no banco
            var user = this.userRepository.findByUsername(username);

            if (user == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            var passwordVerify = passwordEncoder.matches(password, user.getPassword());

            if (passwordVerify) {
                request.setAttribute("idUser", user.getId());
                filterChain.doFilter(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }

        } else {
            // Se não for rota de task, deixa passar
            filterChain.doFilter(request, response);
        }
    }
}