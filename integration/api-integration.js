/**
 * OrganLink API Integration Service
 * 
 * This service integrates the React frontend from builder-quantum-studio
 * with the Spring Boot backend from organlink.
 */

import axios from 'axios';

// API Configuration
const API_BASE_URL = 'http://localhost:8081/api/v1';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000, // 30 seconds timeout
});

// Request interceptor to add authentication token and tenant ID
api.interceptors.request.use(
  (config) => {
    // Get tokens based on user type
    const adminToken = localStorage.getItem('admin_token');
    const hospitalToken = localStorage.getItem('hospital_token');
    const orgToken = localStorage.getItem('organization_token');
    const tenantId = localStorage.getItem('hospital_tenant_id');
    
    // Add appropriate token
    if (adminToken) {
      config.headers.Authorization = `Bearer ${adminToken}`;
    } else if (hospitalToken) {
      config.headers.Authorization = `Bearer ${hospitalToken}`;
    } else if (orgToken) {
      config.headers.Authorization = `Bearer ${orgToken}`;
    }
    
    // Add tenant ID for hospital requests
    if (tenantId) {
      config.headers['X-Tenant-ID'] = tenantId;
    }
    
    console.log(`🔗 API Request: ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => {
    console.error('🚨 API Request Error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    console.log(`✅ API Response: ${response.config.method?.toUpperCase()} ${response.config.url} - ${response.status}`);
    return response;
  },
  (error) => {
    console.error('🚨 API Response Error:', error);
    
    if (error.response?.status === 401) {
      // Unauthorized - clear storage and redirect to appropriate login
      const currentPath = window.location.pathname;
      
      if (currentPath.startsWith('/admin')) {
        localStorage.removeItem('admin_token');
        window.location.href = '/admin/login';
      } else if (currentPath.startsWith('/hospital')) {
        localStorage.removeItem('hospital_token');
        localStorage.removeItem('hospital_tenant_id');
        localStorage.removeItem('hospital_info');
        window.location.href = '/hospital/login';
      } else if (currentPath.startsWith('/organization')) {
        localStorage.removeItem('organization_token');
        localStorage.removeItem('organization_info');
        window.location.href = '/organization/login';
      }
    }
    
    return Promise.reject(error);
  }
);

// Authentication API
export const authAPI = {
  // Admin login
  adminLogin: async (credentials) => {
    const response = await api.post('/admin/login', credentials);
    return response.data;
  },
  
  // Hospital login
  hospitalLogin: async (credentials) => {
    const response = await api.post('/hospital/login', credentials);
    return response.data;
  },
  
  // Organization login
  organizationLogin: async (credentials) => {
    const response = await api.post('/organization/login', credentials);
    return response.data;
  },
  
  // Validate token
  validateToken: async () => {
    const response = await api.get('/auth/validate');
    return response.data;
  },
  
  // Get current user info
  getCurrentUser: async () => {
    const response = await api.get('/auth/me');
    return response.data;
  }
};

// Location API
export const locationAPI = {
  // Get all countries
  getCountries: async () => {
    const response = await api.get('/locations/countries');
    return response.data;
  },
  
  // Get states by country
  getStates: async (countryId) => {
    const url = countryId ? `/locations/states?countryId=${countryId}` : '/locations/states';
    const response = await api.get(url);
    return response.data;
  },
  
  // Get cities by state
  getCitiesByState: async (stateId) => {
    const response = await api.get(`/hospital/cities-by-state?stateId=${stateId}`);
    return response.data;
  },
  
  // Get hospitals by city
  getHospitalsByCity: async (city, stateId) => {
    const response = await api.get(`/hospital/hospitals-by-city?city=${city}&stateId=${stateId}`);
    return response.data;
  },
  
  // Get location hierarchy
  getLocationHierarchy: async () => {
    const response = await api.get('/locations/hierarchy');
    return response.data;
  }
};

// Admin API
export const adminAPI = {
  // Get system statistics
  getSystemStats: async () => {
    const response = await api.get('/admin/stats');
    return response.data;
  },
  
  // Hospital management
  createHospital: async (hospitalData) => {
    const response = await api.post('/admin/hospitals', hospitalData);
    return response.data;
  },
  
  getHospitals: async (page = 0, size = 20) => {
    const response = await api.get(`/admin/hospitals?page=${page}&size=${size}`);
    return response.data;
  },
  
  getHospitalById: async (id) => {
    const response = await api.get(`/admin/hospitals/${id}`);
    return response.data;
  },
  
  updateHospital: async (id, hospitalData) => {
    const response = await api.put(`/admin/hospitals/${id}`, hospitalData);
    return response.data;
  },
  
  // Organization management
  createOrganization: async (orgData) => {
    const response = await api.post('/admin/organizations', orgData);
    return response.data;
  },
  
  getOrganizations: async (page = 0, size = 20) => {
    const response = await api.get(`/admin/organizations?page=${page}&size=${size}`);
    return response.data;
  },
  
  getOrganizationById: async (id) => {
    const response = await api.get(`/admin/organizations/${id}`);
    return response.data;
  },
  
  updateOrganization: async (id, orgData) => {
    const response = await api.put(`/admin/organizations/${id}`, orgData);
    return response.data;
  }
};

// Hospital API
export const hospitalAPI = {
  // Donor management
  registerDonor: async (donorData) => {
    const response = await api.post('/hospital/donors', donorData);
    return response.data;
  },
  
  getDonors: async (page = 0, size = 20, search = '') => {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });
    
    if (search) {
      params.append('search', search);
    }
    
    const response = await api.get(`/hospital/donors?${params}`);
    return response.data;
  },
  
  getDonorById: async (id) => {
    const response = await api.get(`/hospital/donors/${id}`);
    return response.data;
  },
  
  updateDonor: async (id, donorData) => {
    const response = await api.put(`/hospital/donors/${id}`, donorData);
    return response.data;
  },
  
  // Patient management
  registerPatient: async (patientData) => {
    const response = await api.post('/hospital/patients', patientData);
    return response.data;
  },
  
  getPatients: async (page = 0, size = 20, search = '') => {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });
    
    if (search) {
      params.append('search', search);
    }
    
    const response = await api.get(`/hospital/patients?${params}`);
    return response.data;
  },
  
  getPatientById: async (id) => {
    const response = await api.get(`/hospital/patients/${id}`);
    return response.data;
  },
  
  updatePatient: async (id, patientData) => {
    const response = await api.put(`/hospital/patients/${id}`, patientData);
    return response.data;
  },
  
  // Matching dashboard
  getMatches: async (page = 0, size = 20) => {
    const response = await api.get(`/hospital/matches?page=${page}&size=${size}`);
    return response.data;
  },
  
  getMatchById: async (id) => {
    const response = await api.get(`/hospital/matches/${id}`);
    return response.data;
  },
  
  approveMatch: async (id) => {
    const response = await api.post(`/hospital/matches/${id}/approve`);
    return response.data;
  },
  
  rejectMatch: async (id, reason) => {
    const response = await api.post(`/hospital/matches/${id}/reject`, { reason });
    return response.data;
  }
};

// Organization API
export const organizationAPI = {
  // Policy management
  proposePolicy: async (policyData) => {
    const response = await api.post('/organization/policies', policyData);
    return response.data;
  },
  
  getPolicies: async (page = 0, size = 20, status = '') => {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });
    
    if (status) {
      params.append('status', status);
    }
    
    const response = await api.get(`/organization/policies?${params}`);
    return response.data;
  },
  
  getPolicyById: async (id) => {
    const response = await api.get(`/organization/policies/${id}`);
    return response.data;
  },
  
  voteOnPolicy: async (id, vote) => {
    const response = await api.post(`/organization/policies/${id}/vote`, { vote });
    return response.data;
  },
  
  getPolicyHistory: async () => {
    const response = await api.get('/organization/policies/history');
    return response.data;
  }
};

// Blockchain API
export const blockchainAPI = {
  // Get transaction history
  getTransactions: async (page = 0, size = 20) => {
    const response = await api.get(`/blockchain/transactions?page=${page}&size=${size}`);
    return response.data;
  },
  
  // Get transaction by hash
  getTransactionByHash: async (hash) => {
    const response = await api.get(`/blockchain/transactions/${hash}`);
    return response.data;
  },
  
  // Get IPFS records
  getIpfsRecords: async (page = 0, size = 20) => {
    const response = await api.get(`/blockchain/ipfs?page=${page}&size=${size}`);
    return response.data;
  },
  
  // Get IPFS record by CID
  getIpfsRecordByCid: async (cid) => {
    const response = await api.get(`/blockchain/ipfs/${cid}`);
    return response.data;
  }
};

// Utility functions
export const apiUtils = {
  // Check if user is authenticated
  isAuthenticated: () => {
    const adminToken = localStorage.getItem('admin_token');
    const hospitalToken = localStorage.getItem('hospital_token');
    const orgToken = localStorage.getItem('organization_token');
    return !!(adminToken || hospitalToken || orgToken);
  },
  
  // Get user role
  getUserRole: () => {
    if (localStorage.getItem('admin_token')) return 'ADMIN';
    if (localStorage.getItem('hospital_token')) return 'HOSPITAL';
    if (localStorage.getItem('organization_token')) return 'ORGANIZATION';
    return null;
  },
  
  // Clear all auth data
  clearAuthData: () => {
    localStorage.removeItem('admin_token');
    localStorage.removeItem('hospital_token');
    localStorage.removeItem('organization_token');
    localStorage.removeItem('hospital_tenant_id');
    localStorage.removeItem('hospital_info');
    localStorage.removeItem('organization_info');
  }
};

export default {
  authAPI,
  locationAPI,
  adminAPI,
  hospitalAPI,
  organizationAPI,
  blockchainAPI,
  apiUtils
};