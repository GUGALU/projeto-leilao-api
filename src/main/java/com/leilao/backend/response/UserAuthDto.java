package com.leilao.backend.response;

import lombok.Data;

@Data
public class UserAuthDto {
    private String email;
    private String password;
}
