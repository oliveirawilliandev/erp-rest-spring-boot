// pages/Orders/index.js
import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FiPower, FiEye, FiSearch, FiCheckCircle, FiClock, FiXCircle, FiShoppingCart, FiArrowLeft } from 'react-icons/fi';
import './styles.css';
import api from '../../services/api';
import logoImage from '../../assets/logoerp.png';

export default function Orders() {
    const [orders, setOrders] = useState([]);
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

    const extractOrderList = (responseData) => {
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
            case 'DELIVERED': return <FiCheckCircle color="#4CAF50" size={14} />;
            case 'CANCELLED': return <FiXCircle color="#F44336" size={14} />;
            default: return <FiClock color="#FF8C00" size={14} />;
        }
    };

    const getStatusText = (status) => {
        switch(status) {
            case 'PENDING': return 'Pending';
            case 'PROCESSING': return 'Processing';
            case 'SHIPPED': return 'Shipped';
            case 'DELIVERED': return 'Delivered';
            case 'CANCELLED': return 'Cancelled';
            default: return status;
        }
    };

    // Função para atualizar status do pedido - CORRIGIDA
    async function updateOrderStatus(orderId, newStatus) {
        if (!window.confirm(`Are you sure you want to change this order status to ${getStatusText(newStatus)}?`)) {
            setSelectedStatus(prev => ({ ...prev, [orderId]: orders.find(o => o.id === orderId)?.status || '' }));
            return;
        }
        
        setUpdating(true);
        try {
            // CORREÇÃO: Endpoint correto /updateStatus/{id} com query param status
            await api.patch(`api/order/v1/updateStatus/${orderId}`, null, {
                params: { status: newStatus }
            });
            alert(`Order status updated to ${getStatusText(newStatus)}!`);
            
            // Atualizar localmente
            setOrders(prevOrders => 
                prevOrders.map(order => 
                    order.id === orderId 
                        ? { ...order, status: newStatus }
                        : order
                )
            );
            setSelectedStatus(prev => ({ ...prev, [orderId]: newStatus }));
            
        } catch (err) {
            console.error(err);
            const message = err.response?.data?.message || "Error updating order status!";
            alert(message);
            const originalOrder = orders.find(o => o.id === orderId);
            if (originalOrder) {
                setSelectedStatus(prev => ({ ...prev, [orderId]: originalOrder.status }));
            }
        } finally {
            setUpdating(false);
        }
    }

    useEffect(() => {
        const initialStatus = {};
        orders.forEach(order => {
            initialStatus[order.id] = order.status;
        });
        setSelectedStatus(initialStatus);
    }, [orders]);

    async function searchById(id) {
        setLoading(true);
        setSearching(true);
        try {
            const response = await api.get(`api/order/v1/${id}`);
            setOrders([response.data]);
            setTotalPages(1);
            setPage(0);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 404) {
                alert('Order not found!');
                setOrders([]);
            } else {
                alert('Search failed!');
            }
        } finally {
            setLoading(false);
        }
    }

    async function searchByCustomerId(customerId, currentPage = 0, reset = true) {
        setLoading(true);
        setSearching(true);
        
        try {
            const response = await api.get(`/api/order/v1/findByCustomerId/${customerId}`, {
                params: { page: currentPage, size: 10, direction: 'desc' }
            });
            
            const orderList = extractOrderList(response.data);
            const total = response.data?.page?.totalPages || 1;
            
            if (reset) {
                setOrders(orderList);
                setPage(1);
            } else {
                setOrders(prev => [...prev, ...orderList]);
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
            const response = await api.get(`/api/order/v1/findByEmployeeId/${employeeId}`, {
                params: { page: currentPage, size: 10, direction: 'desc' }
            });
            
            const orderList = extractOrderList(response.data);
            const total = response.data?.page?.totalPages || 1;
            
            if (reset) {
                setOrders(orderList);
                setPage(1);
            } else {
                setOrders(prev => [...prev, ...orderList]);
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
            const response = await api.get(`/api/order/v1/findByStatus/${status}`, {
                params: { page: currentPage, size: 10, direction: 'desc' }
            });
            
            const orderList = extractOrderList(response.data);
            const total = response.data?.page?.totalPages || 1;
            
            if (reset) {
                setOrders(orderList);
                setPage(1);
            } else {
                setOrders(prev => [...prev, ...orderList]);
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

    async function loadOrders(currentPage = 0, reset = true) {
        setLoading(true);
        
        try {
            const response = await api.get('/api/order/v1', {
                params: { page: currentPage, size: 10, direction: 'desc' }
            });
            
            const orderList = extractOrderList(response.data);
            const total = response.data?.page?.totalPages || 1;
            
            if (reset) {
                setOrders(orderList);
                setPage(1);
            } else {
                setOrders(prev => [...prev, ...orderList]);
                setPage(prev => prev + 1);
            }
            setTotalPages(total);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 401) logout();
            alert('Error loading orders!');
        } finally {
            setLoading(false);
        }
    }

    const loadMore = () => {
        if (!loading && page < totalPages) {
            if (searching && searchType === 'customerId') {
                searchByCustomerId(searchTerm, page, false);
            } else if (searching && searchType === 'employeeId') {
                searchByEmployeeId(searchTerm, page, false);
            } else if (searching && searchType === 'status') {
                searchByStatus(statusFilter, page, false);
            } else if (!searching) {
                loadOrders(page, false);
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
                case 'customerId':
                    searchByCustomerId(searchTerm.trim(), 0, true);
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
        setOrders([]);
        setPage(0);
        setTotalPages(1);
        loadOrders(0, true);
    };

    useEffect(() => {
        if (searchType === 'status') {
            setSearchTerm('');
        }
    }, [searchType]);

    useEffect(() => {
        loadOrders(0, true);
    }, []);

    const formatPrice = (price) => {
        return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(price);
    };

    const formatDate = (dateStr) => {
        if (!dateStr) return '-';
        return new Date(dateStr).toLocaleDateString('pt-BR');
    };

    return (
        <div className="order-page-container">
            <header>
                <img src={logoImage} alt="ERP Logo" />
                <span>Welcome, <strong>{getDisplayName(fullName).toUpperCase()} </strong></span>
                <Link className="order-page-add-button" to="/orders/new/0">Create New Order</Link>
                <button className="order-page-logout-btn" onClick={logout} type="button">
                    <FiPower size={18} color="#DC143C" />
                </button>
            </header>

            <div className="order-page-title-row">
                <Link to="/dashboard" className="order-page-back-button">
                    <FiArrowLeft size={24} />
                </Link>
                <h1>Orders</h1>                
            </div>     

            <div className="order-page-search-section">
                <form onSubmit={handleSearch} className="order-page-search-form">
                    <select 
                        value={searchType} 
                        onChange={e => setSearchType(e.target.value)}
                        className="order-page-search-type-select"
                    >
                        <option value="id">Search by Order Code (ID)</option>
                        <option value="customerId">Search by Customer ID</option>
                        <option value="employeeId">Search by Employee ID</option>
                        <option value="status">Search by Status</option>
                    </select>
                    
                    {searchType === 'status' ? (
                        <select 
                            value={statusFilter} 
                            onChange={e => setStatusFilter(e.target.value)}
                            className="order-page-status-select"
                        >
                            <option value="">Select Status</option>
                            <option value="PENDING">Pending</option>
                            <option value="PROCESSING">Processing</option>
                            <option value="SHIPPED">Shipped</option>
                            <option value="DELIVERED">Delivered</option>
                            <option value="CANCELLED">Cancelled</option>
                        </select>
                    ) : (
                        <input
                                                    type={
                                searchType === 'id' ||
                                searchType === 'customerId' ||
                                searchType === 'employeeId'
                                    ? 'number'
                                    : 'text'
                            }
                            min={
                                searchType === 'id' ||
                                searchType === 'customerId' ||
                                searchType === 'employeeId'
                                    ? 1
                                    : undefined
                            }
                            placeholder={                                
                                searchType === 'id' ? 'Enter order code (ID)...' :
                                searchType === 'customerId' ? 'Enter customer ID...' :
                                'Enter employee ID...'
                            }
                            value={searchTerm}
                            onChange={e => setSearchTerm(e.target.value)}
                        />
                    )}
                    
                    <button type="submit" className="order-page-search-btn">
                        <FiSearch size={16} /> Search
                    </button>
                    
                    {searching && (
                        <button type="button" className="order-page-clear-btn" onClick={clearSearch}>
                            Clear
                        </button>
                    )}
                </form>
            </div>

            {orders.length > 0 && (
                <ul>
                    {orders.map(order => (
                        <li key={order.id}>
                            <div className="order-page-header">
                                <strong className="order-page-code">Order #{order.id}</strong>
                                <span className={`order-page-status ${order.status?.toLowerCase()}`}>
                                    {getStatusIcon(order.status)}
                                    {getStatusText(order.status)}
                                </span>
                            </div>

                            <div className="order-page-details">
                                <div>
                                    <strong>Customer ID:</strong>
                                    <p>{order.customerId}</p>
                                </div>
                                <div>
                                    <strong>Employee ID:</strong>
                                    <p>{order.employeeId}</p>
                                </div>
                                <div>
                                    <strong>Date:</strong>
                                    <p>{formatDate(order.createdAt)}</p>
                                </div>
                                <div>
                                    <strong>Total:</strong>
                                    <p className="total">{formatPrice(order.totalAmount)}</p>
                                </div>
                            </div>

                            {order.items && order.items.length > 0 && (
                                <div className="order-page-items">
                                    <strong>Items ({order.items.length})</strong>
                                    {order.items.slice(0, 3).map((item, idx) => (
                                        <p key={idx}>
                                            • Product #{item.productId} - Qty: {item.quantity} - {formatPrice(item.unitPrice)}
                                        </p>
                                    ))}
                                    {order.items.length > 3 && (
                                        <p>... and {order.items.length - 3} more</p>
                                    )}
                                </div>
                            )}

                            <div className="order-page-action-buttons">
                                <button onClick={() => navigate(`/orders/new/${order.id}`)} title="View Details">
                                    <FiEye size={20} color="#DC143C" />
                                </button>
                                
                                <div className="order-page-status-selector">
                                    <select
                                        value={selectedStatus[order.id] || order.status}
                                        onChange={(e) => setSelectedStatus(prev => ({ ...prev, [order.id]: e.target.value }))}
                                        className={`order-page-status-dropdown ${selectedStatus[order.id] !== order.status ? 'order-page-status-changed' : ''}`}
                                        disabled={updating}
                                    >
                                        <option value="PENDING">🟡 Pending</option>
                                        <option value="PROCESSING">🔵 Processing</option>
                                        <option value="SHIPPED">🟣 Shipped</option>
                                        <option value="DELIVERED">🟢 Delivered</option>
                                        <option value="CANCELLED">🔴 Cancelled</option>
                                    </select>
                                    
                                    {selectedStatus[order.id] !== order.status && (
                                        <button
                                            onClick={() => updateOrderStatus(order.id, selectedStatus[order.id])}
                                            className="order-page-update-status-btn"
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

            {!searching && page < totalPages && orders.length > 0 && (
                <button className="order-page-load-more" onClick={loadMore} disabled={loading}>
                    {loading ? 'Loading...' : 'Load More'}
                </button>
            )}

            {searching && (searchType === 'customerId' || searchType === 'employeeId' || searchType === 'status') && page < totalPages && orders.length > 0 && (
                <button className="order-page-load-more" onClick={loadMore} disabled={loading}>
                    {loading ? 'Loading...' : 'Load More'}
                </button>
            )}

            {orders.length === 0 && !loading && (
                <p className="order-page-no-results">
                    {searching ? `No orders found.` : 'No orders found.'}
                </p>
            )}
            
            {loading && orders.length === 0 && (
                <p className="order-page-no-results">Loading...</p>
            )}
        </div>
    );
}