package org.example.model;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.StudentBatchResponse;
import org.example.StudentServiceGrpc;

import java.util.Iterator;

public class MyRunnable implements Runnable{

    @Override
    public void run(){
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

        StudentServiceGrpc.StudentServiceBlockingStub stub = StudentServiceGrpc.newBlockingStub(managedChannel);


        Iterator<StudentBatchResponse> response = stub.getBatchStudent(org.example.StudentRequest.newBuilder().setId(3000).build());
        while(response.hasNext()){
            response.next();
//            System.out.println(response.next());
        }
    }
}
