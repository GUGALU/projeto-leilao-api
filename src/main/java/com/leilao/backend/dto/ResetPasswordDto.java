package com.leilao.backend.dto;

import lombok.Data;

@Data
public class ResetPasswordDto {

    private String email;
    private String code;
    private String newPassword;
}
