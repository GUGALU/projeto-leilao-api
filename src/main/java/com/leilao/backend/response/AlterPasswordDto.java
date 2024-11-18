package com.leilao.backend.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AlterPasswordDto {

    @NotNull
    private String recoveryToken;

    @NotNull
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.")
    private String newPassword;
}
