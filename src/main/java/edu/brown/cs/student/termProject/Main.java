package edu.brown.cs.student.termProject;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.BoundingBox;
import database.Database;
import graph.Dijkstra;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
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
    System.out.println("You good");
    Database.setYelpDatabaseConnection();

    /*
    List<String> categories = new ArrayList<>();
    categories.add("Restaurant");
    categories.add("Park");
    categories.add("Museum");
    List<AttractionNode> attractions = BoundingBox.findAttractionsBetween(new double[]{34.136181, -118.432375},
      new double[]{41.856898,
        -71.385573}, categories);
    double[] boundBox = BoundingBox.findBoundingBoxBounds(new double[]{34.136181, -118.432375},
      new double[]{41.856898,
        -71.385573});
    System.out.println(boundBox);

    System.out.println(attractions.size());
    Dijkstra dij = new Dijkstra(attractions);
    List<AttractionNode> path = dij.execute(new double[]{34.136181, -118.432375},
      new double[]{41.856898,
        -71.385573}, 4, boundBox);
    for (AttractionNode n: path){
      System.out.println(n.getName());
    }*/


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

  private static class Test{
    public Test(){

    }
  }


  /**
   * Class that handles getting nearest traversable node for maps3 frontend.
   */
   private static class RouteHandler implements Route {
     @Override
     public Object handle(Request request, Response response) throws Exception {

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
         double[] originCoords = new double[]{originLat, originLon};
         double[] destCoords = new double[]{destLat, destLon};
         int numStops = data.getInt("stopPref");

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
             new double[]{originLat,originLon},
             new double[]{destLat, destLon}, categories);

         //first get bounding box info
         List<AttractionNode> fakeList = new ArrayList<>();
        // Dijkstra dijkstra = new Dijkstra(fakeList);
         System.out.println("number of nodes: " + attractions.size());
         //HARD CODE NUMBER OF STOPS FOR NOW
         Dijkstra dijkstra = new Dijkstra(attractions);
         dijkstra.setPreferences(preferredStop, costPreference);
         System.out.println("Categories");
         for(String s: categories){
           System.out.println(s);
         }
         System.out.println("PreferedStop is: " + preferredStop);
         for(double d: preferredStop){
           System.out.println(d);
         }
         System.out.println("CostPreference is: " + costPreference);

         //HARD CODED START AND END FOR DIJKSTRA FOR NOW
         double[] boxBounds = BoundingBox.findBoundingBoxBounds(originCoords, destCoords);
         route = dijkstra.execute(new double[]{41.83108984050821,-71.40029245994668},
           new double[]{41.819930960017274, -71.41042819577497}, numStops);

       } catch(Exception e){
         System.out.println("problem with dijkstras");
         e.printStackTrace();
       }

       Map<String, Object> variables = ImmutableMap.of("route", route);

       System.out.println(variables);
       try{
         System.out.println(new Gson().toJson(variables));
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
