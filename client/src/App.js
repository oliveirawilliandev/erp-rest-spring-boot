// [REACT-006] Importa a biblioteca React para criar componentes
import React from 'react';

// [REACT-007] Importa os estilos globais da aplicação (reset, fontes, etc)
import './global.css';

// [REACT-008] Importa o componente de rotas da aplicação
import AppRoutes from './routes';

// [REACT-009] Componente principal da aplicação
export default function App() {
  
  return (
    // [REACT-010] JSX (JavaScript XML) - sintaxe que permite HTML dentro do JavaScript
    <AppRoutes /> /* Renderiza o sistema de rotas da aplicação */
  );
}