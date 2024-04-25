package amazonrev.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.io.File;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;

import amazonrev.item.Author;
import amazonrev.item.Image;
import amazonrev.item.MainCategory;
import amazonrev.item.Video;
import amazonrev.parser.types.RawItem;
import amazonrev.util.Log;

/**
 * Parse Amazon Reviews 2023 item metadata.
 */
public class ItemParser extends FileParser {

  static void parse(String dataFilename, String outputFolder) throws IOException {
    JsonMapper mapper = new JsonMapper();
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    BufferedReader reader = new BufferedReader(new FileReader(dataFilename));
    BufferedWriter itemWriter = new BufferedWriter(new FileWriter(new File(outputFolder + "/item.csv")));
    BufferedWriter itemImageWriter = new BufferedWriter(new FileWriter(new File(outputFolder + "/itemimage.csv")));
    BufferedWriter itemVideoWriter = new BufferedWriter(new FileWriter(new File(outputFolder + "/itemvideo.csv")));
    BufferedWriter itemAuthorWriter = new BufferedWriter(new FileWriter(new File(outputFolder + "/itemauthor.csv")));
    BufferedWriter itemHasImageWriter = new BufferedWriter(new FileWriter(new File(outputFolder + "/item_has_itemimage.csv")));
    BufferedWriter itemHasVideoWriter = new BufferedWriter(new FileWriter(new File(outputFolder + "/item_has_itemvideo.csv")));
    BufferedWriter itemHasAuthorWriter = new BufferedWriter(new FileWriter(new File(outputFolder + "/item_has_itemauthor.csv")));
    BufferedWriter categoryWriter = new BufferedWriter(new FileWriter(new File(outputFolder + "/category.csv")));
    BufferedWriter categoryHasItemWriter = new BufferedWriter(new FileWriter(new File(outputFolder + "/category_has_item.csv")));
    BufferedWriter[] allWriters = new BufferedWriter[]{
      itemWriter, 
      itemImageWriter, 
      itemVideoWriter, 
      itemAuthorWriter,
      itemHasImageWriter, 
      itemHasVideoWriter, 
      itemHasAuthorWriter,
      categoryWriter, 
      categoryHasItemWriter, 
    };
    FileHeaders fh = new FileHeaders(separator);
    itemWriter.write(fh.getItem());
    itemImageWriter.write(fh.getItemImage());
    itemVideoWriter.write(fh.getItemVideo());
    itemAuthorWriter.write(fh.getItemAuthor());
    itemHasImageWriter.write(fh.getItemHasItemImage());
    itemHasVideoWriter.write(fh.getItemHasItemVideo());
    itemHasAuthorWriter.write(fh.getItemHasItemAuthor());
    categoryWriter.write(fh.getCategory());
    categoryHasItemWriter.write(fh.getCategoryHasItem());
    int lineCount = 0;
    int itemCount = -1;
    int imageCount = -1;
    int videoCount = -1;
    int authorCount = -1;
    int errCount = 0;
    Map<String, Integer> categoryIndexMap = new HashMap<String, Integer>();
    for (MainCategory category: MainCategory.values()) {
      addKeyToIndexMap(categoryIndexMap, category.toValue());
    }
    try (MappingIterator<RawItem> it = mapper.readerFor(RawItem.class).readValues(reader)) {
      while (it.hasNextValue()) {
        lineCount++;
        RawItem item = null;
        try {
          item = it.nextValue();
        } catch (JsonMappingException jme) {
          Log.error("Could not parse line " + lineCount + " in file " + dataFilename);
          Log.error(jme.toString());
          errCount++;
          if (errCount >= MAX_FAILED_LINES) {
            Log.error("Failed parsing too many lines, aborting!");
            System.exit(1);
          }
        }
        if (item != null) {
          try {
            itemCount++;
            String mainCategory = item.mainCategory();
            if (mainCategory != null) {
              addKeyToIndexMap(categoryIndexMap, mainCategory);
              int mainCategoryId = categoryIndexMap.get(item.mainCategory());
              writeLine(categoryHasItemWriter, new String[] {
                asString(mainCategoryId),
                asString(itemCount),
                "true",
              });
            }
            for (String category: item.categories()) {
              // Item meta categories field includes main category, avoid writing twice
              if (!category.equals(mainCategory)) {
                addKeyToIndexMap(categoryIndexMap, category);
                int categoryId = categoryIndexMap.get(category);
                writeLine(categoryHasItemWriter, new String[] {
                  asString(categoryId),
                  asString(itemCount),
                  "false",
                });
              }
            }
            String priceString = item.price();
            writeLine(itemWriter, new String[] {
              asString(itemCount),
              item.title(),
              item.subtitle(),
              asString(item.averageRating()),
              asString(item.ratingNumber()),
              toPostgresArray(item.features(), mapper),
              toPostgresArray(item.description(), mapper),
              asString(getDouble(priceString)),
              asString(item.store()),
              toJson(item.details(), mapper),
              item.parentAsin(),
            });
            for (Image image: item.images()) {
              imageCount++;
              writeLine(itemImageWriter, new String[] {
                asString(imageCount),
                image.thumb(),
                image.large(),
                image.variant(),
                image.hiRes(),
              });
              writeLine(itemHasImageWriter, new String[] {
                asString(itemCount),
                asString(imageCount),
              });
            }
            for (Video video: item.videos()) {
              videoCount++;
              writeLine(itemVideoWriter, new String[] {
                asString(videoCount),
                video.title(),
                video.url(),
                video.creatorHandle(),
              });
              writeLine(itemHasVideoWriter, new String[] {
                asString(itemCount),
                asString(videoCount),
              });
            }
            Author author = item.author();
            if (author != null) {
              authorCount++;
              writeLine(itemAuthorWriter, new String[] {
                asString(authorCount),
                author.avatar(),
                author.name(),
                toPostgresArray(author.about(), mapper),
              });
              writeLine(itemHasAuthorWriter, new String[] {
                asString(itemCount),
                asString(authorCount),
              });
            }
          } catch (Exception e) {
            // Just abort.
            // No system implemented to rollback failed writes to our csv files, and it's important 
            //   not to have undefined state in the files since they're loaded into our database.
            Log.error("Failed to write item " + item.toString());
            Log.error(e.toString());
            System.exit(1);
          }
        }
      }
    }
    for (String category: new TreeSet<String>(categoryIndexMap.keySet())) {
      int categoryIndex = categoryIndexMap.get(category);
      writeLine(categoryWriter, new String[] {
        asString(categoryIndex),
        category,
      });
    }
    for (BufferedWriter writer : allWriters) {
      try {
        writer.flush();
        writer.close();
      } catch (Exception e) {
        Log.error("Could not close " + writer);
        Log.error(e.toString());
      }
    }
  }

  public static void main(String[] args) throws IOException {
    final String dataFilename = "data/import/meta_Musical_Instruments.jsonl";
    final String outputFolder = "data";
    parse(dataFilename, outputFolder);
  }
}
