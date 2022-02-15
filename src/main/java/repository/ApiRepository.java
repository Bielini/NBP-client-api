package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import nbpapi.Rate;
import nbpapi.RateTable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ApiRepository<T> {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<T> getList(URI uri) throws IOException,InterruptedException {
        final HttpResponse<String> response = getStringHttpResponse(uri);

        return mapper.readValue(response.body(), new TypeReference<List<T>>() {});

    }

    public T getObject(URI uri) throws IOException,InterruptedException {
        final HttpResponse<String> response = getStringHttpResponse(uri);
        return mapper.readValue(response.body(), new TypeReference<T>() {});
    }

    private HttpResponse<String> getStringHttpResponse(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        return client.send(request,HttpResponse.BodyHandlers.ofString());
    }


}
