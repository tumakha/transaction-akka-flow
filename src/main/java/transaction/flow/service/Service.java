package transaction.flow.service;

import transaction.flow.domain.Transaction;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * @author Yuriy Tumakha
 */
@FunctionalInterface
public interface Service {
  CompletionStage<Optional<String>> processTransaction(Transaction tx);
}
