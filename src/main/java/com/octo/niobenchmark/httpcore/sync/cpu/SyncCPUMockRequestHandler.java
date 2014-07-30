package com.octo.niobenchmark.httpcore.sync.cpu;

import com.octo.niobenchmark.httpcore.util.ConsumeCPU;
import com.octo.niobenchmark.httpcore.util.GaussianGenerator;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SyncCPUMockRequestHandler implements HttpRequestHandler {
    private ExecutorService executor = Executors.newFixedThreadPool(200);//Runtime.getRuntime().availableProcessors());

    @Override
    public void handle(HttpRequest httpRequest, final HttpResponse response, HttpContext httpContext) throws HttpException, IOException {
        final String[] params = httpRequest.getRequestLine().getUri().split("\\?")[1].split("\\&");
        final int cpu = Integer.valueOf(params[0].split("\\=")[1]);
        final int latency = Integer.valueOf(params[1].split("\\=")[1]);
        final long gaussedLatency = Math.round(latency * GaussianGenerator.getValue());
        final CountDownLatch latch = new CountDownLatch(1);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (cpu >= 0)
                    ConsumeCPU.consumeCpuInMillisecond(Math.round(cpu * GaussianGenerator.getValue()));
                try {
                    Thread.sleep(gaussedLatency > 0 ? gaussedLatency : 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                response.setStatusCode(HttpStatus.SC_OK);
                response.setEntity(new NStringEntity("Ok", ContentType.create("text/html", "UTF-8")));
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
