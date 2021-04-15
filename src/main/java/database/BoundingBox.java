package database;

import attractions.Museum;
import attractions.Park;
import edu.brown.cs.student.termProject.Restaurant;
import attractions.Shop;
import edu.brown.cs.student.termProject.AttractionNode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class with static functions to find all the attractions within a bounding box
 * around two sets of coordinates.
 */
public class BoundingBox {

  /**
   * Creates a bounding box around the given starting and end positions,
   * and returns a list of the AttractionNodes from the yelp-database within
   * this box that are of the given categories.
   *
   * @param coords1 - [lat, lon] of the start position
   * @param coords2 - [lat, lon] of the end position
   * @param categories - list of acceptable attraction categories
   * @return list of attraction nodes of the given categories within a
   * box around the given positions
   */
  public static List<AttractionNode> findAttractionsBetween(
          double[] coords1, double[] coords2, List<String> categories) {

    double[] boundingBoxBounds = findBoundingBoxBounds(coords1, coords2);

    double[] expandedBoundingBoxBounds = expandBoundingBoxBounds(boundingBoxBounds, 2.0);

    try {
      return findAttractionsWithinBoundingBox(expandedBoundingBoxBounds, categories);
    } catch (SQLException | IOException e) {
      throw new IllegalArgumentException("ERROR: Error while connecting to SQL database");
    }
  }

  /**
   * Finds the bounds for a bounding box to be created around the given coordinates.
   *
   * @param coords1 - [lat, lon] of the first position
   * @param coords2 - [lat, lon] of the second position
   * @return [start lat, end lat, start lon, end lon] of the bounding box
   */
  public static double[] findBoundingBoxBounds(double[] coords1, double[] coords2) {
    double[] bounds = new double[4];

    if (coords1[0] < coords2[0]) {
      bounds[0] = coords1[0];
      bounds[1] = coords2[0];
    } else {
      bounds[0] = coords2[0];
      bounds[1] = coords1[0];
    }

    if (coords1[1] < coords2[1]) {
      bounds[2] = coords1[1];
      bounds[3] = coords2[1];
    } else {
      bounds[2] = coords2[1];
      bounds[3] = coords1[1];
    }

    return bounds;
  }

  /**
   * Extends the given bounding box expansionFactor units in every direction.
   *
   * @param boundingBoxBounds - original bounding box to be expanded
   * @param expansionFactor - number of units to expand the bounding box by in every direction
   * @return [start lat, end lat, start lon, end lon] of the expanded bounding box
   */
  public static double[] expandBoundingBoxBounds(double[] boundingBoxBounds, double expansionFactor) {
    double[] expandedBoundingBoxBounds = new double[4];

    expandedBoundingBoxBounds[0] = boundingBoxBounds[0] - expansionFactor;
    expandedBoundingBoxBounds[1] = boundingBoxBounds[1] + expansionFactor;
    expandedBoundingBoxBounds[2] = boundingBoxBounds[2] - expansionFactor;
    expandedBoundingBoxBounds[3] = boundingBoxBounds[3] + expansionFactor;

    return expandedBoundingBoxBounds;
  }

  /**
   * Returns a list of all the attractions in the yelp database within the given
   * bounding box that are of the given categories.
   *
   * @param boundingBoxBounds - [start lat, end lat, start lon, end lon] of the bounding box
   * @param categories - list of acceptable attraction categories
   * @return list of attraction nodes of the given categories within the given bounding box
   * @throws SQLException - if yelp database cannot be successfully queried
   */
  public static List<AttractionNode> findAttractionsWithinBoundingBox(
          double[] boundingBoxBounds, List<String> categories)
      throws SQLException, IOException {
    Connection conn = Database.getYelpDatabaseConnection();

    String query = "SELECT * FROM yelp_business " +
            "WHERE (latitude BETWEEN ? and ?) " +
            "AND (longitude BETWEEN ? AND ?)" +
            "AND (stars > 3.0)" +
            "AND (review_count > 10)";

    PreparedStatement prep = conn.prepareStatement(query);
    prep.setDouble(1, boundingBoxBounds[0]);
    prep.setDouble(2, boundingBoxBounds[1]);
    prep.setDouble(3, boundingBoxBounds[2]);
    prep.setDouble(4, boundingBoxBounds[3]);

    ResultSet rs = prep.executeQuery();

    List<AttractionNode> attractionsWithinBox = new ArrayList<>();

    while (rs.next()) {

      String id = rs.getString("business_id");
      String name = rs.getString("name");

      String address = rs.getString("address");
      String city = rs.getString("city");
      String state = rs.getString("state");
      String postalCode = rs.getString("postal_code");
      String[] location = new String[]{address, city, state, postalCode};

      double lat = rs.getDouble("latitude");
      double lon = rs.getDouble("longitude");
      double[] coords = new double[]{lat, lon};

      double price = 0; //TODO: Decide on what to do for price
//      String attributesList = rs.getString("attributes");
//      if (attributesList.contains("RestaurantsPriceRange2")) {
//        System.out.println("HERE");
//        System.out.println(attributesList);
//      }

      double rating = rs.getDouble("stars");

      String categoriesList = rs.getString("categories");

      if ((categoriesList.contains("Restaurants")
              || categoriesList.contains("Food")
              || categoriesList.contains("Bars"))
              && categories.contains("Restaurant")) {
        AttractionNode nextAttraction = new Restaurant(
                id, name, location, coords, price, rating);
        attractionsWithinBox.add(nextAttraction);
      } else if (categoriesList.contains("Museums") && categories.contains("Museum")) {
        AttractionNode nextAttraction = new Museum(
                id, name, location, coords, price, rating);
        attractionsWithinBox.add(nextAttraction);
      } else if (categoriesList.contains("Parks") && categories.contains("Park")) {
        AttractionNode nextAttraction = new Park(
                id, name, location, coords, price, rating);
        attractionsWithinBox.add(nextAttraction);
      } else if (categoriesList.contains("Shopping") && categories.contains("Shop")) {
        AttractionNode nextAttraction = new Shop(
                id, name, location, coords, price, rating);
        attractionsWithinBox.add(nextAttraction);
      }
    }
    prep.close();
    rs.close();

    double startLat = boundingBoxBounds[0];
    //[start lat, end lat, start lon, end lon]
    int min = 1;
    double latDist = 0;
    double lonDist = 0;
    //start lat > end lat
    if (boundingBoxBounds[0] > boundingBoxBounds[1]) {
      min = (int) Math.floor(boundingBoxBounds[0] - boundingBoxBounds[1]);
      latDist = boundingBoxBounds[0] - boundingBoxBounds[1];
    } else { //end lat >= start lat
      min = (int) Math.floor(boundingBoxBounds[1] - boundingBoxBounds[0]);
      latDist = boundingBoxBounds[1] - boundingBoxBounds[0];
    }
    //start lon > end lon
    if (boundingBoxBounds[2] > boundingBoxBounds[3]) {
      lonDist = boundingBoxBounds[2] - boundingBoxBounds[3];
      if (min > (int) Math.floor(boundingBoxBounds[2] - boundingBoxBounds[3])) {
        min = (int) Math.floor(boundingBoxBounds[2] - boundingBoxBounds[3]);
      }
    } else { //end lon > start lon
      lonDist = boundingBoxBounds[3] - boundingBoxBounds[2];
      if (min > (int) Math.floor(boundingBoxBounds[3] - boundingBoxBounds[2])) {
          min = (int) Math.floor(boundingBoxBounds[3] - boundingBoxBounds[2]);
        }
    }

    for (int i = 0; i < min; i++) {
      double reqLat = 0;
      double reqLon = 0;
      //start lat > end lat
      if (boundingBoxBounds[0] > boundingBoxBounds[1]) {
        reqLat = (latDist / min )*i + boundingBoxBounds[1];
      } else {
        reqLat = (latDist / min )*i + boundingBoxBounds[0];
      }
      //start lon > end lon
      if (boundingBoxBounds[2] > boundingBoxBounds[3]) {
        reqLon = (lonDist / min )*i + boundingBoxBounds[3];
      } else {
        reqLon = (lonDist / min )*i + boundingBoxBounds[2];
      }
      System.out.println(boundingBoxBounds[0]);
      System.out.println(boundingBoxBounds[1]);
      System.out.println(boundingBoxBounds[2]);
      System.out.println(boundingBoxBounds[3]);
      System.out.println(reqLat);
      System.out.println(reqLon);
      System.out.println("--");
      URL url = new URL(
          "https://api.yelp.com/v3/businesses/search?latitude=" + reqLat + "&longitude=" + reqLon);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      String yelpKey =
          "PlFxZHha-zmiQvrMQUb12P0uD9s_GZRJzzGZqVSJFKgR4iDXj1aBwOBuMc2DBFYkZODPw2V5PaJBMapJ5WA9PA3Lx2cGXq9FkzzT45m9t9I9gsXDdGCwQnuYu6J3YHYx";
      con.setRequestProperty("Authorization", "Bearer " + yelpKey);
      BufferedReader in = null;
      try {
        in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
          System.out.println(line);
          sb.append(line);
        }
        JSONObject json = new JSONObject(sb.toString());
        System.out.println("buffered reader");
        System.out.println(json.getInt("total"));
        if (json.getInt("total") != 0) {
          for (int j = 0; i < json.getInt("total"); i++) {
            System.out.println(json.getJSONArray("businesses"));

          }
        }

      } catch (IOException | JSONException e) {
          System.out.println("ERROR: failed to open input stream");
          e.printStackTrace();
        }
      }


    return attractionsWithinBox;
  }
}
