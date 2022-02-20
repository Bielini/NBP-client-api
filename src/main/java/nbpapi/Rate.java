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
    private BigDecimal ask;
    private BigDecimal bid;
    private BigDecimal mid;




    @Override
    public String toString() {
        return code;
    }
}
