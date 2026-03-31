package br.com.willian.integrationtests.dto.wrappers.json.employees;

import br.com.willian.integrationtests.dto.EmployeesDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

// DTO responsável por mapear a parte "_embedded" da resposta JSON
// utilizada no endpoint de listagem de Employeess.
//
// Essa classe existe para dar suporte a respostas no padrão HAL / HATEOAS,
// onde os dados principais vêm aninhados dentro de um objeto "_embedded".
//
// Exemplo de JSON esperado:
//
// {
//   "_embedded": {
//     "employeesDTOList": [
//       { "id": 1, "firstName": "João", ... }
//     ]
//   }
// }
public class EmployeesEmbeddedDTO implements Serializable {

    // Controle de versão da serialização
    private static final long serialVersionUID = 1L;

    // Lista de Employeess retornada pela API
    // O nome "employeesDTOList" deve corresponder exatamente
    // à chave presente no JSON retornado pelo backend

    @JsonProperty("employees")
    private List<EmployeesDTO> employees;

    // Construtor vazio obrigatório para o Jackson
    // Utilizado durante o processo de desserialização JSON -> Objeto

    public EmployeesEmbeddedDTO() {
    }

    // Retorna a lista de Employeess desserializada do JSON
    public List<EmployeesDTO> getEmployees() {
        return employees;
    }

    // Define a lista de Employeess
    // Normalmente chamado automaticamente pelo Jackson
    // durante a conversão do JSON

    public void setEmployees(List<EmployeesDTO> Employees) {
        this.employees = Employees;
    }
}
