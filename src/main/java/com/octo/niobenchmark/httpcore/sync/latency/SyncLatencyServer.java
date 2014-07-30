package com.octo.niobenchmark.httpcore.sync.latency;

import com.octo.niobenchmark.Parameters;
import com.octo.niobenchmark.httpcore.sync.SyncServer;
import com.octo.niobenchmark.httpcore.util.ConsumeCPU;
import com.octo.niobenchmark.httpcore.util.HTTPRequest;

import java.util.Random;

public class SyncLatencyServer {
	
	private static final int THREADS = 10000;
	public static final int PORT = 8090;
    public static double[] GAUSSIANS_VALUES = new double[Parameters.GAUSSIANS_SIZE];
    public static final String URL = "http://localhost:8090/war/sync/latency";

	public static void main(String[] args) throws Exception {
        ConsumeCPU.consumeCpuInMillisecond(1000);
        Random rand = new Random();
        for (int i = 0; i < Parameters.GAUSSIANS_SIZE; i++) {
            GAUSSIANS_VALUES[i] = Math.abs((rand.nextGaussian() * Parameters.STD_DEVIATION + Parameters.MEAN));
        }
        HTTPRequest.init();
        SyncServer syncServer = new SyncServer(THREADS, PORT);
        syncServer.register("/war/sync/latency", new SyncLatencyRequestHandler());
        syncServer.start();
        System.out.println("Server started");
	}

}
