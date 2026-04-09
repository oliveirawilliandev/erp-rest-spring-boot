// [LOGIN-001] Importa React e hook useState para gerenciamento de estado
import React, { useState } from 'react';

// [LOGIN-002] Importa hook useNavigate para navegação programática
import { useNavigate } from 'react-router-dom';

// [LOGIN-003] Importa estilos específicos do componente
import './styles.css';

// [LOGIN-004] Importa instância do axios configurada (API)
import api from '../../services/api';

// [LOGIN-005] Importa logo da aplicação
import logoImage from '../../assets/logo.png';

// [LOGIN-006] Importa ícone de cadeado
import padlock from '../../assets/padlock.png';

// [LOGIN-007] Componente de tela de login
export default function Login() {

    // [LOGIN-008] Estado para armazenar nome de usuário
    const [userName, setUserName] = useState('');
    
    // [LOGIN-009] Estado para armazenar senha
    const [password, setPassword] = useState('');
    
    // [LOGIN-010] Hook para redirecionamento após login
    const navigate = useNavigate();

    // [LOGIN-011] Função assíncrona para fazer login
    async function login(e) {
        e.preventDefault(); // [LOGIN-012] Previne comportamento padrão do formulário (recarregar página)

        // [LOGIN-013] Monta objeto com os dados do formulário
        const data = { userName, password };

        try {
            // [LOGIN-014] Faz requisição POST para endpoint de autenticação
            const response = await api.post('auth/signin', data);
            
            // [LOGIN-015] Salva nome de usuário no localStorage
            localStorage.setItem('userName', userName);
            
            // [LOGIN-016] Salva token de acesso no localStorage
            localStorage.setItem('accessToken', response.data.accessToken);
            
            // [LOGIN-017] Redireciona para tela de livros após login bem-sucedido
            navigate('/books');
        } catch (err) {
            // [LOGIN-018] Log do erro no console (para debug)
            console.error(err.response || err);
            
            // [LOGIN-019] Alerta de erro para o usuário
            alert("Login Failed! Try again!");
        }
    }

    // [LOGIN-020] Renderiza o formulário de login
    return (
        <div className="login-container">
            <section className="form">
                {/* [LOGIN-021] Logo da aplicação */}
                <img src={logoImage} alt="Erudio Logo" />
                
                {/* [LOGIN-022] Formulário com submit handler */}
                <form onSubmit={login}>
                    <h1>Access your account</h1>
                    
                    {/* [LOGIN-023] Campo de nome de usuário (controlled component) */}
                    <input
                        placeholder="Username"
                        value={userName}
                        onChange={e => setUserName(e.target.value)}
                    />
                    
                    {/* [LOGIN-024] Campo de senha (controlled component) */}
                    <input
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                    />
                    
                    {/* [LOGIN-025] Botão de submit do formulário */}
                    <button className="button" type="submit">Login</button>
                </form>
            </section>
            
            {/* [LOGIN-026] Imagem decorativa (cadeado) */}
            <img src={padlock} alt="Login" />
        </div>
    );
}