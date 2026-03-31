package br.com.willian.dto.v1.request;

import java.io.Serializable;

public record SendVerificationCodeRequestDTO(String email) implements Serializable {
    private static final long serialVersionUID = 1L;

}
