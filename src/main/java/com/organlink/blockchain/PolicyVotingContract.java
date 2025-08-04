package com.organlink.blockchain;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * Auto generated code for PolicyVotingContract.
 * Do not modify!
 * Generated with web3j version 4.10.3.
 */
@SuppressWarnings("rawtypes")
public class PolicyVotingContract extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_APPROVAL_THRESHOLD = "APPROVAL_THRESHOLD";
    public static final String FUNC_VOTING_PERIOD = "VOTING_PERIOD";
    public static final String FUNC_ADMIN = "admin";
    public static final String FUNC_CASTVOTE = "castVote";
    public static final String FUNC_CHANGEADMIN = "changeAdmin";
    public static final String FUNC_EXECUTEPOLICY = "executePolicy";
    public static final String FUNC_GETACTIVEPOLICIES = "getActivePolicies";
    public static final String FUNC_GETELIGIBLEVOTERS = "getEligibleVoters";
    public static final String FUNC_GETORGANIZATIONNAME = "getOrganizationName";
    public static final String FUNC_GETORGANIZATIONSTATUS = "getOrganizationStatus";
    public static final String FUNC_GETORGANIZATIONTYPE = "getOrganizationType";
    public static final String FUNC_GETPOLICYDATA = "getPolicyData";
    public static final String FUNC_GETPOLICYDESCRIPTION = "getPolicyDescription";
    public static final String FUNC_GETPOLICYORGANTYPE = "getPolicyOrganType";
    public static final String FUNC_GETPOLICYPROPOSER = "getPolicyProposer";
    public static final String FUNC_GETPOLICYSTATUS = "getPolicyStatus";
    public static final String FUNC_GETPOLICYTIMES = "getPolicyTimes";
    public static final String FUNC_GETPOLICYTITLE = "getPolicyTitle";
    public static final String FUNC_GETPOLICYVOTES = "getPolicyVotes";
    public static final String FUNC_GETTOTALORGANIZATIONS = "getTotalOrganizations";
    public static final String FUNC_GETTOTALPOLICIES = "getTotalPolicies";
    public static final String FUNC_GETVOTE = "getVote";
    public static final String FUNC_GETVOTECOUNT = "getVoteCount";
    public static final String FUNC_HASVOTED = "hasVoted";
    public static final String FUNC_ORGANIZATIONADDRESSES = "organizationAddresses";
    public static final String FUNC_ORGANIZATIONCOUNT = "organizationCount";
    public static final String FUNC_ORGANIZATIONS = "organizations";
    public static final String FUNC_POLICIES = "policies";
    public static final String FUNC_POLICYCOUNT = "policyCount";
    public static final String FUNC_POLICYDATA = "policyData";
    public static final String FUNC_POLICYDESCRIPTIONS = "policyDescriptions";
    public static final String FUNC_POLICYVOTES = "policyVotes";
    public static final String FUNC_PROPOSEPOLICY = "proposePolicy";
    public static final String FUNC_REGISTERORGANIZATION = "registerOrganization";
    public static final String FUNC_UPDATEORGANIZATIONSTATUS = "updateOrganizationStatus";

    public static final Event ORGANIZATIONREGISTERED_EVENT = new Event("OrganizationRegistered", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Utf8String>() {}));

    public static final Event POLICYEXECUTED_EVENT = new Event("PolicyExecuted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Bool>() {}));

    public static final Event POLICYPROPOSED_EVENT = new Event("PolicyProposed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Address>(true) {}));

    public static final Event VOTECAST_EVENT = new Event("VoteCast", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Bool>() {}));

    @Deprecated
    protected PolicyVotingContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PolicyVotingContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected PolicyVotingContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected PolicyVotingContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<BigInteger> APPROVAL_THRESHOLD() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_APPROVAL_THRESHOLD, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> VOTING_PERIOD() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_VOTING_PERIOD, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> admin() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ADMIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> castVote(BigInteger _policyId, Boolean _support) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CASTVOTE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_policyId), 
                new org.web3j.abi.datatypes.Bool(_support)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> changeAdmin(String _newAdmin) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CHANGEADMIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _newAdmin)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> executePolicy(BigInteger _policyId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_EXECUTEPOLICY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_policyId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<List> getActivePolicies(String _organType) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETACTIVEPOLICIES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_organType)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getEligibleVoters() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETELIGIBLEVOTERS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> getOrganizationName(String _wallet) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETORGANIZATIONNAME, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _wallet)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple2<Boolean, Boolean>> getOrganizationStatus(String _wallet) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETORGANIZATIONSTATUS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _wallet)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}, new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple2<Boolean, Boolean>>(function,
                new Callable<Tuple2<Boolean, Boolean>>() {
                    @Override
                    public Tuple2<Boolean, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<Boolean, Boolean>(
                                (Boolean) results.get(0).getValue(), 
                                (Boolean) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<String> getOrganizationType(String _wallet) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETORGANIZATIONTYPE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _wallet)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getPolicyData(BigInteger _policyId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPOLICYDATA,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_policyId)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getPolicyDescription(BigInteger _policyId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPOLICYDESCRIPTION,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_policyId)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getPolicyOrganType(BigInteger _policyId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPOLICYORGANTYPE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_policyId)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getPolicyProposer(BigInteger _policyId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPOLICYPROPOSER,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_policyId)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple2<Boolean, Boolean>> getPolicyStatus(BigInteger _policyId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPOLICYSTATUS,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_policyId)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}, new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple2<Boolean, Boolean>>(function,
                new Callable<Tuple2<Boolean, Boolean>>() {
                    @Override
                    public Tuple2<Boolean, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<Boolean, Boolean>(
                                (Boolean) results.get(0).getValue(),
                                (Boolean) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<Tuple2<BigInteger, BigInteger>> getPolicyTimes(BigInteger _policyId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPOLICYTIMES,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_policyId)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<BigInteger, BigInteger>>(function,
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(),
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<String> getPolicyTitle(BigInteger _policyId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPOLICYTITLE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_policyId)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple2<BigInteger, BigInteger>> getPolicyVotes(BigInteger _policyId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPOLICYVOTES,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_policyId)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<BigInteger, BigInteger>>(function,
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(),
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getTotalOrganizations() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETTOTALORGANIZATIONS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getTotalPolicies() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETTOTALPOLICIES,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> proposePolicy(String _title, String _description, String _organType, String _policyData) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_PROPOSEPOLICY,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_title),
                new org.web3j.abi.datatypes.Utf8String(_description),
                new org.web3j.abi.datatypes.Utf8String(_organType),
                new org.web3j.abi.datatypes.Utf8String(_policyData)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> registerOrganization(String _wallet, String _name, String _orgType) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REGISTERORGANIZATION,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _wallet),
                new org.web3j.abi.datatypes.Utf8String(_name),
                new org.web3j.abi.datatypes.Utf8String(_orgType)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static PolicyVotingContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new PolicyVotingContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static PolicyVotingContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PolicyVotingContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static PolicyVotingContract load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new PolicyVotingContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static PolicyVotingContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new PolicyVotingContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class OrganizationRegisteredEventResponse extends BaseEventResponse {
        public String wallet;
        public String name;
    }

    public static class PolicyExecutedEventResponse extends BaseEventResponse {
        public BigInteger policyId;
        public Boolean approved;
    }

    public static class PolicyProposedEventResponse extends BaseEventResponse {
        public BigInteger policyId;
        public String title;
        public String proposer;
    }

    public static class VoteCastEventResponse extends BaseEventResponse {
        public BigInteger policyId;
        public String voter;
        public Boolean support;
    }
}
