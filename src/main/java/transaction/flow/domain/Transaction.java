package transaction.flow.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Yuriy Tumakha
 */
@Data
public final class Transaction {

  private String transactionId;
  private Long fromAccount;
  private Long toAccount;
  private BigDecimal amount;
  private String currencyCode;
  private String description;
  private LocalDateTime time;

}
