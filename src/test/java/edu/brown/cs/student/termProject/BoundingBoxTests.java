package edu.brown.cs.student.termProject;

import attractions.Park;
import database.BoundingBox;

import database.Database;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

/**
 * Tests for the BoundingBox class.
 */
public class BoundingBoxTests {

  @Test
  public void testFindBoundingBoxBoundsSameStartEnd() {
    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{0, 0}, new double[]{0, 0}),
            new double[]{0, 0, 0, 0}, 0);

    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{10, 10}, new double[]{10, 10}),
            new double[]{10, 10, 10, 10}, 0);
  }

  @Test
  public void testFindBoundingBoxBoundsVerticalLine() {
    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{-10, 5}, new double[]{10, 5}),
            new double[]{-10, 10, 5, 5}, 0);

    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{34, 10}, new double[]{33, 10}),
            new double[]{33, 34, 10, 10}, 0);
  }

  @Test
  public void testFindBoundingBoxBoundsHorizontalLine() {
    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{-3, 10}, new double[]{-3, 0}),
            new double[]{-3, -3, 0, 10}, 0);

    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{0, 5}, new double[]{0, 5.01}),
            new double[]{0, 0, 5, 5.01}, 0);
  }

  @Test
  public void testFindBoundingBoxBoundsAllDifferentCoordinates() {
    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{-1, 2}, new double[]{5, 4}),
            new double[]{-1, 5, 2, 4}, 0);

    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{-1, 4}, new double[]{5, 2}),
            new double[]{-1, 5, 2, 4}, 0);

    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{5, 2}, new double[]{-1, 4}),
            new double[]{-1, 5, 2, 4}, 0);

    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{5, 4}, new double[]{-1, 2}),
            new double[]{-1, 5, 2, 4}, 0);
  }

  @Test
  public void testExpandBoundingBoxBounds() {
    assertArrayEquals(BoundingBox.expandBoundingBoxBounds(
            new double[]{-1, 5, 2, 4}, 0),
            new double[]{-1, 5, 2, 4}, 0);

    assertArrayEquals(BoundingBox.expandBoundingBoxBounds(
            new double[]{-1, 5, 2, 4}, 2),
            new double[]{-3, 7, 0, 6}, 0);

    assertArrayEquals(BoundingBox.expandBoundingBoxBounds(
            new double[]{-1, 5, 2, 4}, -3),
            new double[]{2, 2, 5, 1}, 0);
  }

  @Test
  public void randomTests() {
    Database.setYelpDatabaseConnection();
    try {
      List<AttractionNode> attractions = BoundingBox.findAttractionsWithinBoundingBox(
              new double[]{40, 41, -106, -105},
      Arrays.asList("Restaurant", "Museum", "Park", "Shop"), 0, 0);
      System.out.println(attractions.size());
      for (AttractionNode a: attractions) {
        try {
          Park x = (Park) a;
        } catch (Exception e) {

        }
      }
    } catch (Exception e) {
      System.out.println("SQL query failed");
    }
  }

}
