package br.com.willian.integrationtests.dto.wrappers.json.employees;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

// DTO responsável por mapear o nível raiz da resposta JSON
// utilizada no endpoint de listagem de Employee.
//
// Essa classe funciona como um "wrapper" da resposta,
// sendo usada principalmente quando a API retorna dados
// no padrão HAL / HATEOAS.
//
// Exemplo de JSON esperado:
//
// {
//   "_embedded": {
//     "employeesDTOList": [
//       { "id": 1, "firstName": "João", ... }
//     ]
//   },
//   "_links": {
//     "self": { "href": "http://localhost/api/employee/v1" }
//   }
// }
public class WrapperEmployeesDTO implements Serializable {

    // Controle de versão da serialização
    private static final long serialVersionUID = 1L;

    // Mapeia o objeto "_embedded" do JSON
    // Esse campo contém a lista real de Employee

    @JsonProperty("_embedded")
    private EmployeesEmbeddedDTO embedded;

    // Construtor vazio obrigatório para o Jackson
    // Utilizado durante a desserialização JSON -> Objeto

    public WrapperEmployeesDTO() {
    }

    // Retorna o objeto Embedded que contém a lista de Employee
    public EmployeesEmbeddedDTO getEmbedded() {
        return embedded;
    }

    // Define o objeto Embedded
    // Normalmente preenchido automaticamente pelo Jackson

    public void setEmbedded(EmployeesEmbeddedDTO embedded) {
        this.embedded = embedded;
    }
}
