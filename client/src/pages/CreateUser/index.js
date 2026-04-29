// pages/CreateUser/index.js
import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { FiArrowLeft, FiUserPlus, FiUser, FiLock, FiUserCheck, FiEye, FiEyeOff, FiMail, FiSend, FiCheckCircle } from 'react-icons/fi';
import './styles.css';
import api from '../../services/api';

export default function CreateUser() {
    const [formData, setFormData] = useState({
        userName: '',
        password: '',
        confirmPassword: '',
        fullName: '',
        email: ''
    });
    const [loading, setLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    
    // Estados para verificação de email
    const [emailVerified, setEmailVerified] = useState(false);
    const [verificationCode, setVerificationCode] = useState('');
    const [sendingCode, setSendingCode] = useState(false);
    const [verifyingCode, setVerifyingCode] = useState(false);
    const [codeSent, setCodeSent] = useState(false);
    const [countdown, setCountdown] = useState(0);
    
    const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    
    let processedValue = value;
    
    // Se for o campo userName, converte para minúsculas automaticamente
    if (name === 'userName') {
        processedValue = value.toLowerCase();
        // Opcional: remove caracteres especiais (apenas letras, números e underscore)
        // processedValue = value.toLowerCase().replace(/[^a-z0-9_]/g, '');
    }
    
    setFormData(prev => ({
        ...prev,
        [name]: processedValue
    }));
    
    // Se mudar o email, reseta a verificação
    if (name === 'email') {
        setEmailVerified(false);
        setCodeSent(false);
        setVerificationCode('');
    }
};

    // Enviar código de verificação
    const sendVerificationCode = async () => {
        if (!formData.email.trim()) {
            alert('Please enter your email address first');
            return;
        }
        if (!formData.email.includes('@')) {
            alert('Please enter a valid email address');
            return;
        }

        setSendingCode(true);
        try {
            await api.post('api/email/v1/verify/send', { email: formData.email });
            setCodeSent(true);
            setCountdown(60);
            
            // Contador regressivo
            const timer = setInterval(() => {
                setCountdown(prev => {
                    if (prev <= 1) {
                        clearInterval(timer);
                        return 0;
                    }
                    return prev - 1;
                });
            }, 1000);
            
            alert('✅ Verification code sent to your email!');
        } catch (err) {
            console.error('Error sending code:', err);
            alert('❌ Failed to send verification code. Please try again.');
        } finally {
            setSendingCode(false);
        }
    };

    // Verificar código
    const verifyCode = async () => {
        if (!verificationCode.trim()) {
            alert('Please enter the verification code');
            return;
        }

        setVerifyingCode(true);
        try {
            await api.post('api/email/v1/verify/validate', {
                email: formData.email,
                code: verificationCode
            });
            setEmailVerified(true);
            alert('✅ Email verified successfully!');
        } catch (err) {
            console.error('Error verifying code:', err);
            alert('❌ Invalid or expired code. Please try again.');
        } finally {
            setVerifyingCode(false);
        }
    };

    const validateForm = () => {
        if (!formData.fullName.trim()) {
            alert('Please enter your full name');
            return false;
        }
        if (!formData.email.trim()) {
            alert('Please enter your email address');
            return false;
        }
        if (!formData.email.includes('@')) {
            alert('Please enter a valid email address');
            return false;
        }
        if (!emailVerified) {
            alert('Please verify your email address first');
            return false;
        }
        if (!formData.userName.trim()) {
            alert('Please enter a username');
            return false;
        }
        if (formData.userName.length < 3) {
            alert('Username must be at least 3 characters');
            return false;
        }
        if (!formData.password) {
            alert('Please enter a password');
            return false;
        }
        if (formData.password.length < 4) {
            alert('Password must be at least 4 characters');
            return false;
        }
        if (formData.password !== formData.confirmPassword) {
            alert('Passwords do not match! Please check your password.');
            return false;
        }
        return true;
    };

    async function handleSubmit(e) {
        e.preventDefault();
        
        if (!validateForm()) return;
        
        setLoading(true);
        
        try {
            const data = {
                userName: formData.userName,
                password: formData.password,
                fullName: formData.fullName,
                email: formData.email
            };
            
            const response = await api.post('auth/createUser', data);
            
            if (response.status === 200 || response.status === 201) {
                alert('✅ User created successfully!\n\nYou can now login with your credentials.');
                
                setFormData({
                    userName: '',
                    password: '',
                    confirmPassword: '',
                    fullName: '',
                    email: ''
                });
                setEmailVerified(false);
                setCodeSent(false);
                setVerificationCode('');
                
                setTimeout(() => {
                    navigate('/');
                }, 2000);
                window.scrollTo(0, 0); 
            }
        } catch (err) {
            console.error('Error creating user:', err.response || err);
            
            let errorMessage = '❌ Failed to create user!\n\n';
            
            if (err.response?.data?.message) {
                errorMessage += err.response.data.message;
            } else if (err.response?.status === 409) {
                errorMessage += 'Username already exists. Please choose another username.';
            } else if (err.response?.status === 400) {
                errorMessage += 'Invalid data. Please check the information provided.';
            } else if (err.response?.status === 403) {
                errorMessage += 'You do not have permission to create users.';
            } else {
                errorMessage += 'Please check the console for more details.';
            }
            
            alert(errorMessage);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="create-user-container">
            <div className="create-user-card">
                <Link to="/" className="back-link">
                    <FiArrowLeft size={20} />
                    Back
                </Link>
                
                <div className="create-user-header">
                    <div className="header-icon">
                        <FiUserPlus size={48} color="#2E8B57" />
                    </div>
                    <h1>Create New User</h1>
                    <p>Fill in the information below to create a new account</p>
                </div>

                <form onSubmit={handleSubmit} className="create-user-form">
                    <div className="form-group">
                        <label htmlFor="fullName">
                            <FiUserCheck size={18} />
                            Full Name
                        </label>
                        <input
                            type="text"
                            id="fullName"
                            name="fullName"
                            value={formData.fullName}
                            onChange={handleChange}
                            placeholder="Enter your full name"
                            disabled={loading}
                            autoComplete="name"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="email">
                            <FiMail size={18} />
                            Email
                        </label>
                        <div className="email-verification-wrapper">
                            <input
                                type="email"
                                id="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                placeholder="Enter your email address"
                                disabled={loading || emailVerified}
                                autoComplete="email"
                                style={{ flex: 1 }}
                            />
                            {!emailVerified && (
                                <button
                                    type="button"
                                    className="send-code-btn"
                                    onClick={sendVerificationCode}
                                    disabled={sendingCode || countdown > 0 || !formData.email.includes('@')}
                                >
                                    {sendingCode ? 'Sending...' : countdown > 0 ? `${countdown}s` :  (
                    <>
                        <FiSend size={16} style={{ marginRight: 6 }} />
                        Enviar
                    </>
                ) }
                                </button>
                            )}
                            {emailVerified && (
                                <span className="verified-badge">
                                    <FiCheckCircle size={20} color="#4CAF50" />
                                </span>
                            )}
                        </div>
                        <small>We'll send a verification code to this email</small>
                    </div>

                    {codeSent && !emailVerified && (
                        <div className="form-group">
                            <label htmlFor="verificationCode">
                                <FiLock size={18} />
                                Verification Code
                            </label>
                            <div className="email-verification-wrapper">
                                <input
                                    type="text"
                                    id="verificationCode"
                                    name="verificationCode"
                                    value={verificationCode}
                                    onChange={(e) => setVerificationCode(e.target.value)}
                                    placeholder="Enter 6-digit code"
                                    disabled={loading || emailVerified}
                                    maxLength={6}
                                />
                                <button
                                    type="button"
                                    className="verify-code-btn"
                                    onClick={verifyCode}
                                    disabled={verifyingCode || !verificationCode}
                                >
                                    {verifyingCode ? 'Verifying...' : 'Verify'}
                                </button>
                            </div>
                        </div>
                    )}

                    <div className="form-group">
                        <label htmlFor="userName">
                            <FiUser size={18} />
                            Username
                        </label>
                        <input
                            type="text"
                            id="userName"
                            name="userName"
                            value={formData.userName}
                            onChange={handleChange}
                            placeholder="Choose a username"
                            disabled={loading}
                            autoComplete="username"
                        />
                        <small>Username must be at least 3 characters</small>
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">
                            <FiLock size={18} />
                            Password
                        </label>
                        <div className="password-input-wrapper">
                            <input
                                type={showPassword ? "text" : "password"}
                                id="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                placeholder="Choose a password"
                                disabled={loading}
                                autoComplete="new-password"
                            />
                            <button
                                type="button"
                                className="toggle-password"
                                onClick={() => setShowPassword(!showPassword)}
                                disabled={loading}
                            >
                                {showPassword ? <FiEyeOff size={18} /> : <FiEye size={18} />}
                            </button>
                        </div>
                        <small>Password must be at least 4 characters</small>
                    </div>

                    <div className="form-group">
                        <label htmlFor="confirmPassword">
                            <FiLock size={18} />
                            Confirm Password
                        </label>
                        <div className="password-input-wrapper">
                            <input
                                type={showConfirmPassword ? "text" : "password"}
                                id="confirmPassword"
                                name="confirmPassword"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                placeholder="Confirm your password"
                                disabled={loading}
                                autoComplete="new-password"
                            />
                            <button
                                type="button"
                                className="toggle-password"
                                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                disabled={loading}
                            >
                                {showConfirmPassword ? <FiEyeOff size={18} /> : <FiEye size={18} />}
                            </button>
                        </div>
                        {formData.password && formData.confirmPassword && (
                            <small style={{ 
                                color: formData.password === formData.confirmPassword ? '#4CAF50' : '#f44336',
                                marginTop: '5px'
                            }}>
                                {formData.password === formData.confirmPassword ? '✓ Passwords match' : '✗ Passwords do not match'}
                            </small>
                        )}
                    </div>

                    <button 
                        type="submit" 
                        className="btn-create-user"
                        disabled={loading || !emailVerified}
                    >
                        {loading ? (
                            <>⏳ Creating user...</>
                        ) : (
                            <>
                                <FiUserPlus size={18} />
                                Create User
                            </>
                        )}
                    </button>
                </form>

                <div className="create-user-footer">
                    <p>Already have an account?</p>
                    <Link to="/" className="login-link">Click here to login</Link>
                </div>
            </div>
        </div>
    );
}