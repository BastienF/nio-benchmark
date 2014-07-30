package com.octo.niobenchmark.httpcore.sync.cpu;

import com.octo.niobenchmark.httpcore.sync.latency.SyncLatencyServer;
import com.octo.niobenchmark.httpcore.util.ConsumeCPU;
import com.octo.niobenchmark.httpcore.util.HTTPRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SyncCPURequestHandler implements HttpRequestHandler {
    private ExecutorService executor = Executors.newFixedThreadPool(200);//Runtime.getRuntime().availableProcessors());

    @Override
    public void handle(HttpRequest httpRequest, final HttpResponse response, HttpContext httpContext) throws HttpException, IOException {
        final String[] params = httpRequest.getRequestLine().getUri().split("\\?")[1].split("\\&");
        final int cpu = Integer.valueOf(params[0].split("\\=")[1]);
        final String latency = params[1].split("\\=")[1];
        final CountDownLatch latch = new CountDownLatch(1);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (cpu >= 0)
                    ConsumeCPU.consumeCpuInMillisecond(cpu);
                if (Integer.valueOf(latency) >= 0)
                    HTTPRequest.sendRequest(SyncLatencyServer.URL, Collections.singletonMap("latency", latency));
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
