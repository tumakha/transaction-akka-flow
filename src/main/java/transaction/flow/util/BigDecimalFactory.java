package transaction.flow.util;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;

/**
 * @author Yuriy Tumakha.
 */
public interface BigDecimalFactory {

  int DECIMAL_SCALE = 2;

  default BigDecimal bigDecimal(double dbl) {
    return bigDecimal(BigDecimal.valueOf(dbl));
  }

  default BigDecimal bigDecimal(int integer) {
    return bigDecimal(BigDecimal.valueOf(integer));
  }

  default BigDecimal bigDecimal(String str) {
    return bigDecimal(new BigDecimal(str));
  }

  default BigDecimal bigDecimal(BigDecimal decimal) {
    return decimal.setScale(DECIMAL_SCALE, HALF_UP);
  }

}
