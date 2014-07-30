package com.octo.niobenchmark.httpcore.async.cpu;

import com.octo.niobenchmark.httpcore.util.ConsumeCPU;
import com.octo.niobenchmark.httpcore.util.GaussianGenerator;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class AsyncCPUMockRequestHandler implements HttpAsyncRequestHandler<HttpRequest> {
    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1);

    @Override
    public HttpAsyncRequestConsumer<HttpRequest> processRequest(final HttpRequest request, final HttpContext context) {
        // Buffer request content in memory for simplicity
        return new BasicAsyncRequestConsumer();
    }

    @Override
    public void handle(final HttpRequest request, final HttpAsyncExchange httpexchange, final HttpContext context) {
        final String[] params = request.getRequestLine().getUri().split("\\?")[1].split("\\&");
        final int cpu = Integer.valueOf(params[0].split("\\=")[1]);
        final int latency = Integer.valueOf(params[1].split("\\=")[1]);
        final long gaussedLatency = Math.round(latency * GaussianGenerator.getValue());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (cpu >= 0)
                    ConsumeCPU.consumeCpuInMillisecond(Math.round(cpu * GaussianGenerator.getValue()));
                scheduledExecutor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        HttpResponse response = httpexchange.getResponse();
                        response.setStatusCode(HttpStatus.SC_OK);
                        response.setEntity(new NStringEntity("Ok", ContentType.create("text/html", "UTF-8")));
                        httpexchange.submitResponse();
                    }
                }, gaussedLatency > 0 ? gaussedLatency : 0, TimeUnit.MILLISECONDS);
            }
        });
    }
}
