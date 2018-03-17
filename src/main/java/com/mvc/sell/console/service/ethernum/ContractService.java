package com.mvc.sell.console.service.ethernum;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.quorum.Quorum;
import org.web3j.quorum.methods.request.PrivateTransaction;
import org.web3j.quorum.tx.ClientTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static org.web3j.tx.Contract.GAS_LIMIT;
import static org.web3j.tx.ManagedTransaction.GAS_PRICE;

/**
 * @author qyc
 */
@Service
public class ContractService {

    private final Quorum quorum;

    private final NodeConfiguration nodeConfiguration;

    @Autowired
    public ContractService(Quorum quorum, NodeConfiguration nodeConfiguration) {
        this.quorum = quorum;
        this.nodeConfiguration = nodeConfiguration;
    }

    public NodeConfiguration getConfig() {
        return nodeConfiguration;
    }

    public String deploy(
            List<String> privateFor, long initialAmount, String tokenName, long decimalUnits,
            String tokenSymbol) throws IOException, TransactionException {
        TransactionManager transactionManager = new ClientTransactionManager(
                quorum, nodeConfiguration.getFromAddress(), privateFor);
        HumanStandardToken humanStandardToken = HumanStandardToken.deploy(
                quorum, transactionManager, GAS_PRICE, GAS_LIMIT.divide(BigInteger.valueOf(100)), BigInteger.ZERO,
                new Uint256(initialAmount), new Utf8String(tokenName), new Uint8(decimalUnits),
                new Utf8String(tokenSymbol));
        return humanStandardToken.getContractAddress();
    }

    public String name(String contractAddress) {
        HumanStandardToken humanStandardToken = load(contractAddress);
        try {
            return extractValue(humanStandardToken.name().get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public TransactionResponse<ApprovalEventResponse> approve(
            List<String> privateFor, String contractAddress, String spender, long value) {
        HumanStandardToken humanStandardToken = load(contractAddress, privateFor);
        try {
            TransactionReceipt transactionReceipt = humanStandardToken
                    .approve(new Address(spender), new Uint256(value));
            return processApprovalEventResponse(humanStandardToken, transactionReceipt);
        } catch (TransactionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long totalSupply(String contractAddress) {
        HumanStandardToken humanStandardToken = load(contractAddress);
        try {
            return extractLongValue(humanStandardToken.totalSupply().get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public TransactionResponse<TransferEventResponse> transferFrom(List<String> privateFor, String contractAddress, String from, String to, long value) {
        HumanStandardToken humanStandardToken = load(contractAddress, privateFor);
        try {
            TransactionReceipt transactionReceipt = humanStandardToken
                    .transferFrom(new Address(from), new Address(to), new Uint256(value));

            System.out.println(transactionReceipt.getTransactionHash());
            return processTransferEventsResponse(humanStandardToken, transactionReceipt);
        } catch (TransactionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public EthSendTransaction eth_sendTransaction(org.web3j.protocol.core.methods.request.Transaction transaction, String contractAddress) throws Exception {
        org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function("transfer", Arrays.<Type>asList(new Address(transaction.getTo()), new Uint256(Numeric.decodeQuantity(transaction.getValue()))), Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(function);
        PrivateTransaction privateTransaction = new PrivateTransaction(transaction.getFrom(), null, GAS_LIMIT.divide(BigInteger.valueOf(30)), contractAddress, BigInteger.ZERO, data, Arrays.asList(transaction.getFrom(), transaction.getTo(), contractAddress));
        EthSendTransaction response = quorum.ethSendTransaction(privateTransaction).send();
        return response;
    }

    public long decimals(String contractAddress) {
        HumanStandardToken humanStandardToken = load(contractAddress);
        try {
            return extractLongValue(humanStandardToken.decimals().get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String version(String contractAddress) {
        HumanStandardToken humanStandardToken = load(contractAddress);
        try {
            return extractValue(humanStandardToken.version().get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BigInteger balanceOf(String contractAddress, String ownerAddress) {
        HumanStandardToken humanStandardToken = load(contractAddress);
        try {
            Uint256 balance = humanStandardToken.balanceOf(new Address(ownerAddress));
            return balance.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BigInteger.ZERO;
    }

    public String symbol(String contractAddress) {
        HumanStandardToken humanStandardToken = load(contractAddress);
        try {
            return extractValue(humanStandardToken.symbol().get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public TransactionResponse<TransferEventResponse> transfer(
            List<String> privateFor, String contractAddress, String to, long value) throws IOException {
        HumanStandardToken humanStandardToken = load(contractAddress, privateFor);
        try {
            TransactionReceipt transactionReceipt = humanStandardToken
                    .transfer(new Address(to), new Uint256(value));
            return processTransferEventsResponse(humanStandardToken, transactionReceipt);
        } catch (TransactionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public TransactionResponse<ApprovalEventResponse> approveAndCall(
            List<String> privateFor, String contractAddress, String spender, long value,
            String extraData) {
        HumanStandardToken humanStandardToken = load(contractAddress, privateFor);
        try {
            TransactionReceipt transactionReceipt = humanStandardToken
                    .approveAndCall(
                            new Address(spender), new Uint256(value),
                            new DynamicBytes(extraData.getBytes()));
            return processApprovalEventResponse(humanStandardToken, transactionReceipt);
        } catch (TransactionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long allowance(String contractAddress, String ownerAddress, String spenderAddress) {
        HumanStandardToken humanStandardToken = load(contractAddress);
        try {
            return extractLongValue((Uint) humanStandardToken.allowance(
                    new Address(ownerAddress), new Address(spenderAddress))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private HumanStandardToken load(String contractAddress, List<String> privateFor) {
        TransactionManager transactionManager = new ClientTransactionManager(
                quorum, nodeConfiguration.getFromAddress(), privateFor);
        return HumanStandardToken.load(
                contractAddress, quorum, transactionManager, GAS_PRICE.divide(BigInteger.valueOf(100)), GAS_LIMIT.divide(BigInteger.valueOf(100)));
    }

    private HumanStandardToken load(String contractAddress) {
        TransactionManager transactionManager = new ClientTransactionManager(
                quorum, nodeConfiguration.getFromAddress(), Collections.emptyList());
        return HumanStandardToken.load(
                contractAddress, quorum, transactionManager, GAS_PRICE.divide(BigInteger.valueOf(100)), GAS_LIMIT.divide(BigInteger.valueOf(100)));
    }

    private <T> T extractValue(Type<T> value) {
        if (value != null) {
            return value.getValue();
        } else {
            throw new RuntimeException("Empty value returned by call");
        }
    }

    private long extractLongValue(Uint value) {
        return extractValue(value).longValueExact();
    }

    private TransactionResponse<ApprovalEventResponse>
    processApprovalEventResponse(
            HumanStandardToken humanStandardToken,
            TransactionReceipt transactionReceipt) {

        return processEventResponse(
                humanStandardToken.getApprovalEvents(transactionReceipt),
                transactionReceipt,
                ApprovalEventResponse::new);
    }

    private TransactionResponse<TransferEventResponse>
    processTransferEventsResponse(
            HumanStandardToken humanStandardToken,
            TransactionReceipt transactionReceipt) {

        return processEventResponse(
                humanStandardToken.getTransferEvents(transactionReceipt),
                transactionReceipt,
                TransferEventResponse::new);
    }

    private <T, R> TransactionResponse<R> processEventResponse(
            List<T> eventResponses, TransactionReceipt transactionReceipt, Function<T, R> map) {
        if (!eventResponses.isEmpty()) {
            return new TransactionResponse<>(
                    transactionReceipt.getTransactionHash(),
                    map.apply(eventResponses.get(0)));
        } else {
            return new TransactionResponse<>(
                    transactionReceipt.getTransactionHash());
        }
    }

    public void transfer() {
    }

    @Data
    public static class TransferEventResponse {
        private String from;
        private String to;
        private long value;

        public TransferEventResponse() {
        }

        public TransferEventResponse(
                HumanStandardToken.TransferEventResponse transferEventResponse) {
            this.from = transferEventResponse.from.toString();
            this.to = transferEventResponse.to.toString();
            this.value = transferEventResponse.value.getValue().longValueExact();
        }
    }

    @Getter
    @Setter
    public static class ApprovalEventResponse {
        private String owner;
        private String spender;
        private long value;

        public ApprovalEventResponse() {
        }

        public ApprovalEventResponse(
                HumanStandardToken.ApprovalEventResponse approvalEventResponse) {
            this.owner = approvalEventResponse.owner.toString();
            this.spender = approvalEventResponse.spender.toString();
            this.value = approvalEventResponse.value.getValue().longValueExact();
        }
    }
}
