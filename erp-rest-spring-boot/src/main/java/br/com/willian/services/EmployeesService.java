    package br.com.oliveirawillian.services;

    import br.com.oliveirawillian.controllers.PersonController;
    import br.com.oliveirawillian.data.dto.v1.PersonDTO;

    import br.com.oliveirawillian.exception.BadRequestException;
    import br.com.oliveirawillian.exception.FileStorageException;
    import br.com.oliveirawillian.exception.RequiredObjectIsNullException;
    import br.com.oliveirawillian.exception.ResourceNotFoundException;

    import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
    import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


    import br.com.oliveirawillian.file.exporter.contract.PersonExporter;
    import br.com.oliveirawillian.file.exporter.factory.FileExporterFactory;
    import br.com.oliveirawillian.file.importer.contract.FileImporter;
    import br.com.oliveirawillian.file.importer.factory.FileImporterFactory;
    import br.com.oliveirawillian.mapper.PersonMapper;
    import br.com.oliveirawillian.model.Person;
    import br.com.oliveirawillian.repository.PersonRepository;
    import jakarta.transaction.Transactional;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.core.io.Resource;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.web.PagedResourcesAssembler;
    import org.springframework.hateoas.EntityModel;
    import org.springframework.hateoas.Link;
    import org.springframework.hateoas.PagedModel;
    import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.InputStream;
    import java.util.List;
    import java.util.Optional;
    import java.util.concurrent.atomic.AtomicLong;


    @Service
    public class PersonService {
        private final AtomicLong conter = new AtomicLong();

        private Logger logger = LoggerFactory.getLogger(PersonService.class.getName());

        @Autowired
        private PagedResourcesAssembler<PersonDTO> assembler;

        @Autowired
        private PersonRepository personRepository;

        @Autowired
        private PersonMapper personMapper;

        @Autowired
        FileImporterFactory importer;
        @Autowired
        FileExporterFactory exporter;



        public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable) {
            logger.info("Finding All Person");
            var people = personRepository.findAll(pageable);
            return buildPageModel(pageable, people);

        }
        public PagedModel<EntityModel<PersonDTO>> findByName(String firstName, Pageable pageable) {
            logger.info("Finding People by name!");
            var people = personRepository.FindPeopleByName(firstName,pageable);
            return buildPageModel(pageable, people);

        }

        public PersonDTO findById(Long id) {
            logger.info("Finding one Person");

            var entityLoaded = personRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("no records found for this ID"));
            var dtoLoaded = personMapper.toDTO(entityLoaded);
            addHateoasList(dtoLoaded);
            return dtoLoaded;

        }

        public Resource exportPerson(Long id, String acceptHeader) {
            logger.info("Exporting data of one Person");

            var entityLoaded = personRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("no records found for this ID"));
            var dtoLoaded = personMapper.toDTO(entityLoaded);
            addHateoasList(dtoLoaded);
            try {
                PersonExporter exporter = this.exporter.getExporter(acceptHeader);
                return exporter.exportPerson(dtoLoaded);
            } catch (Exception e) {
                throw new RuntimeException("Error during file export",e);

            }


        }


        public Resource exportPage(Pageable pageable, String acceptHeader) {
            logger.info("Exporting a Person page!");
            var people = personRepository.findAll(pageable).map(person -> personMapper.toDTO(person)).getContent();
            try {
                PersonExporter exporter = this.exporter.getExporter(acceptHeader);
                return exporter.exportPeople(people);
            } catch (Exception e) {
                throw new RuntimeException("Error during file export",e);
            }



        }

        public PersonDTO create(PersonDTO personDTO) {
            if (personDTO == null) throw new RequiredObjectIsNullException();
            logger.info("Creating one Person");

            var entity = personMapper.toEntity(personDTO); //1
            var entityPersisted = personRepository.save(entity); //2
            var dtoPersonPersisted = personMapper.toDTO(entityPersisted); //3
            addHateoasList(dtoPersonPersisted);
            return dtoPersonPersisted;
        }

        public List<PersonDTO>massCreation(MultipartFile file)  {
            logger.info("Importing People from file!");
           if(file.isEmpty()) throw new BadRequestException("Please set a valid File!");
           try (InputStream inputStream = file.getInputStream()){
               String fileName = Optional.ofNullable(file.getOriginalFilename()).orElseThrow(() -> new BadRequestException("File name cannot be null"));
                FileImporter importer = this.importer.getImporter(fileName);
                List<Person> entities = importer.importFile(inputStream).stream().map(dto -> personRepository.save(personMapper.toEntity(dto))).toList();

                var listPersonDTO = entities.stream().map(entity ->{
                   var dtoListLoaded = personMapper.toDTO(entity);
                   addHateoasList(dtoListLoaded);
                   return dtoListLoaded;
               }).toList();

            return listPersonDTO;
           }catch (Exception e){
               throw new FileStorageException("Error processing file!");
           }
        }

        public PersonDTO update(PersonDTO personDTO) {
            if (personDTO == null) throw new RequiredObjectIsNullException();
            logger.info("updating one Person");

            Person entityLoaded = personRepository.findById(personDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("no records found for this ID"));
            entityLoaded.setFirstName(personDTO.getFirstName());
            entityLoaded.setLastName(personDTO.getLastName());
            entityLoaded.setAddress(personDTO.getAddress());
            entityLoaded.setGender(personDTO.getGender());
            entityLoaded.setEnabled(personDTO.getEnabled());
            entityLoaded.setPhotoUrl(personDTO.getPhotoUrl());
            entityLoaded.setProfileUrl(personDTO.getProfileUrl());
            var entityPersisted = personRepository.save(entityLoaded);

            var dtoPersisted = personMapper.toDTO(entityPersisted);
            addHateoasList(dtoPersisted);
            return dtoPersisted;
        }

        public void delete(Long id) {
            logger.info("Deleting one Person");

            Person entityLoaded = personRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("no records found for this ID"));
            personRepository.delete(entityLoaded);

        }

        @Transactional
        public PersonDTO disablePerson(Long id) {
            logger.info("disabling one Person");


            personRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("no records found for this ID"));
            personRepository.disablePerson(id);
            var personEntity = personRepository.findById(id).get();
            var personDTO = personMapper.toDTO(personEntity);

            logger.info("Entity enabled: {}", personEntity.getEnabled());
            logger.info("DTO enabled: {}", personDTO.getEnabled());
            addHateoasList(personDTO);
            return personDTO;

        }

        private void addHateoasList(PersonDTO personDTO) {
            personDTO.add(linkTo(methodOn(PersonController.class).findById(personDTO.getId())).withSelfRel().withType("GET"));
            personDTO.add(linkTo(methodOn(PersonController.class).findAll(1,12,"asc")).withRel("findAll").withType("GET"));
            personDTO.add(linkTo(methodOn(PersonController.class).findByName("",1,11,"asc")).withRel("findByName").withType("GET"));
            personDTO.add(linkTo(methodOn(PersonController.class).create(personDTO)).withRel("create").withType("POST"));
            personDTO.add(linkTo(methodOn(PersonController.class)).slash("massCreation").withRel("massCreation").withType("POST"));
            personDTO.add(linkTo(methodOn(PersonController.class).update(personDTO)).withRel("update").withType("PUT"));
            personDTO.add(linkTo(methodOn(PersonController.class).disablePerson(personDTO.getId())).withRel("disable").withType("PATCH"));
            personDTO.add(linkTo(methodOn(PersonController.class).delete(personDTO.getId())).withRel("delete").withType("DELETE"));
            personDTO.add(linkTo(methodOn(PersonController.class).exportPage(1,12,"asc", null)).withRel("exportPage").withType("GET").withTitle("Export People"));
        }

        private PagedModel<EntityModel<PersonDTO>> buildPageModel(Pageable pageable, Page<Person> people) {
            var peopleWithLinks = people.map(person ->{
                var dtoListLoaded = personMapper.toDTO(person);
                addHateoasList(dtoListLoaded);
                return dtoListLoaded;
            });
            Link findAllLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).findAll(
                    pageable.getPageNumber(), pageable.getPageSize(), String.valueOf(pageable.getSort()))).withSelfRel();
            return assembler.toModel(peopleWithLinks, findAllLink);
        }
    }
