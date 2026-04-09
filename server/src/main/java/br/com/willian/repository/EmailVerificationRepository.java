package br.com.willian.repository;

import br.com.willian.model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository
        extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByEmailAndCodeAndUsedFalse(
            String email, String code
    );

}
