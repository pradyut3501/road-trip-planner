package edu.brown.cs.student.termProject;

/**
 * The Restaurant class stores information of Restaurants and implements the attraction node
 * interface for Dijkstra's
 */
public class Restaurant implements AttractionNode {
  private String id;
  private String name;
  private String[] location;
  private double[] coordinates;
  private double price;
  private double rating;
  private double cost;

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
                   Double p, Double rate){
    id = RestaurantId;
    name = RestaurantName;
    location = loc;
    coordinates = coords;
    price = p;
    rating = rate;
    cost = Double.POSITIVE_INFINITY;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
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

  @Override
  public void setCost(double c) {
    cost = c;
  }

  @Override
  public double getCost() {
    return cost;
  }
}
