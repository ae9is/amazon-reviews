package amazonrev.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import amazonrev.review.ReviewImage;
import amazonrev.review.Review;
import amazonrev.util.Log;

/**
 * Parse Amazon Reviews 2023 review data.
 */
public class ReviewParser extends FileParser {

  static void parse(String dataFilename, String outputFolder) throws IOException {
    JsonMapper mapper = new JsonMapper();
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
    mapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
    BufferedReader reader = new BufferedReader(new FileReader(dataFilename));
    BufferedWriter reviewWriter = new BufferedWriter(new FileWriter(new File(outputFolder + "/review.csv")));
    BufferedWriter reviewImageWriter = new BufferedWriter(new FileWriter(new File(outputFolder + "/reviewimage.csv")));
    BufferedWriter reviewHasImageWriter = new BufferedWriter(new FileWriter(new File(outputFolder + "/review_has_reviewimage.csv")));
    BufferedWriter usersWriter = new BufferedWriter(new FileWriter(new File(outputFolder + "/users.csv")));
    BufferedWriter userHasReviewWriter = new BufferedWriter(new FileWriter(new File(outputFolder + "/user_has_review.csv")));
    BufferedWriter[] allWriters = new BufferedWriter[] {
        reviewWriter,
        reviewImageWriter,
        reviewHasImageWriter,
        usersWriter,
        userHasReviewWriter,
    };
    FileHeaders fh = new FileHeaders(separator);
    reviewWriter.write(fh.getReview());
    reviewImageWriter.write(fh.getReviewImage());
    reviewHasImageWriter.write(fh.getReviewHasReviewImage());
    usersWriter.write(fh.getUsers());
    userHasReviewWriter.write(fh.getUserHasReview());
    int lineCount = 0;
    int reviewCount = -1;
    int imageCount = -1;
    int errCount = 0;
    Map<String, Integer> userIndexMap = new HashMap<String, Integer>();
    try (MappingIterator<Review> it = mapper.readerFor(Review.class).readValues(reader)) {
      while (it.hasNextValue()) {
        lineCount++;
        Review review = null;
        try {
          review = it.nextValue();
        } catch (JsonMappingException jme) {
          Log.error("Could not parse line " + lineCount + " in file " + dataFilename);
          Log.error(jme.toString());
          errCount++;
          if (errCount >= MAX_FAILED_LINES) {
            Log.error("Failed parsing too many lines, aborting!");
            System.exit(1);
          }
        }
        if (review != null) {
          try {
            reviewCount++;
            writeLine(reviewWriter, new String[] {
                asString(reviewCount),
                asString(review.getRating()),
                review.getTitle(),
                review.getText(),
                review.getAsin(),
                review.getParentAsin(),
                asString(review.getTimestamp().toInstant().toEpochMilli()),
                asString(review.getHelpfulVote()),
                asString(review.getVerifiedPurchase()),
            });
            for (ReviewImage image : review.getImages()) {
              imageCount++;
              writeLine(reviewImageWriter, new String[] {
                  asString(imageCount),
                  image.smallImageURL(),
                  image.mediumImageURL(),
                  image.largeImageURL(),
                  image.attachmentType(),
              });
              writeLine(reviewHasImageWriter, new String[] {
                  asString(reviewCount),
                  asString(imageCount),
              });
            }
            String amznUserID = review.getUserID();
            if (amznUserID != null) {
              addKeyToIndexMap(userIndexMap, amznUserID);
              int uid = userIndexMap.get(amznUserID);
              writeLine(userHasReviewWriter, new String[] {
                  asString(uid),
                  asString(reviewCount),
              });
            }
          } catch (Exception e) {
            // Just abort.
            // No system implemented to rollback failed writes to our csv files, and it's important
            // not to have undefined state in the files since they're loaded into our database.
            Log.error("Failed to write item " + review.toString());
            Log.error(e.toString());
            System.exit(1);
          }
        }
      }
    }
    for (String amznUserID : new TreeSet<String>(userIndexMap.keySet())) {
      int uid = userIndexMap.get(amznUserID);
      writeLine(usersWriter, new String[] {
          asString(uid),
          amznUserID,
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
    final String dataFilename = "data/import/Musical_Instruments.jsonl";
    final String outputFolder = "data/import";
    parse(dataFilename, outputFolder);
  }
}
