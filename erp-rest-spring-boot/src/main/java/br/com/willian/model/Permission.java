package br.com.willian.model; // Pacote da camada de modelo/entidade

import jakarta.persistence.*; // Anotações JPA para mapeamento objeto-relacional
import org.springframework.security.core.GrantedAuthority; // Interface do Spring Security para autoridades

import java.io.Serializable; // Interface para serialização
import java.util.Objects; // Utilitários para equals/hashCode

@Entity // Define a classe como uma entidade JPA
@Table(name = "permission") // Mapeia para a tabela "permission" no banco de dados
public class Permission implements GrantedAuthority, Serializable { // Implementa GrantedAuthority para Spring Security
    private static final long serialVersionUID = 1L; // Versão de serialização

    @Id // Define como chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Geração automática de ID (auto-increment)
    private Long id; // Identificador único da permissão

    @Column(nullable = false, length = 80) // Coluna obrigatória com tamanho máximo 80
    private String description; // Descrição da permissão (ex: "ADMIN", "USER", "MANAGER")

    // Construtor padrão (obrigatório para JPA)
    public Permission() {
    }

    // Método do Spring Security que retorna a autoridade (permissão)
    @Override
    public String getAuthority() {
        return this.description; // Retorna a descrição como autoridade
    }

    // Getter para ID
    public Long getId() {
        return id; // Retorna ID
    }

    // Setter para ID
    public void setId(Long id) {
        this.id = id; // Define ID
    }

    // Getter para descrição
    public String getDescription() {
        return description; // Retorna descrição
    }

    // Setter para descrição
    public void setDescription(String description) {
        this.description = description; // Define descrição
    }

    // equals baseado em ID e descrição
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Permission that)) return false; // Verifica tipo
        return Objects.equals(id, that.id) && // Compara ID
                Objects.equals(description, that.description); // Compara descrição
    }

    // hashCode baseado em ID e descrição
    @Override
    public int hashCode() {
        return Objects.hash(id, description); // Gera hash
    }
}