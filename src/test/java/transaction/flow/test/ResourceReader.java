package transaction.flow.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Yuriy Tumakha
 */
public interface ResourceReader {

  default String getAbsolutePath(String filename) throws IOException, URISyntaxException {
    return getResource(filename).toAbsolutePath().toString();
  }

  default Path getResource(String filename) throws IOException, URISyntaxException {
    URL resource = getClass().getClassLoader().getResource(filename);
    if (resource != null) {
      return Paths.get(resource.toURI());
    } else {
      throw new IOException("File not found: " + filename);
    }
  }

  default String getContent(String filename) throws IOException, URISyntaxException {
    return new String(Files.readAllBytes(getResource(filename)), UTF_8);
  }

}
