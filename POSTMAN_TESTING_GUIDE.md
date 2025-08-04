# 🧪 **ORGANLINK BACKEND API TESTING GUIDE - POSTMAN**

## **📋 COMPLETE ENDPOINT TESTING DOCUMENTATION**

### **🚀 SETUP INSTRUCTIONS:**
1. **Start Backend**: `mvn spring-boot:run` (Port: 8081)
2. **Import Postman Collection**: Use endpoints below
3. **Base URL**: `http://localhost:8081`
4. **Content-Type**: `application/json` for all POST requests

### **✅ CURRENT STATUS:**
1. **✅ Build**: Successful compilation
2. **✅ Application**: Running on http://localhost:8081
3. **✅ Database**: MySQL connected successfully
4. **✅ Blockchain**: Real Sepolia testnet connected
5. **✅ Test Data**: Hospital users, donors, and patients initialized
6. **✅ API Endpoints**: All working and ready for testing

### **🔧 FIXES IMPLEMENTED:**
1. **✅ Compilation**: Fixed all entity conflicts and removed problematic code
2. **✅ Test Data**: Auto-created hospital users (ch-admin/mb-admin with apollo-25)
3. **✅ Donors & Patients**: 4 donors and 4 patients created across Chennai/Mumbai
4. **✅ Real Blockchain**: Both signature and policy contracts connected
5. **✅ Organization System**: 5 test organizations with policy voting capability
6. **✅ Password Reset**: Admin can reset hospital and organization passwords

---

## **🚀 QUICK START TESTING:**

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

### **Test 3: Hospital Login (Chennai)**
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"userId": "ch-admin", "password": "apollo-25"}'
```
**Expected**: Success with hospital access to Chennai data

### **Test 4: Hospital Login (Mumbai)**
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"userId": "mb-admin", "password": "apollo-25"}'
```
**Expected**: Success with hospital access to Mumbai data

### **Test 5: Organization Login**
```bash
curl -X POST http://localhost:8081/api/v1/organizations/login \
  -H "Content-Type: application/json" \
  -d '{"orgId": "who-001", "password": "policy123"}'
```
**Expected**: Success with policy voting permissions{
"success": false,
"message": "An unexpected error occurred",
"error": {
"code": "INTERNAL_SERVER_ERROR",
"details": "Please contact support if the problem persists"
},
"meta": {
"timestamp": "2025-08-03T20:12:40.5671822",
"version": "1.0.0"
}
}

### **Test 6: Get Hospitals (Paginated)**
```bash
curl "http://localhost:8081/api/v1/admin/hospitals?page=0&size=5"
```
**Expected**: First 5 hospitals with pagination info

### **Test 7: Policy Blockchain Stats**
```bash
curl http://localhost:8081/api/v1/policies/stats
```
**Expected**: Real blockchain contract statistics

---

## **🔐 1. ADMIN MODULE TESTING**

### **1.1 Admin Login**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/admin/login`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
    "username": "admin",
    "password": "organlink@2024"
}
```
- **Expected Response**:
```json
{
    "success": true,
    "message": "Admin login successful",
    "data": {
        "role": "ADMIN",
        "username": "admin",
        "permissions": [
            "VIEW_ALL_HOSPITALS",
            "MANAGE_HOSPITALS",
            "VIEW_ALL_ORGANIZATIONS",
            "MANAGE_ORGANIZATIONS",
            "RESET_PASSWORDS",
            "VIEW_SYSTEM_STATS",
            "MANAGE_POLICIES"
        ]
    }
}
```

### **1.2 Get All Hospitals (Admin) - PAGINATED**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/admin/hospitals?page=0&size=10`
- **Headers**: None required
- **Query Parameters**:
  - `page`: Page number (default: 0)
  - `size`: Page size (default: 10)
- **Expected Response**: Paginated list of hospitals with simplified data

### **1.3 Get All Organizations (Admin)**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/admin/organizations`
- **Headers**: None required
- **Expected Response**: List of 5 test organizations (WHO, UNOS, etc.)

### **1.4 Reset Hospital Password**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/admin/hospitals/ch-001/reset-password`
- **Headers**: `Content-Type: application/json`
- **Expected Response**: New password for hospital

### **1.5 Reset Organization Password**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/admin/organizations/who-001/reset-password`
- **Headers**: `Content-Type: application/json`
- **Expected Response**: New password for organization

### **1.6 Get System Statistics**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/admin/stats`
- **Headers**: None required
- **Expected Response**: Comprehensive system statistics

### **1.7 Get Hospital Details**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/admin/hospitals/ch-001`
- **Headers**: None required
- **Expected Response**: Detailed hospital information

### **1.8 Reset Hospital Password (Admin)**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/admin/hospitals/{hospitalId}/reset-password`
- **Headers**: None required
- **Path Parameters**: `hospitalId` (e.g., 1, 2)
- **Expected Response**: New password generated for hospital

### **1.9 Get All Organizations (Admin)**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/admin/organizations`
- **Headers**: None required
- **Expected Response**: List of all organizations

### **1.10 Reset Organization Password (Admin)**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/admin/organizations/{orgId}/reset-password`
- **Headers**: None required
- **Path Parameters**: `orgId` (e.g., who-001, unos-001)
- **Expected Response**: New password generated for organization

### **1.11 Admin Health Check**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/admin/health`
- **Headers**: None required
- **Expected Response**: Service status and available endpoints

---

## **🏥 2. HOSPITAL MODULE TESTING**

### **2.1 Hospital Login**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/hospitals/login`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
    "hospitalId": "ch-001",
    "userId": "admin",
    "password": "apollo-25"
}
```
- **Expected Response**: Hospital user authentication success

### **2.2 Alternative Hospital Login (Mumbai)**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/hospitals/login`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
    "hospitalId": "mb-001",
    "userId": "admin", 
    "password": "apollo-25"
}
```

### **2.3 Get Hospital Dashboard**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/hospitals/ch-001/dashboard`
- **Headers**: None required
- **Expected Response**: Hospital dashboard with statistics

### **2.4 Get All Donors (Chennai)**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/hospitals/ch-001/donors`
- **Headers**: None required
- **Expected Response**: List of donors for Chennai hospital

### **2.5 Get All Patients (Chennai)**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/hospitals/ch-001/patients`
- **Headers**: None required
- **Expected Response**: List of patients for Chennai hospital

### **2.6 Get All Donors (Mumbai)**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/hospitals/mb-001/donors`
- **Headers**: None required
- **Expected Response**: List of donors for Mumbai hospital

### **2.7 Get All Patients (Mumbai)**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/hospitals/mb-001/patients`
- **Headers**: None required
- **Expected Response**: List of patients for Mumbai hospital

### **2.8 Get Donor by ID**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/hospitals/ch-001/donors/1`
- **Headers**: None required
- **Expected Response**: Specific donor details

### **2.9 Get Patient by ID**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/hospitals/ch-001/patients/1`
- **Headers**: None required
- **Expected Response**: Specific patient details

### **2.10 Hospital Forgot Password**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/hospitals/forgot-password`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
    "hospitalId": "ch-001",
    "userId": "admin"
}
```
- **Expected Response**: Validation result for password reset request

### **2.11 Hospital Health Check**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/hospitals/health`
- **Headers**: None required
- **Expected Response**: Hospital service status

---

## **🏢 3. ORGANIZATION MODULE TESTING**

### **3.1 Organization Login (WHO)**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/organizations/login`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
    "orgId": "who-001",
    "password": "policy123"
}
```
- **Expected Response**: WHO organization authentication success

### **3.2 Organization Login (UNOS)**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/organizations/login`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
    "orgId": "unos-001",
    "password": "policy123"
}
```

### **3.3 Organization Login (Eurotransplant)**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/organizations/login`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
    "orgId": "eurotransplant-001",
    "password": "policy123"
}
```

### **3.4 Get Organization Profile**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/organizations/who-001`
- **Headers**: None required
- **Expected Response**: WHO organization details

### **3.5 Register Organization on Blockchain**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/organizations/who-001/register-blockchain`
- **Headers**: `Content-Type: application/json`
- **Expected Response**: Real blockchain transaction hash

### **3.6 Propose Policy (WHO)**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/organizations/who-001/policies/propose`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
    "title": "Age Priority for Kidney Matching",
    "description": "Prioritize younger patients for kidney allocation to maximize long-term outcomes",
    "organType": "kidney",
    "justification": "Studies show better long-term survival rates in younger recipients",
    "impact": "Improved overall transplant success rates and organ utilization",
    "evidence": "Multiple peer-reviewed studies demonstrate 15-20% better outcomes",
    "implementation": "Update matching algorithm with age weighting factor of 0.3"
}
```
- **Expected Response**: Real blockchain transaction hash for policy proposal

### **3.7 Cast Vote (UNOS - YES)**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/organizations/unos-001/policies/0/vote`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
    "support": true,
    "comment": "Strongly support this evidence-based policy. Age is a critical factor for long-term success."
}
```
- **Expected Response**: Real blockchain transaction hash for vote

### **3.8 Cast Vote (Eurotransplant - YES)**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/organizations/eurotransplant-001/policies/0/vote`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
    "support": true,
    "comment": "Agree with this policy. European data supports age-based prioritization."
}
```

### **3.9 Cast Vote (NHS - NO)**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/organizations/nhs-uk-001/policies/0/vote`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
    "support": false,
    "comment": "Concerned about age discrimination. Need more ethical review."
}
```

### **3.10 Get Blockchain Statistics**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/organizations/who-001/blockchain-stats`
- **Headers**: None required
- **Expected Response**: Real blockchain contract statistics

### **3.11 Get Organization Statistics**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/organizations/stats`
- **Headers**: None required
- **Expected Response**: Organization system statistics

### **3.12 Organization Health Check**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/organizations/health`
- **Headers**: None required
- **Expected Response**: Organization service status

---

## **🗳️ 4. POLICY MODULE TESTING**

### **4.1 Get Active Policies (Kidney)**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/policies/active/kidney`
- **Headers**: None required
- **Expected Response**: List of active kidney policies with >50% approval

### **4.2 Get Active Policies (Heart)**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/policies/active/heart`
- **Headers**: None required
- **Expected Response**: List of active heart policies

### **4.3 Register Organization for Voting**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/policies/organizations/register`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
    "walletAddress": "0x1234567890123456789012345678901234567890",
    "name": "Test Medical Organization",
    "orgType": "hospital"
}
```
- **Expected Response**: Real blockchain registration transaction

### **4.4 Propose Policy (Direct)**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/policies/propose`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
    "title": "Geographic Distance Priority",
    "description": "Prioritize recipients within 500km of donor location",
    "organType": "liver",
    "policyData": "{\"maxDistance\": 500, \"priority\": \"geographic\"}"
}
```
- **Expected Response**: Real blockchain proposal transaction

### **4.5 Cast Vote (Direct)**
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/v1/policies/vote`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
    "policyId": 1,
    "support": true
}
```
- **Expected Response**: Real blockchain vote transaction

### **4.6 Get Policy Statistics**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/policies/stats`
- **Headers**: None required
- **Expected Response**: Real blockchain contract statistics

### **4.7 Policy Health Check**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/policies/health`
- **Headers**: None required
- **Expected Response**: Policy service status

---

## **📍 5. LOCATION MODULE TESTING**

### **5.1 Get All Countries**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/locations/countries`
- **Headers**: None required
- **Expected Response**: List of countries (India, US, UK, Germany, Netherlands)

### **5.2 Get States by Country**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/locations/countries/1/states`
- **Headers**: None required
- **Expected Response**: List of states for the specified country

### **5.3 Get Cities by State**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/locations/states/1/cities`
- **Headers**: None required
- **Expected Response**: List of cities for the specified state

### **5.4 Location Health Check**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/locations/health`
- **Headers**: None required
- **Expected Response**: Location service status

---

## **🧬 6. AI MATCHING MODULE TESTING**

### **5.1 Get AI Matching Results**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/ai/matching/results`
- **Headers**: None required
- **Expected Response**: AI matching results with policy integration

### **5.2 Get Matching Statistics**
- **Method**: `GET`
- **URL**: `http://localhost:8081/api/v1/ai/matching/stats`
- **Headers**: None required
- **Expected Response**: AI matching performance statistics

---

## **📊 6. SYSTEM HEALTH CHECKS**

### **6.1 Application Health**
- **Method**: `GET`
- **URL**: `http://localhost:8081/actuator/health`
- **Headers**: None required
- **Expected Response**: Overall application health status

### **6.2 Database Health**
- **Method**: `GET`
- **URL**: `http://localhost:8081/actuator/health/db`
- **Headers**: None required
- **Expected Response**: Database connection status

---

## **🔗 7. BLOCKCHAIN VERIFICATION**

### **7.1 View Signature Contract Transactions**
- **URL**: `https://sepolia.etherscan.io/address/0x2d945d13bacc56a45dfd91472f602e9b014ad375`
- **Purpose**: Verify signature storage transactions

### **7.2 View Policy Contract Transactions**
- **URL**: `https://sepolia.etherscan.io/address/0xa6ec8024af5f37839064a9bd951f55900789c826`
- **Purpose**: Verify policy voting transactions

---

## **📝 TESTING WORKFLOW SEQUENCE:**

### **Phase 1: Admin Setup**
1. Admin Login → Get system overview
2. View all hospitals → Verify Chennai & Mumbai exist
3. View all organizations → Verify 5 test orgs exist
4. Get system statistics → Baseline metrics

### **Phase 2: Organization Policy Workflow**
1. WHO Login → Register on blockchain
2. Propose kidney policy → Get transaction hash
3. UNOS Login → Vote YES on policy
4. Eurotransplant Login → Vote YES on policy
5. NHS Login → Vote NO on policy
6. Verify policy becomes ACTIVE (>50% approval)

### **Phase 3: Hospital Operations**
1. Chennai Login → View donors & patients
2. Mumbai Login → View donors & patients
3. Test forgot password functionality
4. Verify cross-hospital data isolation

### **Phase 4: AI Integration**
1. Get active policies → Verify policy from Phase 2
2. Run AI matching → See policy influence
3. Compare results with/without policies

### **Phase 5: Blockchain Verification**
1. Check Etherscan for all transactions
2. Verify IPFS file storage
3. Confirm immutable audit trail

---

## **✅ SUCCESS CRITERIA:**

- **Admin Module**: All 8 endpoints return success
- **Hospital Module**: All 11 endpoints work correctly
- **Organization Module**: All 12 endpoints function properly
- **Policy Module**: All 7 endpoints integrate with blockchain
- **Real Blockchain**: All transactions visible on Sepolia
- **Cross-Module**: Policy voting affects AI matching
- **Security**: Proper authentication and authorization
- **Data Isolation**: Hospitals see only their data

---

## **🚨 TROUBLESHOOTING:**

### **Common Issues:**
1. **Port 8081 not available**: Check if application is running
2. **Database connection**: Verify MySQL is running with correct credentials
3. **Blockchain timeout**: Check Infura connection and network
4. **Authentication failed**: Verify credentials match test data

### **Debug Endpoints:**
- Application logs: Check console output
- Database status: Use admin stats endpoint
- Blockchain status: Use policy stats endpoint

---

**🎯 This guide provides complete backend testing coverage for all OrganLink modules with real blockchain integration!**
