package org.example.threads;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyRunnable implements Runnable {

    @Override
    public void run() {
            Client client = ClientBuilder.newClient();
            WebTarget webTarget = client.target("https://localhost:8081/getbatch").queryParam("batch_id", 3000);
            Response response = webTarget.request(MediaType.APPLICATION_JSON).get();
            try (InputStream inputStream = response.readEntity(InputStream.class)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
//                    System.out.println(line);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }
}
