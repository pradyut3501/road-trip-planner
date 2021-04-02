package graph;


import attractions.Park;
import org.w3c.dom.Attr;

import java.util.*;

/**
 * Th Dijkstra class performs Dijkstra's algorithm on a list of attraction nodes
 */
public class Dijkstra {
  private List<AttractionNode> nodes;

  public Dijkstra(List<AttractionNode> n){
    nodes = n;
  }
  public List<AttractionNode> execute(double[] starting, double[] ending){
    List <AttractionNode> shortestPath = new ArrayList<>();
    double[] target = ending; //get the target
    if (starting == ending) { //stop if the start and end node are the same
      return null;
    }
    PriorityQueue<AttractionNode> pq = new PriorityQueue(new Comparator<AttractionNode>() {
      @Override //TODO: Will need to edit this comparator
      public int compare(AttractionNode o1, AttractionNode o2) {
        return 0;
      }
    });
    //Make a dumby attraction node to represent start and end? may be a better way to handle this
    AttractionNode start = new Park("0", "", new String[0], starting,0.0,0.0 );
    AttractionNode end = new Park("0", "", new String[0], target,0.0,0.0 );
    return shortestPath;
  }
}
