// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

/**
 * OrganLink Signature Verification Smart Contract
 * Stores IPFS hashes and metadata for organ donation signatures
 * 
 * This contract will be deployed on Ethereum testnet (Sepolia/Goerli)
 * Admin wallet will be the only one making transactions
 */
contract OrganLinkSignatures {
    
    // Admin address (your centralized wallet)
    address public admin;
    
    // Signature record structure
    struct SignatureRecord {
        string ipfsHash;           // IPFS hash of signature image
        string signerName;         // Name of person who signed
        string signerType;         // SELF or GUARDIAN
        string guardianName;       // Guardian name if applicable
        string guardianRelation;   // Relationship to patient/donor
        string entityType;         // DONOR or PATIENT
        uint256 entityId;          // Donor/Patient ID
        string hospitalId;         // Hospital identifier
        uint256 timestamp;         // When signature was verified
        bool isVerified;           // AI verification status
        uint256 confidenceScore;   // AI confidence (0-100)
    }
    
    // Mapping from record ID to signature record
    mapping(uint256 => SignatureRecord) public signatures;
    
    // Counter for signature records
    uint256 public signatureCount;
    
    // Events
    event SignatureStored(
        uint256 indexed recordId,
        string ipfsHash,
        string signerName,
        string entityType,
        string hospitalId
    );
    
    event SignatureVerified(
        uint256 indexed recordId,
        bool isVerified,
        uint256 confidenceScore
    );
    
    // Modifier to restrict access to admin only
    modifier onlyAdmin() {
        require(msg.sender == admin, "Only admin can perform this action");
        _;
    }
    
    // Constructor
    constructor() {
        admin = msg.sender; // The deployer becomes admin
    }
    
    /**
     * Store signature record on blockchain
     * Only admin can call this function
     */
    function storeSignature(
        string memory _ipfsHash,
        string memory _signerName,
        string memory _signerType,
        string memory _guardianName,
        string memory _guardianRelation,
        string memory _entityType,
        uint256 _entityId,
        string memory _hospitalId,
        bool _isVerified,
        uint256 _confidenceScore
    ) public onlyAdmin returns (uint256) {
        
        signatureCount++;
        
        signatures[signatureCount] = SignatureRecord({
            ipfsHash: _ipfsHash,
            signerName: _signerName,
            signerType: _signerType,
            guardianName: _guardianName,
            guardianRelation: _guardianRelation,
            entityType: _entityType,
            entityId: _entityId,
            hospitalId: _hospitalId,
            timestamp: block.timestamp,
            isVerified: _isVerified,
            confidenceScore: _confidenceScore
        });
        
        emit SignatureStored(
            signatureCount,
            _ipfsHash,
            _signerName,
            _entityType,
            _hospitalId
        );
        
        emit SignatureVerified(
            signatureCount,
            _isVerified,
            _confidenceScore
        );
        
        return signatureCount;
    }
    
    /**
     * Get signature record by ID
     */
    function getSignature(uint256 _recordId) public view returns (
        string memory ipfsHash,
        string memory signerName,
        string memory signerType,
        string memory guardianName,
        string memory guardianRelation,
        string memory entityType,
        uint256 entityId,
        string memory hospitalId,
        uint256 timestamp,
        bool isVerified,
        uint256 confidenceScore
    ) {
        require(_recordId > 0 && _recordId <= signatureCount, "Invalid record ID");
        
        SignatureRecord memory record = signatures[_recordId];
        
        return (
            record.ipfsHash,
            record.signerName,
            record.signerType,
            record.guardianName,
            record.guardianRelation,
            record.entityType,
            record.entityId,
            record.hospitalId,
            record.timestamp,
            record.isVerified,
            record.confidenceScore
        );
    }
    
    /**
     * Verify signature integrity using IPFS hash
     */
    function verifySignatureIntegrity(uint256 _recordId, string memory _ipfsHash) 
        public view returns (bool) {
        require(_recordId > 0 && _recordId <= signatureCount, "Invalid record ID");
        
        return keccak256(abi.encodePacked(signatures[_recordId].ipfsHash)) == 
               keccak256(abi.encodePacked(_ipfsHash));
    }
    
    /**
     * Get total number of signatures stored
     */
    function getTotalSignatures() public view returns (uint256) {
        return signatureCount;
    }
    
    /**
     * Change admin (only current admin can do this)
     */
    function changeAdmin(address _newAdmin) public onlyAdmin {
        admin = _newAdmin;
    }
}
