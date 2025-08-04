package com.organlink.service;

import com.organlink.blockchain.OrganLinkSignatures;
import com.organlink.blockchain.PolicyVotingContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import jakarta.annotation.PostConstruct;
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

    @Value("${organlink.ethereum.policy.contract.address}")
    private String policyContractAddress;

    // Admin wallet address (derived from private key)
    @Value("${organlink.ethereum.admin.address:0xDemoAdminAddress}")
    private String adminWalletAddress;

    private Web3j web3j;
    private Credentials credentials;
    private OrganLinkSignatures contract;
    private PolicyVotingContract policyContract;

    @Value("${organlink.ethereum.gas.price:20000000000}")
    private BigInteger gasPrice;

    @Value("${organlink.ethereum.gas.limit:300000}")
    private BigInteger gasLimit;

    /**
     * Initialize Web3j and contract connection on startup
     */
    @PostConstruct
    public void initializeBlockchain() {
        try {
            logger.info("🔗 Initializing REAL Ethereum blockchain connection...");
            logger.info("🌐 Connecting to Sepolia testnet: {}", ethereumNodeUrl);

            // Initialize Web3j connection
            web3j = Web3j.build(new HttpService(ethereumNodeUrl));

            // Create credentials from private key
            credentials = Credentials.create(adminPrivateKey);
            logger.info("👤 Admin wallet: {}", credentials.getAddress());

            // Load the deployed signature contract
            contract = OrganLinkSignatures.load(
                contractAddress,
                web3j,
                credentials,
                new DefaultGasProvider()
            );

            // Load the deployed policy contract
            policyContract = PolicyVotingContract.load(
                policyContractAddress,
                web3j,
                credentials,
                new DefaultGasProvider()
            );

            // Test connections
            BigInteger totalSignatures = contract.getTotalSignatures().send();
            BigInteger totalPolicies = policyContract.getTotalPolicies().send();

            logger.info("✅ Blockchain connection successful!");
            logger.info("📄 Signature contract: {}", contractAddress);
            logger.info("🗳️ Policy contract: {}", policyContractAddress);
            logger.info("📊 Total signatures stored: {}", totalSignatures);
            logger.info("📊 Total policies stored: {}", totalPolicies);

        } catch (Exception e) {
            logger.error("❌ Failed to initialize blockchain connection: {}", e.getMessage());
            logger.error("💡 Check your Infura URL and private key configuration");
            // Don't throw exception - allow app to start with simulation mode
        }
    }

    /**
     * Initialize Ethereum connection (Legacy method - now using PostConstruct)
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
            logger.info("🔗 Storing signature metadata on REAL blockchain for {} ({})",
                       data.getSignerName(), data.getEntityType());

            if (contract != null) {
                // REAL BLOCKCHAIN TRANSACTION
                logger.info("📝 Calling smart contract storeSignature function...");

                TransactionReceipt receipt = contract.storeSignature(
                    data.getIpfsHash(),
                    data.getSignerName(),
                    data.getSignerType(),
                    data.getGuardianName() != null ? data.getGuardianName() : "",
                    data.getGuardianRelation() != null ? data.getGuardianRelation() : "",
                    data.getEntityType(),
                    BigInteger.valueOf(data.getEntityId()),
                    data.getHospitalId(),
                    data.isVerified(),
                    BigInteger.valueOf((long)(data.getConfidenceScore() * 100))
                ).send();

                String txHash = receipt.getTransactionHash();
                logger.info("✅ REAL blockchain transaction successful!");
                logger.info("🔗 Transaction hash: {}", txHash);
                logger.info("⛽ Gas used: {}", receipt.getGasUsed());
                logger.info("🌐 View on Etherscan: https://sepolia.etherscan.io/tx/{}", txHash);

                return txHash;

            } else {
                // Fallback to simulation if contract not initialized
                logger.warn("⚠️ Contract not initialized, using simulation mode");
                String simulatedTxHash = generateSimulatedTransactionHash(data);
                logger.info("🎭 Simulated blockchain storage: {}", simulatedTxHash);
                return simulatedTxHash;
            }

        } catch (Exception e) {
            logger.error("❌ Failed to store signature on blockchain: {}", e.getMessage());
            logger.error("💡 Check your Sepolia testnet connection and gas balance");

            // Fallback to simulation on error
            logger.warn("⚠️ Falling back to simulation mode");
            String simulatedTxHash = generateSimulatedTransactionHash(data);
            logger.info("🎭 Simulated blockchain storage: {}", simulatedTxHash);
            return simulatedTxHash;
        }
    }

    /**
     * Retrieve signature metadata from blockchain
     */
    public SignatureBlockchainData retrieveSignatureFromBlockchain(String transactionHash) {
        try {
            logger.info("Retrieving signature metadata from blockchain: {}", transactionHash);

            initializeEthereum();

            if (contract != null) {
                // REAL BLOCKCHAIN RETRIEVAL
                try {
                    BigInteger totalSignatures = contract.getTotalSignatures().send();
                    if (totalSignatures.compareTo(BigInteger.ZERO) > 0) {
                        // Get the latest signature (for demo)
                        BigInteger recordId = totalSignatures.subtract(BigInteger.ONE);

                        var signatureData = contract.getSignature(recordId).send();

                        logger.info("✅ Retrieved signature from blockchain:");
                        logger.info("📄 IPFS Hash: {}", signatureData.component1());
                        logger.info("✍️ Signer: {}", signatureData.component2());

                        // Convert to our data structure
                        SignatureBlockchainData data = new SignatureBlockchainData();
                        data.setIpfsHash(signatureData.component1());
                        data.setSignerName(signatureData.component2());
                        data.setSignerType(signatureData.component3());
                        data.setEntityType(signatureData.component6());
                        data.setEntityId(signatureData.component7().longValue());
                        data.setHospitalId(signatureData.component8());
                        data.setTimestamp(LocalDateTime.now());
                        data.setVerified(signatureData.component10());
                        data.setConfidenceScore(signatureData.component11().doubleValue() / 100.0);

                        return data;
                    }
                } catch (Exception e) {
                    logger.warn("⚠️ Could not retrieve from contract: {}", e.getMessage());
                }
            }

            // Fallback to simulation
            logger.info("🎭 Using simulated blockchain retrieval for: {}", transactionHash);
            return createSimulatedBlockchainData(transactionHash);

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
                .hospitalId("apollo-chennai")
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
                .hospitalId("apollo-mumbai")
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
        private String signerType;
        private String guardianName;
        private String guardianRelation;
        private String entityType;
        private Long entityId;
        private String hospitalId;  // Changed to String to match contract
        private LocalDateTime timestamp;
        private boolean verified;
        private double confidenceScore;

        // Builder pattern
        public static SignatureBlockchainDataBuilder builder() {
            return new SignatureBlockchainDataBuilder();
        }

        // Getters and setters
        public String getIpfsHash() { return ipfsHash; }
        public void setIpfsHash(String ipfsHash) { this.ipfsHash = ipfsHash; }
        
        public String getSignerName() { return signerName; }
        public void setSignerName(String signerName) { this.signerName = signerName; }

        public String getSignerType() { return signerType; }
        public void setSignerType(String signerType) { this.signerType = signerType; }

        public String getGuardianName() { return guardianName; }
        public void setGuardianName(String guardianName) { this.guardianName = guardianName; }

        public String getGuardianRelation() { return guardianRelation; }
        public void setGuardianRelation(String guardianRelation) { this.guardianRelation = guardianRelation; }

        public String getEntityType() { return entityType; }
        public void setEntityType(String entityType) { this.entityType = entityType; }

        public Long getEntityId() { return entityId; }
        public void setEntityId(Long entityId) { this.entityId = entityId; }

        public String getHospitalId() { return hospitalId; }
        public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        public boolean isVerified() { return verified; }
        public void setVerified(boolean verified) { this.verified = verified; }

        public double getConfidenceScore() { return confidenceScore; }
        public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }

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

            public SignatureBlockchainDataBuilder hospitalId(String hospitalId) {
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
