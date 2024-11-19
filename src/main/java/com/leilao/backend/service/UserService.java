package com.leilao.backend.service;

import com.leilao.backend.entity.User;
import com.leilao.backend.repository.UserRepository;
import com.leilao.backend.response.RecoveryPasswordDto;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

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
        user.setActive(true);
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

    public User create(User user) {
        User userSaved = userRepository.save(user);

        Context context = new Context();
        context.setVariable("name", userSaved.getName());
        try {
            emailService.sendTemplateEmail(
                    userSaved.getEmail(),
                    "Cadastro Efetuado com Sucesso", context,
                    "welcome");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return userSaved;
    }

}
