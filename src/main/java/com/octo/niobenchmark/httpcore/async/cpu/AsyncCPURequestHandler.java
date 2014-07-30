package com.octo.niobenchmark.httpcore.async.cpu;

import com.octo.niobenchmark.httpcore.async.latency.AsyncLatencyServer;
import com.octo.niobenchmark.httpcore.util.ConsumeCPU;
import com.octo.niobenchmark.httpcore.util.GaussianGenerator;
import com.octo.niobenchmark.httpcore.util.HTTPRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.HttpContext;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AsyncCPURequestHandler implements HttpAsyncRequestHandler<HttpRequest> {
    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public HttpAsyncRequestConsumer<HttpRequest> processRequest(final HttpRequest request, final HttpContext context) {
        // Buffer request content in memory for simplicity
        return new BasicAsyncRequestConsumer();
    }

    @Override
    public void handle(final HttpRequest request, final HttpAsyncExchange httpexchange, final HttpContext context) {
        final String[] params = request.getRequestLine().getUri().split("\\?")[1].split("\\&");
        final int cpu = Integer.valueOf(params[0].split("\\=")[1]);
        final String latency = params[1].split("\\=")[1];

        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (cpu >= 0)
                    ConsumeCPU.consumeCpuInMillisecond(Math.round(cpu * GaussianGenerator.getValue()));
                if (Integer.valueOf(latency) >= 0)
                    HTTPRequest.sendReactiveRequest(AsyncLatencyServer.URL, Collections.singletonMap("latency", latency), httpexchange);
                else {
                    HttpResponse responseSend = httpexchange.getResponse();
                    responseSend.setStatusCode(HttpStatus.SC_OK);
                    responseSend.setEntity(new NStringEntity("Ok", ContentType.create("text/html", "UTF-8")));
                    httpexchange.submitResponse();
                }
            }
        });
    }
}
