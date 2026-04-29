// pages/Suppliers/SupplierForm.js
import React, { useState, useEffect } from 'react';
import { useNavigate, Link, useParams } from 'react-router-dom';
import { FiArrowLeft, FiTruck, FiFileText, FiMail, FiPhone, FiMapPin, FiHome, FiMap, FiCheckSquare } from 'react-icons/fi';
import api from '../../services/api';
import './SupplierForm.css';

export default function SupplierForm() {
    const [id, setId] = useState(null);
    const [name, setName] = useState('');
    const [document, setDocument] = useState('');
    const [email, setEmail] = useState('');
    const [phone, setPhone] = useState('');
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
    
    const { id: supplierId } = useParams();

    async function loadSupplier() {
        try {
            const response = await api.get(`api/supplier/v1/${supplierId}`);
            const s = response.data;
            setId(s.id);
            setName(s.name);
            setDocument(s.document);
            setEmail(s.email || '');
            setPhone(s.phone || '');
            setZipCode(s.zipCode || '');
            setStreet(s.street || '');
            setStreetNumber(s.streetNumber || '');
            setAddressComplement(s.addressComplement || '');
            setNeighborhood(s.neighborhood || '');
            setCity(s.city || '');
            setState(s.state || '');
            setActive(s.active);
        } catch (error) {
            console.error(error);
            alert("Error loading supplier!");
            navigate('/suppliers');
        }
    }

    useEffect(() => {
        if (supplierId !== '0') {
            loadSupplier();
        }
    }, [supplierId]);

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

    // Função para formatar documento (CNPJ/CPF)
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
            { field: name, name: 'Company Name' },
            { field: document, name: 'Document (CNPJ/CPF)' },
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

        // Validar documento (CPF ou CNPJ)
        const cleanDocument = document.replace(/\D/g, '');
        if (cleanDocument.length !== 11 && cleanDocument.length !== 14) {
            alert('Document must be either CPF (11 digits) or CNPJ (14 digits)');
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

        const data = {
            name, 
            document: cleanDocument, // Enviar apenas números
            email, 
            phone: cleanPhone, // Enviar apenas números
            zipCode, 
            street, 
            streetNumber, 
            addressComplement,
            neighborhood, 
            city, 
            state, 
            active
        };

        try {
            if (supplierId === '0') {
                await api.post('api/supplier/v1', data);
                alert('Supplier created successfully!');
            } else {
                data.id = id;
                await api.put('api/supplier/v1', data);
                alert('Supplier updated successfully!');
            }
            navigate('/suppliers');
        } catch (err) {
            console.error(err);
            const message = err.response?.data?.message || "Error saving supplier!";
            alert(message);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="supplier-form-container">
            <div className="supplier-form-content">
                <section>
                    <h1>{supplierId === '0' ? 'Add New' : 'Update'} Supplier</h1>
                    <p>Enter supplier information and click {supplierId === '0' ? "'Add'" : "'Update'!"}</p>
                    <Link className="supplier-form-back-link" to="/suppliers">
                        <FiArrowLeft size={16} color="#6A5ACD" />
                        Back to Suppliers
                    </Link>
                </section>

                <form onSubmit={handleSubmit}>
                    {/* Nome da Empresa - NOT NULL */}
                    <div className="form-group">
                        <label>
                            <FiTruck size={14} style={{ marginRight: 4 }} />
                            Company Name <span className="required-star">*</span>
                        </label>
                        <input 
                            placeholder="Company Name" 
                            value={name} 
                            onChange={e => setName(e.target.value)} 
                            required 
                        />
                        <small className="input-hint">🏢 Full company or individual supplier name (required)</small>
                    </div>

                    {/* Documento e Email lado a lado - Document agora é NOT NULL */}
                    <div className="supplier-form-row">
                        <div className="form-group">
                            <label>
                                <FiFileText size={14} style={{ marginRight: 4 }} />
                                Document (CNPJ/CPF) <span className="required-star">*</span>
                            </label>
                            <input 
                                placeholder="CNPJ or CPF" 
                                value={document} 
                                onChange={handleDocumentChange}
                                required 
                            />
                            <small className="input-hint">📄 CNPJ (14 digits) for companies or CPF (11 digits) for individuals (required)</small>
                        </div>
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
                            <small className="input-hint">✉️ Supplier contact email (optional)</small>
                        </div>
                    </div>

                    {/* Telefone - NOT NULL */}
                    <div className="form-group">
                        <label>
                            <FiPhone size={14} style={{ marginRight: 4 }} />
                            Phone <span className="required-star">*</span>
                        </label>
                        <input 
                            placeholder="(11) 99999-9999" 
                            value={phone} 
                            onChange={e => setPhone(e.target.value)} 
                            required
                        />
                        <small className="input-hint">📞 Main contact number including DDD (required)</small>
                    </div>

                    {/* Seção de Endereço */}
                    <div className="address-section">
                        <div className="address-title">
                            <FiMapPin size={16} color="#6A5ACD" />
                            <span>Address Information</span>
                        </div>
                        
                        {/* ZIP Code e Street - Ambos NOT NULL */}
                        <div className="supplier-form-row">
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
                        <div className="supplier-form-row">
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
                        <div className="supplier-form-row three-columns">
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
                                {active ? 'ACTIVE SUPPLIER' : 'INACTIVE SUPPLIER'}
                            </span>
                        </label>
                    </div>

                    <button className="supplier-form-button" type="submit" disabled={loading}>
                        {loading ? 'Saving...' : (supplierId === '0' ? 'Add Supplier' : 'Update Supplier')}
                    </button>
                </form>
            </div>
        </div>
    );
}