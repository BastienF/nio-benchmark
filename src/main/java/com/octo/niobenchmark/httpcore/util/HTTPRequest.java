package com.octo.niobenchmark.httpcore.util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Bastien on 03/07/2014.
 */
public class HTTPRequest {
    public final static int HTTP_CLIENT_MAX_CONNECTIONS = 10000;
    public final static int HTTP_ASYNC_CLIENT_THREADS = 1;

    private static CloseableHttpAsyncClient asyncHttpClient;
    private static CloseableHttpClient httpClient;


    public static void init() {
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setIoThreadCount(HTTP_ASYNC_CLIENT_THREADS).build();
        asyncHttpClient = HttpAsyncClients.custom()
                .setDefaultIOReactorConfig(ioReactorConfig)
                .setMaxConnPerRoute(HTTP_CLIENT_MAX_CONNECTIONS)
                .setMaxConnTotal(HTTP_CLIENT_MAX_CONNECTIONS)
                .build();
        asyncHttpClient.start();

        httpClient = HttpClients.custom()
                .setMaxConnPerRoute(HTTP_CLIENT_MAX_CONNECTIONS)
                .setMaxConnTotal(HTTP_CLIENT_MAX_CONNECTIONS).build();
    }

    private static String buildURL(String url, Map<String, String> getParameters) {
        StringBuilder constructedUrl = new StringBuilder(url);
        if (getParameters.size() > 0)
            constructedUrl.append("?");
        for (Map.Entry<String, String> param : getParameters.entrySet()) {
            constructedUrl.append(param.getKey()).append("=").append(param.getValue()).append("&");
        }
        if (getParameters.size() > 0)
            constructedUrl.deleteCharAt(constructedUrl.length() - 1);
        return constructedUrl.toString();
    }

    public static void sendRequest(String url, Map<String, String> getParameters) {
        HttpGet httpGet = new HttpGet(buildURL(url, getParameters));
        CloseableHttpResponse httpClientResponse = null;
        try {
            httpClientResponse = httpClient.execute(httpGet);
            try {
                EntityUtils.toString(httpClientResponse.getEntity());
            } finally {
                httpClientResponse.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected static final String getBehaviorLogEntry(String status, String name, String color, long initTime, long time) {
        return new StringBuilder().append("{\n\"N\": \"").append(name).append("\",\n\"Th\": \"").append(Thread.currentThread().getName()).append("\",\n\"C\": \"").append(color).append("\",\n\"S\": \"").append(status).append("\",\n\"T\": \"").append((time - initTime) / 1000000).append("\"\n}").toString();
    }

    public static void sendReactiveRequest(final String url, final Map<String, String> getParameters, final HttpAsyncExchange httpexchange) {
        HttpGet httpGet = new HttpGet(buildURL(url, getParameters));
        final long latencyStart = System.nanoTime();

        FutureCallback<HttpResponse> responseCallback = new FutureCallback<HttpResponse>() {

            @Override
            public void completed(final HttpResponse response) {
                try {
                    EntityUtils.toString(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                HttpResponse responseSend = httpexchange.getResponse();
                responseSend.setStatusCode(HttpStatus.SC_OK);
                responseSend.setEntity(new NStringEntity("Ok", ContentType.create("text/html", "UTF-8")));
                httpexchange.submitResponse();
            }

            @Override
            public void failed(final Exception e) {
                throw new RuntimeException(e);
            }

            @Override
            public void cancelled() {
                throw new RuntimeException("Canceled latency request");
            }

        };

        asyncHttpClient.execute(httpGet, responseCallback);
    }
}
