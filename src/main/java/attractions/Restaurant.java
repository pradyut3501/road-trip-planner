package attractions;

import graph.AttractionNode;

/**
 * The the Restaurant class stores information of Restaurants and implements the attraction node
 * interface for Dijkstra's
 */
public class Restaurant implements AttractionNode {
  private String id;
  private String name;
  private String[] location;
  private double[] coordinates;
  private double price;
  private double rating;

  /**
   * The constructor sets the fields
   * @param RestaurantId the id
   * @param RestaurantName the name of the stop
   * @param loc the address of the Restaurant in an array
   * @param coords the latitude/longitude coordinates
   * @param p the price associated with the stop
   * @param rate the five star rating
   */
  public Restaurant(String RestaurantId, String RestaurantName, String[] loc, double[] coords,
                   Double p,
                Double rate){
    id = RestaurantId;
    name = RestaurantName;
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
