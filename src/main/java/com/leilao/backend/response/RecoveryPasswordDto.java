package com.leilao.backend.response;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecoveryPasswordDto {

    @NotNull
    private String email;
    private String token;
}
