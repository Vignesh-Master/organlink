import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AdminLayout from '../../components/AdminLayout';

const HospitalManagement = () => {
  const navigate = useNavigate();
  const [hospitals, setHospitals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [newHospital, setNewHospital] = useState({
    name: '',
    address: '',
    city: '',
    stateId: 1,
    countryId: 1,
    contactNumber: '',
    emailAddress: '',
    licenseNumber: '',
    userId: '',
    password: ''
  });

  useEffect(() => {
    // Check admin authentication
    const adminToken = localStorage.getItem('admin_token');
    if (!adminToken) {
      navigate('/admin/login');
      return;
    }

    loadHospitals();
  }, [navigate, currentPage, searchTerm]);

  const loadHospitals = async () => {
    try {
      setLoading(true);
      
      // For demo purposes, use simulated data
      // In production: await adminAPI.getHospitals(currentPage, 10, searchTerm)
      setTimeout(() => {
        const sampleHospitals = [
          {
            id: 1,
            name: 'Apollo Hospital Chennai',
            city: 'Chennai',
            contactNumber: '+91-44-2829-3333',
            emailAddress: 'admin@apollochennai.com',
            isActive: true,
            tenantId: 'apollo-chennai',
            createdAt: '2024-01-15T10:30:00'
          },
          {
            id: 2,
            name: 'Apollo Hospital Mumbai',
            city: 'Mumbai',
            contactNumber: '+91-22-6767-4444',
            emailAddress: 'admin@apollomumbai.com',
            isActive: true,
            tenantId: 'apollo-mumbai',
            createdAt: '2024-01-20T14:15:00'
          },
          {
            id: 3,
            name: 'AIIMS Delhi',
            city: 'Delhi',
            contactNumber: '+91-11-2658-8500',
            emailAddress: 'admin@aiims.edu',
            isActive: true,
            tenantId: 'aiims-delhi',
            createdAt: '2024-02-01T09:00:00'
          }
        ];

        const filteredHospitals = searchTerm 
          ? sampleHospitals.filter(h => 
              h.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
              h.city.toLowerCase().includes(searchTerm.toLowerCase())
            )
          : sampleHospitals;

        setHospitals(filteredHospitals);
        setTotalPages(1);
        setLoading(false);
      }, 1000);
      
    } catch (error) {
      console.error('Failed to load hospitals:', error);
      setLoading(false);
    }
  };

  const handleCreateHospital = async (e) => {
    e.preventDefault();
    
    try {
      // For demo purposes, simulate API call
      // In production: await adminAPI.createHospital(newHospital)
      
      const createdHospital = {
        id: hospitals.length + 1,
        ...newHospital,
        isActive: true,
        tenantId: newHospital.name.toLowerCase().replace(/[^a-z0-9]/g, '-'),
        createdAt: new Date().toISOString()
      };

      setHospitals(prev => [createdHospital, ...prev]);
      setShowCreateModal(false);
      setNewHospital({
        name: '',
        address: '',
        city: '',
        stateId: 1,
        countryId: 1,
        contactNumber: '',
        emailAddress: '',
        licenseNumber: '',
        userId: '',
        password: ''
      });

      alert('Hospital created successfully!');
      
    } catch (error) {
      console.error('Failed to create hospital:', error);
      alert('Failed to create hospital. Please try again.');
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewHospital(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const toggleHospitalStatus = async (hospitalId, currentStatus) => {
    try {
      // For demo purposes, simulate API call
      // In production: await adminAPI.updateHospital(hospitalId, { isActive: !currentStatus })
      
      setHospitals(prev => prev.map(hospital => 
        hospital.id === hospitalId 
          ? { ...hospital, isActive: !currentStatus }
          : hospital
      ));

      alert(`Hospital ${!currentStatus ? 'activated' : 'deactivated'} successfully!`);
      
    } catch (error) {
      console.error('Failed to update hospital status:', error);
      alert('Failed to update hospital status. Please try again.');
    }
  };

  return (
    <AdminLayout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Hospital Management</h1>
            <p className="text-gray-600 mt-2">Create and manage hospital accounts</p>
          </div>
          <button
            onClick={() => setShowCreateModal(true)}
            className="bg-red-600 text-white px-6 py-3 rounded-lg hover:bg-red-700 transition-colors flex items-center"
          >
            <svg className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
            Create Hospital
          </button>
        </div>

        {/* Search and Filters */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div className="flex flex-col sm:flex-row gap-4">
            <div className="flex-1">
              <input
                type="text"
                placeholder="Search hospitals by name or city..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-red-500"
              />
            </div>
            <button
              onClick={loadHospitals}
              className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center"
            >
              <svg className="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              Refresh
            </button>
          </div>
        </div>

        {/* Hospitals Table */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200">
          <div className="px-6 py-4 border-b border-gray-200">
            <h2 className="text-lg font-semibold text-gray-900">Hospitals ({hospitals.length})</h2>
          </div>
          
          {loading ? (
            <div className="p-8 text-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-red-600 mx-auto"></div>
              <p className="text-gray-500 mt-2">Loading hospitals...</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Hospital
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Contact
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Status
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Created
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {hospitals.map((hospital) => (
                    <tr key={hospital.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div>
                          <div className="text-sm font-medium text-gray-900">{hospital.name}</div>
                          <div className="text-sm text-gray-500">{hospital.city}</div>
                          <div className="text-xs text-gray-400">ID: {hospital.tenantId}</div>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm text-gray-900">{hospital.contactNumber}</div>
                        <div className="text-sm text-gray-500">{hospital.emailAddress}</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                          hospital.isActive 
                            ? 'bg-green-100 text-green-800' 
                            : 'bg-red-100 text-red-800'
                        }`}>
                          {hospital.isActive ? 'Active' : 'Inactive'}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {new Date(hospital.createdAt).toLocaleDateString()}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                        <button
                          onClick={() => toggleHospitalStatus(hospital.id, hospital.isActive)}
                          className={`px-3 py-1 rounded text-xs font-medium ${
                            hospital.isActive
                              ? 'bg-red-100 text-red-700 hover:bg-red-200'
                              : 'bg-green-100 text-green-700 hover:bg-green-200'
                          }`}
                        >
                          {hospital.isActive ? 'Deactivate' : 'Activate'}
                        </button>
                        <button className="text-blue-600 hover:text-blue-900 px-3 py-1 rounded text-xs font-medium bg-blue-100 hover:bg-blue-200">
                          Edit
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Create Hospital Modal */}
        {showCreateModal && (
          <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
            <div className="relative top-20 mx-auto p-5 border w-11/12 md:w-3/4 lg:w-1/2 shadow-lg rounded-md bg-white">
              <div className="mt-3">
                <div className="flex items-center justify-between mb-4">
                  <h3 className="text-lg font-medium text-gray-900">Create New Hospital</h3>
                  <button
                    onClick={() => setShowCreateModal(false)}
                    className="text-gray-400 hover:text-gray-600"
                  >
                    <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </div>

                <form onSubmit={handleCreateHospital} className="space-y-4">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Hospital Name *
                      </label>
                      <input
                        type="text"
                        name="name"
                        value={newHospital.name}
                        onChange={handleInputChange}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-red-500 focus:border-red-500"
                        required
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        City *
                      </label>
                      <input
                        type="text"
                        name="city"
                        value={newHospital.city}
                        onChange={handleInputChange}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-red-500 focus:border-red-500"
                        required
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Contact Number *
                      </label>
                      <input
                        type="tel"
                        name="contactNumber"
                        value={newHospital.contactNumber}
                        onChange={handleInputChange}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-red-500 focus:border-red-500"
                        required
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Email Address *
                      </label>
                      <input
                        type="email"
                        name="emailAddress"
                        value={newHospital.emailAddress}
                        onChange={handleInputChange}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-red-500 focus:border-red-500"
                        required
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        User ID *
                      </label>
                      <input
                        type="text"
                        name="userId"
                        value={newHospital.userId}
                        onChange={handleInputChange}
                        placeholder="e.g., dl-001"
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-red-500 focus:border-red-500"
                        required
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Password *
                      </label>
                      <input
                        type="password"
                        name="password"
                        value={newHospital.password}
                        onChange={handleInputChange}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-red-500 focus:border-red-500"
                        required
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Address
                    </label>
                    <textarea
                      name="address"
                      value={newHospital.address}
                      onChange={handleInputChange}
                      rows={3}
                      className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-red-500 focus:border-red-500"
                    />
                  </div>

                  <div className="flex justify-end space-x-3 pt-4">
                    <button
                      type="button"
                      onClick={() => setShowCreateModal(false)}
                      className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700"
                    >
                      Create Hospital
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        )}
      </div>
    </AdminLayout>
  );
};

export default HospitalManagement;
