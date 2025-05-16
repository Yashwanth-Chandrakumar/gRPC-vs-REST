package org.example.model;

import io.grpc.ServerBuilder;
import org.example.server.Server;

import java.io.File;
import java.io.IOException;


public class MyServers implements Runnable{
    final int port;
    
    public MyServers(int port){
        this.port = port;
    }
    @Override
    public void run(){
//        File certFile = new File("C:\\Users\\Administrator\\IdeaProjects\\jerseygrpc\\localhost+2.pem");
//        File privateKeyFile = new File("C:\\Users\\Administrator\\IdeaProjects\\jerseygrpc\\localhost+2-key.pem");

        io.grpc.Server server = ServerBuilder.forPort(port)
//                .useTransportSecurity(certFile, privateKeyFile)
                .addService(new Server(port))
                .build();
        try {

            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Server running on Localhost: "+port);
        try {
            server.awaitTermination();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
