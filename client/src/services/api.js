// services/api.js
import axios from 'axios';

const api = axios.create({
  baseURL: "/server"
});

// Interceptor para adicionar token em todas as requisições
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('accessToken');
        if (token && token !== 'undefined' && token !== 'null') {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Interceptor para tratar erros de autenticação
api.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response?.status === 401 || error.response?.status === 403) {
            // Token expirado ou inválido
            localStorage.clear();
            sessionStorage.clear();
            window.location.href = '/';
        }
        return Promise.reject(error);
    }
);

export default api;