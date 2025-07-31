// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

/**
 * Global Collaboration Portal - Policy Voting Smart Contract
 * 
 * YOUR CORRECT REQUIREMENT:
 * - Organizations propose policies (e.g., "MELD score for kidney = 30")
 * - Other organizations vote on policies
 * - Policies with >50% votes become active
 * - AI uses active policies as additional metrics
 */
contract PolicyVotingContract {
    
    // Admin address (OrganLink admin)
    address public admin;
    
    // Organization structure
    struct Organization {
        string name;
        address walletAddress;
        bool isActive;
        uint256 registrationDate;
    }
    
    // Policy structure
    struct Policy {
        uint256 policyId;
        string organType;        // kidney, liver, heart, etc.
        string policyName;       // e.g., "MELD_SCORE_THRESHOLD"
        string description;      // Human readable description
        double metricValue;      // The actual metric value (e.g., 30 for MELD score)
        address proposer;        // Organization that proposed
        uint256 proposalDate;
        uint256 votingDeadline;
        bool isActive;
        uint256 yesVotes;
        uint256 noVotes;
        uint256 totalEligibleVoters;
    }
    
    // Vote structure
    struct Vote {
        address voter;
        bool support;           // true = yes, false = no
        uint256 voteDate;
        string reason;          // Optional reason for vote
    }
    
    // Mappings
    mapping(address => Organization) public organizations;
    mapping(uint256 => Policy) public policies;
    mapping(uint256 => mapping(address => Vote)) public policyVotes;
    mapping(address => bool) public isRegisteredOrg;
    
    // Arrays for iteration
    address[] public organizationList;
    uint256[] public policyList;
    
    // Counters
    uint256 public policyCounter;
    uint256 public organizationCounter;
    
    // Events
    event OrganizationRegistered(address indexed orgAddress, string name);
    event PolicyProposed(uint256 indexed policyId, string organType, string policyName, address proposer);
    event VoteCast(uint256 indexed policyId, address indexed voter, bool support);
    event PolicyActivated(uint256 indexed policyId, string organType, string policyName);
    event PolicyRejected(uint256 indexed policyId, string organType, string policyName);
    
    // Modifiers
    modifier onlyAdmin() {
        require(msg.sender == admin, "Only admin can perform this action");
        _;
    }
    
    modifier onlyRegisteredOrg() {
        require(isRegisteredOrg[msg.sender], "Only registered organizations can perform this action");
        _;
    }
    
    modifier validPolicy(uint256 _policyId) {
        require(_policyId > 0 && _policyId <= policyCounter, "Invalid policy ID");
        _;
    }
    
    // Constructor
    constructor() {
        admin = msg.sender;
    }
    
    /**
     * Register new organization (admin only)
     */
    function registerOrganization(address _orgAddress, string memory _name) 
        public onlyAdmin {
        
        require(!isRegisteredOrg[_orgAddress], "Organization already registered");
        require(bytes(_name).length > 0, "Organization name cannot be empty");
        
        organizations[_orgAddress] = Organization({
            name: _name,
            walletAddress: _orgAddress,
            isActive: true,
            registrationDate: block.timestamp
        });
        
        isRegisteredOrg[_orgAddress] = true;
        organizationList.push(_orgAddress);
        organizationCounter++;
        
        emit OrganizationRegistered(_orgAddress, _name);
    }
    
    /**
     * Propose new policy (registered organizations only)
     * Example: proposePolicy("kidney", "MELD_SCORE_THRESHOLD", "Minimum MELD score for kidney allocation", 30)
     */
    function proposePolicy(
        string memory _organType,
        string memory _policyName,
        string memory _description,
        double _metricValue
    ) public onlyRegisteredOrg returns (uint256) {
        
        require(bytes(_organType).length > 0, "Organ type cannot be empty");
        require(bytes(_policyName).length > 0, "Policy name cannot be empty");
        require(_metricValue >= 0, "Metric value must be non-negative");
        
        policyCounter++;
        
        policies[policyCounter] = Policy({
            policyId: policyCounter,
            organType: _organType,
            policyName: _policyName,
            description: _description,
            metricValue: _metricValue,
            proposer: msg.sender,
            proposalDate: block.timestamp,
            votingDeadline: block.timestamp + 30 days, // 30 days voting period
            isActive: false,
            yesVotes: 0,
            noVotes: 0,
            totalEligibleVoters: organizationCounter
        });
        
        policyList.push(policyCounter);
        
        emit PolicyProposed(policyCounter, _organType, _policyName, msg.sender);
        
        return policyCounter;
    }
    
    /**
     * Vote on policy (registered organizations only)
     */
    function voteOnPolicy(uint256 _policyId, bool _support, string memory _reason) 
        public onlyRegisteredOrg validPolicy(_policyId) {
        
        Policy storage policy = policies[_policyId];
        
        require(block.timestamp <= policy.votingDeadline, "Voting period has ended");
        require(policyVotes[_policyId][msg.sender].voter == address(0), "Already voted on this policy");
        require(msg.sender != policy.proposer, "Proposer cannot vote on their own policy");
        
        // Record vote
        policyVotes[_policyId][msg.sender] = Vote({
            voter: msg.sender,
            support: _support,
            voteDate: block.timestamp,
            reason: _reason
        });
        
        // Update vote counts
        if (_support) {
            policy.yesVotes++;
        } else {
            policy.noVotes++;
        }
        
        emit VoteCast(_policyId, msg.sender, _support);
        
        // Check if policy should be activated (>50% yes votes)
        uint256 totalVotes = policy.yesVotes + policy.noVotes;
        if (totalVotes >= policy.totalEligibleVoters / 2) { // At least 50% participation
            uint256 yesPercentage = (policy.yesVotes * 100) / totalVotes;
            
            if (yesPercentage > 50) {
                policy.isActive = true;
                emit PolicyActivated(_policyId, policy.organType, policy.policyName);
            } else {
                emit PolicyRejected(_policyId, policy.organType, policy.policyName);
            }
        }
    }
    
    /**
     * Get active policies for specific organ type
     * This is called by AI matching system
     */
    function getActivePolicies(string memory _organType) 
        public view returns (uint256[] memory, string[] memory, double[] memory) {
        
        // Count active policies for this organ type
        uint256 activeCount = 0;
        for (uint256 i = 1; i <= policyCounter; i++) {
            if (policies[i].isActive && 
                keccak256(abi.encodePacked(policies[i].organType)) == 
                keccak256(abi.encodePacked(_organType))) {
                activeCount++;
            }
        }
        
        // Create arrays for active policies
        uint256[] memory policyIds = new uint256[](activeCount);
        string[] memory policyNames = new string[](activeCount);
        double[] memory metricValues = new double[](activeCount);
        
        uint256 index = 0;
        for (uint256 i = 1; i <= policyCounter; i++) {
            if (policies[i].isActive && 
                keccak256(abi.encodePacked(policies[i].organType)) == 
                keccak256(abi.encodePacked(_organType))) {
                
                policyIds[index] = policies[i].policyId;
                policyNames[index] = policies[i].policyName;
                metricValues[index] = policies[i].metricValue;
                index++;
            }
        }
        
        return (policyIds, policyNames, metricValues);
    }
    
    /**
     * Get policy details
     */
    function getPolicyDetails(uint256 _policyId) 
        public view validPolicy(_policyId) returns (
            string memory organType,
            string memory policyName,
            string memory description,
            double metricValue,
            address proposer,
            uint256 yesVotes,
            uint256 noVotes,
            bool isActive,
            uint256 votePercentage
        ) {
        
        Policy memory policy = policies[_policyId];
        uint256 totalVotes = policy.yesVotes + policy.noVotes;
        uint256 percentage = totalVotes > 0 ? (policy.yesVotes * 100) / totalVotes : 0;
        
        return (
            policy.organType,
            policy.policyName,
            policy.description,
            policy.metricValue,
            policy.proposer,
            policy.yesVotes,
            policy.noVotes,
            policy.isActive,
            percentage
        );
    }
    
    /**
     * Get all policies (for admin dashboard)
     */
    function getAllPolicies() public view returns (uint256[] memory) {
        return policyList;
    }
    
    /**
     * Get organization details
     */
    function getOrganization(address _orgAddress) 
        public view returns (string memory name, bool isActive, uint256 registrationDate) {
        
        Organization memory org = organizations[_orgAddress];
        return (org.name, org.isActive, org.registrationDate);
    }
    
    /**
     * Get total number of registered organizations
     */
    function getTotalOrganizations() public view returns (uint256) {
        return organizationCounter;
    }
    
    /**
     * Get total number of policies
     */
    function getTotalPolicies() public view returns (uint256) {
        return policyCounter;
    }
    
    /**
     * Emergency: Deactivate policy (admin only)
     */
    function deactivatePolicy(uint256 _policyId) public onlyAdmin validPolicy(_policyId) {
        policies[_policyId].isActive = false;
    }
    
    /**
     * Update admin (admin only)
     */
    function updateAdmin(address _newAdmin) public onlyAdmin {
        admin = _newAdmin;
    }
}
