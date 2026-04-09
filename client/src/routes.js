// [ROUTES-001] Importa React para criar componentes
import React from 'react';

// [ROUTES-002] Importa componentes de roteamento do React Router DOM
import { BrowserRouter, Routes, Route } from "react-router-dom";

// [ROUTES-003] Importa página de Login
import Login from './pages/Login';

// [ROUTES-004] Importa página de listagem de livros
import Books from './pages/Books';

// [ROUTES-005] Importa página de criação/edição de livros
import NewBook from './pages/NewBook';

// [ROUTES-006] Componente principal de rotas da aplicação
export default function AppRoutes() {
  return (
    // [ROUTES-007] BrowserRouter: gerencia o histórico de navegação usando HTML5 History API
    <BrowserRouter>
      {/* [ROUTES-008] Routes: container que agrupa todas as rotas (React Router v6) */}
      <Routes>
        {/* [ROUTES-009] Rota raiz (Login) - exact garante correspondência exata */}
        <Route path="/" exact element={<Login />} />
        
        {/* [ROUTES-010] Rota para listagem de livros */}
        <Route path="/books" element={<Books />} />
        
        {/* [ROUTES-011] Rota para criação/edição de livros com parâmetro dinâmico :bookId */}
        {/* bookId = '0' para criação, ou ID do livro para edição */}
        <Route path="/books/new/:bookId" element={<NewBook />} />
      </Routes>
    </BrowserRouter>
  );
}