package attractions;

import graph.AttractionNode;

/**
 * The the Museum class stores information of Museums and implements the attraction node interface
 * for Dijkstra's
 */
public class Museum implements AttractionNode {
  private String id;
  private String name;
  private String[] location;
  private double[] coordinates;
  private double price;
  private double rating;

  /**
   * The constructor sets the fields
   * @param MuseumId the id
   * @param MuseumName the name of the stop
   * @param loc the address of the Museum in an array
   * @param coords the latitude/longitude coordinates
   * @param p the price associated with the stop
   * @param rate the five star rating
   */
  public Museum(String MuseumId, String MuseumName, String[] loc, double[] coords, Double p,
              Double rate){
    id = MuseumId;
    name = MuseumName;
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
