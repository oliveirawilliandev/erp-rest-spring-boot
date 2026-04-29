// pages/Customers/CustomerForm.js
import React, { useState, useEffect } from 'react';
import { useNavigate, Link, useParams } from 'react-router-dom';
import { FiArrowLeft, FiUser, FiMail, FiPhone, FiFileText, FiMapPin, FiHome, FiMap, FiCheckSquare } from 'react-icons/fi';
import './CustomerForm.css';
import api from '../../services/api';

export default function CustomerForm() {
    const [id, setId] = useState(null);
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [phone, setPhone] = useState('');
    const [document, setDocument] = useState('');
    const [zipCode, setZipCode] = useState('');
    const [street, setStreet] = useState('');
    const [streetNumber, setStreetNumber] = useState('');
    const [addressComplement, setAddressComplement] = useState('');
    const [neighborhood, setNeighborhood] = useState('');
    const [city, setCity] = useState('');
    const [state, setState] = useState('');
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
        
    const { id: customerId } = useParams();

    async function loadCustomer() {
        try {
            const response = await api.get(`api/customer/v1/${customerId}`);
            const c = response.data;
            setId(c.id);
            setName(c.name);
            setEmail(c.email || '');
            setPhone(c.phone || '');
            setDocument(c.document || '');
            setZipCode(c.zipCode || '');
            setStreet(c.street || '');
            setStreetNumber(c.streetNumber || '');
            setAddressComplement(c.addressComplement || '');
            setNeighborhood(c.neighborhood || '');
            setCity(c.city || '');
            setState(c.state || '');
            setActive(c.active);
        } catch (error) {
            console.error(error);
            alert("Error loading customer!");
            navigate('/customers');
        }
    }

    useEffect(() => {
        if (customerId !== '0') {
            loadCustomer();
        }
    }, [customerId]);

    async function searchAddress() {
        if (!zipCode || zipCode.length < 8) {
            alert('Enter a valid ZIP code (8 digits)');
            return;
        }

        try {
            const response = await fetch(`https://viacep.com.br/ws/${zipCode}/json/`);
            const data = await response.json();

            if (!data.erro) {
                setStreet(data.logradouro || '');
                setNeighborhood(data.bairro || '');
                setCity(data.localidade || '');
                setState(data.uf || '');
            } else {
                alert('ZIP code not found!');
            }
        } catch (error) {
            console.error(error);
            alert('Error searching address!');
        }
    }

    // Função para formatar telefone
    function formatPhone(value) {
        const numbers = value.replace(/\D/g, '');
        if (numbers.length <= 10) {
            // (11) 1234-5678
            return numbers.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3');
        } else {
            // (11) 91234-5678
            return numbers.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
        }
    }

    function handlePhoneChange(e) {
        const formatted = formatPhone(e.target.value);
        setPhone(formatted);
    }

    // Função para formatar documento (CPF/CNPJ)
    function formatDocument(value) {
        const numbers = value.replace(/\D/g, '');
        
        if (numbers.length <= 11) {
            // CPF: 000.000.000-00
            return numbers
                .replace(/(\d{3})(\d)/, '$1.$2')
                .replace(/(\d{3})(\d)/, '$1.$2')
                .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
        } else {
            // CNPJ: 00.000.000/0000-00
            return numbers
                .replace(/(\d{2})(\d)/, '$1.$2')
                .replace(/(\d{3})(\d)/, '$1.$2')
                .replace(/(\d{3})(\d)/, '$1/$2')
                .replace(/(\d{4})(\d{1,2})$/, '$1-$2');
        }
    }

    function handleDocumentChange(e) {
        const formatted = formatDocument(e.target.value);
        setDocument(formatted);
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setLoading(true);

        // Validar campos obrigatórios baseado no schema do banco
        const requiredFields = [
            { field: name, name: 'Full Name' },
            { field: phone, name: 'Phone' },
            { field: zipCode, name: 'ZIP Code' },
            { field: street, name: 'Street' },
            { field: streetNumber, name: 'Street Number' },
            { field: neighborhood, name: 'Neighborhood' },
            { field: city, name: 'City' },
            { field: state, name: 'State' }
        ];

        const missingFields = requiredFields.filter(f => !f.field);
        if (missingFields.length > 0) {
            alert(`Please fill in all required fields: ${missingFields.map(f => f.name).join(', ')}`);
            setLoading(false);
            return;
        }

        // Validar estado (deve ter exatamente 2 caracteres)
        if (state && state.length !== 2) {
            alert('State must be exactly 2 characters (e.g., SP, RJ, MG)');
            setLoading(false);
            return;
        }

        // Validar telefone (mínimo 10 dígitos)
        const cleanPhone = phone.replace(/\D/g, '');
        if (cleanPhone.length < 10) {
            alert('Phone must have at least 10 digits (DDD + number)');
            setLoading(false);
            return;
        }

        // Validar documento se foi preenchido (opcional mas se preenchido deve ser válido)
        const cleanDocument = document.replace(/\D/g, '');
        if (cleanDocument && cleanDocument.length !== 11 && cleanDocument.length !== 14) {
            alert('If provided, document must be either CPF (11 digits) or CNPJ (14 digits)');
            setLoading(false);
            return;
        }

        // Validar email se foi preenchido
        if (email && !email.includes('@')) {
            alert('Please enter a valid email address');
            setLoading(false);
            return;
        }

        const data = {
            name, 
            email: email || null,
            phone: cleanPhone,
            document: cleanDocument || null,
            zipCode, 
            street, 
            streetNumber, 
            addressComplement: addressComplement || null,
            neighborhood, 
            city, 
            state: state.toUpperCase(), 
            active
        };

        try {
            if (customerId === '0') {
                await api.post('api/customer/v1', data);
                alert('Customer created successfully!');
            } else {
                data.id = id;
                await api.put('api/customer/v1', data);
                alert('Customer updated successfully!');
            }
            navigate('/customers');
        } catch (err) {
            console.error(err);
            const message = err.response?.data?.message || "Error saving customer!";
            alert(message);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="customer-form-container">
            <div className="customer-form-content"> 
                <section>
                    <h1>{customerId === '0' ? 'Add New' : 'Update'} Customer</h1>
                    <p>Enter customer information and click {customerId === '0' ? "'Add'" : "'Update'!"}</p>
                    <Link className="customer-form-back-link" to="/customers">
                        <FiArrowLeft size={16} color="#251fc5" />
                        Back to Customers
                    </Link>
                </section>

                <form onSubmit={handleSubmit}>
                    {/* Nome Completo - NOT NULL */}
                    <div className="form-group">
                        <label>
                            <FiUser size={14} style={{ marginRight: 4 }} />
                            Full Name <span className="required-star">*</span>
                        </label>
                        <input 
                            placeholder="Full Name" 
                            value={name} 
                            onChange={e => setName(e.target.value)} 
                            required 
                        />
                        <small className="input-hint">👤 Customer full name (required)</small>
                    </div>

                    {/* Email - Opcional no banco */}
                    <div className="form-group">
                        <label>
                            <FiMail size={14} style={{ marginRight: 4 }} />
                            Email
                        </label>
                        <input 
                            placeholder="Email" 
                            type="email" 
                            value={email} 
                            onChange={e => setEmail(e.target.value)} 
                        />
                        <small className="input-hint">✉️ Customer email (optional)</small>
                    </div>

                    {/* Telefone e Documento lado a lado */}
                    <div className="customer-form-row">
                        <div className="form-group">
                            <label>
                                <FiPhone size={14} style={{ marginRight: 4 }} />
                                Phone <span className="required-star">*</span>
                            </label>
                            <input 
                                placeholder="(11) 99999-9999" 
                                value={phone} 
                                onChange={handlePhoneChange}
                                required 
                            />
                            <small className="input-hint">📞 Main contact number including DDD (required)</small>
                        </div>
                        <div className="form-group">
                            <label>
                                <FiFileText size={14} style={{ marginRight: 4 }} />
                                Document (CPF/CNPJ)
                            </label>
                            <input 
                                placeholder="CPF or CNPJ" 
                                value={document} 
                                onChange={handleDocumentChange}
                            />
                            <small className="input-hint">📄 CPF (11 digits) or CNPJ (14 digits) (optional)</small>
                        </div>
                    </div>

                    {/* Endereço - Seção com título */}
                    <div className="address-section">
                        <div className="address-title">
                            <FiMapPin size={16} color="#251fc5" />
                            <span>Address Information</span>
                        </div>
                        
                        {/* ZIP Code e Street - Ambos NOT NULL */}
                        <div className="customer-form-row">
                            <div className="form-group">
                                <label>ZIP Code <span className="required-star">*</span></label>
                                <input 
                                    placeholder="ZIP Code" 
                                    value={zipCode} 
                                    onChange={e => setZipCode(e.target.value)} 
                                    onBlur={searchAddress}
                                    required 
                                />
                                <small className="input-hint">🔍 8 digits, auto-fills address (required)</small>
                            </div>
                            <div className="form-group">
                                <label>Street <span className="required-star">*</span></label>
                                <input 
                                    placeholder="Street" 
                                    value={street} 
                                    onChange={e => setStreet(e.target.value)} 
                                    required
                                />
                            </div>
                        </div>

                        {/* Number e Complement - Number é NOT NULL */}
                        <div className="customer-form-row">
                            <div className="form-group">
                                <label>
                                    <FiHome size={14} style={{ marginRight: 4 }} />
                                    Number <span className="required-star">*</span>
                                </label>
                                <input 
                                    placeholder="Number" 
                                    value={streetNumber} 
                                    onChange={e => setStreetNumber(e.target.value)} 
                                    required
                                />
                            </div>
                            <div className="form-group">
                                <label>Complement</label>
                                <input 
                                    placeholder="Complement" 
                                    value={addressComplement} 
                                    onChange={e => setAddressComplement(e.target.value)} 
                                />
                                <small className="input-hint">📍 Apt, suite, building, etc. (optional)</small>
                            </div>
                        </div>

                        {/* Neighborhood, City, State - Todos NOT NULL */}
                        <div className="customer-form-row three-columns">
                            <div className="form-group">
                                <label>Neighborhood <span className="required-star">*</span></label>
                                <input 
                                    placeholder="Neighborhood" 
                                    value={neighborhood} 
                                    onChange={e => setNeighborhood(e.target.value)} 
                                    required
                                />
                            </div>
                            <div className="form-group">
                                <label>City <span className="required-star">*</span></label>
                                <input 
                                    placeholder="City" 
                                    value={city} 
                                    onChange={e => setCity(e.target.value)} 
                                    required
                                />
                            </div>
                            <div className="form-group">
                                <label>State (UF) <span className="required-star">*</span></label>
                                <input 
                                    placeholder="UF" 
                                    value={state} 
                                    onChange={e => setState(e.target.value.toUpperCase())} 
                                    maxLength={2} 
                                    required
                                />
                                <small className="input-hint">2 character state code (required)</small>
                            </div>
                        </div>
                    </div>

                    {/* Status Checkbox - Default TRUE */}
                    <div className="status-checkbox">
                        <label className="status-label">
                            <input 
                                type="checkbox" 
                                checked={active} 
                                onChange={e => setActive(e.target.checked)} 
                            />
                            <span className={`status-text ${active ? 'active' : 'inactive'}`}>
                                <FiCheckSquare size={14} style={{ marginRight: 6 }} />
                                {active ? 'ACTIVE CUSTOMER' : 'INACTIVE CUSTOMER'}
                            </span>
                        </label>
                    </div>

                    <button className="customer-form-button" type="submit" disabled={loading}>
                        {loading ? 'Saving...' : (customerId === '0' ? 'Add Customer' : 'Update Customer')}
                    </button>
                </form>
            </div>
        </div>
    );
}