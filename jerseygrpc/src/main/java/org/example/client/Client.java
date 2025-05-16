package org.example.client;

import com.google.protobuf.FieldMask;
import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.example.HelloResponse;
import org.example.StudentBatchResponse;
import org.example.StudentResponse;
import org.example.StudentServiceGrpc;
import org.example.model.ClientInterceptor;
import org.example.model.MultiAddressNameResolverFactory;
import org.example.model.MyRunnable;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Random;

public class Client {

    public static void test(){
        /**
         * Just a sample message
         */
        ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("dns:///localhost:8080").defaultLoadBalancingPolicy("round_robin").useTransportSecurity().build();

        StudentServiceGrpc.StudentServiceBlockingStub stub = StudentServiceGrpc.newBlockingStub(managedChannel);

        HelloResponse response = stub.getStudent(org.example.HelloRequest.newBuilder().setName("Rajesh").build());
        System.out.println(response.getMessage());
    }
    public static void getAllStudents(){
        /**
         * Complete list of students as protobuffer
         */

        ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("dns:///localhost:8080").defaultLoadBalancingPolicy("round_robin").usePlaintext().build();

        StudentServiceGrpc.StudentServiceBlockingStub stub = StudentServiceGrpc.newBlockingStub(managedChannel);

        FieldMask fieldMask = FieldMask.newBuilder().addPaths("studentId").addPaths("firstName").addPaths("lastName").build();

        Iterator<StudentResponse> response = stub.getAllStudent(org.example.StudentRequest.newBuilder().setId(1).setFieldMask(fieldMask).build());

        while(response.hasNext()){
//            response.next();
            System.out.println(response.next());
        }
    }

    public static void getAllStudentsLb() {
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
            .intercept(new ClientInterceptor())
            .defaultLoadBalancingPolicy("round_robin")
            .usePlaintext()
            .build();

    StudentServiceGrpc.StudentServiceBlockingStub stub = StudentServiceGrpc.newBlockingStub(managedChannel);

    FieldMask fieldMask = FieldMask.newBuilder()
            .addPaths("studentId")
            .addPaths("firstName")
            .addPaths("lastName")
            .build();

    try {
        Random random = new Random();
        int portFinder = random.nextInt(1000);
        Iterator<StudentResponse> response = stub.getAllStudent(
                org.example.StudentRequest.newBuilder()
                        .setId(1)
                        .setFieldMask(fieldMask)
                        .setPortfinder(portFinder)
                        .build()
        );
//        System.out.println("Using Portfinder: "+portFinder);
        while (response.hasNext()) {
//            response.next();
            System.out.println(response.next());
        }
        System.out.println("Done getting all students ");
    } finally {
        // Properly shutdown the channel
        managedChannel.shutdown();
    }
}
    public static void getStudentBatches(int start,int end, int increment){
        /**
         * Complete list of students but as batches
         */

        ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("dns://localhost").defaultLoadBalancingPolicy("round_robin").useTransportSecurity().build();

        StudentServiceGrpc.StudentServiceBlockingStub stub = StudentServiceGrpc.newBlockingStub(managedChannel);

        for(int i=start;i<=end;i+=increment) {
            Iterator<StudentBatchResponse> response = stub.getBatchStudent(org.example.StudentRequest.newBuilder().setId(i).build());
            while (response.hasNext()) {
                response.next();
//            System.out.println(response.next());
            }
        }
    }
    public static void getMultipleClients( int clients) throws InterruptedException {
        /**
         * Complete list of students but as batches of 3000(optimal) records from multiple clients(threads)
         */

        MyRunnable myRunnable = new MyRunnable();
        Thread threads[] = new Thread[clients];
        for(int i=0;i<clients;i++){
            threads[i] = new Thread(myRunnable);
            threads[i].start();
        }
        for(Thread thread : threads){
            thread.join();
        }

    }
    public static void main(String[] args) throws InterruptedException {

        String type = "multipleclients";
        //Options = "all" , "optimalbatch", "multipleclients"
        int startingBatchSize = 1000;
        int endingBatchSize = 100000;
        int increment = 1000;
        int numberOfClients = 3;

        switch (type) {
            case "all":
                getAllStudentsLb();
                break;
            case "optimalbatch":
                getStudentBatches(startingBatchSize,endingBatchSize,increment);
                break;
            case "multipleclients":
                getMultipleClients(numberOfClients);
                break;
            default:
                System.out.println("Invalid option");
        }
    }
}