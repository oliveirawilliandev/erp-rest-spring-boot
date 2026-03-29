package br.com.willian.integrationtests.dto.wrappers.xml.Employees;

import br.com.willian.integrationtests.dto.EmployeesDTO;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.Serializable;
import java.util.List;

/*
 * DTO responsável por mapear a estrutura raiz da resposta XML
 * retornada pelo endpoint de listagem de Employees.
 *
 * Este wrapper representa o <PagedModel>, que é o formato padrão
 * utilizado pelo Spring HATEOAS quando a resposta é serializada em XML.
 *
 * No XML, os dados não vêm dentro de "_embedded" (como no JSON),
 * mas sim organizados em nós <content>.
 *
 * Estrutura XML esperada (simplificada):
 *
 * <PagedModel>
 *   <links>
 *     <rel>self</rel>
 *     <href>http://localhost/api/employee/v1?page=0&amp;size=12</href>
 *   </links>
 *   <content>
 *     <content>
 *       <id>4</id>
 *       <firstName>Ana</firstName>
 *     </content>
 *     <content>
 *       <id>5</id>
 *       <firstName>João</firstName>
 *     </content>
 *   </content>
 * </PagedModel>
 *
 * Esta classe é utilizada exclusivamente em testes de integração
 * para desserializar respostas XML da API.
 */

// Define o nome do elemento raiz do XML que será associado a esta classe.
// Neste caso, informa ao Jackson que todo_ o XML começa com a tag <PagedModel>,
// permitindo que a desserialização funcione corretamente quando a resposta da API utiliza esse elemento como nó raiz (padrão do Spring HATEOAS em XML).
@JacksonXmlRootElement(localName = "PagedModel")
public class PageModelEmployees implements Serializable {

    // Controle de versão da serialização
    private static final long serialVersionUID = 1L;

    /*
     * Mapeia o nó externo <content> do XML, que funciona como um wrapper
     * para a lista de registros retornados.
     *
     * Cada item da lista corresponde a um nó interno <content>,
     * que representa um EmployeesDTO individual.
     */
    @JacksonXmlElementWrapper(localName = "content")
    @JacksonXmlProperty(localName = "content")
    private List<EmployeesDTO> content;

    /*
     * Construtor vazio obrigatório para o Jackson XML.
     * Utilizado durante o processo de desserialização
     * da resposta XML para objeto Java.
     */
    public PageModelEmployees() {
    }

    /*
     * Retorna a lista de Employees desserializada a partir
     * do XML retornado pelo endpoint.
     */
    public List<EmployeesDTO> getContent() {
        return content;
    }

    /*
     * Define a lista de Employees.
     * Normalmente preenchida automaticamente pelo Jackson
     * durante a conversão XML -> Objeto.
     */
    public void setContent(List<EmployeesDTO> content) {
        this.content = content;
    }
}
