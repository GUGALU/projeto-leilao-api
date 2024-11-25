package com.leilao.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlterPasswordDto {

    @NotNull
    private String email;
}
