// pages/Dashboard/index.js
import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FiPower, FiUsers, FiBriefcase, FiPackage, FiTruck, FiShoppingCart, FiShoppingBag, FiBarChart2, FiUser, FiSettings } from 'react-icons/fi';
import './styles.css';
import logoImage from '../../assets/logoerp.png';
import api from '../../services/api';

export default function Dashboard() {
    const fullName = localStorage.getItem('fullName');
    const photoUrl = localStorage.getItem('photoUrl');
    const [profileImage, setProfileImage] = useState(null);
    const [imageError, setImageError] = useState(false);
    const navigate = useNavigate();

     // Função para pegar apenas o primeiro nome
function getFirstName(fullName) {
    if (!fullName) return '';
    // Divide a string em partes usando espaço como separador
    const nameParts = fullName.trim().split(' ');
    // Pega a primeira parte
    return nameParts[0];
}

// Função que limita o tamanho do texto
function limitTextLength(text, maxLength = 10) {
    if (!text) return '';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

// Combinando as duas estratégias
function getDisplayName(fullName) {
    if (!fullName) return '';
    
    // Primeiro: pega o primeiro nome
    let firstName = fullName.trim().split(' ')[0];
    
    // Segundo: limita o tamanho
    const MAX_LENGTH = 12;
    if (firstName.length > MAX_LENGTH) {
        firstName = firstName.substring(0, MAX_LENGTH) + '...';
    }
    
    return firstName;
}

    // ✅ VERIFICAÇÃO DE AUTENTICAÇÃO
    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (!token || token === 'undefined' || token === 'null') {
            navigate('/');
        }
        window.scrollTo(0, 0); 
    }, [navigate]);

    // ✅ CARREGA A IMAGEM COM TOKEN
    useEffect(() => {
        const loadImageWithToken = async () => {
            if (!photoUrl || photoUrl === 'null') {
                return;
            }

            try {
                const token = localStorage.getItem('accessToken');
                const response = await fetch(photoUrl, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (response.ok) {
                    const blob = await response.blob();
                    const imageUrl = URL.createObjectURL(blob);
                    setProfileImage(imageUrl);
                    setImageError(false);
                } else {
                    console.error('Erro ao carregar imagem:', response.status);
                    setImageError(true);
                }
            } catch (error) {
                console.error('Erro na requisição da imagem:', error);
                setImageError(true);
            }
        };

        loadImageWithToken();

        return () => {
            if (profileImage) {
                URL.revokeObjectURL(profileImage);
            }
        };
    }, [photoUrl]);

    function logout() {
        localStorage.clear();
        navigate('/');
    }

    const menuItems = [
        { path: '/customers', icon: <FiUsers size={32} />, title: 'Customers', titlePt: 'Clientes', color: '#251FC5' },
        { path: '/employees', icon: <FiBriefcase size={32} />, title: 'Employees', titlePt: 'Funcionários', color: '#2E8B57' },
        { path: '/products', icon: <FiPackage size={32} />, title: 'Products', titlePt: 'Produtos', color: '#FF8C00' },
        { path: '/ingredients', icon: <FiPackage size={32} />, title: 'Ingredients', titlePt: 'Insumos', color: '#5aa19e' },
        { path: '/suppliers', icon: <FiTruck size={32} />, title: 'Suppliers', titlePt: 'Fornecedores', color: '#6A5ACD' },
        { path: '/orders', icon: <FiShoppingCart size={32} />, title: 'Orders', titlePt: 'Pedidos', color: '#DC143C' },
        { path: '/purchases', icon: <FiShoppingBag size={32} />, title: 'Purchases', titlePt: 'Compras', color: '#20B2AA' },
        { path: '/reports', icon: <FiBarChart2 size={32} />, title: 'Reports', titlePt: 'Relatórios', color: '#9B59B6' },
        { path: '/profile', icon: <FiSettings size={32} />, title: 'Edit Profile', titlePt: 'Editar Perfil', color: '#3498db' },
        { path: '/about', icon: <FiUser size={32} />, title: 'About Me', titlePt: 'Sobre Mim', color: '#E67E22' },
    ];

    const firstName = getFirstName(fullName);
    const displayName = firstName.toUpperCase();

    return (
        <div className="dashboard-page-container">
            <header className="dashboard-page-header">
                {/* Foto do usuário carregada com token */}
                <div className="dashboard-page-user-photo-container">
                    {profileImage && !imageError ? (
                        <img 
                            src={profileImage} 
                            alt="Profile" 
                            className="dashboard-page-user-photo"
                        />
                    ) : (
                        <div className="dashboard-page-user-photo-placeholder">
                            👤
                        </div>
                    )}
                </div>
                
                <div className="dashboard-page-user-info">
                    <span className="dashboard-page-welcome">
                        Welcome, <strong className="dashboard-page-fullName">{getDisplayName(fullName).toUpperCase()}</strong>
                    </span>
                </div>
                
                <button onClick={logout} type="button" className="dashboard-page-logout-btn">
                    <FiPower size={18} color="#251FC5" />
                </button>
            </header>

            <h1 className="dashboard-page-title">ERP Oliveira Dashboard</h1>

            <div className="dashboard-page-menu-grid">
                {menuItems.map((item) => (
                    <Link key={item.path} to={item.path} className="dashboard-page-menu-card" style={{ borderTopColor: item.color }}>
                        <div className="dashboard-page-menu-icon" style={{ color: item.color }}>
                            {item.icon}
                        </div>
                        <div className="dashboard-page-menu-text">
                            <strong className="dashboard-page-menu-title-en">{item.title}</strong>
                            <span className="dashboard-page-menu-title-pt">{item.titlePt}</span>
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    );
}