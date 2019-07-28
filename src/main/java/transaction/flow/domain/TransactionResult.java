package transaction.flow.domain;

import lombok.Value;

/**
 * @author Yuriy Tumakha
 */
@Value
public final class TransactionResult {

  private String transactionId;
  private String errorMsg;
  private Error error;

  public boolean isSuccessful() {
    return errorMsg == null && error == null;
  }

}
