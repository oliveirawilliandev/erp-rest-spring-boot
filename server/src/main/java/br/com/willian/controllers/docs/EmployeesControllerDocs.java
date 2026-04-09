package br.com.willian.controllers.docs;

import br.com.willian.dto.v1.EmployeesDTO;
import br.com.willian.file.exporter.MediaTypes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Employees", description =  "Endepoints for managing Employees")
public interface EmployeesControllerDocs {

    // [CTRL-TRACE: EMP-CTRL-001]
    // Retorna uma lista paginada de recursos
    // Permite paginação e ordenação
    // Retorna dados com suporte a HATEOAS
    @Operation(
            summary = "Find all employees",
            description = "Returns a paginated list of employees with sorting support",
            tags = {"Employees"},
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = EmployeesDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<PagedModel<EntityModel<EmployeesDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    // [CTRL-TRACE: EMP-CTRL-002]
    // Retorna uma lista paginada filtrada por nome
    // Permite paginação e ordenação
    // Retorna dados com suporte a HATEOAS
    @Operation(
            summary = "Find Employees by name",
            description = "Returns a paginated list of employees filtered by first name",
            tags = {"Employees"},
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = EmployeesDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<PagedModel<EntityModel<EmployeesDTO>>> findByName(
            @PathVariable("firstName") String firstName,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    // [CTRL-TRACE: EMP-CTRL-003]
    // Retorna um recurso específico pelo identificador
    @Operation(
            summary = "Find employee by ID",
            description = "Returns a specific employee by its identifier",
            tags = {"Employees"},
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = EmployeesDTO.class))),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    EmployeesDTO findById(@PathVariable("id") Long id);

    // [CTRL-TRACE: EMP-CTRL-004]
    // Cria um novo recurso
    @Operation(
            summary = "Create employee",
            description = "Creates a new employee",
            tags = {"Employees"},
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = EmployeesDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<EmployeesDTO> create(@RequestBody EmployeesDTO employees);

    // [CTRL-TRACE: EMP-CTRL-006]
    // Atualiza um recurso existente
    @Operation(
            summary = "Update employee",
            description = "Updates an existing employee",
            tags = {"Employees"},
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = EmployeesDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    EmployeesDTO update(@RequestBody EmployeesDTO employees);

    // [CTRL-TRACE: EMP-CTRL-007]
     //Atualiza parcialmente (desativa) um recurso
    @Operation(
            summary = "Disable employee",
            description = "Disables an employee by ID",
            tags = {"Employees"},
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = EmployeesDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }

    )
    EmployeesDTO disableEmployee(@PathVariable("id") Long id);

    // [CTRL-TRACE: EMP-CTRL-005]
    // Remove um recurso pelo identificador
    @Operation(
            summary = "Delete employee",
            description = "Deletes an employee by ID",
            tags = {"Employees"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<?> delete(@PathVariable("id") Long id);

    // [CTRL-TRACE: EMP-CTRL-008]
    // Exporta uma lista paginada de registros
    @Operation(summary = "Export Employees", description = "Export a Page of Employees in XLSX and CSV format", tags = {"Employees"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200",
                    content = {@Content(mediaType = MediaTypes.APPLICATION_XLSX_VALUE),
                            @Content(mediaType = MediaTypes.APPLICATION_CSV_VALUE )}),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content)
    })
    ResponseEntity<Resource> exportPage(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            HttpServletRequest request

    );
    // [CTRL-TRACE: EMP-CTRL-009]
    // Exportar dados de funcionários como PDF
    @Operation(summary = "Export Employees data as PDF", description = "Export a specific Employees data as PDF you ID", tags = {"Employees"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200",
                    content = @Content(mediaType = MediaTypes.APPLICATION_PDF_VALUE)),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content)
    })
    ResponseEntity<Resource> export(@PathVariable("id") Long id, HttpServletRequest request);


    // [CTRL-TRACE: EMP-CTRL-010]
    // Realiza importação em massa de registros
    @Operation(summary = "Massive Employees creation", description = "Massive Employees creation upload of XLSX or CSV", tags = {"Employees"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200",
                    content = {@Content(schema = @Schema(implementation = EmployeesDTO.class))}),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content)
    })
    List<EmployeesDTO> massCreation(MultipartFile file);
}
