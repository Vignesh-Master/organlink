package com.organlink.blockchain;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import org.web3j.tuples.generated.Tuple11;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * Auto generated code.
 * Do not modify!
 * Please use the web3j command line tools, or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * codegen module to update.
 *
 * Generated with web3j version 4.10.3.
 */
@SuppressWarnings("rawtypes")
public class OrganLinkSignatures extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_ADMIN = "admin";
    public static final String FUNC_CHANGEADMIN = "changeAdmin";
    public static final String FUNC_GETSIGNATURE = "getSignature";
    public static final String FUNC_GETTOTALSIGNATURES = "getTotalSignatures";
    public static final String FUNC_SIGNATURECOUNT = "signatureCount";
    public static final String FUNC_SIGNATURES = "signatures";
    public static final String FUNC_STORESIGNATURE = "storeSignature";
    public static final String FUNC_VERIFYSIGNATUREINTEGRITY = "verifySignatureIntegrity";

    public static final Event SIGNATURESTORED_EVENT = new Event("SignatureStored", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));

    public static final Event SIGNATUREVERIFIED_EVENT = new Event("SignatureVerified", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Bool>() {}, new TypeReference<Uint256>() {}));

    @Deprecated
    protected OrganLinkSignatures(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected OrganLinkSignatures(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected OrganLinkSignatures(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected OrganLinkSignatures(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<String> admin() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ADMIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> changeAdmin(String _newAdmin) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CHANGEADMIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _newAdmin)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple11<String, String, String, String, String, String, BigInteger, String, BigInteger, Boolean, BigInteger>> getSignature(BigInteger _recordId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSIGNATURE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_recordId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple11<String, String, String, String, String, String, BigInteger, String, BigInteger, Boolean, BigInteger>>(function,
                new Callable<Tuple11<String, String, String, String, String, String, BigInteger, String, BigInteger, Boolean, BigInteger>>() {
                    @Override
                    public Tuple11<String, String, String, String, String, String, BigInteger, String, BigInteger, Boolean, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple11<String, String, String, String, String, String, BigInteger, String, BigInteger, Boolean, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (String) results.get(3).getValue(), 
                                (String) results.get(4).getValue(), 
                                (String) results.get(5).getValue(), 
                                (BigInteger) results.get(6).getValue(), 
                                (String) results.get(7).getValue(), 
                                (BigInteger) results.get(8).getValue(), 
                                (Boolean) results.get(9).getValue(), 
                                (BigInteger) results.get(10).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getTotalSignatures() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETTOTALSIGNATURES, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> signatureCount() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SIGNATURECOUNT,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> storeSignature(String _ipfsHash, String _signerName, String _signerType, String _guardianName, String _guardianRelation, String _entityType, BigInteger _entityId, String _hospitalId, Boolean _isVerified, BigInteger _confidenceScore) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_STORESIGNATURE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_ipfsHash),
                new org.web3j.abi.datatypes.Utf8String(_signerName),
                new org.web3j.abi.datatypes.Utf8String(_signerType),
                new org.web3j.abi.datatypes.Utf8String(_guardianName),
                new org.web3j.abi.datatypes.Utf8String(_guardianRelation),
                new org.web3j.abi.datatypes.Utf8String(_entityType),
                new org.web3j.abi.datatypes.generated.Uint256(_entityId),
                new org.web3j.abi.datatypes.Utf8String(_hospitalId),
                new org.web3j.abi.datatypes.Bool(_isVerified),
                new org.web3j.abi.datatypes.generated.Uint256(_confidenceScore)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> verifySignatureIntegrity(BigInteger _recordId, String _ipfsHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_VERIFYSIGNATUREINTEGRITY,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_recordId),
                new org.web3j.abi.datatypes.Utf8String(_ipfsHash)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public List<SignatureStoredEventResponse> getSignatureStoredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SIGNATURESTORED_EVENT, transactionReceipt);
        ArrayList<SignatureStoredEventResponse> responses = new ArrayList<SignatureStoredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SignatureStoredEventResponse typedResponse = new SignatureStoredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.recordId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.ipfsHash = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.signerName = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.entityType = (String) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.hospitalId = (String) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    @Deprecated
    public static OrganLinkSignatures load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new OrganLinkSignatures(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static OrganLinkSignatures load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new OrganLinkSignatures(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static OrganLinkSignatures load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new OrganLinkSignatures(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static OrganLinkSignatures load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new OrganLinkSignatures(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class SignatureStoredEventResponse extends BaseEventResponse {
        public BigInteger recordId;
        public String ipfsHash;
        public String signerName;
        public String entityType;
        public String hospitalId;
    }

    public static class SignatureVerifiedEventResponse extends BaseEventResponse {
        public BigInteger recordId;
        public Boolean isVerified;
        public BigInteger confidenceScore;
    }
}
