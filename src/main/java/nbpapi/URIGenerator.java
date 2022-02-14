package nbpapi;

import java.net.URI;

public class URIGenerator {

    private static final String prefix = "http://api.nbp.pl/api/exchangerates/tables/";
    private static final String format = "/?format=json";

    public static URI currentTableJson(Table table) {
        String uriStr = String.format("%s%s%s",prefix,table.getValue(),format);
       return URI.create(uriStr);
    }
}
