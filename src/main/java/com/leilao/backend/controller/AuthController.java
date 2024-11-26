package com.leilao.backend.controller;

import com.leilao.backend.commom.security.JwtService;
import com.leilao.backend.entity.User;
import com.leilao.backend.dto.*;
import com.leilao.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserAuthResponseDto authenticateUser(@RequestBody UserAuthDto authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(), authRequest.getPassword()));

            String token = jwtService.generateToken(authentication.getName());
            return new UserAuthResponseDto(token, authRequest.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/send-code")
    @ResponseStatus(HttpStatus.OK)
    public String sendValidationCode(@RequestBody String email) {
        return userService.sendEmailWithCode(email);
    }

    @GetMapping("/validate")
    @ResponseStatus(HttpStatus.OK)
    public String activateUser(@RequestParam String email, @RequestParam String code ) {
        userService.validateCode(email, code);
        return "Usuário ativado com sucesso.";
    }

    @PostMapping("/send-reset-link")
    @ResponseStatus(HttpStatus.OK)
    public String sendResetPasswordLink(@RequestBody AlterPasswordDto request) {
        return userService.sendPasswordResetLink(request.getEmail());
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public String resetPassword(
            @RequestBody ResetPasswordDto request) {
        userService.resetPassword(request.getEmail(), request.getCode(), request.getNewPassword());
        return "Senha redefinida com sucesso.";
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationRequest request) {
        try {
            User user = userService.registerUser(request.getName(), request.getEmail(), request.getPassword());
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


}
