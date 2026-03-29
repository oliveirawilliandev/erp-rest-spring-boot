package br.com.willian.controllers;

import br.com.oliveirawillian.controllers.docs.PersonControllerDocs;
import br.com.oliveirawillian.data.dto.v1.PersonDTO;
import br.com.oliveirawillian.file.exporter.MediaTypes;
import br.com.oliveirawillian.services.PersonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/person/v1")
@Tag(name = "employess", description = "Endpoints for Managing employees")
public class PersonController implements PersonControllerDocs {
    @Autowired
    private PersonService personService;


    @GetMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE})

    @Override
    public  ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findAll(
            @RequestParam(value =  "page", defaultValue = "0") Integer page,
            @RequestParam(value =  "size", defaultValue = "12") Integer size ,
            @RequestParam(value =  "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        return ResponseEntity.ok(personService.findAll(pageable));
    }

    //findByName
    @GetMapping(value = "/findPeopleByName/{firstName}", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE})

        @Override
    public  ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findByName(
                @PathVariable("firstName") String firstName,
                @RequestParam(value =  "page", defaultValue = "0") Integer page,
                @RequestParam(value =  "size", defaultValue = "12") Integer size ,
                @RequestParam(value =  "direction", defaultValue = "asc") String direction
        ){
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        return ResponseEntity.ok(personService.findByName(firstName,pageable));

    }



    @GetMapping(value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})

    //@CrossOrigin(origins = "http://localhost:8080")
    @Override
    public PersonDTO findById(@PathVariable("id") Long id) {

        var person = personService.findById(id);


        return person;
    }
    @GetMapping(value = "/export/{id}",
            produces = {
                    MediaTypes.APPLICATION_PDF_VALUE})

    @Override
    public ResponseEntity<Resource> export(@PathVariable("id") Long id, HttpServletRequest request) {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        Resource file = personService.exportPerson(id, acceptHeader);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(acceptHeader)).header(HttpHeaders.CONTENT_DISPOSITION,"attachment=; filename=person.pdf").body(file);

    }

    //@CrossOrigin(origins = {"http://localhost:8080","http://127.0.0.1:8080"})
    @PostMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE},
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
        @Override
    public PersonDTO create(@RequestBody PersonDTO person) {
        return personService.create(person);
    }
    @GetMapping(value = "/exportPage", produces = {
            MediaTypes.APPLICATION_CSV_VALUE,
            MediaTypes.APPLICATION_XLSX_VALUE,
            MediaTypes.APPLICATION_PDF_VALUE
    })
    @Override
    public   ResponseEntity<Resource> exportPage(
            @RequestParam(value =  "page", defaultValue = "0") Integer page,
            @RequestParam(value =  "size", defaultValue = "12") Integer size ,
            @RequestParam(value =  "direction", defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        Resource file = personService.exportPage(pageable, acceptHeader);
Map<String, String> extensionMap = Map.of(MediaTypes.APPLICATION_XLSX_VALUE, ".xlsx",
        MediaTypes.APPLICATION_CSV_VALUE, ".csv",
        MediaTypes.APPLICATION_PDF_VALUE, ".pdf");

        var fileExtension = extensionMap.getOrDefault(acceptHeader,"");
        var contentType = acceptHeader != null ? acceptHeader : "application/octet-stream";
            var filename = "people+exported" + fileExtension;
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION,"attachment=; filename=\""+ filename + "\"").body(file);

    }

    @PostMapping(   value ="/massCreation" ,
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
        @Override
    public List<PersonDTO> massCreation(@RequestParam("file") MultipartFile file){
        return personService.massCreation(file);
    }




    @PutMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE},
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})

    @Override
    public PersonDTO update(@RequestBody PersonDTO person) {
        return personService.update(person);
    }

    @PatchMapping(value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE}
    )
    @Override
    public PersonDTO disablePerson(@PathVariable("id") Long id){
        return personService.disablePerson(id);
    }

    @DeleteMapping(value = "/{id}")

    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
         personService.delete(id);
         return ResponseEntity.noContent().build();
    }

}
