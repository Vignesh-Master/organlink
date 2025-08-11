/**
 * OrganLink Integration Test
 * 
 * This script tests the integration between the React frontend and Spring Boot backend.
 * It verifies that API calls can be made successfully and data can be retrieved.
 */

// Import the API integration service
const { authAPI, locationAPI, apiUtils } = require('./api-integration');

// Mock localStorage for testing
const localStorageMock = (() => {
  let store = {};
  return {
    getItem: (key) => store[key],
    setItem: (key, value) => { store[key] = value.toString(); },
    removeItem: (key) => { delete store[key]; },
    clear: () => { store = {}; }
  };
})();

// Assign mock to global object
global.localStorage = localStorageMock;

// Test configuration
const config = {
  apiBaseUrl: 'http://localhost:8081/api/v1',
  credentials: {
    hospital: {
      countryId: '1',
      stateId: '1',
      city: 'New York',
      hospitalId: '1',
      userId: 'hospital_user',
      password: 'password123'
    },
    admin: {
      username: 'admin',
      password: 'admin123'
    },
    organization: {
      username: 'org_user',
      password: 'org123'
    }
  }
};

// Test functions
async function testHospitalLogin() {
  console.log('\n🔍 Testing Hospital Login...');
  try {
    const response = await authAPI.hospitalLogin(config.credentials.hospital);
    console.log('✅ Hospital Login Successful');
    console.log('Token:', response.data.token);
    localStorage.setItem('hospital_token', response.data.token);
    localStorage.setItem('hospital_tenant_id', response.data.tenantId);
    return true;
  } catch (error) {
    console.error('❌ Hospital Login Failed:', error.message);
    return false;
  }
}

async function testAdminLogin() {
  console.log('\n🔍 Testing Admin Login...');
  try {
    const response = await authAPI.adminLogin(config.credentials.admin);
    console.log('✅ Admin Login Successful');
    console.log('Token:', response.data.token);
    localStorage.setItem('admin_token', response.data.token);
    return true;
  } catch (error) {
    console.error('❌ Admin Login Failed:', error.message);
    return false;
  }
}

async function testOrganizationLogin() {
  console.log('\n🔍 Testing Organization Login...');
  try {
    const response = await authAPI.organizationLogin(config.credentials.organization);
    console.log('✅ Organization Login Successful');
    console.log('Token:', response.data.token);
    localStorage.setItem('organization_token', response.data.token);
    return true;
  } catch (error) {
    console.error('❌ Organization Login Failed:', error.message);
    return false;
  }
}

async function testGetCountries() {
  console.log('\n🔍 Testing Get Countries API...');
  try {
    const response = await locationAPI.getCountries();
    console.log(`✅ Retrieved ${response.data.length} countries`);
    return true;
  } catch (error) {
    console.error('❌ Get Countries Failed:', error.message);
    return false;
  }
}

async function testTokenValidation() {
  console.log('\n🔍 Testing Token Validation...');
  try {
    const response = await authAPI.validateToken();
    console.log('✅ Token Validation Successful');
    return true;
  } catch (error) {
    console.error('❌ Token Validation Failed:', error.message);
    return false;
  }
}

// Run all tests
async function runTests() {
  console.log('=================================================');
  console.log('🚀 OrganLink Integration Test');
  console.log('=================================================\n');
  
  console.log(`🔗 API Base URL: ${config.apiBaseUrl}`);
  
  // Test authentication
  const hospitalLoginSuccess = await testHospitalLogin();
  if (hospitalLoginSuccess) {
    await testTokenValidation();
  }
  
  // Clear hospital token and test admin login
  localStorage.removeItem('hospital_token');
  localStorage.removeItem('hospital_tenant_id');
  const adminLoginSuccess = await testAdminLogin();
  
  // Clear admin token and test organization login
  localStorage.removeItem('admin_token');
  const orgLoginSuccess = await testOrganizationLogin();
  
  // Test API endpoints
  await testGetCountries();
  
  // Summary
  console.log('\n=================================================');
  console.log('📊 Test Summary');
  console.log('=================================================');
  console.log(`Hospital Login: ${hospitalLoginSuccess ? '✅ Passed' : '❌ Failed'}`);
  console.log(`Admin Login: ${adminLoginSuccess ? '✅ Passed' : '❌ Failed'}`);
  console.log(`Organization Login: ${orgLoginSuccess ? '✅ Passed' : '❌ Failed'}`);
  
  console.log('\n=================================================');
  console.log('Integration test completed!');
  console.log('=================================================');
}

// Run the tests
runTests().catch(error => {
  console.error('Test execution error:', error);
});