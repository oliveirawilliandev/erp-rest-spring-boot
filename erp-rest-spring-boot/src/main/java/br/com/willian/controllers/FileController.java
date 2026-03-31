package br.com.willian.controllers; // Pacote da camada de controle/API

import br.com.willian.controllers.docs.FileControllerDocs; // Interface de documentação Swagger/OpenAPI
import br.com.willian.dto.v1.UploadFileResponseDTO; // DTO para resposta de upload de arquivo
import br.com.willian.services.FileStorageService; // Serviço para operações com arquivos
import jakarta.servlet.http.HttpServletRequest; // Interface para acessar dados da requisição HTTP
import org.slf4j.Logger; // Interface de logging SLF4J
import org.slf4j.LoggerFactory; // Factory para criação de loggers
import org.springframework.beans.factory.annotation.Autowired; // Injeção de dependência
import org.springframework.core.io.Resource; // Representação de recurso para download
import org.springframework.http.HttpHeaders; // Constantes para cabeçalhos HTTP
import org.springframework.http.MediaType; // Constantes para tipos de mídia
import org.springframework.http.ResponseEntity; // Entidade de resposta HTTP
import org.springframework.web.bind.annotation.*; // Anotações Spring para REST controllers
import org.springframework.web.multipart.MultipartFile; // Representação de arquivo upload
import org.springframework.web.servlet.support.ServletUriComponentsBuilder; // Builder para URIs

import java.util.Arrays; // Utilitário para arrays
import java.util.List; // Interface List

@RestController // Indica que é um controller REST com retorno direto
@RequestMapping("/api/file/v1") // Mapeia as requisições para esta URL base
public class FileController implements FileControllerDocs { // Implementa a interface de documentação

    // Cria um logger estático para esta classe com SLF4J
    private static final Logger logger = LoggerFactory.getLogger(FileController.class); // Logger para rastreamento

    @Autowired // Injeta dependência do serviço de armazenamento
    private FileStorageService fileStorageService; // Serviço para operações com arquivos

    //[CTRL-TRACE: FILE-CTRL-001]: Endpoint responsável por realizar o upload de um único arquivo
    @PostMapping( // Mapeia requisições POST
            value = "/uploadFile", // URL específica para upload de arquivo
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, // Aceita apenas multipart/form-data
            produces = MediaType.APPLICATION_JSON_VALUE // Retorna apenas JSON
    )
    @Override // Sobrescreve método da interface
    // Recebe um arquivo via multipart/form-data, persiste no storage e retorna metadados do arquivo salvo
    public UploadFileResponseDTO uploadFile(@RequestParam("file") MultipartFile file) { // Arquivo enviado no formulário

        logger.info("[FILE-CTRL-001] Upload iniciado | originalName={} | size={} bytes | contentType={}",
                file.getOriginalFilename(), file.getSize(), file.getContentType()); // Log com detalhes do arquivo

        // Validação de arquivo vazio
        if (file.isEmpty()) {
            logger.warn("[FILE-CTRL-001] Arquivo vazio recebido para upload | fileName={}", file.getOriginalFilename()); // Log de aviso
        }

        var fileName = fileStorageService.storeFile(file); // Chama serviço para armazenar o arquivo
        logger.debug("[FILE-CTRL-001] Arquivo armazenado com nome: {}", fileName); // Log do nome gerado

        // Constrói URL para download do arquivo
        //http://localhost:8080/api/file/v1/downloadFile/filename.docx
        var downloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath() // Pega contexto atual
                .path("/api/file/v1/downloadFile/") // Adiciona path base
                .path(fileName) // Adiciona nome do arquivo
                .toUriString(); // Converte para string
        logger.debug("[FILE-CTRL-001] URI de download gerada: {}", downloadUri); // Log da URI gerada


        // Retorna DTO com metadados do arquivo
        return new UploadFileResponseDTO(
                fileName, // Nome do arquivo armazenado
                downloadUri, // URI para download
                file.getContentType(), // Tipo do conteúdo
                file.getSize() // Tamanho do arquivo
        );
    }

    //[CTRL-TRACE: FILE-CTRL-002]: Endpoint responsável por realizar o upload de múltiplos arquivos
    @PostMapping( // Mapeia requisições POST
            value = "/uploadMultipleFiles", // URL específica para upload múltiplo
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, // Aceita apenas multipart/form-data
            produces = MediaType.APPLICATION_JSON_VALUE // Retorna apenas JSON
    )
    @Override // Sobrescreve método da interface
    // Reutiliza o fluxo do upload unitário para garantir consistência no comportamento
    public List<UploadFileResponseDTO> uploadMultipleFile(
            @RequestParam("files") MultipartFile[] files) { // Array de arquivos enviados

        logger.info("[FILE-CTRL-002] Upload múltiplo iniciado | totalFiles={}", files.length); // Log da quantidade de arquivos

        // Processa cada arquivo usando o método de upload individual
        var response = Arrays.stream(files) // Stream do array
                .map(this::uploadFile) // Mapeia cada arquivo para seu DTO de resposta
                .toList(); // Coleta em lista

        return response; // Retorna lista de DTOs
    }

    //[CTRL-TRACE: FILE-CTRL-003]: Endpoint responsável por realizar o download de um arquivo pelo nome
    @GetMapping( // Mapeia requisições GET
            value = "/downloadFile/{fileName:.+}") // URL com parâmetro fileName (regex para incluir extensão)
    @Override // Sobrescreve método da interface
    // Localiza o arquivo no storage e retorna como attachment para download
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {

        logger.info("[FILE-CTRL-003] Download solicitado | fileName={}", fileName); // Log do download

        var resource = fileStorageService.loadFileAsResource(fileName); // Carrega arquivo como recurso
        logger.debug("[FILE-CTRL-003] Arquivo carregado: {} | exists={} | readable={}",
                fileName, resource.exists(), resource.isReadable()); // Log detalhado do recurso

        String contentType = null; // Inicializa content type
        try {
            // Tenta determinar o content type baseado na extensão do arquivo
            contentType = request.getServletContext()
                    .getMimeType(resource.getFile().getAbsolutePath()); // Obtém MIME type do arquivo
            logger.debug("[FILE-CTRL-003] Content-Type resolvido: {}", contentType); // Log do content type
        } catch (Exception ex) {
            logger.warn("[FILE-CTRL-003] Não foi possível resolver o Content-Type. Usando fallback.", ex); // Log de aviso

            if(contentType == null) {
                contentType = "application/octet-stream"; // Fallback para tipo genérico
            }
        }

        logger.debug("[FILE-CTRL-003] Download preparado com sucesso | fileName={} | contentType={} ",
                fileName, contentType ); // Log de sucesso com detalhes

        // Retorna arquivo como attachment para download
        return ResponseEntity.ok() // Status 200 OK
                .contentType(MediaType.parseMediaType(contentType)) // Define content type
                .header(
                        HttpHeaders.CONTENT_DISPOSITION, // Cabeçalho de disposição
                        "attachment; filename=\"" + resource.getFilename() + "\"" // Força download com nome original
                )
                .body(resource); // Corpo com o recurso
    }
}