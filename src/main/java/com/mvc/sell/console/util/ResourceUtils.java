package com.mvc.sell.console.util;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ResourceUtils {

    public static CloseableHttpClient getHttpProvider() {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        CloseableHttpClient httpProvider = HttpClients.custom().setConnectionManager(connManager)
                .build();
        return httpProvider;
    }

    public static Properties getNodeConfig(InputStream inputStream) throws IOException {
//        String filePath = System.getProperty("user.dir") + "/application.yml";
//        InputStream inputStream = new BufferedInputStream(new FileInputStream(filePath));
//        ClassPathResource resource = getResourceAsStream("application.yml");
//        InputStream inputStream = resource.getInputStream();
        Properties nodeConfig = new Properties();
        nodeConfig.load(inputStream);
        return nodeConfig;
    }
}