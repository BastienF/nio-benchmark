package com.octo.niobenchmark.httpcore.metrics;

import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AsyncCollectdRequestHandler implements HttpAsyncRequestHandler<HttpRequest> {
    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()-1);

    @Override
    public HttpAsyncRequestConsumer<HttpRequest> processRequest(final HttpRequest request, final HttpContext context) {
        // Buffer request content in memory for simplicity
        return new BasicAsyncRequestConsumer();
    }

    @Override
    public void handle(final HttpRequest request, final HttpAsyncExchange httpexchange, final HttpContext context) {
        //PUTVAL servero7/contextswitch/contextswitch interval=10.000 1400241551.035:3933731
        String contextSwitchLine;
        System.out.println("Request : " + request);
        try {
           /* while ((contextSwitchLine = request. getReader().readLine()) != null) {
                if (contextSwitchLine.contains("contextswitch")) {
                    String[] splitedRequest = contextSwitchLine.split(" ");
                    if (splitedRequest.length == 4) {
                        String contextSwitchNumberStr = splitedRequest[3].split(":")[1];
                        metricsLogger.addContextSwitching(Integer.parseInt(contextSwitchNumberStr));
                        break;
                    }
                }
            }
        } catch (IOException e) {
            logger.warn("Error while parsing collectd log request : " + e.getMessage());
        }*/
    }
}
