package transaction.flow.domain;

import lombok.Value;

/**
 * @author Yuriy Tumakha
 */
@Value
public final class Error {

  private String exception;
  private String message;

}
