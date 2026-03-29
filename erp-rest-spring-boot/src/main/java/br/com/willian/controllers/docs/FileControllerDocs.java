package br.com.oliveirawillian.controllers.docs;

import br.com.oliveirawillian.data.dto.v1.UploadFileResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name="File EndPoint")
public interface FileControllerDocs {

    UploadFileResponseDTO uploadFile(MultipartFile file);

    List<UploadFileResponseDTO> uploadMultipleFile(MultipartFile[] file);

    ResponseEntity<Resource> downloadFile(String fileName, HttpServletRequest request);

}
