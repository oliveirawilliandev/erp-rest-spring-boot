# 🏢 ERP Rest Spring Boot

Um sistema ERP (Enterprise Resource Planning) moderno e escalável desenvolvido com **Spring Boot 3** no backend e **React** no frontend, projetado para gerenciar operações empresariais como produtos, pedidos, funcionários, fornecedores e ingredientes.

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.13-6DB33F?style=for-the-badge&logo=spring)
![React](https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge&logo=react)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-336791?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

---

## 📋 Índice

- [Visão Geral](#visao-geral)
- [Arquitetura](#arquitetura)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pre-requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Como Usar](#como-usar)
- [Endpoints da API](#endpoints-da-api)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Funcionalidades](#funcionalidades)
- [Documentação da API](#documentação-da-api)
- [Docker](#docker)
- [Contribuindo](#contribuindo)
- [Licença](#licença)

---

## 🎯 Visão Geral

ERP Rest Spring Boot é uma solução completa de gerenciamento empresarial que oferece:

✅ **Gerenciamento de Produtos** - Cadastro e controle de inventário  
✅ **Gestão de Pedidos** - Criação e acompanhamento de pedidos  
✅ **Administração de Funcionários** - Controle de recursos humanos  
✅ **Gestão de Fornecedores** - Gerenciamento de parceiros comerciais  
✅ **Controle de Ingredientes** - Administração de matérias-primas  
✅ **Upload de Arquivos** - Armazenamento seguro de documentos  
✅ **Geração de Relatórios** - Relatórios em PDF com JasperReports  
✅ **Códigos QR** - Geração de QR codes para produtos  
✅ **Exportação de Dados** - Export em Excel, CSV e XML  
✅ **Autenticação JWT** - Segurança em camadas com tokens  

---

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT (React)                           │
│                       :3000                                 │
└──────────────────────────┬──────────────────────────────────┘
                           │
                      REST API
                           │
┌──────────────────────────▼──────────────────────────────────┐
│              SERVER (Spring Boot 3)                         │
│                  :8080                                      │
├─────────────────────────────────────────────────────────────┤
│ Controllers    → DTOs → Services → Repositories → Entities  │
├─────────────────────────────────────────────────────────────┤
│              PostgreSQL Database                            │
│              (Flyway Migrations)                            │
└─────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Tecnologias Utilizadas

### Backend
| Tecnologia | Versão | Descrição |
|-----------|--------|-----------|
| **Java** | 21 | Linguagem de programação principal |
| **Spring Boot** | 3.4.13 | Framework web |
| **Spring Data JPA** | - | ORM e acesso a dados |
| **Spring Security** | - | Autenticação e autorização |
| **Spring HATEOAS** | - | REST com hypermedia |
| **PostgreSQL** | 15+ | Banco de dados |
| **Flyway** | - | Versionamento de migrations |
| **JWT (Auth0)** | 4.4.0 | Token-based authentication |
| **MapStruct** | 1.6.3 | Mapeamento de DTOs |
| **OpenAPI 3.0** | 2.8.0 | Documentação de APIs |
| **Apache POI** | 5.3.0 | Manipulação de Excel |
| **JasperReports** | 7.0.1 | Geração de relatórios PDF |
| **ZXing** | 3.5.3 | Geração de QR codes |
| **Maven** | 3.9+ | Gerenciador de dependências |

### Frontend
| Tecnologia | Descrição |
|-----------|-----------|
| **React** | 18+ | Biblioteca UI |
| **Node.js** | 18+ | Runtime JavaScript |
| **npm** | 9+ | Gerenciador de pacotes |

### DevOps
| Tecnologia | Descrição |
|-----------|-----------|
| **Docker** | Containerização |
| **Docker Compose** | Orquestração de containers |
| **GitHub Actions** | CI/CD |

---

## 📦 Pré-requisitos

Antes de começar, verifique se você tem instalado:

- **Java 21+** - [Download](https://www.oracle.com/java/technologies/downloads/#java21)
- **Maven 3.9+** - [Download](https://maven.apache.org/download.cgi)
- **Node.js 18+** - [Download](https://nodejs.org/)
- **PostgreSQL 15+** - [Download](https://www.postgresql.org/download/)
- **Docker & Docker Compose** (opcional) - [Download](https://www.docker.com/products/docker-desktop)
- **Git** - [Download](https://git-scm.com/)

### Verificar Instalação

```bash
# Java
java -version

# Maven
mvn -version

# Node.js
node --version
npm --version

# PostgreSQL
psql --version
```

---

## 🚀 Instalação

### 1. Clonar o Repositório

```bash
git clone https://github.com/oliveirawilliandev/erp-rest-spring-boot.git
cd erp-rest-spring-boot
```

### 2. Configurar Backend

#### 2.1 Criar Banco de Dados

```bash
# Conectar ao PostgreSQL
psql -U postgres

# Criar banco de dados
CREATE DATABASE erp_oliveira;

# Sair
\q
```

#### 2.2 Configurar Variáveis de Ambiente

Crie arquivo `.env` na raiz do servidor:

```bash
# Linux/Mac
cat > server/.env << EOF
URL_BANCO=localhost
DB_USER=postgres
DB_PASSWORD=seu_password_aqui
CONTEXT_PATH_FOTO =/server
CORS_ORIGIN_PATTERNS=localhost
EMAIL_USERNAMA=seu_user_do_email
EMAIL_PASSWORD=seu_password_aqui
EOF
```

#### 2.3 Compilar e Executar

```bash
cd server

# Limpar e compilar
mvn clean compile

# Executar migrations Flyway
mvn flyway:migrate

# Iniciar aplicação
mvn spring-boot:run
```

O servidor estará disponível em: `http://localhost:8080`

### 3. Configurar Frontend

```bash
cd client

# Instalar dependências
npm install

# Iniciar servidor de desenvolvimento
npm start
```

A aplicação abrirá automaticamente em: `http://localhost:3000`

---


## 💻 Como Usar

### Backend

#### Iniciar Servidor
```bash
cd server
mvn spring-boot:run
```

#### Executar Testes
```bash
mvn test
```

#### Compilar JAR
```bash
mvn clean package
java -jar target/erp-rest-spring-boot-0.0.1-SNAPSHOT.jar
```

### Frontend

#### Iniciar Desenvolvimento
```bash
cd client
npm start
```

#### Build para Produção
```bash
npm run build
```

#### Executar Testes
```bash
npm test
```

---

## 📡 Endpoints da API

### Autenticação
```
POST   /api/auth/login          # Login com JWT
POST   /api/auth/refresh        # Renovar token
```

### Produtos
```
GET    /api/product/v1          # Listar produtos (paginado)
GET    /api/product/v1/{id}     # Obter produto por ID
GET    /api/product/v1/findByName/{name}     # Buscar por nome
POST   /api/product/v1          # Criar novo produto
PUT    /api/product/v1          # Atualizar produto
PATCH  /api/product/v1/updateStock/{id}     # Atualizar estoque
DELETE /api/product/v1/{id}     # Deletar produto
```

### Pedidos
```
GET    /api/order/v1            # Listar pedidos (paginado)
GET    /api/order/v1/{id}       # Obter pedido por ID
GET    /api/order/v1/findByCustomerId/{customerId}  # Pedidos do cliente
GET    /api/order/v1/findByStatus/{status}  # Pedidos por status
POST   /api/order/v1            # Criar novo pedido
PATCH  /api/order/v1/updateStatus/{id}      # Atualizar status
POST   /api/order/v1/cancel/{id}            # Cancelar pedido
DELETE /api/order/v1/{id}       # Deletar pedido
```

### Funcionários
```
GET    /api/employee/v1         # Listar funcionários (paginado)
GET    /api/employee/v1/{id}    # Obter funcionário por ID
GET    /api/employee/v1/findEmployeeByName/{name}  # Buscar por nome
GET    /api/employee/v1/findByEmail/{email}        # Buscar por email
GET    /api/employee/v1/active  # Funcionários ativos
POST   /api/employee/v1         # Criar funcionário
PUT    /api/employee/v1         # Atualizar funcionário
PATCH  /api/employee/v1/{id}    # Desativar funcionário
POST   /api/employee/v1/massCreation  # Importação em massa
```

### Fornecedores
```
GET    /api/supplier/v1         # Listar fornecedores
GET    /api/supplier/v1/{id}    # Obter fornecedor por ID
GET    /api/supplier/v1/findByDocument/{document}  # Buscar por documento
POST   /api/supplier/v1         # Criar fornecedor
PUT    /api/supplier/v1         # Atualizar fornecedor
DELETE /api/supplier/v1/{id}    # Deletar fornecedor
```

### Ingredientes
```
GET    /api/ingredient/v1       # Listar ingredientes
GET    /api/ingredient/v1/{id}  # Obter ingrediente
GET    /api/ingredient/v1/findByName/{name}        # Buscar por nome
GET    /api/ingredient/v1/findLowStock             # Estoque baixo
POST   /api/ingredient/v1       # Criar ingrediente
PUT    /api/ingredient/v1       # Atualizar ingrediente
PATCH  /api/ingredient/v1/updateStock/{id}        # Atualizar estoque
DELETE /api/ingredient/v1/{id}  # Deletar ingrediente
```

### Arquivos
```
POST   /api/file/v1/uploadFile              # Upload de arquivo
POST   /api/file/v1/uploadMultipleFiles     # Upload múltiplo
GET    /api/file/v1/downloadFile/{fileName}       # Download arquivo
POST   /api/file/v1/users/{id}/photo              # Upload foto usuário
GET    /api/file/v1/downloadUserPhoto/{fileName}  # Download foto usuário
```

---

## 📁 Estrutura do Projeto

```
erp-rest-spring-boot/
├── server/                                 # Backend Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/br/com/willian/
│   │   │   │   ├── controller/             # Controllers REST
│   │   │   │   ├── service/                # Lógica de negócio
│   │   │   │   ├── repository/             # Acesso a dados
│   │   │   │   ├── entity/                 # Entidades JPA
│   │   │   │   ├── dto/                    # Data Transfer Objects
│   │   │   │   ├── security/               # Configuração JWT
│   │   │   │   └── exception/              # Tratamento de erros
│   │   │   └── resources/
│   │   │       ├── application.properties  # Configurações
│   │   │       └── db/migration/           # Scripts Flyway
│   │   └── test/                           # Testes automatizados
│   └── pom.xml                             # Dependências Maven
│
├── client/                                 # Frontend React
│   ├── src/
│   │   ├── components/                     # Componentes React
│   │   ├── pages/                          # Páginas
│   │   ├── services/                       # Serviços API
│   │   ├── styles/                         # Estilos CSS
│   │   └── App.js                          # Componente principal
│   ├── public/
│   └── package.json                        # Dependências npm
│
├── docker-compose.yml                      # Orquestração Docker
├── Dockerfile                              # Imagem Docker
└── README.md                               # Este arquivo
```

---

## ✨ Funcionalidades

### 🔐 Segurança
- ✅ Autenticação com JWT
- ✅ Criptografia de senhas (BCrypt)
- ✅ Validação de entrada em todos endpoints
- ✅ Rate limiting (configurável)
- ✅ CORS configurável

### 📊 Relatórios
- ✅ Geração de PDFs com JasperReports
- ✅ Exportação em Excel (.xlsx)
- ✅ Exportação em CSV
- ✅ Relatórios parametrizados

### 🎯 Recursos Avançados
- ✅ Geração automática de QR codes
- ✅ Upload/Download de arquivos
- ✅ Busca com filtros avançados
- ✅ Paginação e ordenação
- ✅ Respostas HATEOAS
- ✅ Suporte a múltiplos formatos (JSON, XML, YAML)

### 📚 Documentação
- ✅ OpenAPI 3.0 / Swagger UI
- ✅ Documentação interativa de APIs
- ✅ Schema de requisições e respostas

---

## 📖 Documentação da API

### Acessar Swagger UI

Após iniciar o servidor, acesse:

```
http://localhost:8080/swagger-ui/index.html
```

### Acessar OpenAPI JSON

```
http://localhost:8080/v3/api-docs
```

### Exemplo de Requisição com cURL

```bash
# Listar produtos com paginação
curl -X GET "http://localhost:8080/api/product/v1?page=0&size=10&direction=asc" \
  -H "Content-Type: application/json"

# Criar novo produto
curl -X POST http://localhost:8080/api/product/v1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Produto Teste",
    "description": "Descrição do produto",
    "price": 99.99,
    "stockQuantity": 10
  }'

# Listar funcionários
curl -X GET "http://localhost:8080/api/employee/v1?page=0&size=10" \
  -H "Content-Type: application/json"
```

---

## 🐳 Docker

### Executar com Docker Compose

```bash
# Iniciar todos os containers
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar containers
docker-compose down
```

### Build de Imagem Docker

```bash
# Backend
docker build -t erp-backend:latest ./server

# Frontend
docker build -t erp-frontend:latest ./client
```

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor, siga os passos abaixo:

1. **Fork** o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um **Pull Request**

### Guia de Estilo

- Seguir convenções Java (camelCase, nomes descritivos)
- Adicionar Javadoc para métodos públicos
- Escrever testes para novas funcionalidades
- Manter a cobertura de testes acima de 80%

---

## 📝 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 📞 Suporte

Para suporte, envie um email para [oliveira.willian.dev@gmail.com](mailto:oliveira.willian.dev@gmail.com) ou abra uma [issue](https://github.com/oliveirawilliandev/erp-rest-spring-boot/issues).

---

## 👨‍💻 Autor

**Willian Oliveira**

- GitHub: [@oliveirawilliandev](https://github.com/oliveirawilliandev)
- Email: oliveira.willian.dev@gmail.com

---

## 🙏 Agradecimentos

- Spring Boot Community
- React Community
- PostgreSQL
- GitHub Community

---

**Desenvolvido com ❤️ por Willian Oliveira**

Última atualização: Maio 2026
