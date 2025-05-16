package org.example;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import java.net.URI;

public class Server {
    public static void main(String[] args) {
        URI httpsUri = URI.create("https://localhost:8081/");

        ResourceConfig config = new ResourceConfig(
                org.example.services.Gettest.class,
                org.example.services.Getjson.class,
                org.example.services.Getbatch.class
        ).register(JacksonFeature.class);

        SSLContextConfigurator sslConfig = new SSLContextConfigurator();
        sslConfig.setKeyStoreFile("C:\\Users\\Administrator\\IdeaProjects\\jerseyrest\\server-keystore.p12");
        sslConfig.setKeyStorePass("changeit");
        sslConfig.setKeyStoreType("PKCS12");

        SSLEngineConfigurator sslEngine = new SSLEngineConfigurator(sslConfig)
                .setClientMode(false)
                .setNeedClientAuth(false);

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                httpsUri,
                config,
                true,
                sslEngine
        );

        System.out.println("HTTPS server started at " + httpsUri);
    }
}
