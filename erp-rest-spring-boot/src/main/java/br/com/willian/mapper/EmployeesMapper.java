package br.com.oliveirawillian.mapper;

import br.com.oliveirawillian.data.dto.v1.PersonDTO;
import br.com.oliveirawillian.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    //@Mapping(source = "enabled", target = "habilitado")
        //Source = person
        //Target personDTO




    PersonDTO toDTO(Person person);

    Person toEntity(PersonDTO personDTO);

    List<PersonDTO> toDTOList(List<Person> personList);

    List<Person> toEntityList(List<PersonDTO> personDTOList);
}





