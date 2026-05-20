// routes.js
import React from 'react';
import { BrowserRouter, Routes, Route } from "react-router-dom";

// Layout
import Dashboard from './pages/Dashboard';

// Autenticação
import Login from './pages/Login';

// Clientes
import Customers from './pages/Customers';
import CustomerForm from './pages/Customers/CustomerForm';

// Funcionários
import Employees from './pages/Employees';
import EmployeeForm from './pages/Employees/EmployeeForm';

// Produtos
import Products from './pages/Products';
import ProductForm from './pages/Products/ProductForm';

// Fornecedores
import Suppliers from './pages/Suppliers';
import SupplierForm from './pages/Suppliers/SupplierForm';

// Pedidos
import Orders from './pages/Orders';
import OrderForm from './pages/Orders/OrderForm';

// Compras
import Purchases from './pages/Purchases';
import PurchaseForm from './pages/Purchases/PurchaseForm';

// ingredients
import Ingredients from './pages/Ingredients';
import IngredientForm from './pages/Ingredients/IngredientForm';

//relatorios
import Reports from './pages/Reports';    

//biografia
import About from './pages/About';   

// Criar User Comum
import CreateUser from './pages/CreateUser';

// Profille
import Profile from './pages/Profile';

export default function AppRoutes() {
    return (
        <BrowserRouter basename="/erp">   {/* ← ADICIONE ESTA LINHA AQUI */}
            <Routes>
                {/* Rotas públicas */}
                <Route path="/" element={<Login />} />

                {/* Rotas protegidas (com layout) */}
                <Route path="/dashboard" element={<Dashboard />} />

                {/* Clientes */}
                <Route path="/customers" element={<Customers />} />
                <Route path="/customers/new/:id" element={<CustomerForm />} />

                {/* Funcionários */}
                <Route path="/employees" element={<Employees />} />
                <Route path="/employees/new/:id" element={<EmployeeForm />} />

                {/* Produtos */}
                <Route path="/products" element={<Products />} />
                <Route path="/products/new/:id" element={<ProductForm />} />

                {/* Fornecedores */}
                <Route path="/suppliers" element={<Suppliers />} />
                <Route path="/suppliers/new/:id" element={<SupplierForm />} />

                {/* Pedidos */}
                <Route path="/orders" element={<Orders />} />
                <Route path="/orders/new/:id" element={<OrderForm />} />

                {/* Compras */}
                <Route path="/purchases" element={<Purchases />} />
                <Route path="/purchases/new/:id" element={<PurchaseForm />} />

                {/* Ingredient */}
                <Route path="/ingredients" element={<Ingredients />} />
                <Route path="/ingredients/new/:id" element={<IngredientForm />} />

                {/*Relatorio*/}                
                <Route path="/reports" element={<Reports />} />

                {/*Biografia*/}              
                <Route path="/about" element={<About />} />

                {/*Criar User*/}              
                <Route path="/create-user" element={<CreateUser />} />
                
                {/*Profile*/}              
                <Route path="/profile" element={<Profile />} />
            </Routes>
        </BrowserRouter>
    );
}