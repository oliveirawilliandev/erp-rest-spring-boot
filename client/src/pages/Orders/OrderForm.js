// pages/Orders/OrderForm.js - VERSÃO CORRIGIDA (todos NOT NULL validados)
import React, { useState, useEffect } from 'react';
import { useNavigate, Link, useParams } from 'react-router-dom';
import { FiArrowLeft, FiPlus, FiTrash2, FiUser, FiBriefcase, FiPackage, FiDollarSign, FiShoppingCart, FiInfo } from 'react-icons/fi';
import api from '../../services/api';
import './OrderForm.css';

export default function OrderForm() {
    const [id, setId] = useState(null);
    const [customerId, setCustomerId] = useState('');
    const [employeeId, setEmployeeId] = useState('');
    const [items, setItems] = useState([]);
    const [currentProduct, setCurrentProduct] = useState({ productId: '', quantity: 1 });
    const [products, setProducts] = useState([]);
    const [customers, setCustomers] = useState([]);
    const [employees, setEmployees] = useState([]);
    const [loading, setLoading] = useState(false);
    const [viewOnly, setViewOnly] = useState(false);
    const [orderData, setOrderData] = useState(null);

    const navigate = useNavigate();
    
    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (!token || token === 'undefined' || token === 'null') {
            navigate('/');
        }
        window.scrollTo(0, 0); 
    }, [navigate]);
    
    const { id: orderId } = useParams();

    const extractEmployeeList = (responseData) => {
        if (Array.isArray(responseData)) return responseData;
        if (responseData?._embedded) {
            const embeddedKeys = Object.keys(responseData._embedded);
            if (embeddedKeys.length > 0) return responseData._embedded[embeddedKeys[0]];
        }
        if (responseData?.content) return responseData.content;
        return [];
    };

    const extractCustomerList = (responseData) => {
        if (Array.isArray(responseData)) return responseData;
        if (responseData?._embedded) {
            const embeddedKeys = Object.keys(responseData._embedded);
            if (embeddedKeys.length > 0) return responseData._embedded[embeddedKeys[0]];
        }
        if (responseData?.content) return responseData.content;
        return [];
    };

    const extractProductList = (responseData) => {
        if (Array.isArray(responseData)) return responseData;
        if (responseData?._embedded) {
            const embeddedKeys = Object.keys(responseData._embedded);
            if (embeddedKeys.length > 0) return responseData._embedded[embeddedKeys[0]];
        }
        if (responseData?.content) return responseData.content;
        return [];
    };

    async function loadOrder() {
        try {
            const response = await api.get(`api/order/v1/${orderId}`);
            const o = response.data;
            setId(o.id);
            setCustomerId(o.customerId ? o.customerId.toString() : '');
            setEmployeeId(o.employeeId ? o.employeeId.toString() : '');
            
            const processedItems = (o.items || []).map(item => ({
                productId: item.productId,
                productName: item.product?.name || `Product #${item.productId}`,
                quantity: item.quantity,
                unitPrice: item.unitPrice
            }));
            
            setItems(processedItems);
            setViewOnly(true);
            setOrderData(o);
            
        } catch (error) {
            console.error(error);
            alert("Error loading order!");
            navigate('/orders');
        }
    }

    async function loadProducts() {
        try {
            const response = await api.get('/api/product/v1', { params: { page: 0, size: 100 } });
            const productList = extractProductList(response.data);
            const activeProducts = productList.filter(p => p.active === true && p.stockQuantity > 0);
            setProducts(activeProducts);
        } catch (error) {
            console.error('Error loading products:', error);
        }
    }

    async function loadCustomers() {
        try {
            const response = await api.get('/api/customer/v1', { params: { page: 0, size: 100 } });
            const customerList = extractCustomerList(response.data);
            const activeCustomers = customerList.filter(c => c.active === true);
            setCustomers(activeCustomers);
        } catch (error) {
            console.error('Error loading customers:', error);
        }
    }

    async function loadEmployees() {
        try {
            const response = await api.get('/api/employee/v1', { params: { page: 0, size: 100 } });
            const employeeList = extractEmployeeList(response.data);
            const activeEmployees = employeeList.filter(e => e.active === true);
            setEmployees(activeEmployees);
        } catch (error) {
            console.error('Error loading employees:', error);
        }
    }

    useEffect(() => {
        loadProducts();
        loadCustomers();
        loadEmployees();
        if (orderId !== '0') {
            loadOrder();
        }
    }, [orderId]);

    const addItem = () => {
        // Validar se produto foi selecionado
        if (!currentProduct.productId) {
            alert('Please select a product');
            return;
        }

        // Validar quantidade
        const quantity = parseInt(currentProduct.quantity);
        if (isNaN(quantity) || quantity <= 0) {
            alert('Please enter a valid quantity (greater than 0)');
            return;
        }

        const product = products.find(p => p.id === parseInt(currentProduct.productId));
        if (!product) {
            alert('Selected product not found');
            return;
        }

        // Verificar se produto já foi adicionado
        const existingItem = items.find(item => item.productId === product.id);
        if (existingItem) {
            alert('Product already added! Remove the existing item first if you want to change quantity.');
            return;
        }

        // Verificar estoque disponível
        if (product.stockQuantity < quantity) {
            alert(`Insufficient stock! Available: ${product.stockQuantity} ${product.unitOfMeasure || 'units'}`);
            return;
        }

        setItems([...items, {
            productId: product.id,
            productName: product.name,
            quantity: quantity,
            unitPrice: product.price
        }]);

        setCurrentProduct({ productId: '', quantity: 1 });
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
        if (!customerId) {
            alert('Please select a customer (required field)');
            return;
        }
        
        if (!employeeId) {
            alert('Please select an employee (required field)');
            return;
        }
        
        if (items.length === 0) {
            alert('Please add at least one product to the order (required)');
            return;
        }

        setLoading(true);

        const data = {
            customerId: parseInt(customerId),
            employeeId: parseInt(employeeId),
            items: items.map(item => ({
                productId: item.productId,
                quantity: item.quantity
            }))
        };

        try {
            const response = await api.post('api/order/v1', data);
            alert(`Order created successfully! Total: ${formatPrice(response.data?.totalAmount || calculateTotal())}`);
            navigate('/orders');
        } catch (err) {
            console.error(err);
            const message = err.response?.data?.message || "Error creating order!";
            alert(message);
        } finally {
            setLoading(false);
        }
    }

    const formatPrice = (price) => {
        return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(price);
    };

    const getCustomerName = (id) => {
        const customer = customers.find(c => c.id === parseInt(id));
        return customer ? customer.name : `Customer #${id}`;
    };

    const getEmployeeName = (id) => {
        const employee = employees.find(e => e.id === parseInt(id));
        return employee ? `${employee.firstName} ${employee.lastName}` : `Employee #${id}`;
    };

    // VIEW ONLY - Visualização do pedido (para pedidos existentes)
    if (orderId !== '0' && viewOnly) {
        const totalValue = orderData?.totalAmount || calculateTotal();
        const displayItems = items.length > 0 ? items : [];
        
        return (
            <div className="order-form-container">
                <div className="order-form-content">
                    <section>
                        <h1>Order #{id}</h1>
                        <p>Order details (read-only)</p>
                        <Link className="order-form-back-link" to="/orders">
                            <FiArrowLeft size={16} color="#DC143C" />
                            Back to Orders
                        </Link>
                    </section>

                    <div className="order-form-details-card">
                        <div className="order-form-details-header">
                            <h2>
                                <FiShoppingCart size={20} style={{ marginRight: 8 }} />
                                Order Information
                            </h2>
                            {orderData?.status && (
                                <div className={`order-form-order-status order-form-status-${orderData.status.toLowerCase()}`}>
                                    {orderData.status}
                                </div>
                            )}
                        </div>
                        
                        <div className="order-form-info-grid">
                            <div className="order-form-info-item">
                                <FiUser className="order-form-info-icon" />
                                <div>
                                    <label>Customer <span className="required-star">*</span></label>
                                    <div className="order-form-info-value">
                                        <strong>ID: {customerId}</strong><br />
                                        <span>{orderData?.customer?.name || getCustomerName(customerId)}</span>
                                    </div>
                                </div>
                            </div>

                            <div className="order-form-info-item">
                                <FiBriefcase className="order-form-info-icon" />
                                <div>
                                    <label>Employee <span className="required-star">*</span></label>
                                    <div className="order-form-info-value">
                                        <strong>ID: {employeeId}</strong><br />
                                        <span>
                                            {orderData?.employee 
                                                ? `${orderData.employee.firstName} ${orderData.employee.lastName}`
                                                : getEmployeeName(employeeId)
                                            }
                                        </span>
                                    </div>
                                </div>
                            </div>

                            <div className="order-form-info-item">
                                <FiDollarSign className="order-form-info-icon" />
                                <div>
                                    <label>Total Amount <span className="required-star">*</span></label>
                                    <div className="order-form-info-value order-form-total-value">
                                        {formatPrice(totalValue)}
                                    </div>
                                </div>
                            </div>

                            <div className="order-form-info-item">
                                <FiInfo className="order-form-info-icon" />
                                <div>
                                    <label>Status <span className="required-star">*</span></label>
                                    <div className="order-form-info-value">
                                        {orderData?.status || 'PENDING'}
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="order-form-items-section">
                            <h3>
                                <FiPackage className="order-form-section-icon" />
                                Order Items
                            </h3>
                            
                            {displayItems.length === 0 ? (
                                <div className="order-form-no-items-message">
                                    <FiPackage size={48} color="#DC143C" />
                                    <p><strong>No items found for this order.</strong></p>
                                    {orderData?.status === 'CANCELLED' && (
                                        <p className="cancelled-note">This order was cancelled.</p>
                                    )}
                                    {orderData?.status !== 'CANCELLED' && (
                                        <p className="cancelled-note">This order has no products registered.</p>
                                    )}
                                </div>
                            ) : (
                                <div className="order-form-items-table">
                                    <div className="order-form-items-header">
                                        <span>Product</span>
                                        <span>Quantity</span>
                                        <span>Unit Price</span>
                                        <span>Subtotal</span>
                                    </div>
                                    {displayItems.map((item, idx) => (
                                        <div key={idx} className="order-form-item-row">
                                            <span className="order-form-product-name">{item.productName}</span>
                                            <span className="order-form-quantity">{item.quantity}</span>
                                            <span className="order-form-unit-price">{formatPrice(item.unitPrice)}</span>
                                            <span className="order-form-subtotal">{formatPrice(item.quantity * item.unitPrice)}</span>
                                        </div>
                                    ))}
                                    <div className="order-form-items-footer">
                                        <span></span>
                                        <span></span>
                                        <span className="order-form-total-label">Total:</span>
                                        <span className="order-form-grand-total">{formatPrice(totalValue)}</span>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    // CREATE NEW ORDER
    return (
        <div className="order-form-container">
            <div className="order-form-content">
                <section>
                    <h1>Create New Order</h1>
                    <p>Select customer, employee and products for the order!</p>
                    <Link className="order-form-back-link" to="/orders">
                        <FiArrowLeft size={16} color="#DC143C" />
                        Back to Orders
                    </Link>
                </section>

                <form onSubmit={handleSubmit}>
                    {/* Cliente e Funcionário - Ambos NOT NULL */}
                    <div className="order-form-row">
                        <div className="order-form-input-group">
                            <label>
                                <FiUser size={14} style={{ marginRight: 4 }} />
                                Customer <span className="required-star">*</span>
                            </label>
                            <select 
                                value={customerId} 
                                onChange={e => setCustomerId(e.target.value)} 
                                required
                            >
                                <option value="">Select Customer</option>
                                {customers.map(c => (
                                    <option key={c.id} value={c.id}>
                                        {c.name} - {c.document || 'No document'}
                                    </option>
                                ))}
                            </select>
                            <small className="input-hint">👤 Select the customer placing this order (required)</small>
                        </div>

                        <div className="order-form-input-group">
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
                            <small className="input-hint">👔 Select the employee responsible for this order (required)</small>
                        </div>
                    </div>

                    {/* Seção de Adicionar Produtos */}
                    <div className="order-form-add-item-section">
                        <h3>
                            <FiPackage size={16} style={{ marginRight: 6 }} />
                            Add Products
                        </h3>
                        <div className="order-form-add-item-row">
                            <div className="product-select">
                                <div className="form-group">
                                    <label>Product</label>
                                    <select 
                                        value={currentProduct.productId} 
                                        onChange={e => setCurrentProduct({ ...currentProduct, productId: e.target.value })}
                                    >
                                        <option value="">Select Product</option>
                                        {products.map(p => (
                                            <option key={p.id} value={p.id}>
                                                {p.name} - Stock: {p.stockQuantity} - {formatPrice(p.price)}
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
                                        value={currentProduct.quantity} 
                                        onChange={e => setCurrentProduct({ ...currentProduct, quantity: e.target.value })} 
                                        min="1" 
                                    />
                                </div>
                            </div>
                            <button type="button" className="order-form-add-btn" onClick={addItem}>
                                <FiPlus /> Add Item
                            </button>
                        </div>
                        <small className="input-hint" style={{ marginTop: 12, display: 'block' }}>
                            📦 Only products with stock available are shown
                        </small>
                    </div>

                    {/* Lista de Itens do Pedido */}
                    {items.length > 0 && (
                        <div className="order-form-items-list">
                            <h3>
                                <FiShoppingCart size={16} style={{ marginRight: 6 }} />
                                Order Items ({items.length})
                            </h3>
                            <div className="order-form-items-table">
                                <div className="order-form-items-header">
                                    <span>Product</span>
                                    <span>Quantity</span>
                                    <span>Unit Price</span>
                                    <span>Subtotal</span>
                                    <span>Actions</span>
                                </div>
                                {items.map((item, idx) => (
                                    <div key={idx} className="order-form-item-row">
                                        <span className="order-form-product-name">{item.productName}</span>
                                        <span className="order-form-quantity">{item.quantity}</span>
                                        <span className="order-form-unit-price">{formatPrice(item.unitPrice)}</span>
                                        <span className="order-form-subtotal">{formatPrice(item.quantity * item.unitPrice)}</span>
                                        <span className="actions">
                                            <button type="button" className="order-form-remove-btn" onClick={() => removeItem(idx)} title="Remove item">
                                                <FiTrash2 />
                                            </button>
                                        </span>
                                    </div>
                                ))}
                                <div className="order-form-items-footer">
                                    <span></span>
                                    <span></span>
                                    <span></span>
                                    <span className="order-form-total-label">Total:</span>
                                    <span className="order-form-grand-total">{formatPrice(calculateTotal())}</span>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Resumo do Pedido - Mensagem quando vazio */}
                    {items.length === 0 && (
                        <div className="order-form-empty-cart">
                            <FiShoppingCart size={48} color="#DC143C" opacity={0.5} />
                            <p>No items added yet</p>
                            <small>Select products and click "Add Item" to build your order (required)</small>
                        </div>
                    )}

                    <button 
                        className="order-form-submit-button" 
                        type="submit" 
                        disabled={loading || items.length === 0}
                    >
                        {loading ? 'Creating Order...' : 'Create Order'}
                    </button>
                </form>
            </div>
        </div>
    );
}