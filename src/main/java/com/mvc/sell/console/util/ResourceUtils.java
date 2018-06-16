package com.mvc.sell.console.util;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.*;
import java.util.Properties;

public final class ResourceUtils {

    public static CloseableHttpClient getHttpProvider() {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        CloseableHttpClient httpProvider = HttpClients.custom().setConnectionManager(connManager)
                .build();
        return httpProvider;
    }

    public static BtcdClient getBtcdProvider() throws BitcoindException, CommunicationException,
            IOException {
        return new BtcdClientImpl(getHttpProvider(), getNodeConfig());
    }

    public static Properties getNodeConfig() throws IOException {
        File file = org.springframework.util.ResourceUtils.getFile("classpath:node_config.properties");
        Properties nodeConfig = new Properties();
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        nodeConfig.load(inputStream);
        inputStream.close();
        return nodeConfig;
    }
}