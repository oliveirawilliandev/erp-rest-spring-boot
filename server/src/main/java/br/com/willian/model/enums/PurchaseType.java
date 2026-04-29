// [PURCHASETYPE-001] Pacote dos enums do modelo
package br.com.willian.model.enums;

// [PURCHASETYPE-002] Import do Jackson para desserialização de JSON
import com.fasterxml.jackson.annotation.JsonCreator;

// [PURCHASETYPE-003] Enum que representa as opções de Compras
public enum PurchaseType {
    INGREDIENT, // [PURCHASETYPE-004] INGREDIENTE
    EQUIPMENT, // [PURCHASETYPE-005] EQUIPAMENTO
    PACKAGING, // [PURCHASETYPE-006] EMBALAGEM
    OTHER; // [PURCHASETYPE-007] OUTRO

    // [PURCHASETYPE-008] Método para desserialização de valores case-insensitive via JSON
    @JsonCreator // Indica ao Jackson que este método deve ser usado para criar o enum a partir de uma string
    public static PurchaseType fromValue(String text) {
        // [PURCHASETYPE-009] Se o texto for nulo, retorna null
        if (text == null) return null;

        // [PURCHASETYPE-010] Itera sobre todas as opções do enum
        for (PurchaseType purchaseType : PurchaseType.values()) {
            // [PURCHASETYPE-011] Comparação case-insensitive (ex: "other" → OTHER)
            if (purchaseType.name().equalsIgnoreCase(text)) {
                return purchaseType; // [PURCHASETYPE-012] Retorna o enum correspondente
            }
        }

        // [PURCHASETYPE-013] Se nenhum valor corresponder, lança exceção com valores permitidos
        throw new IllegalArgumentException(
                "Invalid purchaseType value: " + text +
                        ". Allowed values: INGREDIENT, EQUIPMENT, PACKAGING, OTHER."
        );
    }
}