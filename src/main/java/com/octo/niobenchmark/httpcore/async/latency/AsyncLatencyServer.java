package com.octo.niobenchmark.httpcore.async.latency;

import com.octo.niobenchmark.httpcore.async.AsyncServer;
import com.octo.niobenchmark.httpcore.util.ConsumeCPU;
import com.octo.niobenchmark.httpcore.util.GaussianGenerator;
import com.octo.niobenchmark.httpcore.util.HTTPRequest;

public class AsyncLatencyServer {
	
	private static final int THREADS = 1;
	public static final int PORT = 8090;
	public static final String URL = "http://localhost:8090/war/async/latency";


	public static void main(String[] args) throws Exception {
        ConsumeCPU.consumeCpuInMillisecond(1000);
        HTTPRequest.init();
        GaussianGenerator.init();
        AsyncServer asyncServer = new AsyncServer(THREADS, PORT);
        asyncServer.register("/war/async/latency", new AsyncLatencyRequestHandler());
        asyncServer.start();
        System.out.println("Server started");
	}

}
