package com.octo.niobenchmark.httpcore.metrics;

import com.octo.niobenchmark.httpcore.async.AsyncServer;

public class AsyncMetricServer {
	
	private static final int THREADS = 1;
	public static final int PORT = 8091;
	public static final String METRICS_URL = "http://localhost:"
			+ AsyncMetricServer.PORT + "/war/metrics";

	public static void main(String[] args) throws Exception {
        AsyncServer asyncServer = new AsyncServer(THREADS, PORT);
        asyncServer.register("/war/metrics/collectd", new AsyncCollectdRequestHandler());
        asyncServer.register("/start", new AsyncStartRequestHandler());
        asyncServer.register("/stop", new AsyncStopRequestHandler());
        asyncServer.start();
        System.out.println("Server started");
    }

}
