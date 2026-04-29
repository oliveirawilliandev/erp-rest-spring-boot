// [GENDER-001] Pacote dos enums do modelo
package br.com.willian.model.enums;

// [GENDER-002] Import do Jackson para desserialização de JSON
import com.fasterxml.jackson.annotation.JsonCreator;

// [GENDER-003] Enum que representa as opções de gênero do funcionário
public enum GenderType {
    MALE, // [GENDER-004] Masculino
    FEMALE, // [GENDER-005] Feminino
    NON_BINARY, // [GENDER-006] Não binário / Não se identifica com os gêneros tradicionais
    PREFER_NOT_TO_SAY; // [GENDER-007] Prefere não informar o gênero

    // [GENDER-008] Método para desserialização de valores case-insensitive via JSON
    @JsonCreator // Indica ao Jackson que este método deve ser usado para criar o enum a partir de uma string
    public static GenderType fromValue(String text) {
        // [GENDER-009] Se o texto for nulo, retorna null
        if (text == null) return null;

        // [GENDER-010] Itera sobre todas as opções do enum
        for (GenderType gender : GenderType.values()) {
            // [GENDER-011] Comparação case-insensitive (ex: "male" → MALE)
            if (gender.name().equalsIgnoreCase(text)) {
                return gender; // [GENDER-012] Retorna o enum correspondente
            }
        }

        // [GENDER-013] Se nenhum valor corresponder, lança exceção com valores permitidos
        throw new IllegalArgumentException(
                "Invalid gender value: " + text +
                        ". Allowed values: MALE, FEMALE, NON_BINARY, PREFER_NOT_TO_SAY."
        );
    }
}