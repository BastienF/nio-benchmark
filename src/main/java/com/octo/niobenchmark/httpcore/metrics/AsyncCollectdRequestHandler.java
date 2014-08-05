package com.octo.niobenchmark.httpcore.metrics;

import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

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
    public void handle(final HttpRequest httpRequest, final HttpAsyncExchange httpexchange, final HttpContext context) throws HttpException, IOException {
        //PUTVAL servero7/contextswitch/contextswitch interval=10.000 1400241551.035:3933731
        System.out.println(""); // empty line before each request
        System.out.println(httpRequest.getRequestLine());
        System.out.println("-------- HEADERS --------");
        for(Header header: httpRequest.getAllHeaders()) {
            System.out.println(header.getName() + " : " + header.getValue());
        }
        System.out.println("--------");

        HttpEntity entity = null;
        if (httpRequest instanceof HttpEntityEnclosingRequest)
            entity = ((HttpEntityEnclosingRequest)httpRequest).getEntity();

        // For some reason, just putting the incoming entity into
        // the response will not work. We have to buffer the message.
        byte[] data;
        if (entity == null) {
            data = new byte [0];
        } else {
            data = EntityUtils.toByteArray(entity);
        }

        String payload = new String(data);
        for (String line : payload.split("\n")) {
            if (line.contains("contextswitch")){
                String[] splitedRequest = line.split(" ");
                if (splitedRequest.length == 4) {
                    String contextSwitchNumberStr = splitedRequest[3].split(":")[1];
                    System.out.println(contextSwitchNumberStr);
                    break;
                }
            }
        }

        HttpResponse responseSend = httpexchange.getResponse();
        responseSend.setStatusCode(HttpStatus.SC_OK);
        responseSend.setEntity(new NStringEntity("Ok", ContentType.create("text/html", "UTF-8")));
        httpexchange.submitResponse();
    }
}
