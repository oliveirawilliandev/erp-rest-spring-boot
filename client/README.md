# 🎨 ERP Rest Spring Boot - Cliente (React)

Frontend da aplicação ERP desenvolvido com **React 18**, responsável pela interface de usuário moderna e responsiva para gerenciamento empresarial.

![React](https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge&logo=react)
![Node.js](https://img.shields.io/badge/Node.js-18+-339933?style=for-the-badge&logo=node.js)
![npm](https://img.shields.io/badge/npm-9+-CB3837?style=for-the-badge&logo=npm)
![Status](https://img.shields.io/badge/Status-Active-green?style=for-the-badge)

---

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Como Usar](#como-usar)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Funcionalidades](#funcionalidades)
- [Componentes Principais](#componentes-principais)
- [Autenticação](#autenticação)
- [Build e Produção](#build-e-produção)
- [Troubleshooting](#troubleshooting)

---

## Visão Geral

O **Cliente ERP** é uma aplicação React moderna que oferece:

✅ **Dashboard Interativo** - Visualização de dados e métricas  
✅ **Gerenciamento de Produtos** - Cadastro, edição e listagem com paginação  
✅ **Gestão de Pedidos** - Criação e acompanhamento de pedidos  
✅ **Administração de Funcionários** - Controle de RH  
✅ **Gestão de Fornecedores** - Gerenciamento de parceiros comerciais  
✅ **Controle de Ingredientes** - Administração de matérias-primas  
✅ **Gestão de Compras** - Pedidos a fornecedores  
✅ **Autenticação JWT** - Login seguro com tokens  
✅ **Interface Responsiva** - Suporte para desktop, tablet e mobile  
✅ **Formulários Validados** - Validação em tempo real  

---

## Arquitetura

```
┌─────────────────────────────────────────────────────┐
│          React Application (Port 3000)              │
├─────────────────────────────────────────────────────┤
│  Routes (react-router-dom)                          │
│  ├── Login Page (Autenticação)                      │
│  ├── Dashboard (Protected)                          │
│  ├── Resources Pages                                │
│  │   ├── Customers / Products / Employees           │
│  │   ├── Suppliers / Ingredients / Orders           │
│  │   └── Purchase Management                        │
│  └── Forms & Modals                                 │
├─────────────────────────────────────────────────────┤
│  Services Layer                                     │
│  └── API Client (Axios + Interceptors)              │
├─────────────────────────────────────────────────────┤
│  Context & State Management                         │
│  └── AuthContext (Login State)                      │
├─────────────────────────────────────────────────────┤
│  REST API Communication (localhost:8080)            │
└─────────────────────────────────────────────────────┘
```

---

## Tecnologias

| Tecnologia | Versão | Descrição |
|-----------|--------|-----------|
| **React** | 18+ | Biblioteca UI |
| **React Router** | 6+ | Roteamento de páginas |
| **Axios** | Latest | Cliente HTTP |
| **React Icons** | Latest | Ícones SVG |
| **Node.js** | 18+ | Runtime JavaScript |
| **npm** | 9+ | Gerenciador de pacotes |
| **CSS3** | - | Estilização nativa |

---

## Pré-requisitos

Antes de começar, verifique se você tem instalado:

- **Node.js 18+** - [Download](https://nodejs.org/)
- **npm 9+** - Incluído no Node.js
- **Git** - [Download](https://git-scm.com/)
- **Backend rodando** - API deve estar em `http://localhost:8080`

### Verificar Instalação

```bash
node --version    # v18.x.x ou superior
npm --version     # 9.x.x ou superior
```

---

## Instalação

### 1. Clonar o Repositório

```bash
git clone https://github.com/oliveirawilliandev/erp-rest-spring-boot.git
cd erp-rest-spring-boot/client
```

### 2. Instalar Dependências

```bash
npm install
```

### 3. Configurar Variáveis (Opcional)

Se necessário, crie um arquivo `.env` na raiz do projeto:

```bash
REACT_APP_API_URL=http://localhost:8080
REACT_APP_API_TIMEOUT=30000
```

### 4. Iniciar o Servidor de Desenvolvimento

```bash
npm start
```

A aplicação abrirá automaticamente em: `http://localhost:3000`

---

## Como Usar

### Desenvolvimento

```bash
# Iniciar servidor com hot reload
npm start

# Executar testes
npm test

# Gerar coverage de testes
npm test -- --coverage
```

### Produção

```bash
# Build otimizado para produção
npm run build

# Servir a build localmente (após build)
npm install -g serve
serve -s build -l 3000
```

### Debugging

```bash
# Com debug ativo
REACT_APP_DEBUG=true npm start
```

---

## Estrutura do Projeto

```
client/
├── public/
│   ├── index.html              # HTML principal
│   └── favicon.ico             # Ícone da aplicação
│
├── src/
│   ├── index.js                # Entry point da aplicação
│   ├── routes.js               # Configuração de rotas
│   ├── global.css              # Estilos globais
│   │
│   ├── services/
│   │   └── api.js              # Cliente Axios com interceptors
│   │
│   ├── components/
│   │   └── AuthContext.js      # Context de autenticação
│   │
│   ├── pages/
│   │   ├── Login/              # Página de login
│   │   ├── Dashboard/          # Dashboard principal
│   │   ├── Customers/          # Gestão de clientes
│   │   ├── Products/           # Gestão de produtos
│   │   ├── Employees/          # Gestão de funcionários
│   │   ├── Suppliers/          # Gestão de fornecedores
│   │   ├── Ingredients/        # Gestão de ingredientes
│   │   ├── Orders/             # Gestão de pedidos
│   │   ├── Purchases/          # Gestão de compras
│   │   ├── Reports/            # Relatórios
│   │   ├── Profile/            # Perfil do usuário
│   │   ├── About/              # Informações
│   │   └── CreateUser/         # Criar novo usuário
│   │
│   └── styles/                 # Estilos CSS adicionais
│
├── package.json                # Dependências npm
├── .env                        # Variáveis de ambiente (gitignored)
└── .gitignore                  # Git ignore rules
```

---

## Funcionalidades

### 🔐 Autenticação e Segurança
- Login com JWT
- Tokens armazenados no localStorage
- Interceptors automáticos de requisição
- Proteção de rotas (redirecionamento para login)
- Logout seguro com limpeza de estado

### 📊 Gestão de Dados
- Listagem paginada de recursos
- Busca e filtros avançados
- Criação e edição de registros
- Soft delete e ativação/desativação
- Validação de formulários

### 🎨 Interface
- Design responsivo (mobile-first)
- Componentes reutilizáveis
- Ícones SVG com React Icons
- Feedback visual (loading, erros, sucesso)
- Rolagem automática para o topo

### 📱 Responsividade
- Desktop (1024px+)
- Tablet (768px - 1023px)
- Mobile (< 768px)
- Touch-friendly buttons (min 44px)

---

## Componentes Principais

### AuthContext.js
Gerencia o estado de autenticação da aplicação:

```javascript
// Uso em componentes
const { isAuthenticated, logout } = useAuth();

if (!isAuthenticated) {
  return <Navigate to="/" />;
}
```

### api.js
Cliente HTTP centralizado com interceptors:

```javascript
// Adiciona token JWT automaticamente
// Trata erros 401/403 com logout automático
// Base URL: /server (proxy para localhost:8080)

import api from '../../services/api';
const response = await api.get('/api/product/v1');
```

### Routes
Configuração de rotas protegidas e públicas:

```javascript
// Rotas públicas: /
// Rotas protegidas: /dashboard, /products, /employees, etc.
```

---

## Autenticação

### Fluxo de Login

1. Usuário acessa `/` (página de login)
2. Submete credenciais (email/password)
3. Backend valida e retorna JWT token
4. Token é armazenado em `localStorage`
5. Redirect para `/dashboard`

### Fluxo de Renovação de Token

- Token expirado → Erro 401 capturado pelo interceptor
- Interceptor limpa localStorage e redireciona para login
- Usuário deve fazer login novamente

### Token nos Requisições

```javascript
// Adicionado automaticamente pelo interceptor
headers: {
  Authorization: `Bearer ${token}`
}
```

---

## Build e Produção

### Otimizações

```bash
# Build otimizado (minificado e comprimido)
npm run build

# Resultado em: ./build/
# - HTML minificado
# - CSS crítico extraído
# - JS bundled e tree-shaken
# - Imagens otimizadas
```

### Deploy

Após `npm run build`, você pode fazer deploy da pasta `build/` para:

- **Vercel** - `vercel deploy`
- **Netlify** - Drag & drop da pasta `build/`
- **AWS S3 + CloudFront**
- **Docker** - [Ver Dockerfile](../Dockerfile)
- **Nginx** - Servir arquivos estáticos

### Variáveis de Ambiente para Produção

```bash
# .env.production
REACT_APP_API_URL=https://api.seu-dominio.com
REACT_APP_API_TIMEOUT=30000
```

---

## Troubleshooting

### ❌ "Cannot find module 'react'"

```bash
rm -rf node_modules package-lock.json
npm install
```

### ❌ "Port 3000 already in use"

```bash
# Linux/Mac
lsof -i :3000
kill -9 <PID>

# Windows
netstat -ano | findstr :3000
taskkill /PID <PID> /F

# Ou use uma porta diferente
PORT=3001 npm start
```

### ❌ "Cannot connect to backend"

```bash
# Verifique se o backend está rodando
curl http://localhost:8080/swagger-ui/index.html

# Verifique o proxy em package.json
# Deve ter: "proxy": "http://localhost:8080"
```

### ❌ "401 Unauthorized"

- Token expirado: Faça login novamente
- Token inválido: Limpe localStorage (`localStorage.clear()`)
- Backend com JWT desabilitado: Verifique configuração

### ❌ "CORS errors"

- Verifique CORS_ORIGIN_PATTERNS no `.env` do backend
- Deve incluir `localhost:3000` ou `*`

### ❌ "localStorage not accessible"

```bash
# Se usar modo incógnito/privado do navegador
# O localStorage pode estar desabilitado
# Use modo normal ou ative em configurações
```

---

## Scripts Disponíveis

| Script | Descrição |
|--------|-----------|
| `npm start` | Inicia dev server |
| `npm test` | Executa testes |
| `npm run build` | Build para produção |
| `npm run eject` | Expõe configuração (irreversível) |

---

## Documentação Adicional

- [React Docs](https://react.dev/)
- [React Router](https://reactrouter.com/)
- [Axios](https://axios-http.com/)
- [Backend API Docs](../server/README.md)

---

## 📞 Suporte

Para issues ou dúvidas:

- 📧 Email: [oliveira.willian.dev@gmail.com](mailto:oliveira.willian.dev@gmail.com)
- 🐛 Issues: [GitHub Issues](https://github.com/oliveirawilliandev/erp-rest-spring-boot/issues)

---

## 👨‍💻 Autor

**Willian Oliveira**

- GitHub: [@oliveirawilliandev](https://github.com/oliveirawilliandev)
- Email: oliveira.willian.dev@gmail.com

---

**Desenvolvido com ❤️ para o ERP Rest Spring Boot**
