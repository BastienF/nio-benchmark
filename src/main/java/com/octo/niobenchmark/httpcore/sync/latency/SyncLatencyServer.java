package com.octo.niobenchmark.httpcore.sync.latency;

import com.octo.niobenchmark.httpcore.sync.SyncServer;
import com.octo.niobenchmark.httpcore.util.ConsumeCPU;
import com.octo.niobenchmark.httpcore.util.GaussianGenerator;
import com.octo.niobenchmark.httpcore.util.HTTPRequest;

public class SyncLatencyServer {
	
	private static final int THREADS = 10000;
	public static final int PORT = 8090;
    public static final String URL = "http://localhost:8090/war/sync/latency";

	public static void main(String[] args) throws Exception {
        ConsumeCPU.consumeCpuInMillisecond(1000);
        GaussianGenerator.init();
        HTTPRequest.init();
        SyncServer syncServer = new SyncServer(THREADS, PORT);
        syncServer.register("/war/sync/latency", new SyncLatencyRequestHandler());
        syncServer.start();
        System.out.println("Server started");
	}

}
