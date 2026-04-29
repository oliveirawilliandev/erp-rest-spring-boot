// App.js
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

// Importar todas as páginas
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import CreateUser from './pages/CreateUser';
import Customers from './pages/Customers';
import CustomerForm from './pages/Customers/CustomerForm';
import Employees from './pages/Employees';
import EmployeeForm from './pages/Employees/EmployeeForm';
import Products from './pages/Products';
import ProductForm from './pages/Products/ProductForm';
import Suppliers from './pages/Suppliers';
import SupplierForm from './pages/Suppliers/SupplierForm';
import Orders from './pages/Orders';
import OrderForm from './pages/Orders/OrderForm';
import Purchases from './pages/Purchases';
import PurchaseForm from './pages/Purchases/PurchaseForm';
import Reports from './pages/Reports';
import About from './pages/About';
import Profile from './pages/Profile';
import Ingredients from './pages/Ingredients';
import IngredientForm from './pages/Ingredients/IngredientForm';

function PrivateRoute({ children }) {
    const token = localStorage.getItem('accessToken');
    const isValid = token && token !== 'undefined' && token !== 'null';
    return isValid ? children : <Navigate to="/" replace />;
}

function App() {
    return (
        <Router>
            <Routes>
                {/* Rotas públicas */}
                <Route path="/" element={<Login />} />
                <Route path="/create-user" element={<CreateUser />} />
                
                {/* Rotas protegidas - Dashboard */}
                <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
                
                {/* Clientes */}
                <Route path="/customers" element={<PrivateRoute><Customers /></PrivateRoute>} />
                <Route path="/customers/new/:id" element={<PrivateRoute><CustomerForm /></PrivateRoute>} />
                
                {/* Funcionários */}
                <Route path="/employees" element={<PrivateRoute><Employees /></PrivateRoute>} />
                <Route path="/employees/new/:id" element={<PrivateRoute><EmployeeForm /></PrivateRoute>} />
                
                {/* Produtos */}
                <Route path="/products" element={<PrivateRoute><Products /></PrivateRoute>} />
                <Route path="/products/new/:id" element={<PrivateRoute><ProductForm /></PrivateRoute>} />
                
                {/* Fornecedores */}
                <Route path="/suppliers" element={<PrivateRoute><Suppliers /></PrivateRoute>} />
                <Route path="/suppliers/new/:id" element={<PrivateRoute><SupplierForm /></PrivateRoute>} />
                
                {/* Pedidos */}
                <Route path="/orders" element={<PrivateRoute><Orders /></PrivateRoute>} />
                <Route path="/orders/new/:id" element={<PrivateRoute><OrderForm /></PrivateRoute>} />
                
                {/* Compras */}
                <Route path="/purchases" element={<PrivateRoute><Purchases /></PrivateRoute>} />
                <Route path="/purchases/new/:id" element={<PrivateRoute><PurchaseForm /></PrivateRoute>} />

                 {/* Ingredients  */}
                <Route path="/ingredients" element={<Ingredients />} />
                <Route path="/ingredients/new/:id" element={<IngredientForm />} />
                
                {/* Relatórios  */}
                <Route path="/reports" element={<PrivateRoute><Reports /></PrivateRoute>} />

                {/*  Sobre */}
                <Route path="/about" element={<PrivateRoute><About /></PrivateRoute>} />

                {/* Editar Perfil */}                
                <Route path="/profile" element={<Profile />} />
            </Routes>
        </Router>
    );
}

export default App;