package com.octo.niobenchmark.httpcore.sync.latency;

import com.octo.niobenchmark.Parameters;
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
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public final class SyncLatencyRequestHandler implements HttpRequestHandler {
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(200);

    @Override
    public void handle(HttpRequest httpRequest, final HttpResponse response, HttpContext httpContext) throws HttpException, IOException {
        int latency = Integer.valueOf(httpRequest.getRequestLine().getUri().split("\\?")[1].split("\\=")[1]);
        long gaussedLatency = Math.round(latency * SyncLatencyServer.GAUSSIANS_VALUES[ThreadLocalRandom.current().nextInt(0, Parameters.GAUSSIANS_SIZE)]);
        final CountDownLatch latch = new CountDownLatch(1);
        Runnable exec = new Runnable() {

            @Override
            public void run() {
                response.setStatusCode(HttpStatus.SC_OK);
                response.setEntity(new NStringEntity("Ok", ContentType.create("text/html", "UTF-8")));
                latch.countDown();
            }
        };
        executor.schedule(exec, gaussedLatency, TimeUnit.MILLISECONDS);
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
