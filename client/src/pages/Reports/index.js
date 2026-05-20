// pages/Reports/index.js - COMPLETO E FUNCIONANDO
import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
    FiArrowLeft,
    FiFileText,
    FiDownload,
    FiPrinter,
    FiBarChart2,
    FiUsers,
    FiPackage,
    FiShoppingCart,
    FiPower,
    FiSettings
} from 'react-icons/fi';
import api from '../../services/api';
import './styles.css';
import logoImage from '../../assets/logoerp.png';

export default function Reports() {
    const [loading, setLoading] = useState(false);
    const [previewLoading, setPreviewLoading] = useState(false);
    const [showFilters, setShowFilters] = useState(false);
    const [selectedReport, setSelectedReport] = useState(null);
    const [filterSize, setFilterSize] = useState(23);
    const [filterDirection, setFilterDirection] = useState('asc');
    const [actionType, setActionType] = useState(null);

  const API_BASE_URL = '/server/';
    
    const navigate = useNavigate();
    const fullName = localStorage.getItem('fullName');

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
    
    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (!token || token === 'undefined' || token === 'null') {
            navigate('/');
        }
            window.scrollTo(0, 0); 

    }, [navigate]);
    
    function logout() {
        localStorage.clear();
        navigate('/');
    }
        
    const getAuthToken = () => {
        const token = localStorage.getItem('token') || 
                     localStorage.getItem('accessToken') || 
                     sessionStorage.getItem('token') ||
                     sessionStorage.getItem('accessToken');
        return token;
    };

    const reportsList = [
        {
            id: 5,
            name: 'Employees Report',
            namePt: 'Relatório de Funcionários',
            icon: <FiUsers size={28} />,
            description: 'Employees list and export',
            descriptionPt: 'Lista e exportação de funcionários',
            color: '#2E8B57',
            enabled: true,
            supportsFilters: true
        },
        {
            id: 1,
            name: 'Sales Report',
            namePt: 'Relatório de Vendas',
            icon: <FiShoppingCart size={28} />,
            description: 'Complete sales report with totals by period',
            descriptionPt: 'Relatório completo de vendas com totais por período',
            color: '#DC143C',
            enabled: false,
            supportsFilters: false
        },
        {
            id: 2,
            name: 'Products Report',
            namePt: 'Relatório de Produtos',
            icon: <FiPackage size={28} />,
            description: 'Stock status and best selling products',
            descriptionPt: 'Status do estoque e produtos mais vendidos',
            color: '#FF8C00',
            enabled: false,
            supportsFilters: false
        },
        {
            id: 3,
            name: 'Customers Report',
            namePt: 'Relatório de Clientes',
            icon: <FiUsers size={28} />,
            description: 'Customer ranking and purchase history',
            descriptionPt: 'Ranking de clientes e histórico de compras',
            color: '#251FC5',
            enabled: false,
            supportsFilters: false
        },
        {
            id: 4,
            name: 'Financial Report',
            namePt: 'Relatório Financeiro',
            icon: <FiBarChart2 size={28} />,
            description: 'Revenue, expenses and profit analysis',
            descriptionPt: 'Análise de receitas, despesas e lucros',
            color: '#20B2AA',
            enabled: false,
            supportsFilters: false
        }
    ];

    const openFilterModal = (report, action) => {
        setSelectedReport(report);
        setActionType(action);
        setShowFilters(true);
    };

    const handleGenerateWithFilters = async () => {
        if (!selectedReport || selectedReport.id !== 5) return;
        
        setShowFilters(false);
        
        if (actionType === 'generate') {
            await handleGenerateReport(selectedReport.id);
        } else if (actionType === 'preview') {
            await handlePreview(selectedReport.id);
        }
    };

    const handleGenerateReport = async (reportId) => {
        if (reportId !== 5) return;
        
        setLoading(true);
        
        try {
            const token = getAuthToken();
            if (!token) {
                alert('Authentication token not found. Please login again.');
                return;
            }
            
            const url = `${API_BASE_URL}api/employee/v1/exportPage?page=0&size=${filterSize}&direction=${filterDirection}`;
            
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Accept': 'application/pdf',
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                }
            });
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const blob = await response.blob();
            if (blob.size === 0) {
                alert('No data available for this report.');
                return;
            }
            
            const url_blob = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            const timestamp = new Date().toISOString().slice(0, 19).replace(/:/g, '-');
            link.href = url_blob;
            link.setAttribute('download', `employees_report_${filterSize}_${filterDirection}_${timestamp}.pdf`);
            document.body.appendChild(link);
            link.click();
            
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url_blob);
            
        } catch (error) {
            console.error('Erro:', error);
            alert('Error generating report. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handlePreview = async (reportId) => {
        if (reportId !== 5) return;
        
        setPreviewLoading(true);
        
        try {
            const token = getAuthToken();
            if (!token) {
                alert('Authentication token not found. Please login again.');
                return;
            }
            
            const url = `${API_BASE_URL}api/employee/v1/exportPage?page=0&size=${filterSize}&direction=${filterDirection}`;
            
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Accept': 'application/pdf',
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                }
            });
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const blob = await response.blob();
            if (blob.size === 0) {
                alert('No data available for this report.');
                return;
            }
            
            const url_blob = window.URL.createObjectURL(blob);
            
            const previewWindow = window.open();
            if (previewWindow) {
                previewWindow.document.write(`
                    <html>
                        <head>
                            <title>Visualizar Relatório - Funcionários (${filterSize} itens, ${filterDirection === 'asc' ? 'Crescente' : 'Decrescente'})</title>
                            <style>
                                body { margin: 0; padding: 0; }
                                embed { width: 100%; height: 100vh; }
                                .info-bar {
                                    position: fixed;
                                    bottom: 0;
                                    left: 0;
                                    right: 0;
                                    background: rgba(0,0,0,0.8);
                                    color: white;
                                    padding: 8px;
                                    text-align: center;
                                    font-size: 12px;
                                    z-index: 1000;
                                }
                            </style>
                        </head>
                        <body>
                            <embed src="${url_blob}" type="application/pdf" width="100%" height="100%">
                            <div class="info-bar">
                                📄 Relatório: ${filterSize} registros | Ordenação: ${filterDirection === 'asc' ? 'Crescente (A-Z)' : 'Decrescente (Z-A)'}
                            </div>
                        </body>
                    </html>
                `);
            } else {
                const link = document.createElement('a');
                link.href = url_blob;
                link.setAttribute('download', `employees_report_preview.pdf`);
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
            }
            
            setTimeout(() => {
                window.URL.revokeObjectURL(url_blob);
            }, 10000);
            
        } catch (error) {
            console.error('Erro no preview:', error);
            alert('Error previewing report. Please try again.');
        } finally {
            setPreviewLoading(false);
        }
    };

    const handleGenerateClick = (report, action) => {
        if (!report.enabled) return;
        
        if (report.supportsFilters) {
            openFilterModal(report, action);
        } else {
            if (action === 'generate') {
                handleGenerateReport(report.id);
            } else if (action === 'preview') {
                handlePreview(report.id);
            }
        }
    };

    return (
        <div className="reports-page-container">
            <header>
                <img src={logoImage} alt="ERP Logo" />
                <span>Welcome, <strong>{getDisplayName(fullName).toUpperCase()} </strong></span>
                <button className="reports-page-logout-btn" onClick={logout} type="button">
                    <FiPower size={18} color="#9B59B6" />
                </button>
            </header>

            <div className="reports-page-title-row">
                <Link to="/dashboard" className="reports-page-back-button">
                    <FiArrowLeft size={24} />
                </Link>
                <h1>Registered Reports</h1>
            </div>

            <div className="reports-page-grid">
                {reportsList.map((report) => (
                    <div
                        key={report.id}
                        className="reports-page-card"
                        style={{
                            borderTopColor: report.enabled ? report.color : '#ccc',
                            opacity: report.enabled ? 1 : 0.6
                        }}
                    >
                        <div
                            className="reports-page-card-icon"
                            style={{ color: report.enabled ? report.color : '#999' }}
                        >
                            {report.icon}
                        </div>

                        <div className="reports-page-card-info">
                            <h3 className="reports-page-card-title">
                                {report.name}{' '}
                                <span className="reports-page-card-title-pt">({report.namePt})</span>
                                {report.supportsFilters && report.enabled && (
                                    <span className="reports-page-filter-badge">
                                        <FiSettings size={12} /> Customizable
                                    </span>
                                )}
                            </h3>
                            <p className="reports-page-card-description">
                                {report.description}
                                <br />
                                <span className="reports-page-card-description-pt">
                                    {report.descriptionPt}
                                </span>
                            </p>
                            {report.supportsFilters && report.enabled && (
                                <p className="reports-page-filter-hint">
                                    🔧 Click on "Generate" to customize size and sorting
                                </p>
                            )}
                        </div>

                        <div className="reports-page-card-actions">
                            <button
                                onClick={() => handleGenerateClick(report, 'generate')}
                                className="reports-page-btn-generate"
                                disabled={!report.enabled || loading}
                                style={{
                                    backgroundColor: report.enabled ? report.color : '#ccc'
                                }}
                            >
                                <FiFileText size={18} />{' '}
                                {loading && selectedReport?.id === report.id && actionType === 'generate' ? (
                                    'Gerando...'
                                ) : report.enabled ? (
                                    'Generate | Gerar'
                                ) : (
                                    'In Development | Em desenvolvimento'
                                )}
                            </button>

                            <button
                                onClick={() => handleGenerateClick(report, 'preview')}
                                className="reports-page-btn-preview"
                                disabled={!report.enabled || previewLoading}
                            >
                                <FiPrinter size={18} />{' '}
                                {previewLoading && selectedReport?.id === report.id && actionType === 'preview' ? (
                                    'Carregando...'
                                ) : (
                                    'Preview | Visualizar'
                                )}
                            </button>
                        </div>

                        {!report.enabled && (
                            <div className="reports-page-overlay-dev">
                                Em desenvolvimento
                            </div>
                        )}
                    </div>
                ))}
            </div>

            <div className="reports-page-jasper-info">
                <h3 className="reports-page-jasper-title">
                    <FiDownload /> JasperReports Integration | Integração com JasperReports
                </h3>
                <p className="reports-page-jasper-text">
                    Reports generated with JasperReports library. Supports PDF,
                    XLS, CSV and HTML formats.
                    <br />
                    Relatórios gerados com a biblioteca JasperReports. Suporta
                    formatos PDF, XLS, CSV e HTML.
                </p>
            </div>

            {/* Modal de Filtros */}
            {showFilters && selectedReport && (
                <div className="reports-page-modal-overlay" onClick={() => setShowFilters(false)}>
                    <div className="reports-page-modal" onClick={(e) => e.stopPropagation()}>
                        <div className="reports-page-modal-header" style={{ borderBottomColor: selectedReport.color }}>
                            <h2>
                                <FiSettings />
                                Report Settings | Configurações
                            </h2>
                            <button className="reports-page-modal-close" onClick={() => setShowFilters(false)}>
                                ×
                            </button>
                        </div>
                        
                        <div className="reports-page-modal-body">
                            <p className="reports-page-modal-report-name">
                                {selectedReport.name} - {selectedReport.namePt}
                            </p>
                            
                            <div className="reports-page-filter-group">
                                <label>
                                    Number of Records | Quantidade de Registros
                                    <select 
                                        value={filterSize} 
                                        onChange={(e) => setFilterSize(Number(e.target.value))}
                                    >
                                        <option value={10}>10 records | registros</option>
                                        <option value={23}>23 records | registros</option>
                                        <option value={50}>50 records | registros</option>
                                        <option value={100}>100 records | registros</option>
                                        <option value={200}>200 records | registros</option>
                                        <option value={500}>500 records | registros</option>
                                        <option value={1000}>1000 records | registros</option>
                                    </select>
                                </label>
                            </div>

                            <div className="reports-page-filter-group">
                                <label>
                                    Sorting Direction | Direção da Ordenação
                                    <select 
                                        value={filterDirection} 
                                        onChange={(e) => setFilterDirection(e.target.value)}
                                    >
                                        <option value="asc">Ascending (A-Z) | Crescente (A-Z)</option>
                                        <option value="desc">Descending (Z-A) | Decrescente (Z-A)</option>
                                    </select>
                                </label>
                            </div>

                            <div className="reports-page-filter-info">
                                <p>
                                    <strong>URL Parameter:</strong><br/>
                                    <code>size={filterSize} & direction={filterDirection}</code>
                                </p>
                                <p className="reports-page-filter-note">
                                    ⚡ This will affect how many records are displayed and their order.
                                    <br/>
                                    ⚡ Isso afetará quantos registros serão exibidos e sua ordem.
                                </p>
                            </div>
                        </div>

                        <div className="reports-page-modal-footer">
                            <button 
                                className="reports-page-modal-cancel" 
                                onClick={() => setShowFilters(false)}
                            >
                                Cancel | Cancelar
                            </button>
                            <button 
                                className="reports-page-modal-confirm"
                                style={{ backgroundColor: selectedReport.color }}
                                onClick={handleGenerateWithFilters}
                            >
                                {actionType === 'generate' ? (
                                    <>Generate Report | Gerar Relatório</>
                                ) : (
                                    <>Preview Report | Visualizar</>
                                )}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}