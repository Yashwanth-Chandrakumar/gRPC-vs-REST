package org.example.model;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.example.StudentBatchResponse;
import org.example.StudentServiceGrpc;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Random;

public class MyRunnable implements Runnable{

    @Override
    public void run(){
//        ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("dns:///localhost:8080").defaultLoadBalancingPolicy("round_robin").usePlaintext().build();
//
//        StudentServiceGrpc.StudentServiceBlockingStub stub = StudentServiceGrpc.newBlockingStub(managedChannel);

        MultiAddressNameResolverFactory factory = new MultiAddressNameResolverFactory(
                new InetSocketAddress("localhost", 8080),
                new InetSocketAddress("localhost", 8081),
                new InetSocketAddress("localhost", 8082)
        );
        NameResolverRegistry.getDefaultRegistry().register(factory);

        // Create a channel with plaintext communication to match the server configuration
        ManagedChannel managedChannel = NettyChannelBuilder
                .forTarget("multiaddress:///")
                .nameResolverFactory(factory)
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build();
        Random random = new Random();
        int portFinder = random.nextInt(1000);
        StudentServiceGrpc.StudentServiceBlockingStub stub = StudentServiceGrpc.newBlockingStub(managedChannel);


        Iterator<StudentBatchResponse> response = stub.getBatchStudent(org.example.StudentRequest.newBuilder().setId(3000).setPortfinder(portFinder).build());
//        System.out.println("Using Portfinder: "+portFinder+" Thread: "+Thread.currentThread().getName() );
        while(response.hasNext()){
            response.next();
//            System.out.println(response.next());
        }
    }
}
