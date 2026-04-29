package br.com.willian.mapper; // Pacote da camada de mapper

import br.com.willian.dto.v1.security.AccountCredentialsDTO; // DTO para credenciais de conta
import br.com.willian.model.User; // Entidade User
import org.mapstruct.Mapper; // Anotação do MapStruct
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // Configura o MapStruct para gerar implementação como componente Spring
public interface AccountCredentialsMapper {


    // Comentário sobre mapeamento: enabled → habilitado
    //@Mapping(source = "enabled", target = "habilitado") // Exemplo de mapeamento personalizado
    // Source = User, Target = AccountCredentialsDTO
    // [ACC-MAPPER-001] Converte User → AccountCredentialsDTO
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "email", target = "email")  // <-- EXPLÍCITO
    AccountCredentialsDTO toDTO(User user); // Converte entidade User para DTO de credenciais

    // [ACC-MAPPER-002] Converte AccountCredentialsDTO → User
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "email", target = "email")  // <-- EXPLÍCITO
    User toEntity(AccountCredentialsDTO accountCredentialsDTO); // Converte DTO de credenciais para entidade User
}