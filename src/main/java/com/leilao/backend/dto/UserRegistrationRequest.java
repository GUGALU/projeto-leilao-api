package com.leilao.backend.dto;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    public String name;
    public String email;
    public String password;
}
