package edu.brown.cs.student.termProject;

import database.BoundingBox;
import org.junit.Test;

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
  }

}
