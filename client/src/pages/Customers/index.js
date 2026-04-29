// pages/Customers/index.js
import React, { useState, useEffect, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FiPower, FiEdit, FiTrash2, FiUserCheck, FiUserX, FiSearch, FiArrowLeft, FiMail, FiPhone, FiMapPin, FiFileText } from 'react-icons/fi';
import './styles.css';
import api from '../../services/api';
import logoImage from '../../assets/logoerp.png';

export default function Customers() {
    const [customers, setCustomers] = useState([]);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const [loading, setLoading] = useState(false);
    
    const [searchType, setSearchType] = useState('name');
    const [searchValue, setSearchValue] = useState('');
    const [searching, setSearching] = useState(false);
    const [singleResult, setSingleResult] = useState(null);

    const fullName = localStorage.getItem('fullName');
    const navigate = useNavigate();

    function getDisplayName(fullName) {
        if (!fullName) return '';
        let firstName = fullName.trim().split(' ')[0];
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

    async function deleteCustomer(id) {
        if (window.confirm('Are you sure you want to delete this customer?')) {
            try {
                await api.delete(`api/customer/v1/${id}`);
                if (searching) {
                    setSearching(false);
                    setSearchValue('');
                    setSingleResult(null);
                    setPage(0);
                    setCustomers([]);
                    setHasMore(true);
                    fetchCustomers(true);
                } else {
                    setCustomers(customers.filter(customer => customer.id !== id));
                }
                alert('Customer deleted successfully!');
            } catch (err) {
                console.error(err);
                const errorMessage = err.response?.data?.message || err.message;
                if (errorMessage.includes('violates foreign key constraint') || 
                    errorMessage.includes('fk_orders_customer') ||
                    errorMessage.includes('fk_purchases_customer')) {
                    alert('❌ Exclusão não permitida\n\nNão é possível excluir este cliente pois existem PEDIDOS ou COMPRAS vinculados a ele.');
                } else {
                    alert('Erro ao excluir! Tente novamente.');
                }
            }
        }
    }

    async function toggleActive(customer) {
        try {
            const endpoint = customer.active
                ? `api/customer/v1/deactivate/${customer.id}`
                : `api/customer/v1/activate/${customer.id}`;
            const response = await api.patch(endpoint);
            
            if (searching && singleResult) {
                setSingleResult(response.data);
            } else {
                setCustomers(customers.map(c => c.id === customer.id ? response.data : c));
            }
            alert(`Customer ${customer.active ? 'deactivated' : 'activated'} successfully!`);
        } catch (err) {
            console.error(err);
            alert('Operation failed! Try again.');
        }
    }

    async function searchById(id) {
        setLoading(true);
        try {
            const response = await api.get(`api/customer/v1/${id}`);
            setSingleResult(response.data);
            setSearching(true);
            setCustomers([]);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 404) {
                alert('Customer not found!');
            } else {
                alert('Search failed!');
            }
            setSingleResult(null);
        } finally {
            setLoading(false);
        }
    }

    async function searchByEmail(email) {
        setLoading(true);
        try {
            const response = await api.get(`api/customer/v1/findByEmail/${encodeURIComponent(email)}`);
            setSingleResult(response.data);
            setSearching(true);
            setCustomers([]);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 404) {
                alert('Customer not found!');
            } else {
                alert('Search failed!');
            }
            setSingleResult(null);
        } finally {
            setLoading(false);
        }
    }

    async function searchByDocument(document) {
        setLoading(true);
        try {
            const response = await api.get(`api/customer/v1/findByDocument/${encodeURIComponent(document)}`);
            setSingleResult(response.data);
            setSearching(true);
            setCustomers([]);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 404) {
                alert('Customer not found!');
            } else {
                alert('Search failed!');
            }
            setSingleResult(null);
        } finally {
            setLoading(false);
        }
    }

    async function searchByName(name, reset = true) {
        if (loading) return;
        
        setLoading(true);
        const currentPage = reset ? 0 : page;
        
        try {
            const response = await api.get(`/api/customer/v1/findByName/${encodeURIComponent(name)}`, {
                params: { page: currentPage, size: 10, direction: 'asc' }
            });
            
            const content = response.data?._embedded?.customers || [];
            const totalPages = response.data?.page?.totalPages || 0;
            
            if (reset) {
                setCustomers(content);
                setPage(1);
                setSearching(true);
                setSingleResult(null);
            } else {
                setCustomers(prev => [...prev, ...content]);
                setPage(prev => prev + 1);
            }
            
            setHasMore(currentPage + 1 < totalPages);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 401) logout();
        } finally {
            setLoading(false);
        }
    }

    const fetchCustomers = useCallback(async (reset = false) => {
        if (loading) return;
        
        setLoading(true);
        const currentPage = reset ? 0 : page;
        
        try {
            const response = await api.get('/api/customer/v1', {
                params: { page: currentPage, size: 10, direction: 'asc' }
            });
            
            const content = response.data?._embedded?.customers || [];
            const totalPages = response.data?.page?.totalPages || 0;
            
            if (reset) {
                setCustomers(content);
                setPage(1);
            } else {
                setCustomers(prev => [...prev, ...content]);
                setPage(prev => prev + 1);
            }
            
            setHasMore(currentPage + 1 < totalPages);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 401) logout();
        } finally {
            setLoading(false);
        }
    }, [page, searching]);

    useEffect(() => {
        if (!searching) {
            fetchCustomers(true);
        }
    }, []);

    const handleSearch = (e) => {
        e.preventDefault();
        if (!searchValue.trim()) {
            alert('Please enter a search value');
            return;
        }
        
        setPage(0);
        setHasMore(true);
        
        switch(searchType) {
            case 'id':
                searchById(searchValue.trim());
                break;
            case 'email':
                searchByEmail(searchValue.trim());
                break;
            case 'document':
                searchByDocument(searchValue.trim());
                break;
            case 'name':
                searchByName(searchValue.trim(), true);
                break;
            default:
                break;
        }
    };

    const clearSearch = () => {
        setSearching(false);
        setSearchValue('');
        setSingleResult(null);
        setCustomers([]);
        setPage(0);
        setHasMore(true);
        fetchCustomers(true);
    };

    // Formata telefone
    const formatPhone = (phone) => {
        if (!phone) return '-';
        const cleaned = phone.replace(/\D/g, '');
        if (cleaned.length === 11) {
            return `(${cleaned.substring(0, 2)}) ${cleaned.substring(2, 7)}-${cleaned.substring(7)}`;
        }
        if (cleaned.length === 10) {
            return `(${cleaned.substring(0, 2)}) ${cleaned.substring(2, 6)}-${cleaned.substring(6)}`;
        }
        return phone;
    };

    // Formata documento (CPF/CNPJ)
    const formatDocument = (doc) => {
        if (!doc) return '-';
        const cleaned = doc.replace(/\D/g, '');
        if (cleaned.length === 11) {
            return cleaned.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
        }
        if (cleaned.length === 14) {
            return cleaned.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5');
        }
        return doc;
    };

    return (
        <div className="customer-page-container">
            <header>
                <img src={logoImage} alt="ERP Logo" />
                <span>Welcome, <strong>{getDisplayName(fullName).toUpperCase()}</strong></span>
                <Link className="customer-page-add-button" to="/customers/new/0">Add New Customer</Link>
                <button className="customer-page-logout-btn" onClick={logout} type="button">
                    <FiPower size={18} color="#251FC5" />
                </button>
            </header>

            <div className="customer-page-title-row">
                <Link to="/dashboard" className="customer-page-back-button">
                    <FiArrowLeft size={24} />
                </Link>
                <h1>Registered Customers</h1>
            </div>

            <div className="customer-page-search-section">
                <form onSubmit={handleSearch} className="customer-page-search-form">
                    <select 
                        value={searchType} 
                        onChange={e => setSearchType(e.target.value)}
                        className="customer-page-search-type-select"
                    >
                        <option value="name">Search by Name</option>
                        <option value="id">Search by ID</option>
                        <option value="email">Search by Email</option>
                        <option value="document">Search by Document</option>
                    </select>
                    
                    <input
                        type={searchType === 'id' ? 'number' : 'text'}
                        min={searchType === 'id' ? 1 : undefined}
                        placeholder={
                            searchType === 'name' ? 'Enter name...' :
                            searchType === 'id' ? 'Enter ID...' :
                            searchType === 'email' ? 'Enter email...' :
                            'Enter document (CPF/CNPJ)...'
                        }
                        value={searchValue}
                        onChange={(e) => {
    let value = e.target.value;

    if (searchType === 'document') {
        value = value.replace(/\D/g, '');
    }

    setSearchValue(value);
}}
                    />
                    
                    <button type="submit" className="customer-page-search-btn">
                        <FiSearch size={16} /> Search
                    </button>
                    
                    {searching && (
                        <button type="button" className="customer-page-clear-btn" onClick={clearSearch}>
                            Clear Search
                        </button>
                    )}
                </form>
            </div>

            {/* Resultado de busca única (ID, Email, Document) */}
            {searching && singleResult && (
                <div className="customer-page-single-result">
                    <h3>Search Result</h3>
                    <ul className="customer-page-single-result-list">
                        <li>
                            <div className="customer-page-header">
                                <strong className="customer-page-code">ID: #{singleResult.id}</strong>
                                <span className={`customer-page-status ${singleResult.active ? 'active' : 'inactive'}`}>
                                    {singleResult.active ? 'ACTIVE' : 'INACTIVE'}
                                </span>
                            </div>

                            <div className="customer-page-name">
                                <strong>Name:</strong>
                                <p>{singleResult.name}</p>
                            </div>

                            <div className="customer-page-contact">
                                <div className="customer-page-contact-item">
                                    <FiMail size={14} />
                                    <strong>Email:</strong>
                                    <p>{singleResult.email}</p>
                                </div>
                                <div className="customer-page-contact-item">
                                    <FiPhone size={14} />
                                    <strong>Phone:</strong>
                                    <p>{formatPhone(singleResult.phone)}</p>
                                </div>
                            </div>

                            <div className="customer-page-info">
                                <div>
                                    <strong>Document:</strong>
                                    <p>{formatDocument(singleResult.document)}</p>
                                </div>
                                <div>
                                    <strong>Customer Since:</strong>
                                    <p>{singleResult.createdAt ? new Date(singleResult.createdAt).toLocaleDateString('pt-BR') : '-'}</p>
                                </div>
                            </div>

                            {singleResult.city && (
                                <div className="customer-page-address">
                                    <FiMapPin size={14} />
                                    <strong>Address:</strong>
                                    <p>{singleResult.city}, {singleResult.state} - {singleResult.zipCode || ''}</p>
                                </div>
                            )}
                            
                            <div className="customer-page-action-buttons">
                                <button onClick={() => toggleActive(singleResult)} title={singleResult.active ? 'Deactivate' : 'Activate'}>
                                    {singleResult.active ? <FiUserX size={20} color="#FF4444" /> : <FiUserCheck size={20} color="#00AA00" />}
                                </button>
                                <button onClick={() => navigate(`/customers/new/${singleResult.id}`)} title="Edit">
                                    <FiEdit size={20} color="#251FC5" />
                                </button>
                                <button onClick={() => deleteCustomer(singleResult.id)} title="Delete">
                                    <FiTrash2 size={20} color="#251FC5" />
                                </button>
                            </div>
                        </li>
                    </ul>
                </div>
            )}

            {/* Resultados de busca paginada (Name) */}
            {searching && !singleResult && customers.length > 0 && (
                <>
                    <h3>Search Results for: "{searchValue}"</h3>
                    <ul>
                        {customers.map(customer => (
                            <li key={customer.id}>
                                <div className="customer-page-header">
                                    <strong className="customer-page-code">ID: #{customer.id}</strong>
                                    <span className={`customer-page-status ${customer.active ? 'active' : 'inactive'}`}>
                                        {customer.active ? 'ACTIVE' : 'INACTIVE'}
                                    </span>
                                </div>

                                <div className="customer-page-name">
                                    <strong>Name:</strong>
                                    <p>{customer.name}</p>
                                </div>

                                <div className="customer-page-contact">
                                    <div className="customer-page-contact-item">
                                        <FiMail size={14} />
                                        <strong>Email:</strong>
                                        <p>{customer.email}</p>
                                    </div>
                                    <div className="customer-page-contact-item">
                                        <FiPhone size={14} />
                                        <strong>Phone:</strong>
                                        <p>{formatPhone(customer.phone)}</p>
                                    </div>
                                </div>

                                <div className="customer-page-info">
                                    <div>
                                        <strong>Document:</strong>
                                        <p>{formatDocument(customer.document)}</p>
                                    </div>
                                    <div>
                                        <strong>City/State:</strong>
                                        <p>{customer.city}/{customer.state}</p>
                                    </div>
                                </div>

                                {customer.street && (
                                    <div className="customer-page-address">
                                        <FiMapPin size={14} />
                                        <strong>Address:</strong>
                                        <p>{customer.street}, {customer.streetNumber} - {customer.neighborhood}</p>
                                    </div>
                                )}
                                
                                <div className="customer-page-action-buttons">
                                    <button onClick={() => toggleActive(customer)} title={customer.active ? 'Deactivate' : 'Activate'}>
                                        {customer.active ? <FiUserX size={20} color="#FF4444" /> : <FiUserCheck size={20} color="#00AA00" />}
                                    </button>
                                    <button onClick={() => navigate(`/customers/new/${customer.id}`)} title="Edit">
                                        <FiEdit size={20} color="#251FC5" />
                                    </button>
                                    <button onClick={() => deleteCustomer(customer.id)} title="Delete">
                                        <FiTrash2 size={20} color="#251FC5" />
                                    </button>
                                </div>
                            </li>
                        ))}
                    </ul>
                    
                    {hasMore && (
                        <button className="customer-page-load-more" onClick={() => searchByName(searchValue, false)} disabled={loading}>
                            {loading ? 'Loading...' : 'Load More'}
                        </button>
                    )}
                </>
            )}

            {/* Lista normal (sem busca) */}
            {!searching && customers.length > 0 && (
                <ul>
                    {customers.map(customer => (
                        <li key={customer.id}>
                            <div className="customer-page-header">
                                <strong className="customer-page-code">ID: #{customer.id}</strong>
                                <span className={`customer-page-status ${customer.active ? 'active' : 'inactive'}`}>
                                    {customer.active ? 'ACTIVE' : 'INACTIVE'}
                                </span>
                            </div>

                            <div className="customer-page-name">
                                <strong>Name:</strong>
                                <p>{customer.name}</p>
                            </div>

                            <div className="customer-page-contact">
                                <div className="customer-page-contact-item">
                                    <FiMail size={14} />
                                    <strong>Email:</strong>
                                    <p>{customer.email}</p>
                                </div>
                                <div className="customer-page-contact-item">
                                    <FiPhone size={14} />
                                    <strong>Phone:</strong>
                                    <p>{formatPhone(customer.phone)}</p>
                                </div>
                            </div>

                            <div className="customer-page-info">
                                <div>
                                    <strong>Document:</strong>
                                    <p>{formatDocument(customer.document)}</p>
                                </div>
                                <div>
                                    <strong>City/State:</strong>
                                    <p>{customer.city}/{customer.state}</p>
                                </div>
                            </div>

                            {customer.street && (
                                <div className="customer-page-address">
                                    <FiMapPin size={14} />
                                    <strong>Address:</strong>
                                    <p>{customer.street}, {customer.streetNumber} - {customer.neighborhood}</p>
                                </div>
                            )}
                            
                            <div className="customer-page-action-buttons">
                                <button onClick={() => toggleActive(customer)} title={customer.active ? 'Deactivate' : 'Activate'}>
                                    {customer.active ? <FiUserX size={20} color="#FF4444" /> : <FiUserCheck size={20} color="#00AA00" />}
                                </button>
                                <button onClick={() => navigate(`/customers/new/${customer.id}`)} title="Edit">
                                    <FiEdit size={20} color="#251FC5" />
                                </button>
                                <button onClick={() => deleteCustomer(customer.id)} title="Delete">
                                    <FiTrash2 size={20} color="#251FC5" />
                                </button>
                            </div>
                        </li>
                    ))}
                </ul>
            )}

            {!searching && hasMore && customers.length > 0 && (
                <button className="customer-page-load-more" onClick={() => fetchCustomers(false)} disabled={loading}>
                    {loading ? 'Loading...' : 'Load More'}
                </button>
            )}

            {!searching && customers.length === 0 && !loading && (
                <p className="customer-page-no-results">No customers found.</p>
            )}
            
            {searching && !singleResult && customers.length === 0 && !loading && (
                <p className="customer-page-no-results">No customers found for "{searchValue}".</p>
            )}
        </div>
    );
}