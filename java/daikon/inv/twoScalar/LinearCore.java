package daikon.inv.twoScalar;

import daikon.*;
import daikon.inv.*;

public class LinearCore {

  final static boolean debugLinearCore = false;

  // y == ax + b;
  public int a, b;
  public boolean no_invariant = false;

  int values_seen = 0;

  final static int MINPAIRS = 4;

  int[] x_cache = new int[MINPAIRS];
  int[] y_cache = new int[MINPAIRS];

  public LinearCore() {
    super();
  }

  public void add_modified(int x, int y, int count) {
    if (no_invariant) {
      return;
    }

    if (values_seen < MINPAIRS) {
      for (int i=0; i<values_seen; i++)
	if ((x_cache[i] == i) && (y_cache[i] == y))
	  return;
      x_cache[values_seen] = x;
      y_cache[values_seen] = y;
      values_seen++;
      if (values_seen == MINPAIRS) {
	// Find the most separated pair
	int max_separation = 0;
	int max_i = 0;
	int max_j = 1;
	for (int i=0; i<MINPAIRS-1; i++) {
	  for (int j=i+1; j<MINPAIRS; j++) {
	    int xsep = (x_cache[i] - x_cache[j]);
	    int ysep = (y_cache[i] - y_cache[j]);
	    int separation = xsep*xsep + ysep*ysep;
	    if (separation > max_separation) {
	      max_separation = separation;
	      max_i = i;
	      max_j = j;
	    }
	  }
	}
	// Set a and b based on that pair
	set_bi_linear(x_cache[max_i], x_cache[max_j], y_cache[max_i], y_cache[max_j]);
	// Check all values against a and b.
        if (!no_invariant) {
          for (int i=0; i<MINPAIRS; i++) {
            // I should permit a fudge factor here.
            if (y_cache[i] != a*x_cache[i]+b) {
              no_invariant = true;
              if (debugLinearCore) {
                System.out.println("Suppressing " + "LinearCore" + " at index " + i + ": "
                                   + y_cache[i] + " != " + a + "*" + x_cache[i] + "+" + b);
              }
              break;
            }
          }
        }
      }
    } else {
      // Check the new value against a and b.
      if (y != a*x+b) {
	no_invariant = true;
        if (debugLinearCore) {
          System.out.println("Suppressing " + "LinearCore" + " at new value: "
                             + y + " != " + a + "*" + x + "+" + b);
        }
      }
    }
  }

  // Given ((x0,y0),(x1,y1)), set a and b such that y = ax + b.
  // If no such (a,b) exists, then set no_invariant.

  void set_bi_linear(int x0, int x1, int y0, int y1) {
    if (x0 == x1) {
      no_invariant = true;
      if (debugLinearCore) {
        System.out.println("Suppressing " + "LinearCore" + " due to equal x values: (" + x0 + "," + y0 + "), (" + x1 + "," + y1 + ")");
      }
      return;
    }

    double xdiff = x1-x0;

    // Assume that constants have already been found by a previous pass.
    a = (y1-y0)/(x1-x0);
    b = (y0*x1-x0*y1)/(x1-x0);

  }

  public double computeProbability() {
    if (no_invariant)
      return Invariant.PROBABILITY_NEVER;
    if (values_seen < MINPAIRS)
      return Invariant.PROBABILITY_UNKNOWN;
    // This isn't right, is it?
    return 0;
  }

}
