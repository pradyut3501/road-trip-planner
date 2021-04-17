package edu.brown.cs.student.termProject;

/**
 * The attraction node interface is implemented by each attraction and is used for Dijkstra's
 */
public interface AttractionNode {

  /**
   * Gets the unique Id of the node
   *
   * @return Id of the node
   */
  String getId();

  /**
   * Gets the non-unique name of the node
   *
   * @return name of the node
   */
  String getName();

  /**
   * Gets the location of the node
   *
   * @return [address, city, state, postal_code]
   */
  String[] getLocation();

  /**
   * Gets the latitude/longitude location of the node
   *
   * @return [lat,long]
   */
  double[] getCoordinates();

  /**
   * Gets price associated with a node
   *
   * @return price where 1 = $, 2 = $$, 3 = $$$
   */
  double getPrice();

  /**
   * Gets rating of a node
   *
   * @return rating between 0 and 5
   */
  double getRating();

  /**
   *
   * @param PreferredPrice value indicating user's preferred price point
   * @param PreferredStop value indicating how much user prefers that type of stop
   * @param distance from current node to prev
   * @return value heuristic used in Dijkstra's PQ
   */
  double generateValue(double PreferredPrice, double PreferredStop, double distance);

  /**
   * Sets the "cost" associated with the node to be used in Dijkstra's
   *
   * @param c the cost
   */
  void setCost(double c);

  /**
   * Retrieves the "cost" associated with the node to be used in Dijkstra's
   *
   * @return the cost
   */
  double getCost();

  void setDistance(double c);

  double getDistance();

  void setVisited(boolean c);

  boolean getVisited();

  void setNumPrev(int c);

  /**
   * Get's number of previous stops
   * will be used in value heuristic
   * @return integer of number of prev
   */
  int getNumPrev();

  //not sure if this is necessary
  void reset();

  /**
   * integer associated with Attraction type
   * 0 = Museum
   * 1 = Park
   * 2 = Restaurant
   * 3 = Shop
   * @return integer
   */
  int getType();
  /**
   * returns the node's "value"
   * @return the value
   */
  double getValue();

  /**
   * returns the node's number of reviews
   * @return the number of revies
   */
  double getNumReviews();



//  /**
//   * check if the node has been visited
//   *
//   * @returns boolean to represent if it has been visited
//   */
//  boolean visited();

}
