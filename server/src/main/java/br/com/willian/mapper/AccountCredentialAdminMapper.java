package br.com.willian.mapper; // Pacote da camada de mapper

import br.com.willian.dto.v1.security.AccountCredentialAdminDTO;
import br.com.willian.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") // Configura o MapStruct para gerar implementação como componente Spring
public interface AccountCredentialAdminMapper {


    // Comentário sobre mapeamento: enabled → habilitado
    //@Mapping(source = "enabled", target = "habilitado") // Exemplo de mapeamento personalizado
    // Source = User, Target = AccountCredentialsDTO
    // [ACC-MAPPER-001] Converte User → AccountCredentialsDTO
    AccountCredentialAdminDTO toDTO(User user); // Converte entidade User para DTO de credenciais

    // [ACC-MAPPER-002] Converte AccountCredentialsDTO → User
    User toEntity(AccountCredentialAdminDTO accountCredentialsDTO); // Converte DTO de credenciais para entidade User
}