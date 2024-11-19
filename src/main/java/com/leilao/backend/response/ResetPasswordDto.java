package com.leilao.backend.response;

import lombok.Data;

@Data
public class ResetPasswordDto {

    private String email;
    private String token;
    private String newPassword;
}
