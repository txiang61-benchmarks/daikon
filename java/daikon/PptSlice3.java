// ***** This file is automatically generated from PptSlice.java.jpp

package daikon;

import daikon.inv.*;

import daikon.inv.ternary.threeScalar.*;

import org.apache.log4j.Category;

import java.util.*;

import utilMDE.*;

// This file looks a *lot* like part of PptTopLevel.
// (That is fine; its purpose is similar and mostly subsumed by VarValues.)

public final class PptSlice3
  extends PptSlice
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20020122L;

  /**
   * Debug tracer
   **/
  public static final Category debugSpecific = Category.getInstance("daikon.PptSlice3");

  // This is in PptSlice; do not repeat it here!
  // Invariants invs;

  // values_cache maps (interned) values to 8-element arrays of
  // [uuu, uum, umu, umm, muu, mum, mmu, mmm].
  // That is, the first element is (unmod,unmod,unmod);
  // the second element is (unmod,unmod,mod); etc.

  int[] tm_total = new int[8 ];  // "tm" stands for "tuplemod"

  public PptSlice3 (PptTopLevel parent, VarInfo[] var_infos) {
    super(parent, var_infos);
    Assert.assertTrue(var_infos.length == 3);

    Dataflow.init_pptslice_po(this);

    // values_cache = new HashMap(); // [INCR]
    if (this.debugged || debug.isDebugEnabled() || debugSpecific.isDebugEnabled())
      debug.info("Created PptSlice3 " + this.name);

    // Make the caller do this, because
    //  1. there are few callers
    //  2. do not want to instantiate all invariants all at once
    // instantiate_invariants();
  }

  PptSlice3(PptTopLevel parent, VarInfo var_info1, VarInfo var_info2, VarInfo var_info3) {
    this(parent, new VarInfo[] { var_info1, var_info2, var_info3 });
  }

  void instantiate_invariants() {
    Assert.assertTrue(!no_invariants);

    // This test should be done by caller (PptTopLevel):
    // if (isControlled()) return;

    // Instantiate invariants
    if (this.debugged || debug.isDebugEnabled() || debugSpecific.isDebugEnabled())
      debug.info("instantiate_invariants for " + name + ": originally " + invs.size() + " invariants in " + invs);

    Vector new_invs = null;

    ProglangType rep1 = var_infos[0].file_rep_type;
    ProglangType rep2 = var_infos[1].file_rep_type;
    ProglangType rep3 = var_infos[2].file_rep_type;
    if ((rep1 == ProglangType.INT)
        && (rep2 == ProglangType.INT)
        && (rep3 == ProglangType.INT)) {
      new_invs = ThreeScalarFactory.instantiate(this);
    } else if (Daikon.dkconfig_enable_floats
               && (rep1 == ProglangType.DOUBLE)
               && (rep2 == ProglangType.DOUBLE)
               && (rep3 == ProglangType.DOUBLE)) {
      new_invs = ThreeFloatFactory.instantiate(this);
    } else {
      // Do nothing; do not even complain
    }

    if (new_invs != null) {
      for (int i=0; i<new_invs.size(); i++) {
        Invariant inv = (Invariant) new_invs.get(i);
        if (inv == null)
          continue;
        addInvariant(inv);
      }
    }

    if (this.debugged || debug.isDebugEnabled() || debugSpecific.isDebugEnabled()) {
      debug.info("after instantiate_invariants PptSlice3 " + name + " = " + this + " has " + invs.size() + " invariants in " + invs);
    }
    if ((this.debugged  || debugSpecific.isDebugEnabled()) && (invs.size() > 0)) {
      debug.info("the invariants are:");
      for (int i=0; i<invs.size(); i++) {
        Invariant inv = (Invariant) invs.get(i);
        debug.info("  " + inv.format());
        debug.info("    " + inv.repr());
      }
    }

  }

  // These accessors are for abstract methods declared in Ppt
  public int num_samples() {

    int result = tm_total[0] + tm_total[1] + tm_total[2] + tm_total[3]
      + tm_total[4] + tm_total[5] + tm_total[6] + tm_total[7];

    Assert.assertTrue(result >= 0);
    return result;
  }

  public int num_mod_non_missing_samples() {

    int result = tm_total[1] + tm_total[2] + tm_total[3]
       + tm_total[4] + tm_total[5] + tm_total[6] + tm_total[7];

    Assert.assertTrue(result >= 0);
    return result;
  }

  public String tuplemod_samples_summary() {
    Assert.assertTrue(! no_invariants);
    return "UUU=" + tm_total[0]
      + ", UUM=" + tm_total[1]
      + ", UMU=" + tm_total[2]
      + ", UMM=" + tm_total[3]
      + ", MUU=" + tm_total[4]
      + ", MUM=" + tm_total[5]
      + ", MMU=" + tm_total[6]
      + ", MMM=" + tm_total[7];
  }

  // public int num_missing() { return values_cache.num_missing; }

  // Accessing data
  int num_vars() {
    return var_infos.length;
  }
  Iterator var_info_iterator() {
    return Arrays.asList(var_infos).iterator();
  }

  boolean compatible(Ppt other) {
    // This insists that the var_infos lists are identical.  The Ppt
    // copy constructor does reuse the var_infos field.
    return (var_infos == other.var_infos);
  }

  ///////////////////////////////////////////////////////////////////////////
  /// Manipulating values
  ///

  /**
   * This procedure accepts a sample (a ValueTuple), extracts the values
   * from it, casts them to the proper types, and passes them along to the
   * invariants proper.  (The invariants accept typed values rather than a
   * ValueTuple that encapsulates objects of any type whatever.)
   * @param invsFlowed after this method, holds the Invariants that
   * flowed.
   **/
  public List add(ValueTuple full_vt, int count) {
    Assert.assertTrue(! no_invariants);
    Assert.assertTrue(invs.size() > 0);
    // Assert.assertTrue(! already_seen_all); // [INCR]
    for (int i=0; i<invs.size(); i++) {
      Assert.assertTrue(invs.get(i) != null);
    }

    // if (Global.debugInfer.isDebugEnabled()) {
    //   Global.debugInfer.debug ("PptSlice3.add(" + full_vt + ", " + count + ") for " + name);
    // }

    // Do not bother putting values into a slice if missing.

    VarInfo vi1 = var_infos[0];
    VarInfo vi2 = var_infos[1];
    VarInfo vi3 = var_infos[2];

    int mod1 = full_vt.getModified(vi1);
    if (mod1 == ValueTuple.MISSING) {
      // System.out.println("Bailing out of add(" + full_vt + ") for " + name);
      return emptyList;
    }
    if (mod1 == ValueTuple.STATIC_CONSTANT) {
      Assert.assertTrue(vi1.is_static_constant);
      mod1 = ((num_mod_non_missing_samples() == 0)
              ? ValueTuple.MODIFIED : ValueTuple.UNMODIFIED);
    }

    int mod2 = full_vt.getModified(vi2);
    if (mod2 == ValueTuple.MISSING) {
      // System.out.println("Bailing out of add(" + full_vt + ") for " + name);
      return emptyList;
    }
    if (mod2 == ValueTuple.STATIC_CONSTANT) {
      Assert.assertTrue(vi2.is_static_constant);
      mod2 = ((num_mod_non_missing_samples() == 0)
              ? ValueTuple.MODIFIED : ValueTuple.UNMODIFIED);
    }

    int mod3 = full_vt.getModified(vi3);
    if (mod3 == ValueTuple.MISSING) {
      // System.out.println("Bailing out of add(" + full_vt + ") for " + name);
      return emptyList;
    }
    if (mod3 == ValueTuple.STATIC_CONSTANT) {
      Assert.assertTrue(vi3.is_static_constant);
      mod3 = ((num_mod_non_missing_samples() == 0)
              ? ValueTuple.MODIFIED : ValueTuple.UNMODIFIED);
    }

    Object val1 = full_vt.getValue(vi1);

    Object val2 = full_vt.getValue(vi2);

    Object val3 = full_vt.getValue(vi3);

    // if (! already_seen_all) // [INCR]
    {

      Object[] vals = Intern.intern(new Object[] { val1, val2, val3 });

      /* [INCR] ...
      int[] tm_arr = (int[]) values_cache.get(vals);
      if (tm_arr == null) {
        tm_arr = new int[8 ];
        values_cache.put(vals, tm_arr);
      }
      */ // ... [INCR]

      int mod_index = mod1 * 4 + mod2 * 2 + mod3;

      // tm_arr[mod_index] += count; // [INCR]
      tm_total[mod_index] += count;
    }

    // System.out.println("PptSlice3 " + name + ": add " + full_vt + " = " + vt);
    // System.out.println("PptSlice3 " + name + " has " + invs.size() + " invariants.");

    // defer_invariant_removal(); [INCR]

    // Supply the new values to all the invariant objects.
    int num_invs = invs.size();

    Assert.assertTrue((mod1 == vi1.getModified(full_vt))
                  || ((vi1.getModified(full_vt) == ValueTuple.STATIC_CONSTANT)
                      && ((mod1 == ValueTuple.UNMODIFIED)
                          || (mod1 == ValueTuple.MODIFIED))));

    Assert.assertTrue((mod1 != ValueTuple.MISSING)
                  && (mod2 != ValueTuple.MISSING)
                  && (mod3 != ValueTuple.MISSING));
    int mod_index = mod1 * 4 + mod2 * 2 + mod3;
    ProglangType rep1 = vi1.file_rep_type;
    ProglangType rep2 = vi2.file_rep_type;
    ProglangType rep3 = vi3.file_rep_type;
    if ((rep1 == ProglangType.INT)
        && (rep2 == ProglangType.INT)
        && (rep3 == ProglangType.INT)) {
      long value1 = ((Long) val1).longValue();
      long value2 = ((Long) val2).longValue();
      long value3 = ((Long) val3).longValue();
      for (int i=0; i<invs.size(); i++) {
        ThreeScalar inv = (ThreeScalar) invs.get(i);
        if (inv.falsified) continue;
        if (inv.getSuppressor() != null) continue;
        inv.add(value1, value2, value3, mod_index, count);
      }
    } else if ((rep1 == ProglangType.DOUBLE)
               && (rep2 == ProglangType.DOUBLE)
               && (rep3 == ProglangType.DOUBLE)) {
      double value1 = ((Double) val1).doubleValue();
      double value2 = ((Double) val2).doubleValue();
      double value3 = ((Double) val3).doubleValue();
      for (int i = 0; i < invs.size(); i++) {
        ThreeFloat inv = (ThreeFloat) invs.get(i);
        inv.add(value1, value2, value3, mod_index, count);
      }
    } else {
      // temporarily do nothing:  efficiency hack, as there are currently
      // no ternary invariants over non-scalars
    }

    // undefer_invariant_removal(); [INCR]
    return flow_and_remove_falsified();
  }

  public void addInvariant(Invariant invariant) {
    Assert.assertTrue(invariant != null);
    invs.add(invariant);
    Global.instantiated_invariants++;
    if (Global.debugStatistics.isDebugEnabled() || this.debugged || debugSpecific.isDebugEnabled())
      debug.info("instantiated_invariant: " + invariant.format()
                 // [INCR] + "; already_seen_all=" + already_seen_all
                 );

    /* [INCR] ... I think this is now unnecessary; not sure. XXX
    if (already_seen_all) {
      // Make this invariant up to date by supplying it with all the values
      // which have already been seen.
      // (Do not do
      //   Assert.assertTrue(values_cache.entrySet().size() > 0);
      // because all the values might have been missing.  We used to ignore
      // variables that could have some missing values, but no longer.)

      VarInfo vi1 = var_infos[0];
      VarInfo vi2 = var_infos[1];
      VarInfo vi3 = var_infos[2];
      ProglangType rep1 = vi1.rep_type;
      ProglangType rep2 = vi2.rep_type;
      ProglangType rep3 = vi3.rep_type;
      if ((rep1 == ProglangType.INT)
          && (rep2 == ProglangType.INT)
          && (rep3 == ProglangType.INT)) {
        ThreeScalar inv = (ThreeScalar) invariant;
        // Make this invariant up to date by supplying it with all the values.
        for (Iterator itor = values_cache.entrySet().iterator() ; itor.hasNext() ; ) {
          Map.Entry entry = (Map.Entry) itor.next();
          Object[] vals = (Object[]) entry.getKey();
          long val1 = ((Long) vals[0]).longValue();
          long val2 = ((Long) vals[1]).longValue();
          long val3 = ((Long) vals[2]).longValue();
          int[] tm_array = (int[]) entry.getValue();
          for (int mi=0; mi<tm_array.length; mi++) {
            if (tm_array[mi] > 0) {
              inv.add(val1, val2, val3, mi, tm_array[mi]);
              if (inv.falsified)
                break;
            }
          }
        }
      } else if (rep1 == ProglangType.DOUBLE
                 && rep2 == ProglangType.DOUBLE
                 && rep3 == ProglangType.DOUBLE) {
        ThreeFloat inv = (ThreeFloat) invariant;
        for (Iterator itor = values_cache.entrySet().iterator(); itor.hasNext();) {
          Map.Entry entry = (Map.Entry) itor.next();
          Object[] vals = (Object[]) entry.getKey();
          double val1 = ((Double) vals[0]).doubleValue();
          double val2 = ((Double) vals[1]).doubleValue();
          double val3 = ((Double) vals[2]).doubleValue();
          int[] tm_array = (int[]) entry.getValue();
          for (int mi = 0; mi<tm_array.length; mi++) {
            if (tm_array[mi] > 0) {
              inv.add(val1, val2, val3, mi, tm_array[mi]);
              if (inv.falsified)
                break;
            }
          }

          if (inv.falsified)
            break;
        }
      }

    }
    */ // ... [INCR]
  }

}
