package com.sigefve.adaptadores;

// url = https://tapython.xipatlani.tk/
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.concurrent.CompletableFuture;
import java.lang.System;

public class ClienteAPI {

    private String urlBaseSigefve = "https://tapython.xipatlani.tk/";

    public void post1(String endpoint, String cuerpoJson){
        // Create an HttpClient instance
        HttpClient client = HttpClient.newHttpClient();
        String direccionAPI = urlBaseSigefve+endpoint;
        // Create a HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(direccionAPI))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(cuerpoJson))
                .build();

        // Send the request asynchronously

        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Peticion a "+direccionAPI);
        System.out.println("Contenido:\n"+cuerpoJson);
        // Handle the response
        responseFuture.thenApply(HttpResponse::body)
                      .thenAccept(System.out::println)
                      .join(); // Wait for the response
    }

    public static void main(String[] args) {
        ClienteAPI cliente = new ClienteAPI();

        String cuerpoJson = """
{
    \"id_vehiculo\":9876,
    \"nivel_bateria\":100,
    \"temperatura\":50
}
        """;
        // "telemetria = evento.get('telemetria', {})\r\n" + //
        //                 "        id_vehiculo = telemetria.get('id_vehiculo')\r\n" + //
        //                 "        nivel_bateria = telemetria.get('nivel_bateria', 100)\r\n" + //
        //                 "        temperatura = telemetria.get('temperatura_motor', 0)"
        cliente.post1("telemetria", cuerpoJson);
        
    }
}