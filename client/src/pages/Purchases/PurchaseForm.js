// pages/Purchases/PurchaseForm.js - VERSÃO CORRIGIDA (todos NOT NULL validados)
import React, { useState, useEffect } from 'react';
import { useNavigate, Link, useParams } from 'react-router-dom';
import { FiArrowLeft, FiPlus, FiTrash2, FiUser, FiBriefcase, FiPackage, FiDollarSign, FiTruck, FiShoppingCart, FiBox } from 'react-icons/fi';
import api from '../../services/api';
import './PurchaseForm.css';

export default function PurchaseForm() {
    const [id, setId] = useState(null);
    const [supplierId, setSupplierId] = useState('');
    const [employeeId, setEmployeeId] = useState('');
    const [items, setItems] = useState([]);
    const [currentIngredient, setCurrentIngredient] = useState({ ingredientId: '', quantity: 1, unitPrice: 0 });
    const [ingredients, setIngredients] = useState([]);
    const [suppliers, setSuppliers] = useState([]);
    const [employees, setEmployees] = useState([]);
    const [loading, setLoading] = useState(false);
    const [purchaseData, setPurchaseData] = useState(null);
    const [viewOnly, setViewOnly] = useState(false);

    const navigate = useNavigate();
    
    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (!token || token === 'undefined' || token === 'null') {
            navigate('/');
        }
        window.scrollTo(0, 0); 
    }, [navigate]);
    
    const { id: purchaseId } = useParams();

    const extractIngredientList = (responseData) => {
        if (Array.isArray(responseData)) return responseData;
        if (responseData?._embedded) {
            const embeddedKeys = Object.keys(responseData._embedded);
            if (embeddedKeys.length > 0) return responseData._embedded[embeddedKeys[0]];
        }
        if (responseData?.content) return responseData.content;
        return [];
    };

    const extractEmployeeList = (responseData) => {
        if (Array.isArray(responseData)) return responseData;
        if (responseData?._embedded) {
            const embeddedKeys = Object.keys(responseData._embedded);
            if (embeddedKeys.length > 0) return responseData._embedded[embeddedKeys[0]];
        }
        if (responseData?.content) return responseData.content;
        return [];
    };

    const extractSupplierList = (responseData) => {
        if (Array.isArray(responseData)) return responseData;
        if (responseData?._embedded) {
            const embeddedKeys = Object.keys(responseData._embedded);
            if (embeddedKeys.length > 0) return responseData._embedded[embeddedKeys[0]];
        }
        if (responseData?.content) return responseData.content;
        return [];
    };

    async function loadPurchase() {
        try {
            const response = await api.get(`api/purchase/v1/${purchaseId}`);
            const p = response.data;
            setId(p.id);
            setSupplierId(p.supplierId ? p.supplierId.toString() : '');
            setEmployeeId(p.employeeId ? p.employeeId.toString() : '');
            
            const processedItems = (p.items || []).map(item => ({
                ingredientId: item.ingredientId,
                ingredientName: item.ingredient?.name || `Ingredient #${item.ingredientId}`,
                quantity: item.quantity,
                unitPrice: item.unitPrice
            }));
            
            setItems(processedItems);
            setPurchaseData(p);
            setViewOnly(true);
        } catch (error) {
            console.error(error);
            alert("Error loading purchase!");
            navigate('/purchases');
        }
    }

    async function loadIngredients() {
        try {
            const response = await api.get('/api/ingredient/v1', { params: { page: 0, size: 100 } });
            const ingredientList = extractIngredientList(response.data);
            const activeIngredients = ingredientList.filter(i => i.active === true);
            setIngredients(activeIngredients);
        } catch (error) {
            console.error(error);
        }
    }

    async function loadSuppliers() {
        try {
            const response = await api.get('/api/supplier/v1', { params: { page: 0, size: 100 } });
            const supplierList = extractSupplierList(response.data);
            const activeSuppliers = supplierList.filter(s => s.active === true);
            setSuppliers(activeSuppliers);
        } catch (error) {
            console.error(error);
        }
    }

    async function loadEmployees() {
        try {
            const response = await api.get('/api/employee/v1', { params: { page: 0, size: 100 } });
            const employeeList = extractEmployeeList(response.data);
            const activeEmployees = employeeList.filter(e => e.active === true);
            setEmployees(activeEmployees);
        } catch (error) {
            console.error(error);
        }
    }

    useEffect(() => {
        loadIngredients();
        loadSuppliers();
        loadEmployees();
        if (purchaseId && purchaseId !== '0') {
            loadPurchase();
        }
    }, [purchaseId]);

    const handleIngredientSelect = (ingredientId) => {
        const ingredient = ingredients.find(i => i.id === parseInt(ingredientId));
        if (ingredient) {
            setCurrentIngredient({
                ingredientId: ingredient.id,
                quantity: 1,
                unitPrice: ingredient.purchasePrice || 0
            });
        } else {
            setCurrentIngredient({ ingredientId: '', quantity: 1, unitPrice: 0 });
        }
    };

    const addItem = () => {
        // Validar se ingrediente foi selecionado
        if (!currentIngredient.ingredientId) {
            alert('Please select an ingredient');
            return;
        }

        // Validar quantidade
        const quantity = parseInt(currentIngredient.quantity);
        if (isNaN(quantity) || quantity <= 0) {
            alert('Please enter a valid quantity (greater than 0)');
            return;
        }

        const ingredient = ingredients.find(i => i.id === currentIngredient.ingredientId);
        if (!ingredient) {
            alert('Selected ingredient not found');
            return;
        }

        // Verificar se ingrediente já foi adicionado
        const existingItem = items.find(item => item.ingredientId === ingredient.id);
        if (existingItem) {
            alert('Ingredient already added! Remove the existing item first if you want to change quantity.');
            return;
        }

        setItems([...items, {
            ingredientId: ingredient.id,
            ingredientName: ingredient.name,
            quantity: quantity,
            unitPrice: currentIngredient.unitPrice
        }]);

        setCurrentIngredient({ ingredientId: '', quantity: 1, unitPrice: 0 });
    };

    const removeItem = (index) => {
        setItems(items.filter((_, i) => i !== index));
    };

    const calculateTotal = () => {
        return items.reduce((sum, item) => sum + (item.quantity * item.unitPrice), 0);
    };

    async function handleSubmit(e) {
        e.preventDefault();
        
        // Validar campos obrigatórios baseado no schema do banco
        if (!supplierId) {
            alert('Please select a supplier (required field)');
            return;
        }
        
        if (!employeeId) {
            alert('Please select an employee (required field)');
            return;
        }
        
        if (items.length === 0) {
            alert('Please add at least one ingredient to the purchase (required)');
            return;
        }

        setLoading(true);

        const data = {
            supplierId: parseInt(supplierId),
            employeeId: parseInt(employeeId),
            items: items.map(item => ({
                ingredientId: item.ingredientId,
                quantity: item.quantity,
                unitPrice: item.unitPrice
            }))
        };

        try {
            const response = await api.post('api/purchase/v1', data);
            alert(`Purchase created successfully! Total: ${formatPrice(response.data?.totalAmount || calculateTotal())}`);
            navigate('/purchases');
        } catch (err) {
            console.error(err);
            const message = err.response?.data?.message || "Error creating purchase!";
            alert(message);
        } finally {
            setLoading(false);
        }
    }

    const formatPrice = (price) => {
        return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(price);
    };

    const getSupplierName = (id) => {
        const supplier = suppliers.find(s => s.id === parseInt(id));
        return supplier ? supplier.name : `Supplier #${id}`;
    };

    const getEmployeeName = (id) => {
        const employee = employees.find(e => e.id === parseInt(id));
        return employee ? `${employee.firstName} ${employee.lastName}` : `Employee #${id}`;
    };

    // Modo de visualização (para compras existentes)
    if (purchaseId && purchaseId !== '0' && viewOnly) {
        const totalValue = purchaseData?.totalAmount || calculateTotal();
        const displayItems = items.length > 0 ? items : [];
        
        return (
            <div className="purchase-form-container">
                <div className="purchase-form-content">
                    <section>
                        <h1>Purchase #{id}</h1>
                        <p>Purchase details (read-only)</p>
                        <Link className="purchase-form-back-link" to="/purchases">
                            <FiArrowLeft size={16} color="#20B2AA" />
                            Back to Purchases
                        </Link>
                    </section>

                    <div className="purchase-form-details-card">
                        <div className="purchase-form-details-header">
                            <h2>
                                <FiShoppingCart size={20} style={{ marginRight: 8 }} />
                                Purchase Information
                            </h2>
                            {purchaseData?.status && (
                                <div className={`purchase-form-purchase-status purchase-form-status-${purchaseData.status.toLowerCase()}`}>
                                    {purchaseData.status}
                                </div>
                            )}
                        </div>
                        
                        <div className="purchase-form-info-grid">
                            <div className="purchase-form-info-item">
                                <FiTruck className="purchase-form-info-icon" />
                                <div>
                                    <label>Supplier <span className="required-star">*</span></label>
                                    <div className="purchase-form-info-value">
                                        <strong>ID: {supplierId}</strong><br />
                                        <span>{purchaseData?.supplier?.name || getSupplierName(supplierId)}</span>
                                    </div>
                                </div>
                            </div>

                            <div className="purchase-form-info-item">
                                <FiBriefcase className="purchase-form-info-icon" />
                                <div>
                                    <label>Employee <span className="required-star">*</span></label>
                                    <div className="purchase-form-info-value">
                                        <strong>ID: {employeeId}</strong><br />
                                        <span>
                                            {purchaseData?.employee 
                                                ? `${purchaseData.employee.firstName} ${purchaseData.employee.lastName}`
                                                : getEmployeeName(employeeId)
                                            }
                                        </span>
                                    </div>
                                </div>
                            </div>

                            <div className="purchase-form-info-item">
                                <FiDollarSign className="purchase-form-info-icon" />
                                <div>
                                    <label>Total Amount <span className="required-star">*</span></label>
                                    <div className="purchase-form-info-value purchase-form-total-value">
                                        {formatPrice(totalValue)}
                                    </div>
                                </div>
                            </div>

                            <div className="purchase-form-info-item">
                                <FiBox className="purchase-form-info-icon" />
                                <div>
                                    <label>Status <span className="required-star">*</span></label>
                                    <div className="purchase-form-info-value">
                                        {purchaseData?.status || 'PENDING'}
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="purchase-form-items-section">
                            <h3>
                                <FiPackage size={16} style={{ marginRight: 8 }} />
                                Purchase Items (Ingredients)
                            </h3>
                            
                            {displayItems.length === 0 ? (
                                <div className="purchase-form-no-items-message">
                                    <FiBox size={48} color="#20B2AA" opacity={0.5} />
                                    <p><strong>No items found for this purchase.</strong></p>
                                    {purchaseData?.status === 'CANCELLED' && (
                                        <p className="cancelled-note">This purchase was cancelled.</p>
                                    )}
                                    {purchaseData?.status !== 'CANCELLED' && (
                                        <p className="cancelled-note">This purchase has no registered items.</p>
                                    )}
                                    {purchaseData?.status === 'PENDING' && (
                                        <p className="pending-note">This purchase is pending.</p>
                                    )}
                                </div>
                            ) : (
                                <div className="purchase-form-items-table">
                                    <div className="purchase-form-items-header">
                                        <span>Ingredient</span>
                                        <span>Quantity</span>
                                        <span>Unit Price</span>
                                        <span>Subtotal</span>
                                    </div>
                                    {displayItems.map((item, idx) => (
                                        <div key={idx} className="purchase-form-item-row">
                                            <span className="purchase-form-product-name">{item.ingredientName}</span>
                                            <span className="purchase-form-quantity">{item.quantity}</span>
                                            <span className="purchase-form-unit-price">{formatPrice(item.unitPrice)}</span>
                                            <span className="purchase-form-subtotal">{formatPrice(item.quantity * item.unitPrice)}</span>
                                        </div>
                                    ))}
                                    <div className="purchase-form-items-footer">
                                        <span></span>
                                        <span></span>
                                        <span className="purchase-form-total-label">Total:</span>
                                        <span className="purchase-form-grand-total">{formatPrice(totalValue)}</span>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    // Formulário de criação de nova compra
    return (
        <div className="purchase-form-container">
            <div className="purchase-form-content">
                <section>
                    <h1>Create New Purchase</h1>
                    <p>Select supplier, employee and ingredients for the purchase!</p>
                    <Link className="purchase-form-back-link" to="/purchases">
                        <FiArrowLeft size={16} color="#20B2AA" />
                        Back to Purchases
                    </Link>
                </section>

                <form onSubmit={handleSubmit}>
                    {/* Fornecedor e Funcionário - Ambos NOT NULL */}
                    <div className="purchase-form-row">
                        <div className="purchase-form-input-group">
                            <label>
                                <FiTruck size={14} style={{ marginRight: 4 }} />
                                Supplier <span className="required-star">*</span>
                            </label>
                            <select 
                                value={supplierId} 
                                onChange={e => setSupplierId(e.target.value)} 
                                required
                            >
                                <option value="">Select Supplier</option>
                                {suppliers.map(s => (
                                    <option key={s.id} value={s.id}>
                                        {s.name} - {s.document || 'No document'}
                                    </option>
                                ))}
                            </select>
                            <small className="input-hint">🚚 Select the supplier providing the ingredients (required)</small>
                        </div>

                        <div className="purchase-form-input-group">
                            <label>
                                <FiBriefcase size={14} style={{ marginRight: 4 }} />
                                Employee <span className="required-star">*</span>
                            </label>
                            <select 
                                value={employeeId} 
                                onChange={e => setEmployeeId(e.target.value)} 
                                required
                            >
                                <option value="">Select Employee</option>
                                {employees.map(e => (
                                    <option key={e.id} value={e.id}>
                                        {e.firstName} {e.lastName} - {e.jobTitle || 'Employee'}
                                    </option>
                                ))}
                            </select>
                            <small className="input-hint">👔 Select the employee responsible for this purchase (required)</small>
                        </div>
                    </div>

                    {/* Seção de Adicionar Ingredientes */}
                    <div className="purchase-form-add-item-section">
                        <h3>
                            <FiPackage size={16} style={{ marginRight: 6 }} />
                            Add Ingredients
                        </h3>
                        <div className="purchase-form-add-item-row">
                            <div className="product-select">
                                <div className="form-group">
                                    <label>Ingredient</label>
                                    <select 
                                        value={currentIngredient.ingredientId} 
                                        onChange={e => handleIngredientSelect(e.target.value)}
                                    >
                                        <option value="">Select Ingredient</option>
                                        {ingredients.map(i => (
                                            <option key={i.id} value={i.id}>
                                                {i.name} - Stock: {i.stockQuantity} {i.unitOfMeasure} - {formatPrice(i.purchasePrice)}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                            </div>
                            <div className="quantity-input">
                                <div className="form-group">
                                    <label>Quantity</label>
                                    <input 
                                        type="number" 
                                        placeholder="Quantity" 
                                        value={currentIngredient.quantity} 
                                        onChange={e => setCurrentIngredient({ ...currentIngredient, quantity: e.target.value })} 
                                        min="1" 
                                    />
                                </div>
                            </div>
                            <button type="button" className="purchase-form-add-btn" onClick={addItem}>
                                <FiPlus /> Add Item
                            </button>
                        </div>
                        <small className="input-hint" style={{ marginTop: 12, display: 'block' }}>
                            🥫 Only active ingredients are shown. Unit price is based on purchase price.
                        </small>
                    </div>

                    {/* Lista de Itens da Compra */}
                    {items.length > 0 && (
                        <div className="purchase-form-items-list">
                            <h3>
                                <FiShoppingCart size={16} style={{ marginRight: 6 }} />
                                Purchase Items ({items.length})
                            </h3>
                            <div className="purchase-form-items-table">
                                <div className="purchase-form-items-header">
                                    <span>Ingredient</span>
                                    <span>Quantity</span>
                                    <span>Unit Price</span>
                                    <span>Subtotal</span>
                                    <span>Actions</span>
                                </div>
                                {items.map((item, idx) => (
                                    <div key={idx} className="purchase-form-item-row">
                                        <span className="purchase-form-product-name">{item.ingredientName}</span>
                                        <span className="purchase-form-quantity">{item.quantity}</span>
                                        <span className="purchase-form-unit-price">{formatPrice(item.unitPrice)}</span>
                                        <span className="purchase-form-subtotal">{formatPrice(item.quantity * item.unitPrice)}</span>
                                        <span className="actions">
                                            <button type="button" className="purchase-form-remove-btn" onClick={() => removeItem(idx)} title="Remove item">
                                                <FiTrash2 />
                                            </button>
                                        </span>
                                    </div>
                                ))}
                                <div className="purchase-form-items-footer">
                                    <span></span>
                                    <span></span>
                                    <span></span>
                                    <span className="purchase-form-total-label">Total:</span>
                                    <span className="purchase-form-grand-total">{formatPrice(calculateTotal())}</span>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Carrinho Vazio */}
                    {items.length === 0 && (
                        <div className="purchase-form-empty-cart">
                            <FiShoppingCart size={48} color="#20B2AA" opacity={0.5} />
                            <p>No items added yet</p>
                            <small>Select ingredients and click "Add Item" to build your purchase order (required)</small>
                        </div>
                    )}

                    <button 
                        className="purchase-form-submit-button" 
                        type="submit" 
                        disabled={loading || items.length === 0}
                    >
                        {loading ? 'Creating Purchase...' : 'Create Purchase'}
                    </button>
                </form>
            </div>
        </div>
    );
}