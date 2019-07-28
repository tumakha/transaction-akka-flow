package transaction.flow.processor;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;
import akka.stream.testkit.TestSubscriber;
import akka.stream.testkit.javadsl.TestSink;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import transaction.flow.domain.Transaction;
import transaction.flow.domain.TransactionResult;
import transaction.flow.service.Service;
import transaction.flow.service.TransactionService;
import transaction.flow.util.BigDecimalFactory;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Yuriy Tumakha
 */
public class ProcessTransactionFlowTest implements BigDecimalFactory {

  private static final ActorSystem actorSystem = ActorSystem.create("ProcessTransactionFlowTest");
  private final Materializer materializer = ActorMaterializer.create(actorSystem);
  private final Service transactionService = new TransactionService();

  private Flow<Transaction, TransactionResult, NotUsed> processTransactionFlow =
      new TransactionProcessor(actorSystem, transactionService).processTransaction();

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void testProcessFlowSuccessfully() {
    Transaction tx = new Transaction();
    tx.setTransactionId("3611a5ac-6a50-4fb3-bcb1-5a39052ff1b2");
    tx.setAmount(bigDecimal(120.56));

    processFlow(tx, new TransactionResult(tx.getTransactionId(), null, null));
  }

  @Test
  public void testTransactionIdIsNull() {
    Transaction tx = new Transaction();
    tx.setAmount(bigDecimal(100));

    processFlow(tx, new TransactionResult(tx.getTransactionId(), "transactionId is required", null));
  }

  @Test
  public void testAmountIsNull() {
    Transaction tx = new Transaction();
    tx.setTransactionId("2");

    processFlow(tx, new TransactionResult(tx.getTransactionId(), "amount is required", null));
  }

  @Test
  public void testAmountIsZero() {
    Transaction tx = new Transaction();
    tx.setTransactionId("3");
    tx.setAmount(bigDecimal(0));

    TransactionResult transactionResult = processFlow(tx);

    assertThat(transactionResult.getTransactionId(), equalTo(tx.getTransactionId()));
    assertThat(transactionResult.isSuccessful(), is(false));
    assertThat(transactionResult.getErrorMsg(), nullValue());
    assertThat(transactionResult.getError().getMessage(),
        equalTo("java.lang.IllegalArgumentException: Amount can't be zero"));
  }

  @Test
  public void testTransactionIsNull() {
    expectedEx.expect(NullPointerException.class);
    expectedEx.expectMessage("Element must not be null, rule 2.13");

    processFlow(null);
  }

  private void processFlow(Transaction input, TransactionResult expectedResult) {
    testFlow(input).requestNext(expectedResult);
  }

  private TransactionResult processFlow(Transaction input) {
    return testFlow(input).requestNext();
  }

  private TestSubscriber.Probe<TransactionResult> testFlow(Transaction input) {
    return Source.single(input).via(processTransactionFlow)
        .runWith(TestSink.probe(actorSystem), materializer);
  }

  @AfterClass
  public static void shutdown() {
    actorSystem.terminate();
  }

}
