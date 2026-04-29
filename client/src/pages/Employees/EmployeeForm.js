// pages/Employees/EmployeeForm.js - VERSÃO REFATORADA (PADRÃO UNIFICADO)
import React, { useState, useEffect } from 'react';
import { useNavigate, Link, useParams } from 'react-router-dom';
import { FiArrowLeft, FiUser, FiMail, FiPhone, FiSmartphone, FiCalendar, FiBriefcase, FiMapPin, FiHome, FiMap, FiCheckSquare } from 'react-icons/fi';
import api from '../../services/api';
import './EmployeeForm.css';

export default function EmployeeForm() {
    const [id, setId] = useState(null);
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [cpf, setCpf] = useState('');
    const [email, setEmail] = useState('');
    const [phone, setPhone] = useState('');
    const [mobilePhone, setMobilePhone] = useState('');
    const [birthDate, setBirthDate] = useState('');
    const [gender, setGender] = useState('');
    const [zipCode, setZipCode] = useState('');
    const [street, setStreet] = useState('');
    const [streetNumber, setStreetNumber] = useState('');
    const [addressComplement, setAddressComplement] = useState('');
    const [neighborhood, setNeighborhood] = useState('');
    const [city, setCity] = useState('');
    const [state, setState] = useState('');
    const [jobTitle, setJobTitle] = useState('');
    const [department, setDepartment] = useState('');
    const [hireDate, setHireDate] = useState('');
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
    
    const { id: employeeId } = useParams();

    async function loadEmployee() {
        try {
            const response = await api.get(`api/employee/v1/${employeeId}`);
            const e = response.data;
            setId(e.id);
            setFirstName(e.firstName);
            setLastName(e.lastName);
            setCpf(e.cpf);
            setEmail(e.email);
            setPhone(e.phone || '');
            setMobilePhone(e.mobilePhone || '');
            setBirthDate(e.birthDate?.split('T')[0] || '');
            setGender(e.gender || '');
            setZipCode(e.zipCode || '');
            setStreet(e.street || '');
            setStreetNumber(e.streetNumber || '');
            setAddressComplement(e.addressComplement || '');
            setNeighborhood(e.neighborhood || '');
            setCity(e.city || '');
            setState(e.state || '');
            setJobTitle(e.jobTitle || '');
            setDepartment(e.department || '');
            setHireDate(e.hireDate?.split('T')[0] || '');
            setActive(e.active);
        } catch (error) {
            console.error(error);
            alert("Error loading employee!");
            navigate('/employees');
        }
    }

    useEffect(() => {
        if (employeeId !== '0') {
            loadEmployee();
        }
    }, [employeeId]);

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

    async function handleSubmit(e) {
        e.preventDefault();
        setLoading(true);

        const data = {
            firstName, lastName, cpf, email, phone, mobilePhone,
            birthDate, gender, zipCode, street, streetNumber,
            addressComplement, neighborhood, city, state,
            jobTitle, department, hireDate, active
        };

        try {
            if (employeeId === '0') {
                await api.post('api/employee/v1', data);
                alert('Employee created successfully!');
            } else {
                data.id = id;
                await api.put('api/employee/v1', data);
                alert('Employee updated successfully!');
            }
            navigate('/employees');
        } catch (err) {
            console.error(err);
            const message = err.response?.data?.message || "Error saving employee!";
            alert(message);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="employee-form-container">
            <div className="employee-form-content">
                <section>
                    <h1>{employeeId === '0' ? 'Add New' : 'Update'} Employee</h1>
                    <p>Enter employee information and click {employeeId === '0' ? "'Add'" : "'Update'"}!</p>
                    <Link className="employee-form-back-link" to="/employees">
                        <FiArrowLeft size={16} color="#2E8B57" />
                        Back to Employees
                    </Link>
                </section>

                <form onSubmit={handleSubmit}>
                    {/* Nome e Sobrenome lado a lado */}
                    <div className="employee-form-row">
                        <div className="form-group">
                            <label>
                                <FiUser size={14} style={{ marginRight: 4 }} />
                                First Name <span>*</span>
                            </label>
                            <input 
                                placeholder="First Name" 
                                value={firstName} 
                                onChange={e => setFirstName(e.target.value)} 
                                required 
                            />
                            <small className="input-hint">👤 Employee's given name</small>
                        </div>
                        <div className="form-group">
                            <label>
                                <FiUser size={14} style={{ marginRight: 4 }} />
                                Last Name <span>*</span>
                            </label>
                            <input 
                                placeholder="Last Name" 
                                value={lastName} 
                                onChange={e => setLastName(e.target.value)} 
                                required 
                            />
                            <small className="input-hint">👤 Employee's family name</small>
                        </div>
                    </div>

                    {/* CPF e Email lado a lado */}
                    <div className="employee-form-row">
                        <div className="form-group">
                            <label>
                                CPF <span>*</span>
                            </label>
                            <input 
                                placeholder="CPF" 
                                value={cpf} 
                                onChange={e => setCpf(e.target.value)} 
                                required 
                            />
                            <small className="input-hint">📄 Brazilian individual taxpayer ID</small>
                        </div>
                        <div className="form-group">
                            <label>
                                <FiMail size={14} style={{ marginRight: 4 }} />
                                Email <span>*</span>
                            </label>
                            <input 
                                placeholder="Email" 
                                type="email" 
                                value={email} 
                                onChange={e => setEmail(e.target.value)} 
                                required 
                            />
                            <small className="input-hint">✉️ Employee's work email</small>
                        </div>
                    </div>

                    {/* Telefone e Celular lado a lado */}
                    <div className="employee-form-row">
                        <div className="form-group">
                            <label>
                                <FiPhone size={14} style={{ marginRight: 4 }} />
                                Phone
                            </label>
                            <input 
                                placeholder="Phone" 
                                value={phone} 
                                onChange={e => setPhone(e.target.value)} 
                            />
                            <small className="input-hint">📞 Landline phone</small>
                        </div>
                        <div className="form-group">
                            <label>
                                <FiSmartphone size={14} style={{ marginRight: 4 }} />
                                Mobile Phone
                            </label>
                            <input 
                                placeholder="Mobile Phone" 
                                value={mobilePhone} 
                                onChange={e => setMobilePhone(e.target.value)} 
                            />
                            <small className="input-hint">📱 Mobile/WhatsApp contact</small>
                        </div>
                    </div>

                    {/* Data de Nascimento e Gênero lado a lado */}
                    <div className="employee-form-row">
                        <div className="form-group">
                            <label>
                                <FiCalendar size={14} style={{ marginRight: 4 }} />
                                Birth Date <span>*</span>
                            </label>
                            <input 
                                type="date" 
                                value={birthDate} 
                                onChange={e => setBirthDate(e.target.value)} 
                                required 
                            />
                            <small className="input-hint">🎂 Employee's date of birth</small>
                        </div>
                        <div className="form-group">
                            <label>Gender</label>
                            <select value={gender} onChange={e => setGender(e.target.value)}>
                                <option value="">Select Gender</option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                                <option value="OTHER">Other</option>
                            </select>
                            <small className="input-hint">⚥ Gender identification</small>
                        </div>
                    </div>

                    {/* Seção de Endereço */}
                    <div className="address-section">
                        <div className="address-title">
                            <FiMapPin size={16} color="#2E8B57" />
                            <span>Address Information</span>
                        </div>
                        
                        {/* ZIP Code e Street lado a lado */}
                        <div className="employee-form-row">
                            <div className="form-group">
                                <label>ZIP Code</label>
                                <input 
                                    placeholder="ZIP Code" 
                                    value={zipCode} 
                                    onChange={e => setZipCode(e.target.value)} 
                                    onBlur={searchAddress} 
                                />
                                <small className="input-hint">🔍 8 digits, auto-fills address</small>
                            </div>
                            <div className="form-group">
                                <label>Street</label>
                                <input 
                                    placeholder="Street" 
                                    value={street} 
                                    onChange={e => setStreet(e.target.value)} 
                                />
                            </div>
                        </div>

                        {/* Number e Complement lado a lado */}
                        <div className="employee-form-row">
                            <div className="form-group">
                                <label>
                                    <FiHome size={14} style={{ marginRight: 4 }} />
                                    Number
                                </label>
                                <input 
                                    placeholder="Number" 
                                    value={streetNumber} 
                                    onChange={e => setStreetNumber(e.target.value)} 
                                />
                            </div>
                            <div className="form-group">
                                <label>Complement</label>
                                <input 
                                    placeholder="Complement" 
                                    value={addressComplement} 
                                    onChange={e => setAddressComplement(e.target.value)} 
                                />
                                <small className="input-hint">📍 Apt, suite, house, etc.</small>
                            </div>
                        </div>

                        {/* Neighborhood, City, State */}
                        <div className="employee-form-row three-columns">
                            <div className="form-group">
                                <label>Neighborhood</label>
                                <input 
                                    placeholder="Neighborhood" 
                                    value={neighborhood} 
                                    onChange={e => setNeighborhood(e.target.value)} 
                                />
                            </div>
                            <div className="form-group">
                                <label>City</label>
                                <input 
                                    placeholder="City" 
                                    value={city} 
                                    onChange={e => setCity(e.target.value)} 
                                />
                            </div>
                            <div className="form-group">
                                <label>State (UF)</label>
                                <input 
                                    placeholder="UF" 
                                    value={state} 
                                    onChange={e => setState(e.target.value)} 
                                    maxLength={2} 
                                />
                            </div>
                        </div>
                    </div>

                    {/* Cargo e Departamento lado a lado */}
                    <div className="employee-form-row">
                        <div className="form-group">
                            <label>
                                <FiBriefcase size={14} style={{ marginRight: 4 }} />
                                Job Title
                            </label>
                            <input 
                                placeholder="Job Title" 
                                value={jobTitle} 
                                onChange={e => setJobTitle(e.target.value)} 
                            />
                            <small className="input-hint">💼 Employee's position/role</small>
                        </div>
                        <div className="form-group">
                            <label>Department</label>
                            <input 
                                placeholder="Department" 
                                value={department} 
                                onChange={e => setDepartment(e.target.value)} 
                            />
                            <small className="input-hint">🏢 Department or team</small>
                        </div>
                    </div>

                    {/* Data de Contratação */}
                    <div className="employee-form-row">
                        <div className="form-group">
                            <label>
                                <FiCalendar size={14} style={{ marginRight: 4 }} />
                                Hire Date <span>*</span>
                            </label>
                            <input 
                                type="date" 
                                value={hireDate} 
                                onChange={e => setHireDate(e.target.value)} 
                                required 
                            />
                            <small className="input-hint">📅 When the employee started working</small>
                        </div>
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
                                {active ? 'ACTIVE EMPLOYEE' : 'INACTIVE EMPLOYEE'}
                            </span>
                        </label>
                    </div>

                    <button className="employee-form-button" type="submit" disabled={loading}>
                        {loading ? 'Saving...' : (employeeId === '0' ? 'Add Employee' : 'Update Employee')}
                    </button>
                </form>
            </div>
        </div>
    );
}