package edu.brown.cs.student.termProject;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.maps.GeoApiContext;
import database.BoundingBox;
import database.Database;
import graph.Dijkstra;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.json.JSONException;
import spark.*;

import spark.template.freemarker.FreeMarkerEngine;

import com.google.common.collect.ImmutableMap;

import freemarker.template.Configuration;

import org.json.JSONObject;
import com.google.gson.Gson;


/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;
  private static final int TIMER_DELAY = 2000;
  private static final GeoApiContext
      APICONNECTION = new GeoApiContext.Builder().apiKey("AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c").build();

  // map of latest checkins: maps user id to their latest checkins

  /**
   * The initial method called when execution begins.
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private final String[] args;

  /**
   * main constructor.
   * @param args args.
   */
  private Main(String[] args) {
    this.args = args;
  }

  /**
   *
   */
  private void run() {
    Database.setYelpDatabaseConnection();


    List<String> categories = new ArrayList<>();
    categories.add("Restaurant");
    categories.add("Park");
    categories.add("Museum");
    List<AttractionNode> attractions = BoundingBox.findAttractionsBetween(new double[]{34.136181, -118.432375},
      new double[]{41.856898,
        -71.385573}, categories, 5, 3);
//    double reviewRest = 0.0;
//    double numRest = 0.0;
//    double reviewPark = 0.0;
//    double numPark = 0.0;
//    double reviewMus = 0.0;
//    double numMus = 0.0;
//    double reviewShop = 0.0;
//    double numShops = 0.0;
//    for (AttractionNode a: attractions){
//      switch(a.getType()){
//        case 0:
//          reviewMus = reviewMus + a.getNumReviews();
//          numMus ++;
//          break;
//        case 1:
//          reviewPark = reviewPark + a.getNumReviews();
//          numPark++;
//          break;
//        case 2:
//          reviewRest = reviewRest + a.getNumReviews();
//          numRest++;
//          break;
//        case 3:
//          reviewShop = reviewShop + a.getNumReviews();
//          numShops++;
//          break;
//        default:
//          break;
//      }
//    }
//    System.out.println("Average Number of Reviews for Shop: " + reviewShop/numShops);
//    System.out.println("Average Number of Reviews for Restaurant: " + reviewRest/numRest);
//    System.out.println("Average Number of Reviews for Museum: " + reviewMus/numMus);
//    System.out.println("Average Number of Reviews for Park: " + reviewPark/numPark);
//    System.out.println(attractions.size());
    Dijkstra dij = new Dijkstra(attractions, APICONNECTION);
    dij.setPreferences(new double[] {10, 30, 100, 10}, 3);
    List<AttractionNode> path = dij.execute(new double[]{34.136181, -118.432375},
      new double[]{41.856898,
        -71.385573}, 4);
    for (AttractionNode r: path){
      System.out.println(r.getName() + " " + r.getLocation()[0] + " "+ r.getLocation()[2] +
        " and a cost of " + r.getCost() + " and a value of " + r.getValue());
    }


    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);

    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

  }

  /**
   * creates free marker engine.
   * @return free marker engine
   */
  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  /**
   * runs the spark server.
   * @param port given number
   */
  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());


    Spark.options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");

      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });

    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
    Spark.exception(Exception.class, new ExceptionPrinter());
    Spark.post("/route", new RouteHandler());
  }

  /**
   * Handle requests to the front page of our Stars website.
   */
  private static class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title",
          "Stars: Query the database", "answer", " ");
      return new ModelAndView(variables, "query.ftl");
    }
  }
  
  /**
   * Class that handles getting nearest traversable node for maps3 frontend.
   */
   private static class RouteHandler implements Route {
     @Override
     public Object handle(Request request, Response response) throws Exception {
      String error = "";
       List<AttractionNode> route = new ArrayList<>();
       try{
         JSONObject data = new JSONObject(request.body());
         //preferences which will be used for value heuristic
         int costPreference = data.getInt("costPref");
         double restaurantValue = data.getDouble("restValue");
         double museumValue = data.getDouble("musValue");
         double parkValue = data.getDouble("parkValue");
         double shopValue = data.getDouble("shopValue");
         double[] preferredStop = {museumValue, parkValue, restaurantValue, shopValue};
         double originLat = data.getDouble("originLat");
         double originLon = data.getDouble("originLon");
         double destLat = data.getDouble("destLat");
         double destLon = data.getDouble("destLon");
         double maxDist = data.getDouble("maxDist");
         double middleLat = 0.0;
         double middleLong = 0.0;
         try{
         middleLat = data.getDouble("middleLat");
         middleLong = data.getDouble("middleLong");
         
         }
         catch(JSONException e){
         }

         System.out.println("Middle Lat " + middleLat + " and middle long " + middleLong);
         int numStops = 0;
         try{ numStops = data.getInt("stopPref");}
         catch(Exception e){
           error = "Number of stops input is in the wrong format. Must be an integer greater than" +
             " 0";
           return new Gson().toJson(ImmutableMap.of("route", route, "error_message", error));
         }
         List<String> categories = new ArrayList<>();
         if (restaurantValue >= 0.3) {
           categories.add("Restaurant");
         }
         if (parkValue >= 0.3) {
           categories.add("Park");
         }
         if (museumValue >= 0.3) {
           categories.add("Museum");
         }
         if (shopValue >= 0.3){
           categories.add("Shop");
         }
         //NEED TO FIX SO THESE ARE NOT HARD SET START AND END
         List<AttractionNode> attractions = BoundingBox.findAttractionsBetween(
           new double[]{originLat, originLon},
           new double[]{destLat, destLon}, categories, numStops, costPreference);
         System.out.println("number of nodes: " + attractions.size());
         Dijkstra dijkstra = new Dijkstra(attractions, APICONNECTION);
         dijkstra.setPreferences(preferredStop, costPreference);

         if (numStops > 0) {
            double miles = dijkstra.distanceFormulaAPI(originLat,
              originLon, destLat, destLon) / 1000 / Constants.MILES_TO_KILOMETERS;
             System.out.println("Google Maps API: " + miles);

           //check to make sure the inputted max distance is not greater than the minimum distance
          if ((maxDist * Constants.MILES_TO_KILOMETERS) < (dijkstra.distanceFormulaAPI(originLat,
            originLon, destLat, destLon)/1000)){
            error = "It is impossible to complete the trip in " + maxDist + " miles";
            System.out.println(error);
            return new Gson().toJson(ImmutableMap.of("route", route, "error_message", error));
          }
           System.out.println("Categories");
           for (String s : categories) {
             System.out.println(s);
           }
           System.out.println("PreferedStop is: " + preferredStop);
           for (double d : preferredStop) {
             System.out.println(d);
           }
           System.out.println("CostPreference is: " + costPreference);
           //HARD CODED START AND END FOR DIJKSTRA FOR NOW
           route = dijkstra.execute(new double[]{originLat, originLon},
             new double[]{destLat, destLon}, numStops);
         }
         else if (numStops == 0){
           error = "This route has no stops";
           return new Gson().toJson(ImmutableMap.of("route", route, "error_message", error));
         }
         else if (numStops < 0){
           error = "The number of stops cannot be negative";
           return new Gson().toJson(ImmutableMap.of("route", route, "error_message", error));
         }


         double routeLength = 0;
         for (int i = 1; i< route.size(); i++){
           routeLength += dijkstra.distanceFormulaAPI(route.get(i-1).getCoordinates()[0],
             route.get(i-1).getCoordinates()[1],route.get(i).getCoordinates()[0],
             route.get(i).getCoordinates()[1]);
         }
         routeLength += dijkstra.distanceFormulaAPI(route.get(0).getCoordinates()[0],
           route.get(0).getCoordinates()[1], originLat, originLon);
         routeLength += dijkstra.distanceFormulaAPI(route.get(route.size()-1).getCoordinates()[0],
           route.get(route.size()-1).getCoordinates()[1], destLat, destLon);
         routeLength = (routeLength/1000) / Constants.MILES_TO_KILOMETERS;
         System.out.println("Dijkstra's Route is this length " + route.size() + " and this many " +
           "miles: " + routeLength);
         if (routeLength > maxDist){
           double diff = Math.ceil(routeLength-maxDist);
           error = "Unfortunately, we cannot plan you a roadtrip under " + maxDist + " miles. You" +
             " would need to increase your distance constraint by " + diff;
           System.out.println(error);
           route = new ArrayList<>();
         }
         for (AttractionNode r: route){
           System.out.println(r.getName() + " " + r.getLocation()[0] + " "+ r.getLocation()[2] +
             " and a cost of " + r.getCost() + " and a value of " + r.getValue());
         }

       } catch(Exception e){
         System.out.println("problem with dijkstras");
         e.printStackTrace();
       }

       Map<String, Object> variables = ImmutableMap.of("route", route, "error_message", error);

     //  System.out.println(variables);
       try{
        // System.out.println(new Gson().toJson(variables));
       } catch(Exception e) {
         System.out.println("something went wrong here");
         e.printStackTrace();
       }
       return new Gson().toJson(variables);
     }
   }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }


}
