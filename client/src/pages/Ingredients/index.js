// pages/Ingredients/index.js
import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FiPower, FiEdit, FiTrash2, FiPackage, FiDollarSign, FiSearch, FiCheckCircle, FiXCircle, FiArrowLeft, FiAlertTriangle } from 'react-icons/fi';
import './styles.css';
import api from '../../services/api';
import logoImage from '../../assets/logoerp.png';

export default function Ingredients() {
    const [ingredients, setIngredients] = useState([]);
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

    async function deleteIngredient(id) {
        if (window.confirm('Are you sure you want to delete this ingredient?')) {
            try {
                await api.delete(`api/ingredient/v1/${id}`);
                setIngredients(ingredients.filter(i => i.id !== id));
                alert('Ingredient deleted successfully!');
            } catch (err) {
                console.error(err);
                const errorMessage = err.response?.data?.message || err.message;
                
                if (errorMessage.includes('violates foreign key constraint') || 
                    errorMessage.includes('fk_purchase_items_ingredient')) {
                    alert('❌ Deletion not allowed!\n\nThis ingredient cannot be deleted because there are PURCHASES linked to it.');
                } else {
                    alert('Error deleting! Try again.');
                }
            }
        }
    }

    async function toggleActive(ingredient) {
        try {
            const endpoint = ingredient.active
                ? `api/ingredient/v1/deactivate/${ingredient.id}`
                : `api/ingredient/v1/activate/${ingredient.id}`;
            
            const response = await api.patch(endpoint);
            setIngredients(ingredients.map(i => i.id === ingredient.id ? response.data : i));
            alert(`Ingredient ${ingredient.active ? 'deactivated' : 'activated'} successfully!`);
        } catch (err) {
            console.error(err);
            alert('Operation failed! Try again.');
        }
    }

    const extractIngredientList = (responseData) => {
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
            const response = await api.get(`api/ingredient/v1/${id}`);
            setIngredients([response.data]);
            setTotalPages(1);
            setPage(0);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 404) {
                alert('Ingredient not found!');
                setIngredients([]);
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
            const response = await api.get(`/api/ingredient/v1/findByName/${encodeURIComponent(name)}`, {
                params: { page: currentPage, size: 10, direction: 'asc' }
            });
            
            const ingredientList = extractIngredientList(response.data);
            const total = response.data?.page?.totalPages || 1;
            
            if (reset) {
                setIngredients(ingredientList);
                setPage(1);
            } else {
                setIngredients(prev => [...prev, ...ingredientList]);
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

    async function loadIngredients(currentPage = 0, reset = true) {
        setLoading(true);
        
        try {
            const response = await api.get('/api/ingredient/v1', {
                params: { page: currentPage, size: 10, direction: 'asc' }
            });
            
            const ingredientList = extractIngredientList(response.data);
            const total = response.data?.page?.totalPages || 1;
            
            if (reset) {
                setIngredients(ingredientList);
                setPage(1);
            } else {
                setIngredients(prev => [...prev, ...ingredientList]);
                setPage(prev => prev + 1);
            }
            setTotalPages(total);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 401) logout();
            alert('Error loading ingredients!');
        } finally {
            setLoading(false);
        }
    }

    const loadMore = () => {
        if (!loading && page < totalPages) {
            if (searching && searchType === 'name') {
                searchByName(searchTerm, page, false);
            } else if (!searching) {
                loadIngredients(page, false);
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
        setIngredients([]);
        setPage(0);
        setTotalPages(1);
        loadIngredients(0, true);
    };

    useEffect(() => {
        loadIngredients(0, true);
    }, []);

    const formatPrice = (price) => {
        return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(price);
    };

    const isLowStock = (ingredient) => {
        return ingredient.stockQuantity <= ingredient.minimumStock;
    };

    const isCriticalStock = (ingredient) => {
        return ingredient.stockQuantity <= (ingredient.minimumStock * 0.2);
    };

    return (
        <div className="ingredient-page-container">
            <header>
                <img src={logoImage} alt="ERP Logo" />
                <span>Welcome, <strong>{getDisplayName(fullName).toUpperCase()} </strong></span>
                <Link className="ingredient-page-add-button" to="/ingredients/new/0">Add New Ingredient</Link>
                <button className="ingredient-page-logout-btn" onClick={logout} type="button">
                    <FiPower size={18} color="#5aa19e" />
                </button>
            </header>

            <div className="ingredient-page-title-row">
                <Link to="/dashboard" className="ingredient-page-back-button">
                    <FiArrowLeft size={24} />
                </Link>
                <h1>Ingredients Catalog</h1>
            </div>

            <div className="ingredient-page-search-section">
                <form onSubmit={handleSearch} className="ingredient-page-search-form">
                    <select 
                        value={searchType} 
                        onChange={e => setSearchType(e.target.value)}
                        className="ingredient-page-search-type-select"
                    >
                        <option value="name">Search by Name</option>
                        <option value="id">Search by Code (ID)</option>
                    </select>
                    
                    <input
                        type={searchType === 'id' ? 'number' : 'text'}
                        min={searchType === 'id' ? 1 : undefined}
                        placeholder={searchType === 'name' ? 'Enter ingredient name...' : 'Enter ingredient code (ID)...'}
                        value={searchTerm}
                        onChange={e => setSearchTerm(e.target.value)}
                    />
                    
                    <button type="submit" className="ingredient-page-search-btn">
                        <FiSearch size={16} /> Search
                    </button>
                    
                    {searching && (
                        <button type="button" className="ingredient-page-clear-btn" onClick={clearSearch}>
                            Clear
                        </button>
                    )}
                </form>
            </div>

            {ingredients.length > 0 && (
                <ul>
                    {ingredients.map(ingredient => (
                        <li key={ingredient.id} className={isLowStock(ingredient) ? 'low-stock' : ''}>
                            <div className="ingredient-page-header">
                                <strong className="ingredient-page-code">Code: #{ingredient.id}</strong>
                                <span className={`ingredient-page-status ${ingredient.active ? 'active' : 'inactive'}`}>
                                    {ingredient.active ? <FiCheckCircle size={14} /> : <FiXCircle size={14} />}
                                    {ingredient.active ? 'ACTIVE' : 'INACTIVE'}
                                </span>
                            </div>
                            
                            <strong>Name:</strong>
                            <p>{ingredient.name}</p>
                            
                            <strong>Description:</strong>
                            <p className="description">{ingredient.description || 'No description'}</p>
                            
                            <div className="ingredient-page-price-stock">
                                <div>
                                    <strong>Purchase Price:</strong>
                                    <p className="ingredient-page-price">{formatPrice(ingredient.purchasePrice)}</p>
                                </div>
                                <div>
                                    <strong>Unit:</strong>
                                    <p>{ingredient.unitOfMeasure}</p>
                                </div>
                                <div>
                                    <strong>Stock / Minimum:</strong>
                                    <p className={isLowStock(ingredient) ? 'ingredient-page-low-stock' : ''}>
                                        {ingredient.stockQuantity} / {ingredient.minimumStock}
                                        {isLowStock(ingredient) && (
                                            <span className="stock-warning">
                                                {isCriticalStock(ingredient) ? ' ⚠️ CRITICAL!' : ' ⚠️ Low stock!'}
                                            </span>
                                        )}
                                    </p>
                                </div>
                            </div>
                            
                            {ingredient.preferredSupplierName && (
                                <div className="ingredient-page-supplier">
                                    <strong>Supplier:</strong>
                                    <p>{ingredient.preferredSupplierName}</p>
                                </div>
                            )}
                            
                            <div className="ingredient-page-action-buttons">
                                <button onClick={() => toggleActive(ingredient)} title={ingredient.active ? 'Deactivate' : 'Activate'}>
                                    {ingredient.active ? <FiXCircle size={20} color="#FF4444" /> : <FiCheckCircle size={20} color="#00AA00" />}
                                </button>
                                <button onClick={() => navigate(`/ingredients/new/${ingredient.id}`)}>
                                    <FiEdit size={20} color="#5aa19e" />
                                </button>
                                <button onClick={() => deleteIngredient(ingredient.id)}>
                                    <FiTrash2 size={20} color="#5aa19e" />
                                </button>
                            </div>
                        </li>
                    ))}
                </ul>
            )}

            {!searching && page < totalPages && ingredients.length > 0 && (
                <button className="ingredient-page-load-more" onClick={loadMore} disabled={loading}>
                    {loading ? 'Loading...' : 'Load More'}
                </button>
            )}

            {searching && searchType === 'name' && page < totalPages && ingredients.length > 0 && (
                <button className="ingredient-page-load-more" onClick={loadMore} disabled={loading}>
                    {loading ? 'Loading...' : 'Load More'}
                </button>
            )}

            {ingredients.length === 0 && !loading && (
                <p className="ingredient-page-no-results">
                    {searching ? `No ingredients found for "${searchTerm}".` : 'No ingredients found.'}
                </p>
            )}
            
            {loading && ingredients.length === 0 && (
                <p className="ingredient-page-no-results">Loading...</p>
            )}
        </div>
    );
}