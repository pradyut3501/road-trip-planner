package graph;

/**
 * The attraction node interface is implemented by each attraction and is used for Dijkstra's
 */
public interface AttractionNode {
  /**
   * Gets the location of the node
   *
   * @returns [id, name, city, state code, zip code]
   */
  String[] getLocation();

  /**
   * Gets the latitude/longitude location of the node
   *
   * @returns [lat,long]
   */
  double[] getCoordinates();

  /**
   * Gets price associated with a node
   *
   * @returns price where 1 = $, 2 = $$, 3 = $$$
   */
  double getPrice();

  /**
   * Gets rating of a node
   *
   * @returns rating between 0 and 5
   */
  double getRating();

  /**
   * Gets the value of the node computed with our value algorithm
   *
   * @returns numerical "value"
   */
  double generateValue();

  /**
   * Sets the "cost" associated with the node to be used in Dijkstra's
   *
   * @param c the cost
   */
  void setCost(double c);

  /**
   * Retrieves the "cost" associated with the node to be used in Dijkstra's
   *
   * @returns the cost
   */
  double getCost();

//  /**
//   * check if the node has been visited
//   *
//   * @returns boolean to represent if it has been visited
//   */
//  boolean visited();

}
