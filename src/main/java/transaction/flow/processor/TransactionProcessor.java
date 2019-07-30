package transaction.flow.processor;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorAttributes;
import akka.stream.IOResult;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;
import transaction.flow.domain.Error;
import transaction.flow.domain.Transaction;
import transaction.flow.domain.TransactionResult;
import transaction.flow.service.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;

/**
 * @author Yuriy Tumakha
 */
public final class TransactionProcessor extends StreamProcessor implements FileSource, JsonSupport {

  private final Service transactionService;

  public TransactionProcessor(ActorSystem actorSystem, Service transactionService) {
    super(actorSystem);
    this.transactionService = transactionService;
  }

  public CompletionStage<Done> processFromFiles(String... filenames) {
    return Source.from(asList(filenames))
        .flatMapConcat(this::processFile)
        .withAttributes(ActorAttributes.withSupervisionStrategy(exceptionDecider))
        .runWith(resultCollector, materializer);
  }

  private Source<String, CompletionStage<IOResult>> processFile(String filename) {
    return linesFromFile(filename)
        .via(parseJson(Transaction.class))
        .via(processTransaction())
        .via(convertToJson())
        // TransactionResult objects grouped into groups of 100 and converted into one JSON String
        .groupedWithin(100, Duration.ofSeconds(2))
        .map(List::toString);
  }

  Flow<Transaction, TransactionResult, NotUsed> processTransaction() {
    return Flow.of(Transaction.class)
        .mapAsync(10, this::process);
  }

  private CompletionStage<TransactionResult> process(Transaction tx) {
    return transactionService.processTransaction(tx)
        .handle((errorMsg, ex) -> {
          String msg = ofNullable(errorMsg).flatMap(identity()).orElse(null);
          Error error = ofNullable(ex).map(e -> new Error(e.getClass().getName(), e.getMessage())).orElse(null);
          return new TransactionResult(tx.getTransactionId(), msg, error);
        });
  }

}
