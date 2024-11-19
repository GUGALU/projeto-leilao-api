package com.leilao.backend.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AlterPasswordDto {

    @NotNull
    private String email;
}
