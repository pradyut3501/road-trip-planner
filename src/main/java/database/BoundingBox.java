package database;

import graph.AttractionNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Class with static functions to find all the attractions within a bounding box
 * around two sets of coordinates.
 */
public class BoundingBox {


  public static List<AttractionNode> findAttractionsBetween(
          double[] coords1, double[] coords2, List<String> categories) {

    return new ArrayList<>();

  }

  public static double[] findBoundingBoxBounds(double[] coords1, double[] coords2) {
    double[] bounds = new double[4];

    if (coords1[0] < coords2[0]) {
      bounds[0] = coords1[0];
      bounds[1] = coords2[0];
    } else {
      bounds[0] = coords2[0];
      bounds[1] = coords1[0];
    }

    if (coords1[1] < coords2[1]) {
      bounds[2] = coords1[1];
      bounds[3] = coords2[1];
    } else {
      bounds[2] = coords2[1];
      bounds[3] = coords1[1];
    }

    return bounds;
  }

}
