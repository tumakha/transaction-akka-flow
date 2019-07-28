package transaction.flow.processor;

import akka.Done;
import akka.actor.ActorSystem;
import akka.stream.Supervision;
import akka.stream.javadsl.Sink;
import com.fasterxml.jackson.core.JsonParseException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import transaction.flow.service.Service;
import transaction.flow.service.TransactionService;
import transaction.flow.test.ResourceReader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Yuriy Tumakha
 */
public class TransactionProcessorTest implements ResourceReader {

  private static final ActorSystem actorSystem = ActorSystem.create("TransactionProcessorTest");
  private final Service service = new TransactionService();
  private final TransactionProcessor transactionProcessor = new TransactionProcessor(actorSystem, service);
  private List<String> results;
  private List<Throwable> errors;

  @Before
  public void init() {
    errors = new ArrayList<>();
    results = new ArrayList<>();
    transactionProcessor.resultCollector = Sink.foreach(results::add);
    transactionProcessor.exceptionDecider = ex -> {
      errors.add(ex);
      return Supervision.resume();
    };
  }

  @Test
  public void testProcessFromFile() throws IOException, URISyntaxException {
    CompletionStage<Done> done = transactionProcessor.processFromFiles(getAbsolutePath("transactions.txt"));
    done.toCompletableFuture().join();

    assertThatJson(results.toString()).isEqualTo(getContent("transaction-results.json"));

    assertThat(errors.size(), equalTo(1));

    Throwable ex = errors.get(0);
    assertThat(ex, instanceOf(JsonParseException.class));
    assertThat(ex.getMessage(), allOf(containsString("Unexpected character"),
        containsString("{invalid json}")));
  }

  @AfterClass
  public static void shutdown() {
    actorSystem.terminate();
  }

}
