package amazonrev.parser;

import java.io.IOException;

import amazonrev.util.Log;

public class App {

  public static void main(String[] args) throws IOException {
    Log.info("Parsing item meta data ...");
    ItemParser.main(args);
    Log.info("Parsing review data ...");
    ReviewParser.main(args);
    Log.info("Done.");
  }
}
