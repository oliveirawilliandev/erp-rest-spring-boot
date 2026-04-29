// [ORDER-STATUS-001] Pacote dos enums do modelo
package br.com.willian.model.enums;

// [ORDER-STATUS-002] Import do Jackson para desserialização de JSON
import com.fasterxml.jackson.annotation.JsonCreator;

// [ORDER-STATUS-003] Enum que representa os possíveis status de um pedido
public enum OrderStatus {
    PENDING, // [ORDER-STATUS-004] Pedido aguardando processamento (pendente)
    PROCESSING, // [ORDER-STATUS-005] Pedido está sendo processado/preparado
    SHIPPED, // [ORDER-STATUS-006] Pedido Recebido com sucesso
    CANCELLED,
    DELIVERED; // [ORDER-STATUS-007] Pedido cancelado pelo usuário ou sistema

    // [ORDER-STATUS-008] Método para desserialização de valores case-insensitive via JSON
    @JsonCreator // Indica ao Jackson que este método deve ser usado para criar o enum a partir de uma string
    public static OrderStatus fromValue(String text) {
        // [ORDER-STATUS-009] Se o texto for nulo, retorna null
        if (text == null) return null;

        // [ORDER-STATUS-010] Itera sobre todas as opções do enum
        for (OrderStatus status : OrderStatus.values()) {
            // [ORDER-STATUS-011] Comparação case-insensitive (ex: "pending" → PENDING)
            if (status.name().equalsIgnoreCase(text)) {
                return status; // [ORDER-STATUS-012] Retorna o enum correspondente
            }
        }

        // [ORDER-STATUS-013] Se nenhum valor corresponder, lança exceção com valores permitidos
        throw new IllegalArgumentException(
                "Invalid order status: " + text +
                        ". Allowed values: PENDING, PROCESSING, COMPLETED, CANCELLED."
        );
    }
}