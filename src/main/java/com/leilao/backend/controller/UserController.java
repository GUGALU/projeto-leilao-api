package com.leilao.backend.controller;

import com.leilao.backend.commom.security.JwtService;
import com.leilao.backend.entity.User;
import com.leilao.backend.response.UserAuthDto;
import com.leilao.backend.response.UserAuthResponseDto;
import com.leilao.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public User getUser(@RequestBody Integer id) {
        return userService.findById(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@RequestBody Integer id) {
        userService.deleteById(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@RequestBody User user) {
        return userService.update(user);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserAuthResponseDto authenticateUser(@RequestBody UserAuthDto authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(), authRequest.getPassword()));
        return new UserAuthResponseDto(jwtService.generateToken(authentication.getName()), authRequest.getEmail());
    }

}
