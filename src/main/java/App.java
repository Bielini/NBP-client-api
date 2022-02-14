import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nbpapi.RateTable;
import nbpapi.Table;
import nbpapi.URIGenerator;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URIGenerator.currentTableJson(Table.TABLE_B))
                .GET()
                .build();
        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println(response.body());

        ObjectMapper mapper = new ObjectMapper();
        final List<RateTable> rateTableList = mapper.readValue(response.body(), new TypeReference<List<RateTable>>() {
        });

        RateTable currency = rateTableList.get(0);

        System.out.println(currency.getNo());

    }
}
