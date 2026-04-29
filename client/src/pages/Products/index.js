// pages/Products/index.js
import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FiPower, FiEdit, FiTrash2, FiPackage, FiDollarSign, FiSearch, FiCheckCircle, FiXCircle, FiArrowLeft } from 'react-icons/fi';
import './styles.css';
import api from '../../services/api';
import logoImage from '../../assets/logoerp.png';

export default function Products() {
    const [products, setProducts] = useState([]);
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

    async function deleteProduct(id) {
        if (window.confirm('Are you sure you want to delete this product?')) {
            try {
                await api.delete(`api/product/v1/${id}`);
                setProducts(products.filter(p => p.id !== id));
                alert('Product deleted successfully!');
            }catch (err) {
            console.error(err);
            
            // Verifica se o erro é de violação de chave estrangeira
            const errorMessage = err.response?.data?.message || err.message;
            
            if (errorMessage.includes('violates foreign key constraint') || 
                errorMessage.includes('fk_orders_customer') ||
                errorMessage.includes('fk_purchases_customer')) {
                
                alert('❌ Exclusão não permitida \n\n Este produto não pode ser excluído pois existem PEDIDOS ou COMPRAS vinculados a ele.');
            } else {
                alert('Erro ao excluir! Tente novamente.');
            }
            }
        }
    }

    async function toggleActive(product) {
        try {
            const endpoint = product.active
                ? `api/product/v1/deactivate/${product.id}`
                : `api/product/v1/activate/${product.id}`;
            
            const response = await api.patch(endpoint);
            setProducts(products.map(p => p.id === product.id ? response.data : p));
            alert(`Product ${product.active ? 'deactivated' : 'activated'} successfully!`);
        } catch (err) {
            console.error(err);
            alert('Operation failed! Try again.');
        }
    }

    const extractProductList = (responseData) => {
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
            const response = await api.get(`api/product/v1/${id}`);
            setProducts([response.data]);
            setTotalPages(1);
            setPage(0);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 404) {
                alert('Product not found!');
                setProducts([]);
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
            const response = await api.get(`/api/product/v1/findByName/${encodeURIComponent(name)}`, {
                params: { page: currentPage, size: 10, direction: 'asc' }
            });
            
            const productList = extractProductList(response.data);
            const total = response.data?.page?.totalPages || 1;
            
            if (reset) {
                setProducts(productList);
                setPage(1);
            } else {
                setProducts(prev => [...prev, ...productList]);
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

    async function loadProducts(currentPage = 0, reset = true) {
        setLoading(true);
        
        try {
            const response = await api.get('/api/product/v1', {
                params: { page: currentPage, size: 10, direction: 'asc' }
            });
            
            const productList = extractProductList(response.data);
            const total = response.data?.page?.totalPages || 1;
            
            if (reset) {
                setProducts(productList);
                setPage(1);
            } else {
                setProducts(prev => [...prev, ...productList]);
                setPage(prev => prev + 1);
            }
            setTotalPages(total);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 401) logout();
            alert('Error loading products!');
        } finally {
            setLoading(false);
        }
    }

    const loadMore = () => {
        if (!loading && page < totalPages) {
            if (searching && searchType === 'name') {
                searchByName(searchTerm, page, false);
            } else if (!searching) {
                loadProducts(page, false);
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
        
        if (searchType === 'id') {
            searchById(searchTerm.trim());
        } else {
            searchByName(searchTerm.trim(), 0, true);
        }
    };

    const clearSearch = () => {
        setSearchTerm('');
        setSearching(false);
        setProducts([]);
        setPage(0);
        setTotalPages(1);
        loadProducts(0, true);
    };

    useEffect(() => {
        loadProducts(0, true);
    }, []);

    const formatPrice = (price) => {
        return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(price);
    };

    return (
        <div className="product-page-container">
            <header>
                <img src={logoImage} alt="ERP Logo" />
                <span>Welcome, <strong>{getDisplayName(fullName).toUpperCase()} </strong></span>
                <Link className="product-page-add-button" to="/products/new/0">Add New Product</Link>
                <button className="product-page-logout-btn" onClick={logout} type="button">
                    <FiPower size={18} color="#FF8C00" />
                </button>
            </header>

            <div className="product-page-title-row">
                <Link to="/dashboard" className="product-page-back-button">
                    <FiArrowLeft size={24} />
                </Link>
                <h1>Products Catalog</h1>
            </div>

            <div className="product-page-search-section">
                <form onSubmit={handleSearch} className="product-page-search-form">
                    <select 
                        value={searchType} 
                        onChange={e => setSearchType(e.target.value)}
                        className="product-page-search-type-select"
                    >
                        <option value="name">Search by Name</option>
                        <option value="id">Search by Code (ID)</option>
                    </select>
                    
                    <input
                        type={searchType === 'id' ? 'number' : 'text'}
                        min={searchType === 'id' ? 1 : undefined}
                        placeholder={searchType === 'name' ? 'Enter product name...' : 'Enter product code (ID)...'}
                        value={searchTerm}
                        onChange={e => setSearchTerm(e.target.value)}
                    />
                    
                    <button type="submit" className="product-page-search-btn">
                        <FiSearch size={16} /> Search
                    </button>
                    
                    {searching && (
                        <button type="button" className="product-page-clear-btn" onClick={clearSearch}>
                            Clear
                        </button>
                    )}
                </form>
            </div>

            {products.length > 0 && (
                <ul>
                    {products.map(product => (
                        <li key={product.id}>
                            <div className="product-page-header">
                                <strong className="product-page-code">Code: #{product.id}</strong>
                                <span className={`product-page-status ${product.active ? 'active' : 'inactive'}`}>
                                    {product.active ? <FiCheckCircle size={14} /> : <FiXCircle size={14} />}
                                    {product.active ? 'ACTIVE' : 'INACTIVE'}
                                </span>
                            </div>
                            
                            <strong>Name:</strong>
                            <p>{product.name}</p>
                            
                            <strong>Description:</strong>
                            <p className="description">{product.description || 'No description'}</p>
                            
                            <div className="product-page-price-stock">
                                <div>
                                    <strong>Price:</strong>
                                    <p className="product-page-price">{formatPrice(product.price)}</p>
                                </div>
                                <div>
                                    <strong>Stock:</strong>
                                    <p className={product.stockQuantity <= 5 ? 'product-page-low-stock' : ''}>
                                        {product.stockQuantity} units
                                    </p>
                                </div>
                            </div>
                            
                            <div className="product-page-action-buttons">
                                <button onClick={() => toggleActive(product)} title={product.active ? 'Deactivate' : 'Activate'}>
                                    {product.active ? <FiXCircle size={20} color="#FF4444" /> : <FiCheckCircle size={20} color="#00AA00" />}
                                </button>
                                <button onClick={() => navigate(`/products/new/${product.id}`)}>
                                    <FiEdit size={20} color="#FF8C00" />
                                </button>
                                <button onClick={() => deleteProduct(product.id)}>
                                    <FiTrash2 size={20} color="#FF8C00" />
                                </button>
                            </div>
                        </li>
                    ))}
                </ul>
            )}

            {!searching && page < totalPages && products.length > 0 && (
                <button className="product-page-load-more" onClick={loadMore} disabled={loading}>
                    {loading ? 'Loading...' : 'Load More'}
                </button>
            )}

            {searching && searchType === 'name' && page < totalPages && products.length > 0 && (
                <button className="product-page-load-more" onClick={loadMore} disabled={loading}>
                    {loading ? 'Loading...' : 'Load More'}
                </button>
            )}

            {products.length === 0 && !loading && (
                <p className="product-page-no-results">
                    {searching ? `No products found for "${searchTerm}".` : 'No products found.'}
                </p>
            )}
            
            {loading && products.length === 0 && (
                <p className="product-page-no-results">Loading...</p>
            )}
        </div>
    );
}