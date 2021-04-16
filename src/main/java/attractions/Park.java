package attractions;

import edu.brown.cs.student.termProject.AttractionNode;
import edu.brown.cs.student.termProject.Constants;

/**
 * The park class stores information of parks and implements the attraction node interface for
 * Dijkstra's
 */
public class Park implements AttractionNode {
  private String id;
  private String name;
  private String[] location;
  private double[] coordinates;
  private double price;
  private double rating;
  private double value;
  private double cost;
  private boolean visit = false;
  private double distance = 0;
  private int numPrev = 0;
  private String nodeType = "park";
  private double numReviews;


  /**
   * The constructor sets the fields
   * @param parkId the id
   * @param parkName the name of the stop
   * @param loc the address of the park in an array
   * @param coords the latitude/longitude coordinates
   * @param p the price associated with the stop
   * @param rate the five star rating
   */
  public Park(String parkId, String parkName, String[] loc, double[] coords, double p,
              double rate, double reviewCount){
    id = parkId;
    name = parkName;
    location = loc;
    coordinates = coords;
    price = p;
    value = 0;
    rating = rate;
    cost = Double.POSITIVE_INFINITY;
    numReviews = reviewCount;

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
  public double generateValue(double PreferredPrice, double PreferredStop, double distance) {
    double parkValue = PreferredStop;
    value = (1- parkValue/Constants.VALUE_BOUND) * distance;
    //value is 1 minus the % preference for the stop times the total distance. At most the total
    // distance should double
    //value = (Constants.VALUE_BOUND- parkValue) * Constants.VALUE_SCALE_PARKS;
    value = value + (1- numReviews/Constants.AVERAGE_REVIEWS_PARKS) * distance;
    value = value + (1 - rating/Constants.MAX_RATING) * distance;
    //value = value + (Math.abs(price-PreferredPrice)) * distance;

    value = value * Constants.VALUE_SCALE;
  //  System.out.println("park value is: " + value);
    return value;
  }

  @Override
  public void setCost(double c) {
    cost = c;
  }

  @Override
  public double getCost() {
    return cost;
  }

  @Override
  public void setDistance(double c) {distance = c;}

  @Override
  public double getDistance() { return distance; }

  @Override
  public void setVisited(boolean c) { visit = c; }

  @Override
  public boolean getVisited() { return visit; }

  @Override
  public void setNumPrev(int c) {numPrev = c;}

  @Override
  public int getNumPrev() {return numPrev;}

  @Override
  public void reset() {
    distance = 0;
    visit = false;
    numPrev = 0;
  }

  @Override
  public int getType() {return 1;}

  @Override
  public double getValue() {
    return value;
  }

  @Override
  public double getNumReviews() {
    return numReviews;
  }

}
