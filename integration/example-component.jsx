/**
 * Example React Component using OrganLink API Integration
 * 
 * This component demonstrates how to use the API integration service
 * and WebSocket service in a React component.
 */

import React, { useState, useEffect } from 'react';
import { authAPI, hospitalAPI, donorAPI } from '../services/organlink-api';
import webSocketService from '../services/websocket-service';

const OrganLinkDashboard = () => {
  // State variables
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [hospitalInfo, setHospitalInfo] = useState(null);
  const [donors, setDonors] = useState([]);
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Check authentication status on component mount
  useEffect(() => {
    const checkAuth = async () => {
      try {
        // Validate token
        await authAPI.validateToken();
        setIsLoggedIn(true);
        
        // Get hospital info
        const hospitalResponse = await hospitalAPI.getCurrentHospital();
        setHospitalInfo(hospitalResponse.data);
        
        // Initialize WebSocket
        initializeWebSocket();
      } catch (err) {
        console.error('Authentication error:', err);
        setIsLoggedIn(false);
        setError('Authentication failed. Please log in again.');
      } finally {
        setLoading(false);
      }
    };
    
    checkAuth();
    
    // Cleanup WebSocket on component unmount
    return () => {
      webSocketService.disconnect();
    };
  }, []);

  // Initialize WebSocket connection
  const initializeWebSocket = () => {
    webSocketService.initialize(
      undefined, // Use default URL
      () => {
        console.log('WebSocket connected successfully');
        
        // Subscribe to hospital notifications
        const subscriptionId = webSocketService.subscribe(
          '/user/queue/notifications', 
          (message) => {
            console.log('New notification received:', message);
            setNotifications(prev => [message, ...prev]);
          }
        );
        
        // Store subscription ID for cleanup
        window.notificationSubscriptionId = subscriptionId;
      },
      (error) => {
        console.error('WebSocket connection error:', error);
      }
    );
  };

  // Load donors when hospital info is available
  useEffect(() => {
    if (hospitalInfo) {
      loadDonors();
    }
  }, [hospitalInfo]);

  // Load donors from API
  const loadDonors = async () => {
    try {
      setLoading(true);
      const response = await donorAPI.getAllDonors();
      setDonors(response.data);
    } catch (err) {
      console.error('Error loading donors:', err);
      setError('Failed to load donors. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  // Handle logout
  const handleLogout = () => {
    authAPI.logout();
    setIsLoggedIn(false);
    setHospitalInfo(null);
    webSocketService.disconnect();
    window.location.href = '/login';
  };

  // Render loading state
  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  // Render error state
  if (error) {
    return <div className="error">{error}</div>;
  }

  // Render login prompt if not logged in
  if (!isLoggedIn) {
    return <div className="not-logged-in">Please log in to access the dashboard.</div>;
  }

  return (
    <div className="dashboard-container">
      {/* Hospital Info Header */}
      <header className="dashboard-header">
        <h1>OrganLink Hospital Dashboard</h1>
        {hospitalInfo && (
          <div className="hospital-info">
            <h2>{hospitalInfo.name}</h2>
            <p>{hospitalInfo.address}, {hospitalInfo.city}, {hospitalInfo.state.name}</p>
          </div>
        )}
        <button className="logout-button" onClick={handleLogout}>Logout</button>
      </header>

      {/* Main Dashboard Content */}
      <div className="dashboard-content">
        <div className="dashboard-section">
          <h3>Registered Donors</h3>
          {donors.length === 0 ? (
            <p>No donors registered yet.</p>
          ) : (
            <ul className="donors-list">
              {donors.map(donor => (
                <li key={donor.id} className="donor-item">
                  <span>{donor.firstName} {donor.lastName}</span>
                  <span>Blood Type: {donor.bloodType}</span>
                  <span>Organs: {donor.organs.map(o => o.organType.name).join(', ')}</span>
                </li>
              ))}
            </ul>
          )}
          <button className="refresh-button" onClick={loadDonors}>Refresh</button>
        </div>

        {/* Notifications Section */}
        <div className="dashboard-section notifications">
          <h3>Notifications</h3>
          {notifications.length === 0 ? (
            <p>No new notifications.</p>
          ) : (
            <ul className="notifications-list">
              {notifications.map((notification, index) => (
                <li key={index} className="notification-item">
                  <div className="notification-title">{notification.title}</div>
                  <div className="notification-message">{notification.message}</div>
                  <div className="notification-time">
                    {new Date(notification.timestamp).toLocaleString()}
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
};

export default OrganLinkDashboard;