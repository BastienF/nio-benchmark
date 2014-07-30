package com.octo.niobenchmark.httpcore.sync.cpu;

import com.octo.niobenchmark.httpcore.sync.SyncServer;
import com.octo.niobenchmark.httpcore.util.ConsumeCPU;
import com.octo.niobenchmark.httpcore.util.GaussianGenerator;
import com.octo.niobenchmark.httpcore.util.HTTPRequest;

public class SyncCPUServer {
	
	private static final int THREADS = 10000;
	public static final int PORT = 8089;
	
	public static final String CPU_URL = "http://localhost:"
			+ SyncCPUServer.PORT + "/war/cpu";

	public static void main(String[] args) throws Exception {

        ConsumeCPU.consumeCpuInMillisecond(1000);
        HTTPRequest.init();
        GaussianGenerator.init();
        SyncServer syncServer = new SyncServer(THREADS, PORT);
        syncServer.register("/war/sync/cpu", new SyncCPURequestHandler());
        syncServer.register("/war/mock/sync/cpu", new SyncCPUMockRequestHandler());
        syncServer.start();
        System.out.println("Server started");
	}

}
