package daikon.inv.filter;

import daikon.VarInfo;
import daikon.inv.*;
import daikon.inv.filter.*;
import daikon.inv.binary.twoSequence.*;
import daikon.PrintInvariants;

import utilMDE.Assert;
import java.util.logging.Level;

class ObviousEqualityFilter extends InvariantFilter {
  public String getDescription() {
    return "Suppress obvious equality invariants [deprecated]";
  }

  // Print the variable, its canonical variable, and whether they can be mising.
  private void print_var(String name, VarInfo vi) {
    System.out.println(name + "=" + vi.name.name());
    // [INCR] System.out.println("  canonical=" + vi.canonicalRep().name.name());
    System.out.println("  canBeMissing=" + vi.canBeMissing
                       /* [INCR] + "," + vi.canonicalRep().canBeMissing */);
    System.out.println("  repr=" + vi.repr());
  }


  boolean shouldDiscardInvariant( Invariant invariant ) {
    if (!(invariant instanceof Equality)) return false;

    Equality eq = (Equality) invariant;

    if (eq.getVars().size() < 2) return true;

    return false;

    /* [INCR]
    if (!IsEqualityComparison.it.accept( invariant )) {
      return false;
    }

    Assert.assertTrue(invariant instanceof Comparison);
    Comparison comp = (Comparison)invariant;

    VarInfo v1 = comp.var1();
    VarInfo v2 = comp.var2();

    /// Debugging
    // print_var("v1", v1);
    // print_var("v2", v2);
    // if (! ((v1.canonicalRep() == v2.canonicalRep())
    //        || v1.canBeMissing || v2.canBeMissing)) {
    //   daikon.PptTopLevel ppt = invariant.ppt.parent;
    //   for (int i=0; i<ppt.var_infos.length; i++) {
    //     VarInfo vi = ppt.var_infos[i];
    //     print_var("var_infos[" + i + "]", vi);
    //   }
    // }

    // The following assertion doesn't always hold, because of a bug
    // in the way we handle canonical-ness. If we first see a == b,
    // then c == d, then a == c, we don't correctly merge the {a, b}
    // canonical family with the {c, d} family. (See
    // set_equal_to_slots() in PptTopLevel). So don't assert it until
    // that's fixed (maybe never in version 2). -SMcC
    // Assert.assertTrue((v1.canonicalRep() == v2.canonicalRep())
    //                  || v1.canBeMissing || v2.canBeMissing);

    VarInfo canonical = v1.canonicalRep();

    if (PrintInvariants.debugFiltering.isLoggable(Level.FINE)) {
      PrintInvariants.debugFiltering.fine ("in ObviousEquality filter\n");
      PrintInvariants.debugFiltering.fine ("\tv1  is " + v1.name.name() + "\n");
      PrintInvariants.debugFiltering.fine ("\tv2  is " + v2.name.name() + "\n");
      PrintInvariants.debugFiltering.fine ("\tcan is " + canonical.name.name() + "\n");
    }

    if (!canonical.equalToNonobvious().contains(v1) && !canonical.equals(v1)) {
      if (PrintInvariants.debugFiltering.isLoggable(Level.FINE)) {
        PrintInvariants.debugFiltering.fine ("it was obvious that " + canonical.name.name() + " == " + v1.name.name() + "\n");
      }
      return true;
    }

    if (!canonical.equalToNonobvious().contains(v2) && !canonical.equals(v2)) {
      if (PrintInvariants.debugFiltering.isLoggable(Level.FINE)) {
        PrintInvariants.debugFiltering.fine ("it was obvious that " + canonical.name.name() + " == " + v2.name.name() + "\n");
      }
      return true;
    }

    if ((invariant instanceof SeqComparison)
        || (invariant instanceof SeqComparisonFloat)) {
      VarInfo super1 = v1.isDerivedSubSequenceOf();
      VarInfo super2 = v2.isDerivedSubSequenceOf();
      if ((super1 != null) && (super2 != null) && (super1 == super2)) {
        return true;
      }
    }
    */ // ... [INCR]

//        VarInfo[] variables = invariant.ppt.var_infos; // note: only 2 variables
//        for (int i = 0; i < variables.length; i++) {
//          if (variables[i].isCanonical()) {
//            // Test if equality is "nonobvious".  This test rarely fails, but is
//            // necessary for correctness.
//            if (variables[i].equalToNonobvious().contains( variables[1-i] )) {
//              return false;
//            } else {
//              return true;
//            }
//          }
//        }
//      }
//      return false;
  }
}
