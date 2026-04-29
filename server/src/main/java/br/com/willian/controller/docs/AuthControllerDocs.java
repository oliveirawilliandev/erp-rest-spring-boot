package br.com.willian.controller.docs;

import br.com.willian.dto.v1.security.AccountCredentialAdminDTO;
import br.com.willian.dto.v1.security.AccountCredentialsDTO;
import br.com.willian.dto.v1.security.UserEditProfileDTO;
import br.com.willian.dto.v1.security.UserProfileDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthControllerDocs {
    @Operation(
            summary = "Authenticates a user and returns a token",
            description = "Validates user credentials and generates an access token for authentication.",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<?> signin( AccountCredentialsDTO accountCredentialsDTO) throws Exception;

    @Operation(
            summary = "Refresh token for authenticated user and returns a token",
            description = "Generates a new access token using the provided refresh token and username.",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<?> refreshToken( String userName,  String refreshToken) throws Exception;

    @Operation(
            summary = "Create a new User",
            description = "Registers a new user in the system with the provided credentials.",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Conflict", responseCode = "409", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<AccountCredentialsDTO> create(AccountCredentialsDTO credentialsDTO);
    @Operation(
            summary = "Create a new User power Admin",
            description = "Registers a new user Power Admin in the system with the provided credentials.",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Conflict", responseCode = "409", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<AccountCredentialAdminDTO> createAdmin( AccountCredentialAdminDTO credentialsDTO);

    /**
     * [AUTH-CTRL-006]
     *
     * Busca o perfil do usuário atualmente logado.
     *
     * FLUXO:
     * 1. Obtém o username do token JWT
     * 2. Busca os dados do usuário no banco
     * 3. Retorna as informações incluindo a URL da foto
     *
     * RESPOSTAS:
     * - 200: Perfil encontrado com sucesso
     * - 404: Usuário não encontrado
     * - 401: Token inválido ou não fornecido
     */
    @Operation(
            summary = "Get current user profile",
            description = "Returns the profile information of the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile found successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    ResponseEntity<UserProfileDTO> getCurrentUser();

    @Operation(
            summary = "Update user profile",
            description = "Update data profile information of the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    ResponseEntity update(@RequestBody UserEditProfileDTO userEditProfileDTO) ;
}
