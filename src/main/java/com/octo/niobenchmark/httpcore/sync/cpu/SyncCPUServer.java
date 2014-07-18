package com.octo.niobenchmark.httpcore.sync.cpu;

import com.octo.niobenchmark.httpcore.sync.SyncServer;
import com.octo.niobenchmark.httpcore.util.ConsumeCPU;
import com.octo.niobenchmark.httpcore.util.HTTPRequest;

public class SyncCPUServer {
	
	private static final int THREADS = 1;
	public static final int PORT = 8089;
	
	public static final String CPU_URL = "http://localhost:"
			+ SyncCPUServer.PORT + "/war/cpu";

	public static void main(String[] args) throws Exception {

        ConsumeCPU.consumeCpuInMillisecond(1000);
        HTTPRequest.init();
        SyncServer syncServer = new SyncServer(THREADS, PORT);
        syncServer.register("/war/sync/cpu", new SyncCPURequestHandler());
        syncServer.start();
	}

}
