package br.com.willian.dto.v1;

import java.io.Serializable; // Permite serialização do objeto (transporte em rede/cache)
import java.util.Objects; // Utilitário para equals e hashCode

public class UploadFileResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L; // Controle de versão da serialização

    private String fileName;            // Nome do arquivo salvo
    private String fileDownloadUri;     // URL para download do arquivo
    private String fileType;            // Tipo MIME do arquivo (ex: image/png, application/pdf)
    private Long fileSize;              // Tamanho do arquivo em bytes

    public UploadFileResponseDTO(
            String fileName,            // Nome do arquivo
            String fileDownloadUri,     // URI de download
            String fileType,            // Tipo MIME
            Long fileSize               // Tamanho em bytes
    ) {
        this.fileName = fileName;       // Define nome do arquivo
        this.fileDownloadUri = fileDownloadUri; // Define URI de download
        this.fileType = fileType;       // Define tipo MIME
        this.fileSize = fileSize;       // Define tamanho do arquivo
    }

    public UploadFileResponseDTO() {
        // Construtor padrão exigido por frameworks de serialização (Jackson)
    }

    public String getFileName() {
        return fileName; // Retorna nome do arquivo
    }

    public void setFileName(String fileName) {
        this.fileName = fileName; // Define nome do arquivo
    }

    public String getFileDownloadUri() {
        return fileDownloadUri; // Retorna URI de download
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri; // Define URI de download
    }

    public String getFileType() {
        return fileType; // Retorna tipo MIME do arquivo
    }

    public void setFileType(String fileType) {
        this.fileType = fileType; // Define tipo MIME
    }

    public Long getFileSize() {
        return fileSize; // Retorna tamanho do arquivo em bytes
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize; // Define tamanho do arquivo
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UploadFileResponseDTO that)) return false; // Verifica tipo
        return Objects.equals(fileName, that.fileName) &&
                Objects.equals(fileDownloadUri, that.fileDownloadUri) &&
                Objects.equals(fileType, that.fileType) &&
                Objects.equals(fileSize, that.fileSize); // Compara campos relevantes
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                fileName,
                fileDownloadUri,
                fileType,
                fileSize
        ); // Gera hash consistente com equals
    }
}
