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

    List<String> categories = new ArrayList<>();
    categories.add("Restaurant");
    categories.add("Park");
    categories.add("Museum");
    List<AttractionNode> attractions = BoundingBox.findAttractionsBetween(new double[]{40.67708,
        -74.078168},
      new double[]{41.856898,
        -71.385573}, categories);
    double[] boundBox = BoundingBox.findBoundingBoxBounds(new double[]{40.67708,
        -74.078168},
      new double[]{41.856898,
        -71.385573});
    System.out.println(boundBox);

    System.out.println(attractions.size());
    Dijkstra dij = new Dijkstra(attractions);
    dij.execute(new double[]{40.67708,
        -74.078168},
      new double[]{41.856898,
        -71.385573}, 4, boundBox);

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

       System.out.println("here");
       try{
         JSONObject data = new JSONObject(request.body());
         //preferences which will be used for value heuristic
         int costPreference = data.getInt("costPref");
         double restaurantValue = data.getDouble("restValue");
         double museumValue = data.getDouble("musValue");
         double parkValue = data.getDouble("parkValue");
         double shopValue = data.getDouble("shopValue");
         double[] preferredStop = {museumValue, parkValue, restaurantValue, shopValue};

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
             new double[]{41.83108984050821,-71.40029245994668},
             new double[]{41.819930960017274, -71.41042819577497}, categories);

         //first get bounding box info
         List<AttractionNode> fakeList = new ArrayList<>();
        // Dijkstra dijkstra = new Dijkstra(fakeList);
         System.out.println("number of nodes: " + attractions.size());
         //HARD CODE NUMBER OF STOPS FOR NOW
         int numStops = 4;
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
       //  dijkstra.execute(new double[]{41.83108984050821,-71.40029245994668},
        //   new double[]{41.819930960017274, -71.41042819577497}, numStops);

         //dijkstra.execute();

       } catch(Exception e){
         System.out.println("problem with data");
       }
       // creating filler stops for now to test with front-end
       //AttractionNode[] stops = new AttractionNode[];
       System.out.println("here1");

       Map <String, AttractionNode> route = new HashMap<>();
       List<AttractionNode> stops = new ArrayList<>();
       System.out.println("here2");
       List<Double> stops2 = new ArrayList<>();
       stops2.add(4.0);

       try{
         //Museum m = new Museum();
         //Park p = new Park();
         String[] rLoc = new String[]{"475 3rd St", "San Francisco", "CA", "94107"};
         double [] rCoords = new double[]{37.7817529521, -122.39612197};
         Restaurant r = new Restaurant("tnhfDv5Il8EaGSXZGiuQGg", "Garaje", rLoc, rCoords, 1.0,  4.5);
         Restaurant r2 = new Restaurant("tnhfDv5Il8EaGSXZGiuQGh", "Garaje", rLoc, rCoords, 1.0,  4.5);

         //Shop s = new Shop();

         //stops.put(m.getId(), m);
         //stops.put(p.getId(), p);
         //route.put(r.getId(), r);
         //route.put(r2.getId(), r2);
          stops.add(r);
          stops.add(r2);
         //Test t = new Test();
         //stops.add(t);

         //stops.put(s.getId(), s);

       } catch(Exception e){
         System.out.println("something went wrong");
       }
       System.out.println("here3");

       String status = "success";

       Map<String, Object> variables = ImmutableMap.of("route", stops);
       System.out.println("here4");

       return new Gson().toJson(variables);
       //return new Gson().toJson("stirng");
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
