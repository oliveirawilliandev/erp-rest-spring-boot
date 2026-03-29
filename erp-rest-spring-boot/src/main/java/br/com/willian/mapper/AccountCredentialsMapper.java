package br.com.oliveirawillian.mapper;

import br.com.oliveirawillian.data.dto.v1.BooksDTO;
import br.com.oliveirawillian.data.dto.v1.security.AccountCredentialsDTO;
import br.com.oliveirawillian.model.Books;
import br.com.oliveirawillian.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountCredentialsMapper {

    //@Mapping(source = "enabled", target = "habilitado")
        //Source = books
        //Target booksDTO




    AccountCredentialsDTO toDTO(User user);

    User toEntity(AccountCredentialsDTO accountCredentialsDTO);

    //List<AccountCredentialsDTO> toDTOList(List<User> userList);

    //List<User> toEntityList(List<AccountCredentialsDTO> accountCredentialsDTOList);
}





