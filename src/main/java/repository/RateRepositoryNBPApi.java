package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nbpapi.Rate;
import nbpapi.RateTable;
import nbpapi.Table;
import nbpapi.URIGenerator;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

public class RateRepositoryNBPApi implements RateRepository {
    private final HttpClient client = HttpClient.newHttpClient();
    @Override
    public List<Rate> findByTableAndDate(Table table, LocalDate date) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URIGenerator.currentTableJson(table))
                .GET()
                .build();
        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        final List<RateTable> rateTableList = mapper.readValue(response.body(), new TypeReference<List<RateTable>>() {
        });
        return rateTableList.get(0).getRates();
    }
}
