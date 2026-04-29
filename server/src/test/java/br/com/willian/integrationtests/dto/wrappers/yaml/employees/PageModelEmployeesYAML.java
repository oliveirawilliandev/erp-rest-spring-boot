package br.com.willian.integrationtests.dto.wrappers.yaml.employees;

import br.com.willian.integrationtests.dto.EmployeesDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Wrapper para desserialização de respostas YAML
 * do endpoint paginado de Employee.
 *
 * Usado exclusivamente em testes de integração YAML.
 */
public class PageModelEmployeesYAML implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<EmployeesDTO> content;

    public PageModelEmployeesYAML() {
    }

    public List<EmployeesDTO> getContent() {
        return content;
    }

    public void setContent(List<EmployeesDTO> content) {
        this.content = content;
    }
}
