# 🚀 ERP Rest Spring Boot - Servidor (Backend)

Backend da aplicação ERP desenvolvido com **Spring Boot 3** e **Java 21**, responsável por toda a lógica de negócio e gerenciamento de dados.

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-6DB33F?style=for-the-badge&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-336791?style=for-the-badge&logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apache-maven)

---

## 📋 Índice

- [Quick Start](#quick-start)
- [Pré-requisitos](#pré-requisitos)
- [Configuração](#configuração)
- [Executar](#executar)
- [Estrutura](#estrutura)
- [Documentação da API](#documentação-da-api)

---

## Quick Start

```bash
# 1. Clonar e navegar
git clone https://github.com/oliveirawilliandev/erp-rest-spring-boot.git
cd erp-rest-spring-boot/server

# 2. Configurar variáveis de ambiente (.env)
# Veja a seção "Configuração" abaixo

# 3. Executar
mvn spring-boot:run
```

Servidor disponível em: `http://localhost:8080`

---

## Pré-requisitos

- **Java 21+** - [Download](https://www.oracle.com/java/technologies/downloads/#java21)
- **Maven 3.9+** - [Download](https://maven.apache.org/download.cgi)
- **PostgreSQL 15+** - [Download](https://www.postgresql.org/download/)

### Verificar instalação

```bash
java -version
mvn -version
psql --version
```

---

## Configuração

### 1. Banco de Dados

```bash
# Conectar ao PostgreSQL
psql -U postgres

# Criar banco
CREATE DATABASE erp_oliveira;
\q
```

### 2. Variáveis de Ambiente

Crie arquivo `.env` na raiz do servidor:

```bash
URL_BANCO=localhost
DB_USER=postgres
DB_PASSWORD=sua_senha
CONTEXT_PATH_FOTO=/server
CORS_ORIGIN_PATTERNS=localhost
EMAIL_USERNAMA=seu_email@gmail.com
EMAIL_PASSWORD=sua_senha_app
```

---

## Executar

### Desenvolvimento

```bash
# Compilar
mvn clean compile

# Executar migrations Flyway
mvn flyway:migrate

# Iniciar aplicação
mvn spring-boot:run
```

### Produção

```bash
# Build JAR
mvn clean package

# Executar JAR
java -jar target/erp-rest-spring-boot-0.0.1-SNAPSHOT.jar
```

### Docker

```bash
# Build imagem
docker build -t erp-backend:latest .

# Executar container
docker run -p 8080:8080 erp-backend:latest
```

---

## Estrutura

```
server/
├── src/main/java/br/com/willian/
│   ├── controller/       # REST Controllers
│   ├── service/          # Lógica de negócio
│   ├── repository/       # Acesso a dados (JPA)
│   ├── entity/           # Entidades do banco
│   ├── dto/              # Data Transfer Objects
│   ├── security/         # Configuração JWT
│   ├── exception/        # Tratamento de erros
│   └── config/           # Configurações gerais
│
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/     # Scripts Flyway
│
├── pom.xml               # Dependências Maven
└── Dockerfile
```

---

## Documentação da API

Após iniciar o servidor, acesse:

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Endpoints Principais

#### Autenticação
```
POST   /api/auth/login          # Login
POST   /api/auth/refresh        # Renovar token
```

#### Recursos
```
GET    /api/product/v1          # Listar produtos
GET    /api/order/v1            # Listar pedidos
GET    /api/employee/v1         # Listar funcionários
GET    /api/supplier/v1         # Listar fornecedores
GET    /api/ingredient/v1       # Listar ingredientes
```

Veja [README.md](../README.md) para lista completa de endpoints.

---

## Testes

```bash
# Executar testes
mvn test

# Gerar relatório de cobertura
mvn test jacoco:report
```

---

## Troubleshooting

### ❌ "Connection refused to PostgreSQL"
- Verifique se PostgreSQL está rodando
- Valide URL_BANCO, DB_USER e DB_PASSWORD no .env

### ❌ "Port 8080 already in use"
```bash
# Matar processo na porta 8080
lsof -i :8080 | grep LISTEN | awk '{print $2}' | xargs kill -9
```

### ❌ "Flyway migration failed"
- Verifique se banco de dados `erp_oliveira` existe
- Limpe migrations anteriores: `DELETE FROM flyway_schema_history;`

---

## 📚 Documentação Adicional

- [README Principal](../README.md) - Visão geral do projeto
- [README Cliente](../client/README.md) - Frontend React
- [Spring Boot Docs](https://spring.io/projects/spring-boot)

---

## 📞 Suporte

- 📧 Email: [oliveira.willian.dev@gmail.com](mailto:oliveira.willian.dev@gmail.com)
- 🐛 Issues: [GitHub Issues](https://github.com/oliveirawilliandev/erp-rest-spring-boot/issues)

---

**Desenvolvido com ❤️ por Willian Oliveira**
