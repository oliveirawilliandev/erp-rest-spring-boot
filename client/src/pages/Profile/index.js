// pages/Profile/index.js
import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FiArrowLeft, FiSave, FiUser, FiMail, FiLock, FiUserCheck, FiPower, FiCamera } from 'react-icons/fi';
import './styles.css';
import api from '../../services/api';
import logoImage from '../../assets/logoerp.png';

export default function Profile() {
    const [formData, setFormData] = useState({
        userName: '',
        fullName: '',
        email: '',
        password: '',
        confirmPassword: ''
    });
    const [userId, setUserId] = useState(null);
    const [loading, setLoading] = useState(true);
    const [profileImage, setProfileImage] = useState(null);
    const [selectedFile, setSelectedFile] = useState(null);
    const [uploading, setUploading] = useState(false);
    
    const navigate = useNavigate();
    const token = localStorage.getItem('accessToken');

    // Verifica autenticação
    useEffect(() => {
        if (!token || token === 'undefined' || token === 'null') {
            navigate('/');
        }
        window.scrollTo(0, 0); 
    }, [token, navigate]);

    // Carrega os dados do usuário do backend via /auth/me
    useEffect(() => {
        const loadUserProfile = async () => {
            setLoading(true);
            try {
                console.log('Buscando perfil do usuário via /auth/me...');
                const response = await api.get('/auth/me');
                console.log('Resposta do /auth/me:', response.data);
                
                const userData = response.data;
                
                if (userData) {
                    // Salva o ID do usuário
                    setUserId(userData.id);
                    
                    // Preenche o formulário com os dados do backend
                    setFormData({
                        userName: userData.userName || '',
                        fullName: userData.fullName || '',
                        email: userData.email || '',
                        password: '',
                        confirmPassword: ''
                    });
                    
                    // Atualiza o localStorage com os dados do backend
                    if (userData.id) localStorage.setItem('userId', userData.id);
                    if (userData.userName) localStorage.setItem('userName', userData.userName);
                    if (userData.fullName) localStorage.setItem('fullName', userData.fullName);
                    if (userData.email) localStorage.setItem('email', userData.email);
                    if (userData.photoUrl) localStorage.setItem('photoUrl', userData.photoUrl);
                    
                    // Carrega a foto se existir
                    if (userData.photoUrl && userData.photoUrl !== 'null') {
                        loadUserPhoto(userData.photoUrl);
                    }
                }
            } catch (error) {
                console.error('Erro ao carregar perfil via /auth/me:', error);
                console.error('Detalhes:', error.response?.data);
                
                // Fallback: tenta usar dados do localStorage
                const userName = localStorage.getItem('userName');
                const fullName = localStorage.getItem('fullName');
                const email = localStorage.getItem('email');
                const storedUserId = localStorage.getItem('userId');
                const photoUrl = localStorage.getItem('photoUrl');
                
                setFormData({
                    userName: userName || '',
                    fullName: fullName || '',
                    email: email || '',
                    password: '',
                    confirmPassword: ''
                });
                
                if (storedUserId) setUserId(parseInt(storedUserId));
                if (photoUrl && photoUrl !== 'null') loadUserPhoto(photoUrl);
                
                alert('Não foi possível carregar os dados do servidor. Usando dados locais.');
            } finally {
                setLoading(false);
            }
        };
        
        loadUserProfile();
    }, []);

    // Função para carregar a foto com token
    const loadUserPhoto = async (photoUrl) => {
        try {
            const response = await fetch(photoUrl, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                const blob = await response.blob();
                const imageUrl = URL.createObjectURL(blob);
                setProfileImage(imageUrl);
            }
        } catch (error) {
            console.error('Erro ao carregar foto:', error);
        }
    };

    function handleChange(e) {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    }

    // Validação de email
    const validateEmail = (email) => {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    };

    function handleFileChange(e) {
        const file = e.target.files[0];
        if (file) {
            if (!file.type.startsWith('image/')) {
                alert('Por favor, selecione uma imagem válida');
                return;
            }
            
            if (!file.type.includes('jpeg') && !file.type.includes('jpg')) {
                alert('Apenas arquivos JPG são permitidos');
                return;
            }
            
            setSelectedFile(file);
            const previewUrl = URL.createObjectURL(file);
            setProfileImage(previewUrl);
        }
    }

    async function uploadPhoto() {
        if (!selectedFile || !userId) return false;
        
        setUploading(true);
        const uploadFormData = new FormData();
        uploadFormData.append('file', selectedFile);
        
        try {
            await api.post(`/api/file/v1/users/${userId}/photo`, uploadFormData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            
            // Busca a nova URL da foto
            const response = await api.get('/auth/me');
            if (response.data && response.data.photoUrl) {
                localStorage.setItem('photoUrl', response.data.photoUrl);
            }
            
            return true;
        } catch (error) {
            console.error('Erro ao fazer upload da foto:', error);
            alert('Erro ao atualizar a foto');
            return false;
        } finally {
            setUploading(false);
        }
    }

    async function handleSubmit(e) {
        e.preventDefault();
        
        // Valida email
        if (formData.email && !validateEmail(formData.email)) {
            alert('Por favor, insira um email válido (ex: nome@email.com)');
            return;
        }
        
        if (formData.password && formData.password !== formData.confirmPassword) {
            alert('As senhas não coincidem');
            return;
        }
        
        setLoading(true);
        
        try {
            // Prepara os dados para atualização
            const updateData = {
                id: userId,
                userName: formData.userName,
                fullName: formData.fullName,
                email: formData.email,
                photoUrl: localStorage.getItem('photoUrl') || '',
                password: formData.password || ''
            };
            
            console.log('Enviando atualização para /auth/update:', updateData);
            
            // Atualiza dados do usuário
            await api.put('/auth/update', updateData);
            
            // Atualiza foto se necessário
            if (selectedFile) {
                await uploadPhoto();
            }
            
            // Atualiza localStorage
            localStorage.setItem('fullName', formData.fullName);
            if (formData.email) {
                localStorage.setItem('email', formData.email);
            }
            
            alert('Perfil atualizado com sucesso!');
            
            // Recarrega os dados
            window.location.reload();
            
        } catch (error) {
            console.error('Erro ao atualizar perfil:', error);
            const errorMessage = error.response?.data?.message || 'Erro ao atualizar perfil. Tente novamente.';
            alert(errorMessage);
        } finally {
            setLoading(false);
        }
    }

    function logout() {
        localStorage.clear();
        navigate('/');
    }

    if (loading) {
        return (
            <div className="profile-page-container">
                <div className="profile-page-loading">Carregando dados do perfil...</div>
            </div>
        );
    }

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

    return (
        <div className="profile-page-container">
            <header>
                <img src={logoImage} alt="ERP Logo" />
               <span>Welcome, <strong>{getDisplayName(formData.fullName || localStorage.getItem('fullName'))} </strong></span>
                <button className="profile-page-logout-btn" onClick={logout} type="button">
                    <FiPower size={18} color="#3498db" />
                </button>
            </header>

            <div className="profile-page-title-row">
                <Link to="/dashboard" className="profile-page-back-button">
                    <FiArrowLeft size={24} />
                </Link>
                <h1>Edit Profile</h1>
            </div>

            <div className="profile-page-content">
                <form onSubmit={handleSubmit} className="profile-page-form">
                    <div className="profile-page-photo-section">
                        <div className="profile-page-photo-container">
                            {profileImage ? (
                                <img 
                                    src={profileImage} 
                                    alt="Profile" 
                                    className="profile-page-photo"
                                />
                            ) : (
                                <div className="profile-page-photo-placeholder">
                                    <FiUser size={48} />
                                </div>
                            )}
                            <label className="profile-page-photo-upload-label">
                                <FiCamera size={20} />
                                <input 
                                    type="file" 
                                    accept="image/jpeg,image/jpg"
                                    onChange={handleFileChange}
                                    style={{ display: 'none' }}
                                />
                                <span>Change Photo</span>
                            </label>
                            {uploading && <span className="profile-page-uploading">Uploading...</span>}
                        </div>
                        <small className="profile-page-photo-note">Only JPG files allowed</small>
                    </div>

                    <div className="profile-page-form-fields">
                        <div className="profile-page-form-group">
                            <label><FiUserCheck size={16} /> Username (cannot be changed)</label>
                            <input
                                type="text"
                                name="userName"
                                value={formData.userName}
                                disabled
                                className="profile-page-input-disabled"
                            />
                        </div>

                        <div className="profile-page-form-group">
                            <label><FiUser size={16} /> Full Name *</label>
                            <input
                                type="text"
                                name="fullName"
                                value={formData.fullName}
                                onChange={handleChange}
                                placeholder="Your full name"
                                required
                            />
                        </div>

                        <div className="profile-page-form-group">
                            <label><FiMail size={16} /> Email</label>
                            <input
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                placeholder="your@email.com"
                            />
                        </div>

                        <div className="profile-page-form-group">
                            <label><FiLock size={16} /> New Password</label>
                            <input
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                placeholder="Leave blank to keep current password"
                            />
                        </div>

                        <div className="profile-page-form-group">
                            <label><FiLock size={16} /> Confirm New Password</label>
                            <input
                                type="password"
                                name="confirmPassword"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                placeholder="Confirm your new password"
                            />
                        </div>

                        <div className="profile-page-form-actions">
                            <button type="submit" className="profile-page-save-btn" disabled={loading}>
                                <FiSave size={18} />
                                {loading ? 'Saving...' : 'Save Changes'}
                            </button>
                            <Link to="/dashboard" className="profile-page-cancel-btn">
                                Cancel
                            </Link>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}