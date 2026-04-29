// components/PrivateRoute.js
import React from 'react';
import { Navigate } from 'react-router-dom';

export default function PrivateRoute({ children }) {
    const accessToken = localStorage.getItem('accessToken');
    const isAuthenticated = accessToken && accessToken !== 'undefined' && accessToken !== 'null';
    
    return isAuthenticated ? children : <Navigate to="/" replace />;
}