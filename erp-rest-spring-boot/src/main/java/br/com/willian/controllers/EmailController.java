package br.com.oliveirawillian.controllers;

import br.com.oliveirawillian.controllers.docs.EmailControllerDocs;
import br.com.oliveirawillian.data.dto.v1.request.EmailRequestDTO;
import br.com.oliveirawillian.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api/email/v1")
public class EmailController implements EmailControllerDocs {

    @Autowired
    private EmailService emailService;
    @PostMapping
    @Override
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequestDTO) {
        emailService.sendSimpleMail(emailRequestDTO);
        return new ResponseEntity<>("Email send with success!", HttpStatus.OK);
    }
    @PostMapping(value = "/withAttachment",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<String> sendEmailWithAttachment(@RequestParam("emailRequest") String emailRequest, @RequestParam("attachment") MultipartFile attachment) {
    emailService.sendEmailWithAttachment(emailRequest,attachment);
    return new ResponseEntity<>("Email with attachment send with successfully!", HttpStatus.OK);
    }
}
