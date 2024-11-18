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

    public String generateRecoveryCode(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));

        String code = String.format("%04d", (int) (Math.random() * 10000));
        user.setValidationCode(code);
        userRepository.save(user);

        try {
            Context context = new Context();
            context.setVariable("name", user.getName());
            context.setVariable("code", code);

            emailService.sendTemplateEmail(email, "Código de Recuperação de Senha", context, "recoveryPassword");
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Falha ao enviar o email.");
        }

        return code;
    }

    public void resetPassword(RecoveryPasswordDto dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));

        if (!dto.getCode().equals(user.getValidationCode())) {
            throw new IllegalArgumentException("Código inválido.");
        }

        user.setPassword(dto.getNewPassword());
        user.setValidationCode(null);
        userRepository.save(user);
    }

    public void sendValidationEmail(User user) {
        String token = String.valueOf((int) (Math.random() * 999999));
        user.setValidationCode(token);
        userRepository.save(user);

        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable("token", token);

        try {
            emailService.sendTemplateEmail(user.getEmail(), "Confirmação de Cadastro", context, "emailValidationTemplate");
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao enviar e-mail de validação.");
        }
    }

    public void validateUser(String email, String token) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));

        if (!token.equals(user.getValidationCode())) {
            throw new IllegalArgumentException("Token inválido.");
        }

        user.setValidationCode(null);
        user.setActive(true);
        userRepository.save(user);
    }


}
