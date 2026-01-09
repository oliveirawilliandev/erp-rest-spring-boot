//package br.com.willian.controllers.docs;
//
//import br.com.oliveirawillian.data.dto.v1.PersonDTO;
//import br.com.oliveirawillian.file.exporter.MediaTypes;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.ArraySchema;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.core.io.Resource;
//import org.springframework.hateoas.EntityModel;
//import org.springframework.hateoas.PagedModel;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//public interface PersonControllerDocs {
//
//
//    @Operation(summary = "Find All People", description = "Finds All People", tags = {"People"}, responses = {
//            @ApiResponse(description = "Success", responseCode = "200",
//                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
//                            array = @ArraySchema(schema = @Schema(implementation = PersonDTO.class)))}),
//            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
//            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
//            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
//            @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
//            @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content)
//    })
//    ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findAll(
//            @RequestParam(value = "page", defaultValue = "0") Integer page,
//            @RequestParam(value = "size", defaultValue = "12") Integer size,
//            @RequestParam(value = "direction", defaultValue = "asc") String direction
//    );
//    @Operation(summary = "Find People by firstName", description = "Find People by their firstName", tags = {"People"}, responses = {
//        @ApiResponse(description = "Success", responseCode = "200",
//        content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
//        array = @ArraySchema(schema = @Schema(implementation = PersonDTO.class)))}),
//        @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
//        @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
//        @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
//        @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
//        @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content)
//    })
//    ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findByName(
//    @PathVariable("firstName") String firstName,
//    @RequestParam(value = "page", defaultValue = "0") Integer page,
//    @RequestParam(value = "size", defaultValue = "12") Integer size,
//    @RequestParam(value = "direction", defaultValue = "asc") String direction
//    );
//
//
//    @Operation(summary = "Find a People", description = "Finds specific People by your ID", tags = {"People"}, responses = {
//    @ApiResponse(description = "Success", responseCode = "200",
//    content = @Content(schema = @Schema(implementation = PersonDTO.class))),
//    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
//    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
//    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
//    @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
//    @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content)
//})
//    PersonDTO findById(@PathVariable("id") Long id);
//
//    @Operation(summary = "Export Person data as PDF", description = "Export a specific Person data as PDF you ID", tags = {"People"}, responses = {
//            @ApiResponse(description = "Success", responseCode = "200",
//                    content = @Content(mediaType = MediaTypes.APPLICATION_PDF_VALUE)),
//    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
//    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
//    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
//    @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
//    @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content)
//})
//    ResponseEntity<Resource> export(@PathVariable("id") Long id, HttpServletRequest request);
//
//
//    @Operation(summary = "Create a People", description = "Create People", tags = {"People"}, responses = {
//    @ApiResponse(description = "Success", responseCode = "200",
//    content = @Content(schema = @Schema(implementation = PersonDTO.class))),
//    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
//    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
//    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
//    @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
//    @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content)
//})
//    PersonDTO create(@RequestBody PersonDTO person);
//
//
//    @Operation(summary = "Export People", description = "Export a Page of People in XLSX and CSV format", tags = {"People"}, responses = {
//            @ApiResponse(description = "Success", responseCode = "200",
//                    content = {@Content(mediaType = MediaTypes.APPLICATION_XLSX_VALUE),
//                            @Content(mediaType = MediaTypes.APPLICATION_CSV_VALUE )}),
//            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
//            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
//            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
//            @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
//            @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content)
//    })
//    ResponseEntity<Resource> exportPage(
//            @RequestParam(value = "page", defaultValue = "0") Integer page,
//            @RequestParam(value = "size", defaultValue = "12") Integer size,
//            @RequestParam(value = "direction", defaultValue = "asc") String direction,
//            HttpServletRequest request
//
//    );
//
//    @Operation(summary = "Massive People creation", description = "Massive People creation upload of XLSX or CSV", tags = {"People"}, responses = {
//    @ApiResponse(description = "Success", responseCode = "200",
//    content = {@Content(schema = @Schema(implementation = PersonDTO.class))}),
//    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
//            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
//            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
//            @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
//            @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content)
//    })
//    List<PersonDTO> massCreation(MultipartFile file);
//
//
//    @Operation(summary = "Update a People", description = "Update specific People", tags = {"People"}, responses = {
//            @ApiResponse(description = "Success", responseCode = "200",
//                    content = @Content(schema = @Schema(implementation = PersonDTO.class))),
//            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
//            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
//            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
//            @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
//            @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content)
//    })
//    PersonDTO update(@RequestBody PersonDTO person);
//
//
//    @Operation(summary = "disable a People", description = "disable specific People by your ID", tags = {"People"}, responses = {
//            @ApiResponse(description = "Success", responseCode = "200",
//                    content = @Content(schema = @Schema(implementation = PersonDTO.class))),
//            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
//            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
//            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
//            @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
//            @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content)
//    })
//    PersonDTO disablePerson(@PathVariable("id") Long id);
//
//    @Operation(summary = "Delete a People", description = "Delete specific People by your ID", tags = {"People"}, responses = {
//            @ApiResponse(description = "Success", responseCode = "200",
//                    content = @Content(schema = @Schema(implementation = PersonDTO.class))),
//            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
//            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
//            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
//            @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
//            @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content)
//    })
//    ResponseEntity<?> delete(@PathVariable("id") Long id);
//}
