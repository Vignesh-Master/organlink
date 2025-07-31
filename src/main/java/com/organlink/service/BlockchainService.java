package com.organlink.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for Ethereum blockchain operations
 * Stores IPFS hashes and metadata on blockchain for immutable record-keeping
 * 
 * How Ethereum stores data:
 * - Smart contracts store key-value pairs
 * - Each transaction gets a unique hash
 * - Data is immutable once stored
 * - Distributed across thousands of nodes
 * - Cryptographically secured
 */
@Service
public class BlockchainService {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainService.class);

    // Centralized Admin Wallet Configuration
    @Value("${organlink.ethereum.node.url:https://sepolia.infura.io/v3/demo}")
    private String ethereumNodeUrl;

    @Value("${organlink.ethereum.admin.private.key:DEMO_PRIVATE_KEY}")
    private String adminPrivateKey;

    @Value("${organlink.ethereum.contract.address:0xDemoContractAddress}")
    private String contractAddress;

    // Admin wallet address (derived from private key)
    @Value("${organlink.ethereum.admin.address:0xDemoAdminAddress}")
    private String adminWalletAddress;

    private Web3j web3j;
    private Credentials credentials;

    /**
     * Initialize Ethereum connection
     */
    private void initializeEthereum() {
        if (web3j == null) {
            try {
                // Connect to Ethereum node (Sepolia testnet for demo)
                web3j = Web3j.build(new HttpService(ethereumNodeUrl));
                
                if (adminPrivateKey != null && !adminPrivateKey.isEmpty()) {
                    credentials = Credentials.create(adminPrivateKey);
                    logger.info("Connected to Ethereum network: {}", ethereumNodeUrl);
                } else {
                    logger.warn("No private key configured, using simulated blockchain operations");
                }
            } catch (Exception e) {
                logger.error("Failed to connect to Ethereum network: {}", e.getMessage());
                logger.warn("Using simulated blockchain operations for demo");
            }
        }
    }

    /**
     * Store signature metadata on Ethereum blockchain
     * This creates an immutable record linking IPFS hash to patient/donor
     */
    public String storeSignatureOnBlockchain(SignatureBlockchainData data) {
        try {
            logger.info("Storing signature metadata on blockchain for {} ({})", 
                       data.getSignerName(), data.getEntityType());

            initializeEthereum();

            if (web3j != null && credentials != null) {
                // In a real implementation, this would call a smart contract
                // For demo, we'll simulate the transaction
                String txHash = simulateBlockchainTransaction(data);
                logger.info("Signature metadata stored on blockchain: {}", txHash);
                return txHash;
            } else {
                // Simulate blockchain transaction for demo
                String simulatedTxHash = generateSimulatedTransactionHash(data);
                logger.info("Simulated blockchain storage: {}", simulatedTxHash);
                return simulatedTxHash;
            }

        } catch (Exception e) {
            logger.error("Failed to store signature on blockchain: {}", e.getMessage());
            throw new RuntimeException("Blockchain storage failed: " + e.getMessage());
        }
    }

    /**
     * Retrieve signature metadata from blockchain
     */
    public SignatureBlockchainData retrieveSignatureFromBlockchain(String transactionHash) {
        try {
            logger.info("Retrieving signature metadata from blockchain: {}", transactionHash);

            initializeEthereum();

            if (web3j != null) {
                // In real implementation, this would query the smart contract
                // For demo, we'll simulate the retrieval
                return simulateBlockchainRetrieval(transactionHash);
            } else {
                // Simulate retrieval for demo
                logger.info("Simulated blockchain retrieval for tx: {}", transactionHash);
                return createSimulatedBlockchainData(transactionHash);
            }

        } catch (Exception e) {
            logger.error("Failed to retrieve signature from blockchain: {}", e.getMessage());
            throw new RuntimeException("Blockchain retrieval failed: " + e.getMessage());
        }
    }

    /**
     * Verify signature integrity using blockchain
     */
    public boolean verifySignatureIntegrity(String ipfsHash, String transactionHash) {
        try {
            logger.info("Verifying signature integrity: IPFS={}, TX={}", ipfsHash, transactionHash);

            SignatureBlockchainData blockchainData = retrieveSignatureFromBlockchain(transactionHash);
            boolean isValid = ipfsHash.equals(blockchainData.getIpfsHash());
            
            logger.info("Signature integrity verification: {}", isValid ? "VALID" : "INVALID");
            return isValid;

        } catch (Exception e) {
            logger.error("Failed to verify signature integrity: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Simulate blockchain transaction for demo purposes
     */
    private String simulateBlockchainTransaction(SignatureBlockchainData data) {
        // Generate realistic transaction hash
        String txData = data.getIpfsHash() + data.getSignerName() + data.getEntityType() + System.currentTimeMillis();
        return "0x" + Integer.toHexString(txData.hashCode()).toLowerCase() + "abcdef123456789012345678";
    }

    /**
     * Simulate blockchain retrieval for demo purposes
     */
    private SignatureBlockchainData simulateBlockchainRetrieval(String transactionHash) {
        return SignatureBlockchainData.builder()
                .ipfsHash("QmSimulated" + transactionHash.substring(2, 12))
                .signerName("Simulated Signer")
                .entityType("DONOR")
                .entityId(1L)
                .hospitalId(1L)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create simulated blockchain data
     */
    private SignatureBlockchainData createSimulatedBlockchainData(String transactionHash) {
        return SignatureBlockchainData.builder()
                .ipfsHash("QmDemo" + transactionHash.substring(2, 12))
                .signerName("Demo Signer")
                .entityType("PATIENT")
                .entityId(1L)
                .hospitalId(1L)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Generate simulated transaction hash
     */
    private String generateSimulatedTransactionHash(SignatureBlockchainData data) {
        String hashInput = data.getIpfsHash() + data.getSignerName() + System.currentTimeMillis();
        return "0x" + Integer.toHexString(hashInput.hashCode()).toLowerCase() + "demo123456789012345678";
    }

    /**
     * Data class for blockchain storage
     */
    public static class SignatureBlockchainData {
        private String ipfsHash;
        private String signerName;
        private String entityType;
        private Long entityId;
        private Long hospitalId;
        private LocalDateTime timestamp;

        // Builder pattern
        public static SignatureBlockchainDataBuilder builder() {
            return new SignatureBlockchainDataBuilder();
        }

        // Getters and setters
        public String getIpfsHash() { return ipfsHash; }
        public void setIpfsHash(String ipfsHash) { this.ipfsHash = ipfsHash; }
        
        public String getSignerName() { return signerName; }
        public void setSignerName(String signerName) { this.signerName = signerName; }
        
        public String getEntityType() { return entityType; }
        public void setEntityType(String entityType) { this.entityType = entityType; }
        
        public Long getEntityId() { return entityId; }
        public void setEntityId(Long entityId) { this.entityId = entityId; }
        
        public Long getHospitalId() { return hospitalId; }
        public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        public static class SignatureBlockchainDataBuilder {
            private SignatureBlockchainData data = new SignatureBlockchainData();

            public SignatureBlockchainDataBuilder ipfsHash(String ipfsHash) {
                data.setIpfsHash(ipfsHash);
                return this;
            }

            public SignatureBlockchainDataBuilder signerName(String signerName) {
                data.setSignerName(signerName);
                return this;
            }

            public SignatureBlockchainDataBuilder entityType(String entityType) {
                data.setEntityType(entityType);
                return this;
            }

            public SignatureBlockchainDataBuilder entityId(Long entityId) {
                data.setEntityId(entityId);
                return this;
            }

            public SignatureBlockchainDataBuilder hospitalId(Long hospitalId) {
                data.setHospitalId(hospitalId);
                return this;
            }

            public SignatureBlockchainDataBuilder timestamp(LocalDateTime timestamp) {
                data.setTimestamp(timestamp);
                return this;
            }

            public SignatureBlockchainData build() {
                return data;
            }
        }
    }
}
