package br.com.willian.repository;

import br.com.willian.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByDescription(String description);
    Set<Permission> findByDescriptionIn(Set<String> descriptions);
}

