package transaction.flow;

import akka.Done;
import akka.actor.ActorSystem;
import transaction.flow.processor.TransactionProcessor;
import transaction.flow.service.Service;
import transaction.flow.service.TransactionService;

import static java.lang.String.format;

/**
 * @author Yuriy Tumakha
 */
public final class MainApp {

  public static void main(String[] args) {
    if (args.length == 0)
      System.out.println("Usage: java -jar transaction-flow.jar filename filename2 ... filenameN");
    else {
      ActorSystem actorSystem = ActorSystem.create("TransactionProcessor");
      Service transactionService = new TransactionService();

      TransactionProcessor transactionProcessor = new TransactionProcessor(actorSystem, transactionService);
      transactionProcessor.processFromFiles(args)
          .handle((done, ex) -> {
            if (ex != null)
              System.out.println(format("Terminated with %s: %s", ex.getClass().getName(), ex.getMessage()));

            actorSystem.terminate();
            return Done.getInstance();
          });
    }
  }

}
