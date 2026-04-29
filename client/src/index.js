// index.js
import React from 'react';
import ReactDOM from 'react-dom/client';
import AppRoutes from './routes';
import './global.css';   // ← Mudar para importar routes

const root = ReactDOM.createRoot(document.getElementById('root'));

root.render(
  <React.StrictMode>
    <AppRoutes />  {/* ← Usar AppRoutes em vez de App */}
  </React.StrictMode>
);