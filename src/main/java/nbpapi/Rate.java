package nbpapi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rate {
    private String currency;
    private String code;
    private String ask;
    private double bid;
    private double mid;




    @Override
    public String toString() {
        return code;
    }
}
