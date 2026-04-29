// pages/Ingredients/IngredientForm.js - VERSÃO CORRIGIDA (todos NOT NULL)
import React, { useState, useEffect } from 'react';
import { useNavigate, Link, useParams } from 'react-router-dom';
import { FiArrowLeft, FiPackage, FiFileText, FiDollarSign, FiBox, FiAlertTriangle, FiBarChart2, FiTruck, FiCheckSquare } from 'react-icons/fi';
import api from '../../services/api';
import './IngredientForm.css';

export default function IngredientForm() {
    const [id, setId] = useState(null);
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [purchasePrice, setPurchasePrice] = useState('');
    const [stockQuantity, setStockQuantity] = useState('0');
    const [minimumStock, setMinimumStock] = useState('0');
    const [unitOfMeasure, setUnitOfMeasure] = useState('');
    const [active, setActive] = useState(true);
    const [suppliers, setSuppliers] = useState([]);
    const [preferredSupplierId, setPreferredSupplierId] = useState('');
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();
    
    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (!token || token === 'undefined' || token === 'null') {
            navigate('/');
        }
        window.scrollTo(0, 0); 
    }, [navigate]);
    
    const { id: ingredientId } = useParams();

    // Carregar fornecedores para o select
    async function loadSuppliers() {
        try {
            const response = await api.get('/api/supplier/v1', { params: { page: 0, size: 100 } });
            const supplierList = extractSupplierList(response.data);
            const activeSuppliers = supplierList.filter(s => s.active);
            setSuppliers(activeSuppliers);
        } catch (error) {
            console.error(error);
        }
    }

    const extractSupplierList = (responseData) => {
        if (Array.isArray(responseData)) return responseData;
        if (responseData?._embedded) {
            const embeddedKeys = Object.keys(responseData._embedded);
            if (embeddedKeys.length > 0) return responseData._embedded[embeddedKeys[0]];
        }
        if (responseData?.content) return responseData.content;
        return [];
    };

    async function loadIngredient() {
        try {
            const response = await api.get(`api/ingredient/v1/${ingredientId}`);
            const i = response.data;
            setId(i.id);
            setName(i.name);
            setDescription(i.description || '');
            setPurchasePrice(i.purchasePrice);
            setStockQuantity(i.stockQuantity.toString());
            setMinimumStock(i.minimumStock.toString());
            setUnitOfMeasure(i.unitOfMeasure);
            setActive(i.active);
            setPreferredSupplierId(i.preferredSupplierId ? i.preferredSupplierId.toString() : '');
        } catch (error) {
            console.error(error);
            alert("Error loading ingredient!");
            navigate('/ingredients');
        }
    }

    useEffect(() => {
        loadSuppliers();
        if (ingredientId !== '0') {
            loadIngredient();
        }
    }, [ingredientId]);

    async function handleSubmit(e) {
        e.preventDefault();
        setLoading(true);

        // Validar campos obrigatórios baseado no schema do banco
        const requiredFields = [
            { field: name, name: 'Ingredient Name' },
            { field: purchasePrice, name: 'Purchase Price' },
            { field: stockQuantity, name: 'Stock Quantity' },
            { field: minimumStock, name: 'Minimum Stock' },
            { field: unitOfMeasure, name: 'Unit of Measure' },
            { field: preferredSupplierId, name: 'Preferred Supplier' }
        ];

        const missingFields = requiredFields.filter(f => !f.field && f.field !== 0);
        if (missingFields.length > 0) {
            alert(`Please fill in all required fields: ${missingFields.map(f => f.name).join(', ')}`);
            setLoading(false);
            return;
        }

        // Validar se o preço é um número válido
        const purchasePriceNumber = parseFloat(purchasePrice);
        if (isNaN(purchasePriceNumber) || purchasePriceNumber < 0) {
            alert('Purchase price must be a valid number greater than or equal to 0');
            setLoading(false);
            return;
        }

        // Validar estoque
        const stockQuantityNumber = parseInt(stockQuantity);
        if (isNaN(stockQuantityNumber) || stockQuantityNumber < 0) {
            alert('Stock quantity must be a valid number greater than or equal to 0');
            setLoading(false);
            return;
        }

        // Validar estoque mínimo
        const minimumStockNumber = parseInt(minimumStock);
        if (isNaN(minimumStockNumber) || minimumStockNumber < 0) {
            alert('Minimum stock must be a valid number greater than or equal to 0');
            setLoading(false);
            return;
        }

        // Validar se fornecedor foi selecionado
        if (!preferredSupplierId) {
            alert('Please select a preferred supplier');
            setLoading(false);
            return;
        }

        const data = {
            name,
            description: description || null,
            purchasePrice: purchasePriceNumber,
            stockQuantity: stockQuantityNumber,
            minimumStock: minimumStockNumber,
            unitOfMeasure,
            active,
            preferredSupplierId: parseInt(preferredSupplierId)
        };

        try {
            if (ingredientId === '0') {
                await api.post('api/ingredient/v1', data);
                alert('Ingredient created successfully!');
            } else {
                data.id = id;
                await api.put('api/ingredient/v1', data);
                alert('Ingredient updated successfully!');
            }
            navigate('/ingredients');
        } catch (err) {
            console.error(err);
            const message = err.response?.data?.message || "Error saving ingredient!";
            alert(message);
        } finally {
            setLoading(false);
        }
    }

    // Unidades de medida comuns para padaria
    const unitOptions = [
        { value: 'kg', label: 'Kilogram (kg)' },
        { value: 'g', label: 'Gram (g)' },
        { value: 'L', label: 'Liter (L)' },
        { value: 'mL', label: 'Milliliter (mL)' },
        { value: 'un', label: 'Unit (un)' },
        { value: 'pacote', label: 'Package' },
        { value: 'dúzia', label: 'Dozen' },
        { value: 'cx', label: 'Box' },
        { value: 'saco', label: 'Bag' },
        { value: 'fardo', label: 'Bale' }
    ];

    return (
        <div className="ingredient-form-container">
            <div className="ingredient-form-content">
                <section>
                    <h1>{ingredientId === '0' ? 'Add New' : 'Update'} Ingredient</h1>
                    <p>Enter ingredient information (raw materials for bakery) and click {ingredientId === '0' ? "'Add'" : "'Update'!"}</p>
                    <Link className="ingredient-form-back-link" to="/ingredients">
                        <FiArrowLeft size={16} color="#5aa19e" />
                        Back to Ingredients
                    </Link>
                </section>

                <form onSubmit={handleSubmit}>
                    {/* Nome do Ingrediente - NOT NULL */}
                    <div className="form-group">
                        <label>
                            <FiPackage size={14} style={{ marginRight: 4 }} />
                            Ingredient Name <span className="required-star">*</span>
                        </label>
                        <input 
                            placeholder="Ex: Wheat Flour, Sugar, Yeast, Butter..." 
                            value={name} 
                            onChange={e => setName(e.target.value)} 
                            required 
                        />
                        <small className="input-hint">🍞 Name of the raw material used in production (required)</small>
                    </div>

                    {/* Descrição - Opcional no banco */}
                    <div className="form-group">
                        <label>
                            <FiFileText size={14} style={{ marginRight: 4 }} />
                            Description
                        </label>
                        <textarea 
                            placeholder="Description, specifications, brand, etc." 
                            value={description} 
                            onChange={e => setDescription(e.target.value)} 
                            rows="3"
                        />
                        <small className="input-hint">📝 Additional details about the ingredient (optional)</small>
                    </div>

                    {/* Preço e Unidade - Ambos NOT NULL */}
                    <div className="ingredient-form-price-row">
                        <div className="form-group">
                            <label>
                                <FiDollarSign size={14} style={{ marginRight: 4 }} />
                                Purchase Price <span className="required-star">*</span>
                            </label>
                            <input 
                                placeholder="0.00" 
                                type="number" 
                                step="0.01" 
                                min="0" 
                                value={purchasePrice} 
                                onChange={e => setPurchasePrice(e.target.value)} 
                                required 
                            />
                            <small className="input-hint">💰 Cost per unit of measure (required)</small>
                        </div>
                        <div className="form-group">
                            <label>
                                <FiBarChart2 size={14} style={{ marginRight: 4 }} />
                                Unit of Measure <span className="required-star">*</span>
                            </label>
                            <select 
                                value={unitOfMeasure} 
                                onChange={e => setUnitOfMeasure(e.target.value)} 
                                required
                            >
                                <option value="">Select Unit *</option>
                                {unitOptions.map(unit => (
                                    <option key={unit.value} value={unit.value}>
                                        {unit.label}
                                    </option>
                                ))}
                            </select>
                            <small className="input-hint">📏 Measurement unit (kg, L, unit, etc.) - required</small>
                        </div>
                    </div>

                    {/* Estoque e Estoque Mínino - Ambos NOT NULL com DEFAULT 0 */}
                    <div className="ingredient-form-stock-row">
                        <div className="form-group">
                            <label>
                                <FiBox size={14} style={{ marginRight: 4 }} />
                                Current Stock <span className="required-star">*</span>
                            </label>
                            <input 
                                placeholder="0" 
                                type="number" 
                                min="0" 
                                value={stockQuantity} 
                                onChange={e => setStockQuantity(e.target.value)} 
                                required
                            />
                            <small className="input-hint">📦 Current quantity in inventory (required)</small>
                        </div>
                        <div className="form-group">
                            <label>
                                <FiAlertTriangle size={14} style={{ marginRight: 4 }} />
                                Minimum Stock (Alert) <span className="required-star">*</span>
                            </label>
                            <input 
                                placeholder="0" 
                                type="number" 
                                min="0" 
                                value={minimumStock} 
                                onChange={e => setMinimumStock(e.target.value)} 
                                required
                            />
                            <small className="input-hint">⚠️ Alert when stock falls below this level (required)</small>
                        </div>
                    </div>

                    {/* Fornecedor Preferencial - NOT NULL (chave estrangeira) */}
                    <div className="form-group">
                        <label>
                            <FiTruck size={14} style={{ marginRight: 4 }} />
                            Supplier <span className="required-star">*</span>
                        </label>
                        <select 
                            value={preferredSupplierId} 
                            onChange={e => setPreferredSupplierId(e.target.value)}
                            className="ingredient-form-supplier-select"
                            required
                        >
                            <option value="">Select Supplier *</option>
                            {suppliers.map(s => (
                                <option key={s.id} value={s.id}>
                                    {s.name} - {s.document}
                                </option>
                            ))}
                        </select>
                        <small className="input-hint">🏭 Main supplier for this ingredient (required)</small>
                    </div>

                    {/* Status Checkbox - DEFAULT TRUE */}
                    <div className="status-checkbox">
                        <label className="status-label">
                            <input 
                                type="checkbox" 
                                checked={active} 
                                onChange={e => setActive(e.target.checked)} 
                            />
                            <span className={`status-text ${active ? 'active' : 'inactive'}`}>
                                <FiCheckSquare size={14} style={{ marginRight: 6 }} />
                                {active ? 'ACTIVE INGREDIENT' : 'INACTIVE INGREDIENT'}
                            </span>
                        </label>
                    </div>

                    <button className="ingredient-form-button" type="submit" disabled={loading}>
                        {loading ? 'Saving...' : (ingredientId === '0' ? 'Add Ingredient' : 'Update Ingredient')}
                    </button>
                </form>
            </div>
        </div>
    );
}