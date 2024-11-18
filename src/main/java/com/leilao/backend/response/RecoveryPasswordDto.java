package com.leilao.backend.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecoveryPasswordDto {

    @NotNull
    @Email
    private String email;
    private String code;
    private String newPassword;
}
