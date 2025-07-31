import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiUtils } from '../services/api';

const ProtectedRoute = ({ children }) => {
  const navigate = useNavigate();

  useEffect(() => {
    if (!apiUtils.isAuthenticated()) {
      navigate('/login');
    }
  }, [navigate]);

  if (!apiUtils.isAuthenticated()) {
    return (
      <div className="loading-container" style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        fontSize: '18px'
      }}>
        Redirecting to login...
      </div>
    );
  }

  return children;
};

export default ProtectedRoute;
