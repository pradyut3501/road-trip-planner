package attractions;

import graph.AttractionNode;

/**
 * The the park class stores information of parks and implements the attraction node interface for
 * Dijkstra's
 */
public class Park implements AttractionNode {
  private String id;
  private String name;
  private String[] location;
  private double[] coordinates;
  private double price;
  private double rating;

  /**
   * The constructor sets the fields
   * @param parkId the id
   * @param parkName the name of the stop
   * @param loc the address of the park in an array
   * @param coords the latitude/longitude coordinates
   * @param p the price associated with the stop
   * @param rate the five star rating
   */
  public Park(String parkId, String parkName, String[] loc, double[] coords, Double p,
              Double rate){
    id = parkId;
    name = parkName;
    location = loc;
    coordinates = coords;
    price = p;
    rating = rate;

  }
  @Override
  public String[] getLocation() {
    return location;
  }

  @Override
  public double[] getCoordinates() {
    return coordinates;
  }

  @Override
  public double getPrice() {
    return price;
  }

  @Override
  public double getRating() {
    return rating;
  }

  @Override
  public double generateValue() {
    return 0;
  }
}
