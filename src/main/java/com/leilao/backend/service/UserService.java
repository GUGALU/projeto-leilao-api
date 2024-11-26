package com.leilao.backend.service;

import com.leilao.backend.entity.User;
import com.leilao.backend.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public String sendEmailWithCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));

        String code = String.format("%06d", (int) (Math.random() * 1000000));
        user.setValidationCode(code);
        userRepository.save(user);

        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable(email, email);
        context.setVariable("code", code);

        try {
            emailService.sendTemplateEmail(email, "Código de Validação", context, "emailValidate");
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao enviar e-mail.");
        }

        return "Código enviado com sucesso para o email.";
    }

    public void validateCode(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));

        if (!code.equals(user.getValidationCode())) {
            throw new IllegalArgumentException("Código inválido.");
        }

        user.setValidationCode(null);
        user.setEmailConfirmedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public String sendPasswordResetLink(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));

        String token = UUID.randomUUID().toString();
        user.setValidationCode(token);
        userRepository.save(user);

        String resetLink = "localhost:8080/auth/reset-password?email=" + email + "&token=" + token;

        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable("link", resetLink);

        try {
            emailService.sendTemplateEmail(email, "Redefinição de Senha", context, "passwordResetTemplate");
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao enviar e-mail de redefinição de senha.");
        }

        return "Link de redefinição enviado com sucesso.";
    }

    public void resetPassword(String email, String token, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));

        if (!token.equals(user.getValidationCode())) {
            throw new IllegalArgumentException("Token inválido ou expirado.");
        }

        user.setPassword(newPassword);
        user.setValidationCode(null);
        userRepository.save(user);
    }

    public User registerUser(String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("E-mail já está em uso.");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);

//        Context context = new Context();
//        try {
//            emailService.sendTemplateEmail(
//                    user.getEmail(),
//                    "Cadastro Efetuado com Sucesso", context,
//                    "welcome");
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }

        return userRepository.save(user);
    }

}
