# 🎯 **SIMPLE ADMIN TESTING GUIDE - ORGANLINK**

## **📋 CURRENT STATUS:**
- **✅ Admin Module**: Working (login, create hospitals/organizations, manage)
- **❌ Hospital Login**: Removed (will add after admin works)
- **❌ Organization Login**: Removed (will add after admin works)
- **❌ Complex Features**: Removed (focus on basics first)

---

## **🚀 QUICK START - ADMIN ONLY:**

### **Test 1: Application Health Check**
```bash
curl http://localhost:8081/api/v1/actuator/health
```
**Expected**: `{"status":"UP"}`

### **Test 2: Admin Login**
```bash
curl -X POST http://localhost:8081/api/v1/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "organlink@2024"}'
```
**Expected**: Success with admin permissions

### **Test 3: Get All Hospitals (Simple List)**
```bash
curl http://localhost:8081/api/v1/admin/hospitals
```
**Expected**: Simple list like organizations (not paginated)

### **Test 4: Clear All Data (Start Fresh)**
```bash
curl -X DELETE http://localhost:8081/api/v1/admin/clear-all-data
```
**Expected**: All hospitals and organizations deleted

### **Test 5: Get All Organizations (Should be Empty)**
```bash
curl http://localhost:8081/api/v1/admin/organizations
```
**Expected**: Empty list `{"organizations": [], "count": 0}`

### **Test 6: Create Hospital (with Country & State Selection)**
```bash
curl -X POST http://localhost:8081/api/v1/admin/hospitals \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Apollo Hospital Chennai",
    "code": "CH-001",
    "city": "Chennai",
    "address": "21, Greams Lane, Chennai",
    "contactNumber": "+91-44-2829-3333",
    "email": "chennai@apollohospitals.com",
    "countryId": 1,
    "stateId": 1,
    "userId": "ch-admin",
    "password": "apollo-25"
  }'
```
**Expected**: Hospital created with country (India) and state (Tamil Nadu) linkage

### **Test 7: Create Organization (Simple - Only Name, ID, Password)**
```bash
curl -X POST http://localhost:8081/api/v1/admin/organizations \
  -H "Content-Type: application/json" \
  -d '{
    "orgId": "who-001",
    "name": "World Health Organization",
    "password": "policy123"
  }'
```
**Expected**: Global organization created for policy voting

---

## **🏥 ADMIN MODULE ENDPOINTS:**

### **1.1 Admin Login**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/admin/login`
- **Body**:
```json
{
  "username": "admin",
  "password": "organlink@2024"
}
```
- **Expected Response**: Success with admin token

### **1.2 Get All Hospitals (Simple)**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/admin/hospitals`
- **Headers**: None required
- **Expected Response**: Simple list format
```json
{
  "success": true,
  "data": {
    "hospitals": [
      {
        "id": 1,
        "name": "Hospital Name",
        "code": "H-001",
        "city": "City",
        "isActive": true
      }
    ],
    "count": 1
  }
}
```

### **1.3 Create Hospital (with Country & State Selection)**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/admin/hospitals`
- **Workflow**:
  1. First get countries: `GET /api/v1/locations/countries`
  2. Then get states: `GET /api/v1/locations/states?countryId=1`
  3. Create hospital with selected country and state
- **Body**:
```json
{
  "name": "Apollo Hospital Chennai",
  "code": "CH-001",
  "city": "Chennai",
  "address": "21, Greams Lane, Chennai",
  "contactNumber": "+91-44-2829-3333",
  "email": "chennai@apollohospitals.com",
  "countryId": 1,
  "stateId": ,
  "userId": "ch-admin",
  "password": "apollo-25"
}
```
- **Expected Response**: Hospital created with country (India) and state (Tamil Nadu) linkage

### **1.4 Get Individual Hospital**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/admin/hospitals/{hospitalId}`
- **Path Parameters**: `hospitalId` (e.g., 1, 2)
- **Expected Response**: Hospital details

### **1.5 Update Hospital**
- **Method**: `PUT`
- **URL**: `http://localhost:8081/api/v1/admin/hospitals/{hospitalId}`
- **Body**:
```json
{
  "name": "Updated Hospital Name",
  "address": "Updated Address",
  "city": "Updated City",
  "contactNumber": "+91-44-9999999",
  "emailAddress": "updated@hospital.com",
  "isActive": true
}
```

### **1.6 Delete Hospital**
- **Method**: `DELETE`
- **URL**: `http://localhost:8081/api/v1/admin/hospitals/{hospitalId}`
- **Expected Response**: Hospital and user deleted

### **1.7 Reset Hospital Password**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/admin/hospitals/{hospitalId}/reset-password`
- **Expected Response**: New password generated

### **1.8 Get All Organizations**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/admin/organizations`
- **Expected Response**: List of 5 test organizations

### **1.9 Create Organization (Simple - Global Organizations)**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/admin/organizations`
- **Note**: Organizations like WHO, UNOS, Eurotransplant are unique global entities
- **Body**:
```json
{
  "orgId": "who-001",
  "name": "World Health Organization",
  "password": "policy123"
}
```
- **Other Examples**:
```json
{
  "orgId": "unos-001",
  "name": "United Network for Organ Sharing",
  "password": "policy123"
}
```
```json
{
  "orgId": "eurotransplant-001",
  "name": "Eurotransplant",
  "password": "policy123"
}
```

### **1.10 Delete Organization**
- **Method**: `DELETE`
- **URL**: `http://localhost:8081/api/v1/admin/organizations/{orgId}`
- **Expected Response**: Organization deleted

### **1.11 Reset Organization Password**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/admin/organizations/{orgId}/reset-password`
- **Expected Response**: New password generated

### **1.12 System Statistics**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/admin/stats`
- **Expected Response**: System statistics

---

## **📍 LOCATION MODULE ENDPOINTS:**

### **2.1 Get Countries**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/locations/countries`
- **Expected Response**: List of countries

### **2.2 Get States by Country**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/locations/countries/{countryId}/states`
- **Expected Response**: States for the country

### **2.3 Get Cities by State**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/locations/states/{stateId}/cities`
- **Expected Response**: Cities for the state

---

## **🎯 TESTING WORKFLOW:**

### **Phase 1: Basic Admin Functions**
1. **Health Check**: Verify application is running
2. **Admin Login**: Test admin authentication
3. **View Data**: Get hospitals and organizations lists
4. **Create Hospital**: Create test hospital with user
5. **Create Organization**: Create test organization
6. **Reset Passwords**: Test password reset functionality
7. **Delete Items**: Test deletion functionality

### **Phase 2: After Admin Works (Next Steps)**
1. **Add Hospital Login**: Simple hospital authentication
2. **Add Organization Login**: Simple organization authentication
3. **Add Basic Data**: Donors and patients (without OCR)
4. **Add Policy Functions**: Basic policy operations

---

## **🔧 TROUBLESHOOTING:**

### **Common Issues:**
1. **Port 8081 not responding**: Check if application started successfully
2. **Admin login fails**: Verify credentials (admin/organlink@2024)
3. **Hospital creation fails**: Check required fields and database connection
4. **Internal server errors**: Check application logs for specific errors

### **Success Indicators:**
- **✅ Health check returns UP**
- **✅ Admin login returns success**
- **✅ Hospital list returns simple format (not paginated)**
- **✅ Organization list returns 5 test organizations**
- **✅ Hospital creation works with user account**

---

## **🎉 GOAL:**

**Get the admin module working perfectly first, then add other modules one by one.**

**Once admin functions work 100%, we'll add:**
1. Hospital login (simple)
2. Organization login (simple)
3. Basic data management
4. Policy operations

**Focus: Make admin module bulletproof before adding complexity!**
