// pages/Employees/index.js
import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FiPower, FiEdit, FiTrash2, FiUserCheck, FiUserX, FiSearch, FiArrowLeft, FiX, FiMail, FiPhone, FiBriefcase, FiMapPin } from 'react-icons/fi';
import './styles.css';
import api from '../../services/api';
import logoImage from '../../assets/logoerp.png';

export default function Employees() {
    const [employees, setEmployees] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(false);
    
    const [searchTerm, setSearchTerm] = useState('');
    const [searchType, setSearchType] = useState('name');
    const [searching, setSearching] = useState(false);

    const fullName = localStorage.getItem('fullName');
    const navigate = useNavigate();

    function getDisplayName(fullName) {
        if (!fullName) return '';
        let firstName = fullName.trim().split(' ')[0];
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

    async function deleteEmployee(id) {
        if (window.confirm('Are you sure you want to delete this employee?')) {
            try {
                await api.delete(`api/employee/v1/${id}`);
                setEmployees(employees.filter(emp => emp.id !== id));
                alert('Employee deleted successfully!');
            } catch (err) {
                console.error(err);
                const errorMessage = err.response?.data?.message || err.message;
                if (errorMessage.includes('violates foreign key constraint') ||
                    errorMessage.includes('fk_orders_customer') ||
                    errorMessage.includes('fk_purchases_customer')) {
                    alert('❌ Exclusão não permitida\n\nEste funcionário não pode ser excluído pois existem registros vinculados a ele.');
                } else {
                    alert('Erro ao excluir! Tente novamente.');
                }
            }
        }
    }

    async function toggleActive(employee) {
        try {
            let response;
            if (employee.active) {
                response = await api.patch(`api/employee/v1/${employee.id}`);
            } else {
                response = await api.patch(`api/employee/v1/activate/${employee.id}`);
            }
            setEmployees(employees.map(e => e.id === employee.id ? response.data : e));
            alert(`Employee ${employee.active ? 'deactivated' : 'activated'} successfully!`);
        } catch (err) {
            console.error(err);
            alert('Operation failed! Try again.');
        }
    }

    const extractEmployeeList = (responseData) => {
        if (Array.isArray(responseData)) return responseData;
        if (responseData && responseData._embedded) {
            const embeddedKeys = Object.keys(responseData._embedded);
            if (embeddedKeys.length > 0) return responseData._embedded[embeddedKeys[0]];
        }
        if (responseData && responseData.content) return responseData.content;
        if (responseData && responseData.id) return [responseData];
        return [];
    };

    // Busca por ID
    async function searchById(id) {
        setLoading(true);
        setSearching(true);
        try {
            const response = await api.get(`api/employee/v1/${id}`);
            setEmployees([response.data]);
            setTotalPages(1);
            setPage(0);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 404) {
                alert('Employee not found!');
                setEmployees([]);
            } else {
                alert('Search failed!');
            }
        } finally {
            setLoading(false);
        }
    }

    // Busca por Nome
    async function searchByName(name, currentPage = 0, reset = true) {
        setLoading(true);
        setSearching(true);
        try {
            const response = await api.get(`/api/employee/v1/findEmployeeByName/${encodeURIComponent(name)}`, {
                params: { page: currentPage, size: 10, direction: 'asc' }
            });
            const employeeList = extractEmployeeList(response.data);
            const total = response.data?.page?.totalPages || 1;
            if (reset) {
                setEmployees(employeeList);
                setPage(1);
            } else {
                setEmployees(prev => [...prev, ...employeeList]);
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

    // Busca por Email
    async function searchByEmail(email) {
        setLoading(true);
        setSearching(true);
        try {
            const response = await api.get(`/api/employee/v1/findByEmail/${encodeURIComponent(email)}`);
            setEmployees([response.data]);
            setTotalPages(1);
            setPage(0);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 404) {
                alert('Employee not found with this email!');
                setEmployees([]);
            } else {
                alert('Search failed!');
            }
        } finally {
            setLoading(false);
        }
    }

    // Busca por CPF
    async function searchByDocument(document) {
        setLoading(true);
        setSearching(true);
        try {
            const response = await api.get(`/api/employee/v1/findByDocument/${encodeURIComponent(document)}`);
            setEmployees([response.data]);
            setTotalPages(1);
            setPage(0);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 404) {
                alert('Employee not found with this CPF!');
                setEmployees([]);
            } else {
                alert('Search failed!');
            }
        } finally {
            setLoading(false);
        }
    }

    // Busca por Department
    async function searchByDepartment(department, currentPage = 0, reset = true) {
        setLoading(true);
        setSearching(true);
        try {
            const response = await api.get(`/api/employee/v1/department/${encodeURIComponent(department)}`, {
                params: { page: currentPage, size: 10, direction: 'asc' }
            });
            const employeeList = extractEmployeeList(response.data);
            const total = response.data?.page?.totalPages || 1;
            if (reset) {
                setEmployees(employeeList);
                setPage(1);
            } else {
                setEmployees(prev => [...prev, ...employeeList]);
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

    // Busca por Job Title
    async function searchByJobTitle(jobTitle, currentPage = 0, reset = true) {
        setLoading(true);
        setSearching(true);
        try {
            const response = await api.get(`/api/employee/v1/jobTitle/${encodeURIComponent(jobTitle)}`, {
                params: { page: currentPage, size: 10, direction: 'asc' }
            });
            const employeeList = extractEmployeeList(response.data);
            const total = response.data?.page?.totalPages || 1;
            if (reset) {
                setEmployees(employeeList);
                setPage(1);
            } else {
                setEmployees(prev => [...prev, ...employeeList]);
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

    // Busca por Ativos
    async function searchActive(currentPage = 0, reset = true) {
        setLoading(true);
        setSearching(true);
        try {
            const response = await api.get('/api/employee/v1/active', {
                params: { page: currentPage, size: 10, direction: 'asc' }
            });
            const employeeList = extractEmployeeList(response.data);
            const total = response.data?.page?.totalPages || 1;
            if (reset) {
                setEmployees(employeeList);
                setPage(1);
            } else {
                setEmployees(prev => [...prev, ...employeeList]);
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

    // Busca por Inativos
    async function searchInactive(currentPage = 0, reset = true) {
        setLoading(true);
        setSearching(true);
        try {
            const response = await api.get('/api/employee/v1/inactive', {
                params: { page: currentPage, size: 10, direction: 'asc' }
            });
            const employeeList = extractEmployeeList(response.data);
            const total = response.data?.page?.totalPages || 1;
            if (reset) {
                setEmployees(employeeList);
                setPage(1);
            } else {
                setEmployees(prev => [...prev, ...employeeList]);
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

    async function loadEmployees(currentPage = 0, reset = true) {
        setLoading(true);
        setSearching(false);
        try {
            const response = await api.get('/api/employee/v1', {
                params: { page: currentPage, size: 10, direction: 'asc' }
            });
            const employeeList = extractEmployeeList(response.data);
            const total = response.data?.page?.totalPages || 1;
            if (reset) {
                setEmployees(employeeList);
                setPage(1);
            } else {
                setEmployees(prev => [...prev, ...employeeList]);
                setPage(prev => prev + 1);
            }
            setTotalPages(total);
        } catch (err) {
            console.error(err);
            if (err.response?.status === 401) logout();
            alert('Error loading employees!');
        } finally {
            setLoading(false);
        }
    }

    const loadMore = () => {
        if (!loading && page < totalPages) {
            if (searching) {
                switch (searchType) {
                    case 'name':
                        searchByName(searchTerm, page, false);
                        break;
                    case 'active':
                        searchActive(page, false);
                        break;
                    case 'inactive':
                        searchInactive(page, false);
                        break;
                    case 'department':
                        searchByDepartment(searchTerm, page, false);
                        break;
                    case 'jobTitle':
                        searchByJobTitle(searchTerm, page, false);
                        break;
                    default:
                        loadEmployees(page, false);
                }
            } else {
                loadEmployees(page, false);
            }
        }
    };

    const handleSearch = (e) => {
        e.preventDefault();
        
        if (searchType !== 'active' && searchType !== 'inactive' && !searchTerm.trim()) {
            alert('Please enter a search value');
            return;
        }
        
        setPage(0);
        setTotalPages(1);
        
        switch (searchType) {
            case 'id':
                searchById(searchTerm.trim());
                break;
            case 'email':
                searchByEmail(searchTerm.trim());
                break;
            case 'document':
                searchByDocument(searchTerm.trim());
                break;
            case 'name':
                searchByName(searchTerm.trim(), 0, true);
                break;
            case 'department':
                searchByDepartment(searchTerm.trim(), 0, true);
                break;
            case 'jobTitle':
                searchByJobTitle(searchTerm.trim(), 0, true);
                break;
            case 'active':
                searchActive(0, true);
                break;
            case 'inactive':
                searchInactive(0, true);
                break;
            default:
                loadEmployees(0, true);
        }
    };

    const clearSearch = () => {
        setSearchTerm('');
        setSearching(false);
        setEmployees([]);
        setPage(0);
        setTotalPages(1);
        loadEmployees(0, true);
    };

    useEffect(() => {
        loadEmployees(0, true);
    }, []);

    const getPlaceholder = () => {
        switch (searchType) {
            case 'name': return 'Enter name...';
            case 'id': return 'Enter ID...';
            case 'email': return 'Enter email...';
            case 'document': return 'Enter CPF...';
            case 'department': return 'Enter department...';
            case 'jobTitle': return 'Enter job title...';
            case 'active': return 'Click Search to show active employees';
            case 'inactive': return 'Click Search to show inactive employees';
            default: return 'Search...';
        }
    };

    const formatPhone = (phone) => {
        if (!phone) return '-';
        const cleaned = phone.replace(/\D/g, '');
        if (cleaned.length === 11) {
            return `(${cleaned.substring(0, 2)}) ${cleaned.substring(2, 7)}-${cleaned.substring(7)}`;
        }
        if (cleaned.length === 10) {
            return `(${cleaned.substring(0, 2)}) ${cleaned.substring(2, 6)}-${cleaned.substring(6)}`;
        }
        return phone;
    };

    const formatCPF = (cpf) => {
        if (!cpf) return '-';
        const cleaned = cpf.replace(/\D/g, '');
        if (cleaned.length === 11) {
            return cleaned.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
        }
        return cpf;
    };

    return (
        <div className="employee-page-container">
            <header>
                <img src={logoImage} alt="ERP Logo" />
                <span>Welcome, <strong>{getDisplayName(fullName).toUpperCase()}</strong></span>
                <Link className="employee-page-add-button" to="/employees/new/0">Add New Employee</Link>
                <button className="employee-page-logout-btn" onClick={logout} type="button">
                    <FiPower size={18} color="#2E8B57" />
                </button>
            </header>

            <div className="employee-page-title-row">
                <Link to="/dashboard" className="employee-page-back-button">
                    <FiArrowLeft size={24} />
                </Link>
                <h1>Registered Employees</h1>
            </div>

            <div className="employee-page-search-section">
                <form onSubmit={handleSearch} className="employee-page-search-form">
                    <select
                        value={searchType}
                        onChange={e => setSearchType(e.target.value)}
                        className="employee-page-search-type-select"
                    >
                        <option value="name">Search by Name</option>
                        <option value="id">Search by ID</option>
                        <option value="email">Search by Email</option>
                        <option value="document">Search by CPF</option>
                        <option value="department">Search by Department</option>
                        <option value="jobTitle">Search by Job Title</option>
                        <option value="active">Active Employees</option>
                        <option value="inactive">Inactive Employees</option>
                    </select>

                    {(searchType !== 'active' && searchType !== 'inactive') && (
                        <input
                            type={searchType === 'id' ? 'number' : 'text'}
                            inputMode={searchType === 'document' ? 'numeric' : undefined}
                            placeholder={getPlaceholder()}
                            value={searchTerm}
                            onChange={(e) => {
                                let value = e.target.value;

                                if (searchType === 'document') {
                                    value = value.replace(/\D/g, '').slice(0, 11);
                                }

                                setSearchTerm(value);
                            }}
                        />
                    )}

                    <button type="submit" className="employee-page-search-btn">
                        <FiSearch size={16} /> Search
                    </button>

                    {searching && (
                        <button type="button" className="employee-page-clear-btn" onClick={clearSearch}>
                            Clear
                        </button>
                    )}
                </form>
            </div>

            {employees.length > 0 && (
                <ul>
                    {employees.map(employee => (
                        <li key={employee.id}>
                            <div className="employee-page-header">
                                <strong className="employee-page-code">ID: #{employee.id}</strong>
                                <span className={`employee-page-status ${employee.active ? 'active' : 'inactive'}`}>
                                    {employee.active ? 'ACTIVE' : 'INACTIVE'}
                                </span>
                            </div>

                            <div className="employee-page-name">
                                <strong>Name:</strong>
                                <p>{employee.firstName} {employee.lastName}</p>
                            </div>

                            <div className="employee-page-contact">
                                <div className="employee-page-contact-item">
                                    <FiMail size={14} />
                                    <strong>Email:</strong>
                                    <p>{employee.email}</p>
                                </div>
                                <div className="employee-page-contact-item">
                                    <FiPhone size={14} />
                                    <strong>Phone:</strong>
                                    <p>{formatPhone(employee.mobilePhone || employee.phone)}</p>
                                </div>
                            </div>

                            <div className="employee-page-info">
                                <div>
                                    <strong>CPF:</strong>
                                    <p>{formatCPF(employee.cpf)}</p>
                                </div>
                                <div>
                                    <strong>Birth Date:</strong>
                                    <p>{employee.birthDate ? new Date(employee.birthDate).toLocaleDateString('pt-BR') : '-'}</p>
                                </div>
                            </div>

                            <div className="employee-page-professional">
                                <div className="employee-page-professional-item">
                                    <FiBriefcase size={14} />
                                    <strong>Job Title:</strong>
                                    <p>{employee.jobTitle || '-'}</p>
                                </div>
                                <div className="employee-page-professional-item">
                                    <strong>Department:</strong>
                                    <p>{employee.department || '-'}</p>
                                </div>
                                <div className="employee-page-professional-item">
                                    <strong>Hire Date:</strong>
                                    <p>{employee.hireDate ? new Date(employee.hireDate).toLocaleDateString('pt-BR') : '-'}</p>
                                </div>
                            </div>

                            {employee.city && (
                                <div className="employee-page-address">
                                    <FiMapPin size={14} />
                                    <strong>Address:</strong>
                                    <p>{employee.city}, {employee.state} - {employee.zipCode || ''}</p>
                                </div>
                            )}

                            <div className="employee-page-action-buttons">
                                <button onClick={() => toggleActive(employee)} title={employee.active ? 'Deactivate' : 'Activate'}>
                                    {employee.active ? <FiUserX size={20} color="#FF4444" /> : <FiUserCheck size={20} color="#00AA00" />}
                                </button>
                                <button onClick={() => navigate(`/employees/new/${employee.id}`)} title="Edit">
                                    <FiEdit size={20} color="#2E8B57" />
                                </button>
                                <button onClick={() => deleteEmployee(employee.id)} title="Delete">
                                    <FiTrash2 size={20} color="#2E8B57" />
                                </button>
                            </div>
                        </li>
                    ))}
                </ul>
            )}

            {!searching && page < totalPages && employees.length > 0 && (
                <button className="employee-page-load-more" onClick={loadMore} disabled={loading}>
                    {loading ? 'Loading...' : 'Load More'}
                </button>
            )}

            {searching && page < totalPages && employees.length > 0 && (
                <button className="employee-page-load-more" onClick={loadMore} disabled={loading}>
                    {loading ? 'Loading...' : 'Load More'}
                </button>
            )}

            {employees.length === 0 && !loading && (
                <p className="employee-page-no-results">
                    {searching ? `No employees found.` : 'No employees found.'}
                </p>
            )}

            {loading && employees.length === 0 && (
                <p className="employee-page-no-results">Loading...</p>
            )}
        </div>
    );
}