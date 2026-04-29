// pages/Products/ProductForm.js
import React, { useState, useEffect } from 'react';
import { useNavigate, Link, useParams } from 'react-router-dom';
import { FiArrowLeft, FiPackage, FiFileText, FiDollarSign, FiTrendingUp, FiBox, FiCheckSquare } from 'react-icons/fi';
import api from '../../services/api';
import './ProductForm.css';

export default function ProductForm() {
    const [id, setId] = useState(null);
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [price, setPrice] = useState('');
    const [startingPrice, setStartingPrice] = useState('');
    const [stockQuantity, setStockQuantity] = useState('');
    const [active, setActive] = useState(true);
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();
    
    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (!token || token === 'undefined' || token === 'null') {
            navigate('/');
        }
        window.scrollTo(0, 0); 
    }, [navigate]);
    
    const { id: productId } = useParams();

    async function loadProduct() {
        try {
            const response = await api.get(`api/product/v1/${productId}`);
            const p = response.data;
            setId(p.id);
            setName(p.name);
            setDescription(p.description || '');
            setPrice(p.price);
            setStartingPrice(p.startingPrice);
            setStockQuantity(p.stockQuantity);
            setActive(p.active);
        } catch (error) {
            console.error(error);
            alert("Error loading product!");
            navigate('/products');
        }
    }

    useEffect(() => {
        if (productId !== '0') {
            loadProduct();
        }
    }, [productId]);

    // Função para formatar preço como moeda brasileira
    const formatPriceToBRL = (value) => {
        if (!value && value !== 0) return '';
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(value);
    };

    // Função para converter string de moeda para número
    const parsePriceFromInput = (value) => {
        // Remove tudo que não é número ou vírgula/ponto
        let cleanValue = value.toString().replace(/[^\d,.-]/g, '');
        // Substitui vírgula por ponto para conversão
        cleanValue = cleanValue.replace(',', '.');
        const number = parseFloat(cleanValue);
        return isNaN(number) ? 0 : number;
    };

    function handlePriceChange(e) {
        const rawValue = e.target.value;
        // Permite digitação livre, converte ao perder foco
        setPrice(rawValue);
    }

    function handlePriceBlur() {
        if (price) {
            const numberValue = parsePriceFromInput(price);
            setPrice(numberValue.toString());
        }
    }

    function handleStartingPriceChange(e) {
        const rawValue = e.target.value;
        setStartingPrice(rawValue);
    }

    function handleStartingPriceBlur() {
        if (startingPrice) {
            const numberValue = parsePriceFromInput(startingPrice);
            setStartingPrice(numberValue.toString());
        }
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setLoading(true);

        // Validar campos obrigatórios baseado no schema do banco
        const requiredFields = [
            { field: name, name: 'Product Name' },
            { field: price, name: 'Price' },
            { field: startingPrice, name: 'Starting Price' },
            { field: stockQuantity, name: 'Stock Quantity' }
        ];

        const missingFields = requiredFields.filter(f => !f.field && f.field !== 0);
        if (missingFields.length > 0) {
            alert(`Please fill in all required fields: ${missingFields.map(f => f.name).join(', ')}`);
            setLoading(false);
            return;
        }

        // Validar se os preços são números válidos
        const priceNumber = parseFloat(price);
        const startingPriceNumber = parseFloat(startingPrice);
        const stockQuantityNumber = parseInt(stockQuantity);

        if (isNaN(priceNumber) || priceNumber < 0) {
            alert('Price must be a valid number greater than or equal to 0');
            setLoading(false);
            return;
        }

        if (isNaN(startingPriceNumber) || startingPriceNumber < 0) {
            alert('Starting price must be a valid number greater than or equal to 0');
            setLoading(false);
            return;
        }

        if (isNaN(stockQuantityNumber) || stockQuantityNumber < 0) {
            alert('Stock quantity must be a valid number greater than or equal to 0');
            setLoading(false);
            return;
        }

        const data = {
            name,
            description: description || null,
            price: priceNumber,
            startingPrice: startingPriceNumber,
            stockQuantity: stockQuantityNumber,
            active
        };

        try {
            if (productId === '0') {
                await api.post('api/product/v1', data);
                alert('Product created successfully!');
            } else {
                data.id = id;
                await api.put('api/product/v1', data);
                alert('Product updated successfully!');
            }
            navigate('/products');
        } catch (err) {
            console.error(err);
            const message = err.response?.data?.message || "Error saving product!";
            alert(message);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="product-form-container">
            <div className="product-form-content">
                <section>
                    <h1>{productId === '0' ? 'Add New' : 'Update'} Product</h1>
                    <p>Enter product information and click {productId === '0' ? "'Add'" : "'Update'!"}</p>
                    <Link className="product-form-back-link" to="/products">
                        <FiArrowLeft size={16} color="#FF8C00" />
                        Back to Products
                    </Link>
                </section>

                <form onSubmit={handleSubmit}>
                    {/* Nome do Produto - NOT NULL */}
                    <div className="form-group">
                        <label>
                            <FiPackage size={14} style={{ marginRight: 4 }} />
                            Product Name <span className="required-star">*</span>
                        </label>
                        <input 
                            placeholder="Ex: Bolo de Chocolate" 
                            value={name} 
                            onChange={e => setName(e.target.value)} 
                            required 
                        />
                        <small className="input-hint">📦 Product name as it will appear in the catalog (required)</small>
                    </div>

                    {/* Descrição - Opcional no banco */}
                    <div className="form-group">
                        <label>
                            <FiFileText size={14} style={{ marginRight: 4 }} />
                            Description
                        </label>
                        <textarea 
                            placeholder="Detailed product description, features, specifications..." 
                            value={description} 
                            onChange={e => setDescription(e.target.value)} 
                            rows="4"
                        />
                        <small className="input-hint">📝 Describe the main features and benefits of the product (optional)</small>
                    </div>

                    {/* Preços - Ambos NOT NULL */}
                    <div className="prices-section">
                        <div className="prices-title">
                            <FiDollarSign size={16} color="#FF8C00" />
                            <span>Pricing Information</span>
                        </div>
                        
                        <div className="product-form-price-row">
                            <div className="form-group">
                                <label>
                                    <FiTrendingUp size={14} style={{ marginRight: 4 }} />
                                    Price <span className="required-star">*</span>
                                </label>
                                <input 
                                    placeholder="0.00" 
                                    type="number" 
                                    step="0.01" 
                                    min="0" 
                                    value={price} 
                                    onChange={handlePriceChange}
                                    onBlur={handlePriceBlur}
                                    required 
                                />
                                <small className="input-hint">💰 Normal selling price (required)</small>
                            </div>
                            <div className="form-group">
                                <label>
                                    <FiTrendingUp size={14} style={{ marginRight: 4 }} />
                                    Starting Price <span className="required-star">*</span>
                                </label>
                                <input 
                                    placeholder="0.00" 
                                    type="number" 
                                    step="0.01" 
                                    min="0" 
                                    value={startingPrice} 
                                    onChange={handleStartingPriceChange}
                                    onBlur={handleStartingPriceBlur}
                                    required 
                                />
                                <small className="input-hint">🏷️ Cost/initial price (required)</small>
                            </div>
                        </div>
                    </div>

                    {/* Estoque - NOT NULL */}
                    <div className="form-group">
                        <label>
                            <FiBox size={14} style={{ marginRight: 4 }} />
                            Stock Quantity <span className="required-star">*</span>
                        </label>
                        <input 
                            placeholder="0" 
                            type="number" 
                            min="0" 
                            value={stockQuantity} 
                            onChange={e => setStockQuantity(e.target.value)} 
                            required 
                        />
                        <small className="input-hint">📊 Number of units available in inventory (required)</small>
                    </div>

                    {/* Status Checkbox */}
                    <div className="status-checkbox">
                        <label className="status-label">
                            <input 
                                type="checkbox" 
                                checked={active} 
                                onChange={e => setActive(e.target.checked)} 
                            />
                            <span className={`status-text ${active ? 'active' : 'inactive'}`}>
                                <FiCheckSquare size={14} style={{ marginRight: 6 }} />
                                {active ? 'ACTIVE PRODUCT' : 'INACTIVE PRODUCT'}
                            </span>
                        </label>
                    </div>
                    <button className="product-form-button" type="submit" disabled={loading}>
                        {loading ? 'Saving...' : (productId === '0' ? 'Add Product' : 'Update Product')}
                    </button>
                </form>
            </div>
        </div>
    );
}