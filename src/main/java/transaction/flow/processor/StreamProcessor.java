package transaction.flow.processor;

import akka.Done;
import akka.actor.ActorSystem;
import akka.japi.function.Function;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.Supervision;
import akka.stream.javadsl.Sink;

import java.util.concurrent.CompletionStage;


/**
 * @author Yuriy Tumakha
 */
abstract class StreamProcessor {

  final ActorSystem actorSystem;
  final Materializer materializer;
  Function<Throwable, Supervision.Directive> exceptionDecider;
  Sink<String, CompletionStage<Done>> resultCollector;

  StreamProcessor(ActorSystem actorSystem) {
    this.actorSystem = actorSystem;
    materializer = ActorMaterializer.create(actorSystem);
    exceptionDecider = defaultExceptionDecider();
    resultCollector = defaultResultCollector();
  }

  private Function<Throwable, Supervision.Directive> defaultExceptionDecider() {
    return ex -> {
      actorSystem.log().error("{}: {}", ex.getClass().getName(), ex.getMessage());
      return Supervision.resume();
    };
  }

  private Sink<String, CompletionStage<Done>> defaultResultCollector() {
    return Sink.foreach(System.out::println);
  }

}
