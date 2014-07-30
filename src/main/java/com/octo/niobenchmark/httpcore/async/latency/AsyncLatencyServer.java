package com.octo.niobenchmark.httpcore.async.latency;

import com.octo.niobenchmark.Parameters;
import com.octo.niobenchmark.httpcore.async.AsyncServer;
import com.octo.niobenchmark.httpcore.util.ConsumeCPU;
import com.octo.niobenchmark.httpcore.util.HTTPRequest;

import java.util.Random;

public class AsyncLatencyServer {
	
	private static final int THREADS = 1;
	public static final int PORT = 8090;
	public static final String URL = "http://localhost:8090/war/async/latency";
    public static double[] GAUSSIANS_VALUES = new double[Parameters.GAUSSIANS_SIZE];

	public static void main(String[] args) throws Exception {
        ConsumeCPU.consumeCpuInMillisecond(1000);
        Random rand = new Random();
        for (int i = 0; i < Parameters.GAUSSIANS_SIZE; i++) {
            GAUSSIANS_VALUES[i] = Math.abs((rand.nextGaussian() * Parameters.STD_DEVIATION + Parameters.MEAN));
        }
        HTTPRequest.init();
        AsyncServer asyncServer = new AsyncServer(THREADS, PORT);
        asyncServer.register("/war/async/latency", new AsyncLatencyRequestHandler());
        asyncServer.start();
        System.out.println("Server started");
	}

}
