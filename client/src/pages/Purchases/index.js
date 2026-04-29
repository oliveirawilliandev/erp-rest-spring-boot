// pages/Purchases/index.js - ATUALIZADO PARA INGREDIENT
import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FiPower, FiEye, FiSearch, FiCheckCircle, FiClock, FiXCircle, FiArrowLeft } from 'react-icons/fi';
import './styles.css';
import api from '../../services/api';
import logoImage from '../../assets/logoerp.png';

export default function Purchases() {
    const [purchases, setPurchases] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(false);
    const [updating, setUpdating] = useState(false);
    const [selectedStatus, setSelectedStatus] = useState({});
    
    const [searchTerm, setSearchTerm] = useState('');
    const [searchType, setSearchType] = useState('id');
    const [searching, setSearching] = useState(false);
    const [statusFilter, setStatusFilter] = useState('');

    const fullName = localStorage.getItem('fullName');
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

    const extractPurchaseList = (responseData) => {
        if (Array.isArray(responseData)) return responseData;
        if (responseData && responseData._embedded) {
            const embeddedKeys = Object.keys(responseData._embedded);
            if (embeddedKeys.length > 0) return responseData._embedded[embeddedKeys[0]];
        }
        if (responseData && responseData.content) return responseData.content;
        if (responseData && responseData.id) return [responseData];
        return [];
    };

    const getStatusIcon = (status) => {
        switch(status) {
            case 'RECEIVED': return <FiCheckCircle color="#4CAF50" size={14} />;
            case 'CANCELLED': return <FiXCircle color="#F44336" size={14} />;
            default: return <FiClock color="#FF8C00" size={14} />;
        }
    };

    const getStatusText = (status) => {
        switch(status) {
            case 'PENDING': return 'Pending';
            case 'APPROVED': return 'Approved';
            case 'SHIPPED': return 'Shipped';
            case 'RECEIVED': return 'Received';
            case 'CANCELLED': return 'Cancelled';
            default: return status;
        }
    };

    async function updatePurchaseStatus(purchaseId, newStatus) {
        if (!window.confirm(`Are you sure you want to change this purchase status to ${getStatusText(newStatus)}?`)) {
            setSelectedStatus(prev => ({ ...prev, [purchaseId]: purchases.find(p => p.id === purchaseId)?.status || '' }));
            return;
        }
        
        setUpdating(true);
        try {
            await api.patch(`api/purchase/v1/updateStatus/${purchaseId}`, null, {
                params: { status: newStatus }
            });
            alert(`Purchase status updated to ${getStatusText(newStatus)}!`);
            
            setPurchases(prevPurchases => 
                prevPurchases.map(purchase => 
                    purchase.id === purchaseId 
                        ? { ...purchase, status: newStatus }
                        : purchase
                )
            );
            setSelectedStatus(prev => ({ ...prev, [purchaseId]: newStatus }));
            
        } catch (err) {
            console.error(err);
            const message = err.response?.data?.message || "Error updating purchase status!";
            alert(message);
            const originalPurchase = purchases.find(p => p.id === purchaseId);
            if (originalPurchase) {
                setSelectedStatus(prev => ({ ...prev, [purchaseId]: originalPurchase.status }));
            }
        } finally {
            setUpdating(false);
        }
    }

    useEffect(() => {
        const initialStatus = {};
        purchases.forEach(purchase => {
            initialStatus[purchase.id] = purchase.status;
        });
        setSelectedStatus(initialStatus);
    }, [purchases]);

    async function searchById(id) {
        setLoading(true);
        setSearching(true);
        try {
            const response = await api.get(`api/purchase/v1/${id}`);
            setPurchases([response.data]);
            setTotalPages(1);
            setPage(0);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 404) {
                alert('Purchase not found!');
                setPurchases([]);
            } else {
                alert('Search failed!');
            }
        } finally {
            setLoading(false);
        }
    }

    async function searchBySupplierId(supplierId, currentPage = 0, reset = true) {
        setLoading(true);
        setSearching(true);
        
        try {
            const response = await api.get(`/api/purchase/v1/findBySupplierId/${supplierId}`, {
                params: { page: currentPage, size: 10, direction: 'desc' }
            });
            
            const purchaseList = extractPurchaseList(response.data);
            const total = response.data?.page?.totalPages || 1;
            
            if (reset) {
                setPurchases(purchaseList);
                setPage(1);
            } else {
                setPurchases(prev => [...prev, ...purchaseList]);
                setPage(prev => prev + 1);
            }
            setTotalPages(total);
        } catch (err) {
            console.error(err);
            alert('Search failed!');
        } finally {
            setLoading(false);
        }
    }

    async function searchByEmployeeId(employeeId, currentPage = 0, reset = true) {
        setLoading(true);
        setSearching(true);
        
        try {
            const response = await api.get(`/api/purchase/v1/findByEmployeeId/${employeeId}`, {
                params: { page: currentPage, size: 10, direction: 'desc' }
            });
            
            const purchaseList = extractPurchaseList(response.data);
            const total = response.data?.page?.totalPages || 1;
            
            if (reset) {
                setPurchases(purchaseList);
                setPage(1);
            } else {
                setPurchases(prev => [...prev, ...purchaseList]);
                setPage(prev => prev + 1);
            }
            setTotalPages(total);
        } catch (err) {
            console.error(err);
            alert('Search failed!');
        } finally {
            setLoading(false);
        }
    }

    async function searchByStatus(status, currentPage = 0, reset = true) {
        setLoading(true);
        setSearching(true);
        
        try {
            const response = await api.get(`/api/purchase/v1/findByStatus/${status}`, {
                params: { page: currentPage, size: 10, direction: 'desc' }
            });
            
            const purchaseList = extractPurchaseList(response.data);
            const total = response.data?.page?.totalPages || 1;
            
            if (reset) {
                setPurchases(purchaseList);
                setPage(1);
            } else {
                setPurchases(prev => [...prev, ...purchaseList]);
                setPage(prev => prev + 1);
            }
            setTotalPages(total);
        } catch (err) {
            console.error(err);
            alert('Search failed!');
        } finally {
            setLoading(false);
        }
    }

    async function loadPurchases(currentPage = 0, reset = true) {
        setLoading(true);
        
        try {
            const response = await api.get('/api/purchase/v1', {
                params: { page: currentPage, size: 10, direction: 'desc' }
            });
            
            const purchaseList = extractPurchaseList(response.data);
            const total = response.data?.page?.totalPages || 1;
            
            if (reset) {
                setPurchases(purchaseList);
                setPage(1);
            } else {
                setPurchases(prev => [...prev, ...purchaseList]);
                setPage(prev => prev + 1);
            }
            setTotalPages(total);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 401) logout();
            alert('Error loading purchases!');
        } finally {
            setLoading(false);
        }
    }

    const loadMore = () => {
        if (!loading && page < totalPages) {
            if (searching && searchType === 'supplierId') {
                searchBySupplierId(searchTerm, page, false);
            } else if (searching && searchType === 'employeeId') {
                searchByEmployeeId(searchTerm, page, false);
            } else if (searching && searchType === 'status') {
                searchByStatus(statusFilter, page, false);
            } else if (!searching) {
                loadPurchases(page, false);
            }
        }
    };

    const handleSearch = (e) => {
        e.preventDefault();
        
        if (searchType === 'status') {
            if (!statusFilter) {
                alert('Please select a status');
                return;
            }
            setSearchTerm(statusFilter);
            setSearching(true);
            setPage(0);
            setTotalPages(1);
            searchByStatus(statusFilter, 0, true);
        } else {
            if (!searchTerm.trim()) {
                alert('Please enter a search value');
                return;
            }
            
            setPage(0);
            setTotalPages(1);
            
            switch(searchType) {
                case 'id':
                    searchById(searchTerm.trim());
                    break;
                case 'supplierId':
                    searchBySupplierId(searchTerm.trim(), 0, true);
                    break;
                case 'employeeId':
                    searchByEmployeeId(searchTerm.trim(), 0, true);
                    break;
                default:
                    break;
            }
        }
    };

    const clearSearch = () => {
        setSearchTerm('');
        setStatusFilter('');
        setSearching(false);
        setPurchases([]);
        setPage(0);
        setTotalPages(1);
        loadPurchases(0, true);
    };

    useEffect(() => {
        if (searchType === 'status') {
            setSearchTerm('');
        }
    }, [searchType]);

    useEffect(() => {
        loadPurchases(0, true);
    }, []);

    const formatPrice = (price) => {
        return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(price);
    };

    const formatDate = (dateStr) => {
        if (!dateStr) return '-';
        return new Date(dateStr).toLocaleDateString('pt-BR');
    };

    const statusOptions = [
        { value: 'PENDING', label: 'Pending', color: '#FF8C00' },
        { value: 'APPROVED', label: 'Approved', color: '#9c399c'},
        { value: 'SHIPPED', label: 'Shipped', color: '#2196F3' },
        { value: 'RECEIVED', label: 'Received', color: '#4CAF50' },
        { value: 'CANCELLED', label: 'Cancelled', color: '#F44336' }
    ];

    return (
        <div className="purchase-page-container">
            <header>
                <img src={logoImage} alt="ERP Logo" />
                <span>Welcome, <strong>{getDisplayName(fullName).toUpperCase()} </strong></span>
                <Link className="purchase-page-add-button" to="/purchases/new/0">Create New Purchase</Link>
                <button className="purchase-page-logout-btn" onClick={logout} type="button">
                    <FiPower size={18} color="#20B2AA" />
                </button>
            </header>

            <div className="purchase-page-title-row">
                <Link to="/dashboard" className="purchase-page-back-button">
                    <FiArrowLeft size={24} />
                </Link>
                <h1>Purchases</h1>
            </div>

            <div className="purchase-page-search-section">
                <form onSubmit={handleSearch} className="purchase-page-search-form">
                    <select 
                        value={searchType} 
                        onChange={e => setSearchType(e.target.value)}
                        className="purchase-page-search-type-select"
                    >
                        <option value="id">Search by Purchase Code (ID)</option>
                        <option value="supplierId">Search by Supplier ID</option>
                        <option value="employeeId">Search by Employee ID</option>
                        <option value="status">Search by Status</option>
                    </select>
                    
                    {searchType === 'status' ? (
                        <select 
                            value={statusFilter} 
                            onChange={e => setStatusFilter(e.target.value)}
                            className="purchase-page-status-select"
                        >
                            <option value="">Select Status</option>
                            {statusOptions.map(opt => (
                                <option key={opt.value} value={opt.value}>{opt.label}</option>
                            ))}
                        </select>
                    ) : (
                        <input
                            type={searchType === 'id' || searchType === 'supplierId' || searchType === 'employeeId' ? 'number' : 'text'}
                            min={searchType === 'id' || searchType === 'supplierId' || searchType === 'employeeId' ? 1 : undefined}
                            placeholder={
                                searchType === 'id' ? 'Enter purchase code (ID)...' :
                                searchType === 'supplierId' ? 'Enter supplier ID...' :
                                'Enter employee ID...'
                            }
                            value={searchTerm}
                            onChange={e => setSearchTerm(e.target.value)}
                        />
                    )}
                    
                    <button type="submit" className="purchase-page-search-btn">
                        <FiSearch size={16} /> Search
                    </button>
                    
                    {searching && (
                        <button type="button" className="purchase-page-clear-btn" onClick={clearSearch}>
                            Clear
                        </button>
                    )}
                </form>
            </div>

            {purchases.length > 0 && (
                <ul>
                    {purchases.map(purchase => (
                        <li key={purchase.id}>
                            <div className="purchase-page-header">
                                <strong className="purchase-page-code">Purchase #{purchase.id}</strong>
                                <span className={`purchase-page-status ${purchase.status?.toLowerCase()}`}>
                                    {getStatusIcon(purchase.status)}
                                    {getStatusText(purchase.status)}
                                </span>
                            </div>

                            <div className="purchase-page-details">
                                <div>
                                    <strong>Supplier ID:</strong>
                                    <p>{purchase.supplierId}</p>
                                </div>
                                <div>
                                    <strong>Employee ID:</strong>
                                    <p>{purchase.employeeId}</p>
                                </div>
                                <div>
                                    <strong>Date:</strong>
                                    <p>{formatDate(purchase.purchaseDate)}</p>
                                </div>
                                <div>
                                    <strong>Total:</strong>
                                    <p className="total">{formatPrice(purchase.totalAmount)}</p>
                                </div>
                            </div>

                            {purchase.items && purchase.items.length > 0 && (
                                <div className="purchase-page-items">
                                    <strong>Items ({purchase.items.length})</strong>
                                    {purchase.items.slice(0, 3).map((item, idx) => (
                                        <p key={idx}>
                                            • {item.ingredient?.name || `Ingredient #${item.ingredientId}`} - Qty: {item.quantity} {item.ingredient?.unitOfMeasure || 'un'} - {formatPrice(item.unitPrice)}
                                        </p>
                                    ))}
                                    {purchase.items.length > 3 && (
                                        <p>... and {purchase.items.length - 3} more</p>
                                    )}
                                </div>
                            )}

                            <div className="purchase-page-action-buttons">
                                <button onClick={() => navigate(`/purchases/new/${purchase.id}`)} title="View Details">
                                    <FiEye size={20} color="#20B2AA" />
                                </button>
                                
                                <div className="purchase-page-status-selector">
                                    <select
                                        value={selectedStatus[purchase.id] || purchase.status}
                                        onChange={(e) => setSelectedStatus(prev => ({ ...prev, [purchase.id]: e.target.value }))}
                                        className={`purchase-page-status-dropdown ${selectedStatus[purchase.id] !== purchase.status ? 'purchase-page-status-changed' : ''}`}
                                        disabled={updating}
                                    >
                                        <option value="PENDING">🟡 Pending</option>
                                        <option value="APPROVED">🟣 Approved</option>
                                        <option value="SHIPPED">🔵 Shipped</option>
                                        <option value="RECEIVED">🟢 Received</option>
                                        <option value="CANCELLED">🔴 Cancelled</option>
                                    </select>
                                    
                                    {selectedStatus[purchase.id] !== purchase.status && (
                                        <button
                                            onClick={() => updatePurchaseStatus(purchase.id, selectedStatus[purchase.id])}
                                            className="purchase-page-update-status-btn"
                                            disabled={updating}
                                        >
                                            <FiCheckCircle size={14} /> Update
                                        </button>
                                    )}
                                </div>
                            </div>
                        </li>
                    ))}
                </ul>
            )}

            {!searching && page < totalPages && purchases.length > 0 && (
                <button className="purchase-page-load-more" onClick={loadMore} disabled={loading}>
                    {loading ? 'Loading...' : 'Load More'}
                </button>
            )}

            {searching && (searchType === 'supplierId' || searchType === 'employeeId' || searchType === 'status') && page < totalPages && purchases.length > 0 && (
                <button className="purchase-page-load-more" onClick={loadMore} disabled={loading}>
                    {loading ? 'Loading...' : 'Load More'}
                </button>
            )}

            {purchases.length === 0 && !loading && (
                <p className="purchase-page-no-results">
                    {searching ? `No purchases found.` : 'No purchases found.'}
                </p>
            )}
            
            {loading && purchases.length === 0 && (
                <p className="purchase-page-no-results">Loading...</p>
            )}
        </div>
    );
}