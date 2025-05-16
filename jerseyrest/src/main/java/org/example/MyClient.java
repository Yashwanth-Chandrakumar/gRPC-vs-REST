package org.example;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.threads.MyRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyClient {
    public static void getAllText(){
        /**
         *  Single payload Response type: String
         */
        Client client = ClientBuilder.newClient();
        String response = client.target("https://localhost:8081/getall").request(MediaType.TEXT_PLAIN).get(String.class);
        System.out.println(response);
    }

    public static void getAllJson(){
        /**
         *  Single payload Response type: JSON
         */

        Client client = ClientBuilder.newClient();
        String response = client.target("https://localhost:8081/getjson").request(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println(response);
    }

    public static void getOptimalBatch(int start,int end, int increment) throws IOException {
        /**
         *  Batch Payload finding the optimal batch size. Increments by 1000.
         *  For each batch size client runs 100 times to get average metrics.
         */

        Client client = ClientBuilder.newClient();
        for (int i=start;i<=end;i+=increment) {
            WebTarget webTarget = client.target("https://localhost:8081/getbatch").queryParam("batch_id", i);
            Response response = webTarget.request(MediaType.APPLICATION_JSON).get();
            try (InputStream inputStream = response.readEntity(InputStream.class)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
//                    System.out.println(line);
                    }
                }
            }
        }
    }

    public static void getMultipleClients(int numberOfClients) throws InterruptedException {
        /**
         *  Batch Payload. Multiple threads resembling clients.
         *  For each client requests for 3000 batch size(optimal).
         *  For each request server iterates 100 times to get average metrics.
         */
        MyRunnable runnable = new MyRunnable();
        Thread threads[] = new Thread[numberOfClients];
        for (int i=0;i<numberOfClients;i++){
            threads[i] = new Thread(runnable);
            threads[i].start();

        }
        for(Thread thread : threads){
            thread.join();
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        String type = "optimalbatch";
        //Options = "alltext" , "alljson" , "optimalbatch" , "multipleclients"
        int startingBatchSize = 1000;
        int endingBatchSize = 100000;
        int increment = 1000;
        int numberOfClients = 10;

        switch (type) {
            case "alltext":
                getAllText();
                break;
            case "alljson":
                getAllJson();
                break;
            case "optimalbatch":
                getOptimalBatch(startingBatchSize,endingBatchSize,increment);
                break;
            case "multipleclients":
                getMultipleClients(numberOfClients);
                break;
            default:
                System.out.println("Invalid option");
        }

    }
}
