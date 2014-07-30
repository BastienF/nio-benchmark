package com.octo.niobenchmark.httpcore.async.cpu;

import com.octo.niobenchmark.httpcore.async.AsyncServer;
import com.octo.niobenchmark.httpcore.util.ConsumeCPU;
import com.octo.niobenchmark.httpcore.util.GaussianGenerator;
import com.octo.niobenchmark.httpcore.util.HTTPRequest;

public class AsyncCPUServer {
	
	private static final int THREADS = 1;
	public static final int PORT = 8089;
	public static final String CPU_URL = "http://localhost:"
			+ AsyncCPUServer.PORT + "/war/cpu";

	public static void main(String[] args) throws Exception {
        ConsumeCPU.consumeCpuInMillisecond(1000);
        HTTPRequest.init();
        GaussianGenerator.init();
        AsyncServer asyncServer = new AsyncServer(THREADS, PORT);
        asyncServer.register("/war/async/cpu", new AsyncCPURequestHandler());
        asyncServer.start();
        System.out.println("Server started");
    }

}
