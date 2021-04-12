package graph;


import attractions.Park;
import edu.brown.cs.student.termProject.AttractionNode;
import edu.brown.cs.student.termProject.Constants;
import org.w3c.dom.Attr;

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
  }
  public List<AttractionNode> execute(double[] starting, double[] ending, int numStops){
    double pathDistance = distanceFormula(starting[0], starting[1], ending[0], ending[1]);
    visited = new HashMap<>();
    distances = new HashMap<>();
    previous = new HashMap<>();
    PriorityQueue<AttractionNode> pq = new PriorityQueue(new Comparator<AttractionNode>() {
      public int compare(AttractionNode o1, AttractionNode o2) {
        if ((o1.getCost() + o1.generateValue(costPreference, preferredStop[o1.getType()])) >
          (o2.getCost()) + o2.generateValue(costPreference, preferredStop[o2.getType()])) {
          return 1;
        }
        if ((o2.getCost() + o2.generateValue(costPreference, preferredStop[o2.getType()])) >
          (o1.getCost() + o1.generateValue(costPreference, preferredStop[o1.getType()]))) {
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
    AttractionNode start = new Park("0", "starting Node", new String[0], starting,0.0,0.0 );
    nodes.add(start);
    AttractionNode end = new Park("0", "ending Node", new String[0], target,0.0,0.0 );
    nodes.add(end);
    distances.replace(end, 0.0); //setting the distance of end node to 0
    pq.add(start);
    double minLon = 100000;
    double maxLon = -100000;
    for (AttractionNode node: nodes){
     // System.out.println(node.getLocation()[2]);
      visited.put(node, false);
      previous.put(node, null);
      node.setCost(Double.POSITIVE_INFINITY);
    }
    start.setCost(0.0);
    System.out.println("Ideal Spacing is" + pathDistance/numStops);

    while (!(pq.isEmpty()) && !(visited.get(end))) {
      AttractionNode current = pq.poll();
      visited.replace(current,true); //mark the popped node as visited
      List<AttractionNode> connectedNodes = getConnectedNodes(current, end, pathDistance, numStops);
        for (AttractionNode node: connectedNodes) {
          //if the current nodes cost plus the distance between the current and the node you are
          // examining is less than the node you are examining's cost
          double edgeWeight = distanceFormula(current.getCoordinates()[0],
            current.getCoordinates()[1], node.getCoordinates()[0], node.getCoordinates()[1]);
          if ((current.getCost() + edgeWeight) < node.getCost()) {
            node.setCost(current.getCost() + edgeWeight); //Will need to add value to this!!
            previous.replace(node, current);
            pq.add(node);
          }
        }
        }
    AttractionNode curr = end;
   // System.out.println("Previous node to the end: " + previous.get(end));
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
   // shortestPath.add(start);
    Collections.reverse(shortestPath);
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

  private List<AttractionNode> getConnectedNodes(AttractionNode node,
                                                 AttractionNode end, double distance,int numStops){
    double spacing = distance/numStops;
    List<AttractionNode> connects = nodes;
    List<AttractionNode> toRemove = new ArrayList<>(); //To avoid concurrent modification error
    for(AttractionNode n: connects){
      if (distanceFormula(n.getCoordinates()[0],
        n.getCoordinates()[1], end.getCoordinates()[0], end.getCoordinates()[1]) >=
        distanceFormula(node.getCoordinates()[0], node.getCoordinates()[1],end.getCoordinates()[0],
          end.getCoordinates()[1])
      ){
        toRemove.add(n); //remove the node from the list of possible connecting nodes if it is
        // farther from the target than the current node
      }
    }
    for(AttractionNode r: toRemove){
      connects.remove(r);
    }
//    System.out.println("The node " + node.getName() + "has this many connections: " + connects
//    .size());
    Collections.sort(connects, new Comparator<AttractionNode>(){
      @Override
      //this comparator will sort the nodes based on HOW CLOSE THEY are to the target spacing,
      // i.e. nodes that are spaced closest to the "spacing" metric will be considered connected
      public int compare(AttractionNode o1, AttractionNode o2) {
        double distance1 = distanceFormula(o1.getCoordinates()[0], o1.getCoordinates()[1],
          node.getCoordinates()[0], node.getCoordinates()[1]);
        double distance2 = distanceFormula(o2.getCoordinates()[0], o2.getCoordinates()[1],
          node.getCoordinates()[0], node.getCoordinates()[1]);
        if (Math.abs(distance1-spacing) > Math.abs(distance2-spacing)){
          return 1;
        }
        if (Math.abs(distance2-spacing) > Math.abs(distance1-spacing)){
          return -1;
        }
        else{
          return 0;
        }
      }
    });
    if (node == end){
      System.out.println("The connected nodes to the end re" + connects);
    }
    if(connects.size()>=2){
      return connects.subList(0,2);
    }
   else if(connects.size() >= 1){
     return connects;
    }
   else{
     return new ArrayList<>();
    }
  }

  public void setPreferences(double[] prefStop, int costPref){
    preferredStop = prefStop;
    costPreference = costPref;

  }
}
