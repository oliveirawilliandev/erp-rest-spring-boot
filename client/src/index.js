// [REACT-001] Importa a biblioteca React para criar componentes
import React from 'react';

// [REACT-002] Importa o ReactDOM para renderizar a aplicação no DOM
import ReactDOM from 'react-dom/client';

// [REACT-003] Importa o componente principal da aplicação (App)
import App from './App';

// [REACT-004] Cria a raiz do React no elemento HTML com id 'root'
// O ponto de ancoragem está localizado no arquivo public/index.html
const root = ReactDOM.createRoot(document.getElementById('root'));

// [REACT-005] Renderiza a aplicação no DOM
// React.StrictMode ativa verificações adicionais para boas práticas
root.render(
  <React.StrictMode>
    <App /> {/* Componente principal da aplicação */}
  </React.StrictMode>
);