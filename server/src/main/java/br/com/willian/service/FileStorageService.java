package br.com.willian.service; // Pacote da camada de serviço

import br.com.willian.config.FileStorageConfig; // Configurações de diretório de upload
import br.com.willian.exception.FileNotFoundException; // Exceção para arquivo não encontrado
import br.com.willian.exception.FileStorageException; // Exceção para erro de armazenamento
import org.slf4j.Logger; // Interface de logging
import org.slf4j.LoggerFactory; // Fábrica de loggers
import org.springframework.beans.factory.annotation.Autowired; // Injeção de dependência
import org.springframework.core.io.Resource; // Representa um recurso (arquivo)
import org.springframework.core.io.UrlResource; // Recurso baseado em URL
import org.springframework.stereotype.Service; // Marca a classe como Service
import org.springframework.util.StringUtils; // Utilitários para String
import org.springframework.web.multipart.MultipartFile; // Arquivo enviado via upload

import java.nio.file.Files; // Manipulação de arquivos
import java.nio.file.Path; // Representação de caminho
import java.nio.file.Paths; // Criação de caminhos
import java.nio.file.StandardCopyOption; // Opções de cópia de arquivos

@Service // Serviço gerenciado pelo Spring
public class FileStorageService {

    private final Path fileStorageLocation; // Caminho base para armazenamento dos arquivos
    private final Path userPhotoStorageLocation;  // ADICIONAR este campo

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class); // Logger da classe

    // [SERVICE-TRACE: FILE-SRV-001]
    // Construtor com injeção da configuração de storage
    @Autowired // Injeta dependência da configuração
    public FileStorageService(FileStorageConfig fileStorageConfig) {

        logger.info("[FILE-SRV-001] Inicializando FileStorageService | uploadDir={}",
                fileStorageConfig.getUploadDir()); // Log do diretório configurado

        Path path = Paths.get(fileStorageConfig.getUploadDir()) // Diretório configurado
                .toAbsolutePath()                       // Caminho absoluto
                .normalize();                           // Normaliza o caminho

        this.fileStorageLocation = path;
        logger.debug("[FILE-SRV-001] Caminho absoluto normalizado: {}", this.fileStorageLocation); // Log do caminho final


        // ADICIONAR: Diretório para fotos de usuário
        Path userPhotoPath = Paths.get(fileStorageConfig.getUserPhotoDir())
                .toAbsolutePath()
                .normalize();
        this.userPhotoStorageLocation = userPhotoPath;
        logger.debug("[FILE-SRV-001] User photo dir: {}", this.userPhotoStorageLocation);


        try {
            logger.debug("[FILE-SRV-001] Criando diretório: {}", this.fileStorageLocation); // Log de criação
            Files.createDirectories(this.fileStorageLocation); // Cria o diretório se não existir
            Files.createDirectories(this.userPhotoStorageLocation);  // ADICIONAR esta linha
            logger.debug("[FILE-SRV-001] Diretório criado/verificado com sucesso"); // Log de sucesso
        } catch (Exception e) {
            logger.error("[FILE-SRV-001] Falha ao criar diretório: {}", this.fileStorageLocation, e); // Log de erro
            throw new FileStorageException(
                    "Could not create the directory where files will be stored", e); // Exceção
        }

    }

    // [SERVICE-TRACE: FILE-SRV-002]
    // Salva um arquivo no disco
    public String storeFile(MultipartFile file) {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename()); // Limpa o nome do arquivo
        logger.info("[FILE-SRV-002] StoreFile iniciado | originalName={} | cleanedName={} | size={} bytes | contentType={}",
                file.getOriginalFilename(), fileName, file.getSize(), file.getContentType()); // Log detalhado do upload

        // Validação de arquivo vazio
        if (file.isEmpty()) {
            logger.warn("[FILE-SRV-002] Arquivo vazio recebido para armazenamento | fileName={}", fileName); // Log de aviso
        }

        try {
            // Proteção contra path traversal
            if (fileName.contains("..")) {
                logger.error("[FILE-SRV-002] Path traversal detectado | fileName={}", fileName); // Log de erro de segurança
                throw new FileStorageException(
                        "Sorry! Filename contains invalid path sequence " + fileName); // Exceção
            }

            logger.debug("[FILE-SRV-002] Salvando arquivo no disco | fileName={} | targetDir={}",
                    fileName, this.fileStorageLocation); // Log de operação

            Path targetLocation = this.fileStorageLocation.resolve(fileName); // Caminho final do arquivo
            logger.debug("[FILE-SRV-002] Caminho alvo: {}", targetLocation); // Log do caminho

            Files.copy(
                    file.getInputStream(),                       // Conteúdo do arquivo
                    targetLocation,                              // Destino
                    StandardCopyOption.REPLACE_EXISTING          // Substitui se existir
            );

            logger.debug("[FILE-SRV-002] Arquivo armazenado com sucesso | fileName={} | size={} bytes | target={}",
                    fileName, file.getSize(), targetLocation); // Log de sucesso

            return fileName; // Retorna o nome do arquivo salvo

        } catch (Exception e) {
            logger.error("[FILE-SRV-002] Falha ao armazenar arquivo | fileName={} | erro={}",
                    fileName, e.getMessage(), e); // Log de erro
            throw new FileStorageException(
                    "Could not store the file " + fileName + ". Please try again!", e); // Exceção
        }
    }

    // [SERVICE-TRACE: FILE-SRV-003]
    // Carrega um arquivo como Resource
    public Resource loadFileAsResource(String fileName) {

        logger.info("[FILE-SRV-003] LoadFile iniciado | fileName={}", fileName); // Log da tentativa

        try {
            Path filePath = this.fileStorageLocation
                    .resolve(fileName) // Resolve o caminho do arquivo
                    .normalize();      // Normaliza o caminho
            logger.debug("[FILE-SRV-003] Caminho resolvido: {}", filePath); // Log do caminho

            Resource resource = new UrlResource(filePath.toUri()); // Cria o recurso a partir da URI
            logger.debug("[FILE-SRV-003] Resource criado | exists={} | readable={} | uri={}",
                    resource.exists(), resource.isReadable(), filePath.toUri()); // Log detalhado

            if (resource.exists() && resource.isReadable()) {
                logger.info("[FILE-SRV-003] Arquivo carregado com sucesso | fileName={} | path={}",
                        fileName, filePath); // Log de sucesso
                return resource; // Retorna o arquivo se estiver acessível
            } else {
                logger.warn("[FILE-SRV-003] Arquivo não encontrado ou não legível | fileName={} | exists={} | readable={}",
                        fileName, resource.exists(), resource.isReadable()); // Log de aviso
                throw new FileNotFoundException(
                        "Could not read file: " + fileName); // Exceção
            }

        } catch (Exception e) {
            logger.error("[FILE-SRV-003] Falha ao carregar arquivo | fileName={} | erro={}",
                    fileName, e.getMessage(), e); // Log de erro
            throw new FileNotFoundException(
                    "Could not load the file " + fileName + ". Please try again!"); // Exceção
        }
    }

    // Método específico para foto de usuário com ID
    public String storeFilePhotoUser(Long id, MultipartFile file) {
        // Pega a extensão do arquivo original
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";

        if (originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // Cria o novo nome usando apenas o ID + extensão
        String newFileName = id + extension;  // Ex: 11.png, 11.jpg, 11.pdf

        logger.info("[FILE-SRV-002] StoreFile Photo User | userId={} | originalName={} | newName={}",
                id, originalFileName, newFileName);

        // Salva com o novo nome no diretório de fotos
        return storeFile(file, this.userPhotoStorageLocation, newFileName);
    }

    // Método privado que aceita o nome do arquivo
    private String storeFile(MultipartFile file, Path storageLocation, String fileName) {
        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence " + fileName);
            }

            Path targetLocation = storageLocation.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            logger.info("[FILE-SRV-002] Arquivo armazenado: {}", fileName);
            return fileName;

        } catch (Exception e) {
            logger.error("Falha ao armazenar: {}", fileName, e);
            throw new FileStorageException("Could not store the file " + fileName, e);
        }
    }

    // MÉTODO PRIVADO AUXILIAR (ADICIONAR este método)
    private String storeFile(MultipartFile file, Path storageLocation) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        logger.info("[FILE-SRV-002] StoreFile iniciado | originalName={} | cleanedName={} | size={} bytes | contentType={} | targetDir={}",
                file.getOriginalFilename(), fileName, file.getSize(), file.getContentType(), storageLocation);

        if (file.isEmpty()) {
            logger.warn("[FILE-SRV-002] Arquivo vazio recebido para armazenamento | fileName={}", fileName);
        }

        try {
            if (fileName.contains("..")) {
                logger.error("[FILE-SRV-002] Path traversal detectado | fileName={}", fileName);
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            logger.debug("[FILE-SRV-002] Salvando arquivo no disco | fileName={} | targetDir={}",
                    fileName, storageLocation);

            Path targetLocation = storageLocation.resolve(fileName);
            logger.debug("[FILE-SRV-002] Caminho alvo: {}", targetLocation);

            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            logger.debug("[FILE-SRV-002] Arquivo armazenado com sucesso | fileName={} | size={} bytes | target={}",
                    fileName, file.getSize(), targetLocation);

            return fileName;

        } catch (Exception e) {
            logger.error("[FILE-SRV-002] Falha ao armazenar arquivo | fileName={} | erro={}",
                    fileName, e.getMessage(), e);
            throw new FileStorageException("Could not store the file " + fileName + ". Please try again!", e);
        }
    }

    // NOVO MÉTODO - para carregar foto de usuário (ADICIONAR)
    public Resource loadUserPhotoAsResource(String fileName) {
        return loadFileAsResource(fileName, this.userPhotoStorageLocation);
    }

    // MÉTODO PRIVADO AUXILIAR para load (ADICIONAR)
    private Resource loadFileAsResource(String fileName, Path storageLocation) {
        logger.info("[FILE-SRV-003] LoadFile iniciado | fileName={} | storageDir={}", fileName, storageLocation);

        try {
            Path filePath = storageLocation
                    .resolve(fileName)
                    .normalize();
            logger.debug("[FILE-SRV-003] Caminho resolvido: {}", filePath);

            Resource resource = new UrlResource(filePath.toUri());
            logger.debug("[FILE-SRV-003] Resource criado | exists={} | readable={} | uri={}",
                    resource.exists(), resource.isReadable(), filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                logger.info("[FILE-SRV-003] Arquivo carregado com sucesso | fileName={} | path={}",
                        fileName, filePath);
                return resource;
            } else {
                logger.warn("[FILE-SRV-003] Arquivo não encontrado ou não legível | fileName={} | exists={} | readable={}",
                        fileName, resource.exists(), resource.isReadable());
                throw new FileNotFoundException("Could not read file: " + fileName);
            }

        } catch (Exception e) {
            logger.error("[FILE-SRV-003] Falha ao carregar arquivo | fileName={} | erro={}",
                    fileName, e.getMessage(), e);
            throw new FileNotFoundException("Could not load the file " + fileName + ". Please try again!");
        }
    }



}