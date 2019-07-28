package transaction.flow.processor;

import akka.stream.IOResult;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Framing;
import akka.stream.javadsl.Source;
import akka.util.ByteString;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletionStage;

import static akka.stream.javadsl.FramingTruncation.ALLOW;

/**
 * @author Yuriy Tumakha
 */
interface FileSource {

  String lineSeparator = System.getProperty("line.separator");
  int maxLineSize = 4096;

  default Source<String, CompletionStage<IOResult>> linesFromFile(String filename) {
    return FileIO.fromPath(getPath(filename), maxLineSize)
        .via(Framing.delimiter(ByteString.fromString(lineSeparator), maxLineSize, ALLOW))
        .map(ByteString::utf8String);
  }

  default Path getPath(String filename) {
    Path path = Paths.get(filename);
    System.out.println("\nSource " + path.toAbsolutePath());
    return path;
  }

}
