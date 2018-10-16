package com.mvc.sell.console;

import com.mvc.sell.console.config.SpringContextUtil;
import com.mvc.sell.console.service.TransactionService;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.context.annotation.Bean;
import org.stellar.sdk.*;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * test
 *
 * @author qiyichen
 * @create 2018/5/31 17:34
 */
public class test {

    public static void main(String[] args) throws IOException {

        Web3j web3j = Web3j.build(new HttpService("http://192.168.213.170:8545", okHttpClient() , false));
        EthTransaction result = web3j.ethGetTransactionByHash("0x5551146f20bb9bcc5886e7ad184ea99cacff521cd7d6cf3dbc5ef3beaa118441").send();
        TransactionReceipt result2 = web3j.ethGetTransactionReceipt(result.getTransaction().get().getHash()).send().getResult();
        Boolean isFail = !"0x".equalsIgnoreCase(result.getTransaction().get().getInput()) & result2.getLogs().size() == 0 && null != result2.getGasUsed() && result2.getGasUsed().compareTo(BigInteger.ZERO) > 0;

        System.out.println(result.hasError());
//        EventListener listert = new EventListener() {
//            @Override
//            public void onEvent(Object o) {
//                System.out.println(111);
//            }
//        };
//        EventSource st = server.transactions().stream(listert);
//        while (true){
//            if(!st.isOpen()){
//                st.open();
//            }
//        }






    }

    public static OkHttpClient okHttpClient() throws IOException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request requestWithUserAgent = originalRequest.newBuilder()
                                .build();
                        return chain.proceed(requestWithUserAgent);
                    }
                });
        return builder.build();
    }
}
