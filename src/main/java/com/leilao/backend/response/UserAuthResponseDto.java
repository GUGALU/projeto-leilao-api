package com.leilao.backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAuthResponseDto {
    private String token;
    private String email;
}
