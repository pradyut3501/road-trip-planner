package attractions;

import edu.brown.cs.student.termProject.AttractionNode;
import edu.brown.cs.student.termProject.Constants;

/**
 * The Museum class stores information of Museums and implements the attraction node interface
 * for Dijkstra's
 */
public class Museum implements AttractionNode {
  private String id;
  private String name;
  private String[] location;
  private double[] coordinates;
  private double price;
  private double rating;
  private double cost;
  private boolean visit = false;
  private double distance = 0;
  private int numPrev = 0;
  private double value;
  private String nodeType = "museum";
  private double numReviews;


  /**
   * The constructor sets the fields
   * @param MuseumId the id
   * @param MuseumName the name of the stop
   * @param loc the address of the Museum in an array
   * @param coords the latitude/longitude coordinates
   * @param p the price associated with the stop
   * @param rate the five star rating
   */
  public Museum(String MuseumId, String MuseumName, String[] loc, double[] coords, double p,
              double rate, double reviewCount){
    id = MuseumId;
    name = MuseumName;
    location = loc;
    coordinates = coords;
    price = p;
    rating = rate;
    cost = Double.POSITIVE_INFINITY;
    value = 0;
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
    double museumValue = PreferredStop;
    value = (1- museumValue/Constants.VALUE_BOUND) * distance;
   // value = (Constants.VALUE_BOUND- museumValue) * Constants.VALUE_SCALE_MUSEUMS;
    value = value + (1- numReviews/Constants.AVERAGE_REVIEWS_MUSEUMS) * distance;
   // value = value * (1 - rating *.1);
    value = value + (1 - rating/Constants.MAX_RATING) * distance;
    value = value + (Math.abs(price-PreferredPrice)) * distance;
    value = value * Constants.VALUE_SCALE;
    System.out.println("museum value is: " + value);
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
  public int getType() {return 0;}

  @Override
  public double getValue() {
    return value;
  }

  @Override
  public double getNumReviews() {
    return numReviews;
  }
}
