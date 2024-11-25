package com.leilao.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecoveryPasswordDto {

    @NotNull
    private String email;
    private String token;
}
