package br.com.oliveirawillian.services;

import br.com.oliveirawillian.config.FileStorageConfig;
import br.com.oliveirawillian.exception.FileNotFoundException;
import br.com.oliveirawillian.exception.FileStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);


    @Autowired
    public FileStorageService(FileStorageConfig fileStorageConfig) {
        Path path = Paths.get(fileStorageConfig.getUploadDir()).toAbsolutePath().toAbsolutePath().normalize();
        this.fileStorageLocation = path;
        try {
            logger.info("Creating Directory {}", this.fileStorageLocation);
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            logger.error("could not create the directory where files will be stored");
            throw new FileStorageException("could not create the directory where files will be stored", e);

        }

    }

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {

            if (fileName.contains("..")) {
                logger.error("sorry! Filename contains invalid path sequence " + fileName);
                throw new FileStorageException("sorry! Filename contains invalid path sequence " + fileName);
            }
            logger.info("Salving file in disk {}", fileName);
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (Exception e) {
            logger.error("could not store the file" + fileName + "please try again!");
            throw new FileStorageException("could not store the file" + fileName + "please try again!", e);
        }
    }
public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }else  {
                throw new FileNotFoundException("Could not read file: " + fileName);
            }

        }catch (Exception e) {
            logger.error("could not load the file" + fileName + "please try again!");
            throw new FileNotFoundException("could not load the file" + fileName + "please try again!");
        }
}

}
