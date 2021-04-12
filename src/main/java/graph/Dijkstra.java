package graph;


import attractions.Park;
import edu.brown.cs.student.termProject.AttractionNode;
import edu.brown.cs.student.termProject.Constants;

import java.util.*;

/**
 * Th Dijkstra class performs Dijkstra's algorithm on a list of attraction nodes
 */
public class Dijkstra {
  private List<AttractionNode> nodes;
  private HashMap<AttractionNode, Boolean> visited; //keep track of visited nodes
  private HashMap<AttractionNode, Double> distances; //keep track of distances between node and end
  private HashMap<AttractionNode,AttractionNode> previous; //keep track of previous pointers
  private HashMap<Integer, List<AttractionNode>> connectionChunks; //split the nodes into chunks
  private HashMap<AttractionNode, Integer> chunkLookup; //lookup by node for which chunk you
  // belong to

  private double[] preferredStop;
  private int costPreference;

  public Dijkstra(List<AttractionNode> n){
    nodes = n;
    visited = new HashMap<>();
    distances = new HashMap<>();
    previous = new HashMap<>();
    connectionChunks = new HashMap<>();
    chunkLookup = new HashMap<>();
//    for (AttractionNode node: nodes){
//      distances.put(node, distanceFormula(0.0,0.0,0.0,0.0));
//      node.setCost(distances.get(node)); //will need to add value to this!
////      node.setCost(node.generateValue() + distances.get(node)); //set the cost of the node based off
//      // of
//      // its
//      // calculated
//      // value and its distance to the target
//    }
  }
  public List<AttractionNode> execute(double[] starting, double[] ending, int numStops,
                                      double[] boundBox){
    visited = new HashMap<>();
    distances = new HashMap<>();
    previous = new HashMap<>();
    PriorityQueue<AttractionNode> pq = new PriorityQueue(new Comparator<AttractionNode>() {
      public int compare(AttractionNode o1, AttractionNode o2) {
        //The comparator implements the A* extension as it considers both path cost and
        // distance to target for priority
        if ((o1.getCost() + distances.get(o1)) > (o2.getCost() + distances.get(o2))) {
          return 1;
        }
        if ((o2.getCost() + distances.get(o2)) > (o1.getCost() + distances.get(o1))) {
          return -1;
        }
        return 0;
      }
    });
    List <AttractionNode> shortestPath = new ArrayList<>();
    double[] target = ending; //get the target
    if (starting == ending) { //stop if the start and end node are the same
      return null;
    }
    //Make a dumby attraction node to represent start and end? may be a better way to handle this
    AttractionNode start = new Park("0", "", new String[0], starting,0.0,0.0 );
    nodes.add(start);
    AttractionNode end = new Park("0", "", new String[0], target,0.0,0.0 );
    nodes.add(end);
    distances.replace(end, 0.0); //setting the distance of end node to 0
    pq.add(start);
    double minLon = 100000;
      double maxLon = -100000;
    for (AttractionNode node: nodes){
      distances.put(node, distanceFormula(0.0,0.0,0.0,0.0));
      visited.put(node, false);
      previous.put(node, null);
      node.setCost(Double.POSITIVE_INFINITY); //Initialize all costs to infinity with the

      //OK HERE THE BOUNDING BOX WAS SET IMPROPERLY SO MANUALLY GETTING THE COORDINATES OF THE
      // SMALLEST AND LARGEST LONG VALUES FOR ATTRACTIONS
      if (node.getCoordinates()[1] > maxLon){
        maxLon = node.getCoordinates()[1];
      }
      if (node.getCoordinates()[1] < minLon){
        minLon = node.getCoordinates()[1];

      }
    //  System.out.println(node.getCoordinates()[1]);
      // starting cost being 0
      //node.setCost(distances.get(node)); //Will need to add value to this !!
      //node.setCost(node.generateValue() + distances.get(node)); //set the cost of the node based
      // on it's calculated value and its distance to the target
    }
    start.setCost(0.0);
//    double sizeOfChunks = (Math.abs((ending[1])-(starting[1])))/numStops;
    double sizeOfChunks = (Math.abs((maxLon)-(minLon)))/numStops;
    System.out.println("Starting is " + minLon + " and ending is: "+ maxLon);
    //getting the longitudinal difference of the start and end and dividing it by the number of
    // stops to get the "size" of each chunk (a range of longitudes)
    for(int i = 0; i < numStops; i ++){ //looping through the number of stops to create that many
      // chunks
      List<AttractionNode> currentChunk = new ArrayList<>(); //keep track of the nodes associated
      // with each "chunk"
      double minLong = minLon + i*sizeOfChunks;
      double maxLong = minLon + (i+1)*sizeOfChunks;
      System.out.println("minLong is: " + minLong + " and maxLong is: " + maxLong);
      for(AttractionNode node: nodes){
        //System.out.println("Node has long of:  "+ node.getCoordinates()[1]);
        if (node.getCoordinates()[1] >= minLong && node.getCoordinates()[1] <= maxLong){
          currentChunk.add(node);
          if (node == start){
            System.out.println("Start has been added");
          }
          if (!chunkLookup.containsKey(node)){
            chunkLookup.put(node, i);
          }
        }
      }
      System.out.println("Chunk number: "+ i + " added with this many attraction nodes: "+ currentChunk.size());
      connectionChunks.put(i, currentChunk);
    }
    List<AttractionNode> lastChunk = new ArrayList<>();
    lastChunk.add(end);
    connectionChunks.put(numStops, lastChunk); //making the last chunk only the end node
    chunkLookup.replace(end, numStops);
    while (!(pq.isEmpty()) && !(visited.get(end))) {
      AttractionNode current = pq.poll();
      visited.replace(current,true); //mark the popped node as visited
      if (chunkLookup.get(current) + 1 <= numStops){
        List<AttractionNode> connectedNodes = connectionChunks.get(chunkLookup.get(current) + 1);
        for (AttractionNode node: connectedNodes) {
          //if the current nodes cost plus the distance between the current and the node you are
          // examining is less than the node you are examining's cost
          double edgeWeight = distanceFormula(current.getCoordinates()[0],
            current.getCoordinates()[1], node.getCoordinates()[0], node.getCoordinates()[1]);
         // System.out.println("Edge WEight is: "+ edgeWeight);
          if ((current.getCost() + edgeWeight) <= node.getCost()) {
            node.setCost(current.getCost() + edgeWeight); //Will need to add value to this!!
            //node.setCost(current.getCost() + edgeWeight + node.generateValue());
           // System.out.println("Adding to queue");
            previous.replace(node, current);
            pq.add(node);
          }
        }
      }
        }
    AttractionNode curr = end;
//    for (AttractionNode n: nodes){
//      if(previous.get(n)!= null){
//        System.out.println("A previous");
//      }
//    }
    System.out.println("Previous node to the end: " + previous.get(end));
    //gets the shortest path by looking at previous
    //start with the end, while the previous node is not the start add to current path
    if(previous.get(curr)== start){
      System.out.println("shortest path is direct one :(");
    }
    while(previous.get(curr)!= start){
      shortestPath.add(previous.get(curr));
      curr = previous.get(curr);
      //System.out.println(curr);
    }
    shortestPath.add(start);
    return shortestPath;
  }

  /**
   * The distance formula will use the google maps API to compute the distance between a node and
   * the path
   */
  private Double distanceFormula(double lat1,double long1, double lat2, double long2){
    double latDist = lat2 - lat1;
    double longDist = long2 - long1;
    latDist = Math.toRadians(latDist);
    longDist = Math.toRadians(longDist);
    double la1 = Math.toRadians(lat1);
    double la2 = Math.toRadians(lat2);
    double a = Math.pow(Math.sin(latDist / 2), 2) + Math.cos(la1) * Math.cos(la2)
      * Math.pow(Math.sin((longDist / 2)), 2);
    return (2.0 * Constants.EARTH_RADIUS * Math.asin(Math.sqrt(a)));


  }


  public void setPreferences(double[] prefStop, int costPref){
    preferredStop = prefStop;
    costPreference = costPref;

  }
}
