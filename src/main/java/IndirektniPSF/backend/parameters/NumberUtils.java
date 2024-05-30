package IndirektniPSF.backend.parameters;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface NumberUtils {

    public static double roundToTwoDecimals(double value) {
       return Math.abs(BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_DOWN)
                .doubleValue());
    }
}
