// pages/Suppliers/index.js
import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FiPower, FiEdit, FiTrash2, FiUserCheck, FiUserX, FiSearch, FiArrowLeft } from 'react-icons/fi';
import './styles.css';
import api from '../../services/api';
import logoImage from '../../assets/logoerp.png';

export default function Suppliers() {
    const [suppliers, setSuppliers] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(false);
    
    const [searchTerm, setSearchTerm] = useState('');
    const [searchType, setSearchType] = useState('name');
    const [searching, setSearching] = useState(false);

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

    async function deleteSupplier(id) {
        if (window.confirm('Are you sure you want to delete this supplier?')) {
            try {
                await api.delete(`api/supplier/v1/${id}`);
                setSuppliers(suppliers.filter(s => s.id !== id));
                alert('Supplier deleted successfully!');
            }catch (err) {
            console.error(err);
            
            // Verifica se o erro é de violação de chave estrangeira
            const errorMessage = err.response?.data?.message || err.message;
            
            if (errorMessage.includes('violates foreign key constraint') || 
                errorMessage.includes('fk_orders_customer') ||
                errorMessage.includes('fk_purchases_customer')) {
                
                alert('❌ Exclusão não permitida \n\n Este fornecedor não pode ser excluído pois existem COMPRAS vinculadas a ele.');
            } else {
                alert('Erro ao excluir! Tente novamente.');
            }
            }
        }
    }

    async function toggleActive(supplier) {
        try {
            const endpoint = supplier.active
                ? `api/supplier/v1/deactivate/${supplier.id}`
                : `api/supplier/v1/activate/${supplier.id}`;
            
            const response = await api.patch(endpoint);
            setSuppliers(suppliers.map(s => s.id === supplier.id ? response.data : s));
            alert(`Supplier ${supplier.active ? 'deactivated' : 'activated'} successfully!`);
        } catch (err) {
            console.error(err);
            alert('Operation failed! Try again.');
        }
    }

    const extractSupplierList = (responseData) => {
        if (Array.isArray(responseData)) return responseData;
        if (responseData && responseData._embedded) {
            const embeddedKeys = Object.keys(responseData._embedded);
            if (embeddedKeys.length > 0) return responseData._embedded[embeddedKeys[0]];
        }
        if (responseData && responseData.content) return responseData.content;
        if (responseData && responseData.id) return [responseData];
        return [];
    };

    async function searchById(id) {
        setLoading(true);
        setSearching(true);
        try {
            const response = await api.get(`api/supplier/v1/${id}`);
            setSuppliers([response.data]);
            setTotalPages(1);
            setPage(0);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 404) {
                alert('Supplier not found!');
                setSuppliers([]);
            } else {
                alert('Search failed!');
            }
        } finally {
            setLoading(false);
        }
    }

    async function searchByDocument(document) {
        setLoading(true);
        setSearching(true);
        try {
            const response = await api.get(`api/supplier/v1/findByDocument/${encodeURIComponent(document)}`);
            setSuppliers([response.data]);
            setTotalPages(1);
            setPage(0);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 404) {
                alert('Supplier not found!');
                setSuppliers([]);
            } else {
                alert('Search failed!');
            }
        } finally {
            setLoading(false);
        }
    }

    async function searchByName(name, currentPage = 0, reset = true) {
        setLoading(true);
        setSearching(true);
        
        try {
            const response = await api.get(`/api/supplier/v1/findByName/${encodeURIComponent(name)}`, {
                params: { page: currentPage, size: 10, direction: 'asc' }
            });
            
            const supplierList = extractSupplierList(response.data);
            const total = response.data?.page?.totalPages || 1;
            
            if (reset) {
                setSuppliers(supplierList);
                setPage(1);
            } else {
                setSuppliers(prev => [...prev, ...supplierList]);
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

    async function loadSuppliers(currentPage = 0, reset = true) {
        setLoading(true);
        
        try {
            const response = await api.get('/api/supplier/v1', {
                params: { page: currentPage, size: 10, direction: 'asc' }
            });
            
            const supplierList = extractSupplierList(response.data);
            const total = response.data?.page?.totalPages || 1;
            
            if (reset) {
                setSuppliers(supplierList);
                setPage(1);
            } else {
                setSuppliers(prev => [...prev, ...supplierList]);
                setPage(prev => prev + 1);
            }
            setTotalPages(total);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 401) logout();
            alert('Error loading suppliers!');
        } finally {
            setLoading(false);
        }
    }

    const loadMore = () => {
        if (!loading && page < totalPages) {
            if (searching && searchType === 'name') {
                searchByName(searchTerm, page, false);
            } else if (!searching) {
                loadSuppliers(page, false);
            }
        }
    };

    const handleSearch = (e) => {
        e.preventDefault();
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
            case 'document':
                searchByDocument(searchTerm.trim());
                break;
            case 'name':
                searchByName(searchTerm.trim(), 0, true);
                break;
            default:
                break;
        }
    };

    const clearSearch = () => {
        setSearchTerm('');
        setSearching(false);
        setSuppliers([]);
        setPage(0);
        setTotalPages(1);
        loadSuppliers(0, true);
    };

    useEffect(() => {
        loadSuppliers(0, true);
    }, []);

    return (
        <div className="supplier-page-container">
            <header>
                <img src={logoImage} alt="ERP Logo" />
                <span>Welcome, <strong>{getDisplayName(fullName).toUpperCase()} </strong></span>
                <Link className="supplier-page-add-button" to="/suppliers/new/0">Add New Supplier</Link>
                <button className="supplier-page-logout-btn" onClick={logout} type="button">
                    <FiPower size={18} color="#6A5ACD" />
                </button>
            </header>

            <div className="supplier-page-title-row">
                <Link to="/dashboard" className="supplier-page-back-button">
                    <FiArrowLeft size={24} />
                </Link>
                <h1>Registered Suppliers</h1>
            </div>

            <div className="supplier-page-search-section">
                <form onSubmit={handleSearch} className="supplier-page-search-form">
                    <select 
                        value={searchType} 
                        onChange={e => setSearchType(e.target.value)}
                        className="supplier-page-search-type-select"
                    >
                        <option value="name">Search by Name</option>
                        <option value="id">Search by Code (ID)</option>
                        <option value="document">Search by Document (CNPJ/CPF)</option>
                    </select>
                    
                    <input
                        type={searchType === 'id' ? 'number' : 'text'}
                        min={searchType === 'id' ? 1 : undefined}
                        placeholder={
                            searchType === 'name' ? 'Enter supplier name...' :
                            searchType === 'id' ? 'Enter supplier code (ID)...' :
                            'Enter CNPJ/CPF...'
                        }
                        value={searchTerm}
                        onChange={e => setSearchTerm(e.target.value)}
                    />
                    
                    <button type="submit" className="supplier-page-search-btn">
                        <FiSearch size={16} /> Search
                    </button>
                    
                    {searching && (
                        <button type="button" className="supplier-page-clear-btn" onClick={clearSearch}>
                            Clear
                        </button>
                    )}
                </form>
            </div>

            {suppliers.length > 0 && (
                <ul>
                    {suppliers.map(supplier => (
                        <li key={supplier.id}>
                            <div className="supplier-page-header">
                                <strong className="supplier-page-code">Code: #{supplier.id}</strong>
                                <span className={`supplier-page-status ${supplier.active ? 'active' : 'inactive'}`}>
                                    {supplier.active ? 'ACTIVE' : 'INACTIVE'}
                                </span>
                            </div>
                            
                            <strong>Name:</strong>
                            <p>{supplier.name}</p>
                            
                            <strong>Document (CNPJ/CPF):</strong>
                            <p>{supplier.document}</p>
                            
                            <strong>Email:</strong>
                            <p>{supplier.email || '-'}</p>
                            
                            <strong>Phone:</strong>
                            <p>{supplier.phone || '-'}</p>
                            
                            <strong>Address:</strong>
                            <p>
                                {supplier.street ? `${supplier.street}, ${supplier.streetNumber || 's/n'}` : '-'}
                                {supplier.city && supplier.state ? ` - ${supplier.city}/${supplier.state}` : ''}
                            </p>
                            
                            <div className="supplier-page-action-buttons">
                                <button onClick={() => toggleActive(supplier)} title={supplier.active ? 'Deactivate' : 'Activate'}>
                                    {supplier.active ? <FiUserX size={20} color="#FF4444" /> : <FiUserCheck size={20} color="#00AA00" />}
                                </button>
                                <button onClick={() => navigate(`/suppliers/new/${supplier.id}`)}>
                                    <FiEdit size={20} color="#6A5ACD" />
                                </button>
                                <button onClick={() => deleteSupplier(supplier.id)}>
                                    <FiTrash2 size={20} color="#6A5ACD" />
                                </button>
                            </div>
                        </li>
                    ))}
                </ul>
            )}

            {!searching && page < totalPages && suppliers.length > 0 && (
                <button className="supplier-page-load-more" onClick={loadMore} disabled={loading}>
                    {loading ? 'Loading...' : 'Load More'}
                </button>
            )}

            {searching && searchType === 'name' && page < totalPages && suppliers.length > 0 && (
                <button className="supplier-page-load-more" onClick={loadMore} disabled={loading}>
                    {loading ? 'Loading...' : 'Load More'}
                </button>
            )}

            {suppliers.length === 0 && !loading && (
                <p className="supplier-page-no-results">
                    {searching ? `No suppliers found for "${searchTerm}".` : 'No suppliers found.'}
                </p>
            )}
            
            {loading && suppliers.length === 0 && (
                <p className="supplier-page-no-results">Loading...</p>
            )}
        </div>
    );
}