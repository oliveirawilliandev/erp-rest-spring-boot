// pages/Login/index.js
import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import './styles.css';
import api from '../../services/api';
import logoImage from '../../assets/logoerp.png';

export default function Login() {
    const [userName, setUserName] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    // Função para tratar a mudança do username
    const handleUserNameChange = (e) => {
        // Converte para minúsculas automaticamente
        const value = e.target.value.toLowerCase();
        // Opcional: remove caracteres especiais (apenas letras, números e underscore)
        // const cleanedValue = value.toLowerCase().replace(/[^a-z0-9_]/g, '');
        setUserName(value);
    };

    async function login(e) {
        e.preventDefault();

        if (!userName || !password) {
            alert("Please enter username and password!");
            return;
        }

        setLoading(true);
        
        // Garantir que o username está em minúsculas antes de enviar
        const data = { 
            userName: userName.toLowerCase(), // Garantia extra
            password 
        };

        try {
            const response = await api.post('auth/signin', data);

            console.log('Resposta do login:', response.data); // DEBUG

            // Salva todos os dados no localStorage
            localStorage.setItem('userName', data.userName); // Usa o username em minúsculas
            localStorage.setItem('fullName', response.data.fullName);
            localStorage.setItem('accessToken', response.data.accessToken);
            localStorage.setItem('refreshToken', response.data.refreshToken);
            localStorage.setItem('photoUrl', response.data.photoUrl);

            console.log('PhotoUrl salva:', response.data.photoUrl); // DEBUG

            navigate('/dashboard');
        } catch (err) {
            console.error(err.response || err);
            const message = err.response?.data?.message || "Login Failed! Try again!";
            alert(message);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="login-page-container">
            <div className="login-page-card">
                <div className="login-page-logo-container">
                    <img src={logoImage} alt="ERP Logo" className="login-page-logo-image" />
                </div>
                
                <form onSubmit={login} className="login-page-form">
                    <h1 className="login-page-title">Access your account</h1>

                    <input
                        className="login-page-input"
                        placeholder="Username"
                        value={userName}
                        onChange={handleUserNameChange}
                        disabled={loading}
                        autoComplete="username"
                    />

                    <input
                        className="login-page-input"
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        disabled={loading}
                        autoComplete="current-password"
                    />

                    <button className="login-page-button" type="submit" disabled={loading}>
                        {loading ? 'Loading...' : 'Login'}
                    </button>

                    <div className="login-page-create-account">
                        <Link to="/create-user" className="login-page-link">
                            Don't have an account? <span className="login-page-link-highlight">Create one</span>
                        </Link>
                    </div>
                </form>
            </div>
        </div>
    );
}