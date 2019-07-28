package transaction.flow.service;

import transaction.flow.domain.Transaction;
import transaction.flow.util.BigDecimalFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.math.BigDecimal.ZERO;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Yuriy Tumakha
 */
public final class TransactionService implements Service, BigDecimalFactory {

  @Override
  public CompletionStage<Optional<String>> processTransaction(Transaction tx) {
    return CompletableFuture.supplyAsync(() -> {
      assert tx != null : "Transaction must not be null";

      if (tx.getTransactionId() == null)
        return Optional.of("transactionId is required");
      else if (tx.getAmount() == null)
        return Optional.of("amount is required");

      if (bigDecimal(ZERO).equals(bigDecimal(tx.getAmount())))
        throw new IllegalArgumentException("Amount can't be zero");

      emulateLongProcessing(500);
      System.out.println("Processed successfully " + tx);

      return Optional.empty();
    });
  }

  private void emulateLongProcessing(int milliseconds) {
    try {
      MILLISECONDS.sleep(milliseconds);
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }
  }

}
