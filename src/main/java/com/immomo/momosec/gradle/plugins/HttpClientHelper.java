package com.immomo.momosec.gradle.plugins;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

public class HttpClientHelper {

    private final int timeout = 15 * 1000;

    public HttpClient buildHttpClient() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        return httpClientBuilder.build();
    }
}
