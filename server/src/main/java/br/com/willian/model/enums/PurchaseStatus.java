// [PURCHASE-STATUS-001] Pacote dos enums do modelo
package br.com.willian.model.enums;

// [PURCHASE-STATUS-002] Import do Jackson para desserialização de JSON
import com.fasterxml.jackson.annotation.JsonCreator;

// [PURCHASE-STATUS-003] Enum que representa os possíveis status de uma compra (pedido de compra)
public enum PurchaseStatus {
    PENDING, // [PURCHASE-STATUS-004] Compra pendente (aguardando aprovação)
    APPROVED, // [PURCHASE-STATUS-005] Compra aprovada (aguardando envio)
    SHIPPED, // [PURCHASE-STATUS-006] Compra enviada pelo fornecedor
    RECEIVED, // [PURCHASE-STATUS-007] Compra recebida no estoque
    CANCELLED; // [PURCHASE-STATUS-008] Compra cancelada

    // [PURCHASE-STATUS-009] Método para desserialização de valores case-insensitive via JSON
    @JsonCreator // Indica ao Jackson que este método deve ser usado para criar o enum a partir de uma string
    public static PurchaseStatus fromValue(String text) {
        // [PURCHASE-STATUS-010] Se o texto for nulo, retorna null
        if (text == null) return null;

        // [PURCHASE-STATUS-011] Itera sobre todas as opções do enum
        for (PurchaseStatus status : PurchaseStatus.values()) {
            // [PURCHASE-STATUS-012] Comparação case-insensitive (ex: "approved" → APPROVED)
            if (status.name().equalsIgnoreCase(text)) {
                return status; // [PURCHASE-STATUS-013] Retorna o enum correspondente
            }
        }

        // [PURCHASE-STATUS-014] Se nenhum valor corresponder, lança exceção com valores permitidos
        throw new IllegalArgumentException(
                "Invalid purchase status: " + text +
                        ". Allowed values: PENDING, APPROVED, SHIPPED, RECEIVED, CANCELLED."
        );
    }
}