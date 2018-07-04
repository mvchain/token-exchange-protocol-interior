package com.mvc.sell.console.config;

import com.mvc.sell.console.service.TransactionService;
import com.mvc.sell.console.util.ResourceUtils;
import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.examples.client.VerboseBtcdClientImpl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.stellar.sdk.Network;
import org.stellar.sdk.Server;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.http.HttpService;
import org.web3j.quorum.Quorum;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * beanconfig
 *
 * @author qiyichen
 * @create 2018/3/8 19:53
 */
@Configuration
public class BeanConfig {

    @Value("${service.geth}")
    public String WALLET_SERVICE;
    @Value("${service.xlm}")
    public String xlm;
    @Value("${btcd.config.path}")
    private String path;

    @Bean
    public OkHttpClient okHttpClient() throws IOException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new Interceptor() {
                    TransactionService transactionService = SpringContextUtil.getBean("transactionService");

                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request requestWithUserAgent = originalRequest.newBuilder()
                                .build();
                        Response result = null;
                        try {
                            result = chain.proceed(requestWithUserAgent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                transactionService.startListen();
                                return null;
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                        return result;
                    }
                });
        return builder.build();
    }

    @Bean
    public Quorum quorum(OkHttpClient okHttpClient) {
        return Quorum.build(new HttpService(WALLET_SERVICE, okHttpClient, false));
    }

    @Bean
    public Admin admin(OkHttpClient okHttpClient) {
        return Admin.build(new HttpService(WALLET_SERVICE, okHttpClient, false));
    }

    @Bean
    public Web3j web3j(OkHttpClient okHttpClient) {
        return Web3j.build(new HttpService(WALLET_SERVICE, okHttpClient, false));
    }

    @Bean
    public Server server() {
        if (xlm.indexOf("test") > 0) {
            Network.useTestNetwork();
        } else {
            Network.usePublicNetwork();
        }
        Server server = new Server(xlm);
        server.setHttpClient(new OkHttpClient.Builder().build());
        return server;
    }

    @Bean
    public BtcdClient btcdClient() throws IOException, BitcoindException, CommunicationException {
        CloseableHttpClient httpProvider = ResourceUtils.getHttpProvider();
        Properties nodeConfig = ResourceUtils.getNodeConfig(path);
        BtcdClient client = new VerboseBtcdClientImpl(httpProvider, nodeConfig);
        return client;
    }
}
