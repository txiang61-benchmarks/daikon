package daikon.inv.binary.sequenceString;

import daikon.*;
import daikon.inv.*;
import utilMDE.*;
import daikon.derive.*;
import daikon.derive.unary.*;
import daikon.derive.binary.*;
import java.util.*;

// *****
// Automatically generated from Member-cpp.java
// *****

public final class Member extends SequenceString  {

  public final static boolean debugMember = false;
  // public final static boolean debugMember = true;

  protected Member(PptSlice ppt, boolean seq_first) {
    super(ppt, seq_first);
    Assert.assert(sclvar().rep_type == ProglangType.STRING );
    Assert.assert(seqvar().rep_type == ProglangType.STRING_ARRAY );
  }

  public static Member instantiate(PptSlice ppt, boolean seq_first) {
    VarInfo seqvar = ppt.var_infos[seq_first ? 0 : 1];
    VarInfo sclvar = ppt.var_infos[seq_first ? 1 : 0];

    if (isEqualToObviousMember(sclvar, seqvar)) {
      Global.implied_noninstantiated_invariants += 1;
      if (debugMember) {
        System.out.println("Member not instantiated (obvious): "
                           + sclvar.name + " in " + seqvar.name);
      }
      return null;
    }

    if (debugMember) {
      System.out.println("Member instantiated: "
                         + sclvar.name + " in " + seqvar.name);
    }
    return new Member(ppt, seq_first);
  }

  public boolean isObviousImplied() {
    VarInfo seqvar = ppt.var_infos[seq_first ? 0 : 1];
    VarInfo sclvar = ppt.var_infos[seq_first ? 1 : 0];
    return isEqualToObviousMember(sclvar, seqvar);
  }

  // Like isObviousMember, but also checks everything equal to the given
  // variables.
  public static boolean isEqualToObviousMember(VarInfo sclvar, VarInfo seqvar) {
    Assert.assert(sclvar.isCanonical());
    Assert.assert(seqvar.isCanonical());
    Vector scl_equalto = sclvar.equalTo();
    scl_equalto.add(0, sclvar);
    Vector seq_equalto = seqvar.equalTo();
    seq_equalto.add(0, seqvar);

    for (int sclidx=0; sclidx<scl_equalto.size(); sclidx++) {
      for (int seqidx=0; seqidx<seq_equalto.size(); seqidx++) {
        VarInfo this_sclvar = (VarInfo) scl_equalto.elementAt(sclidx);
        VarInfo this_seqvar = (VarInfo) seq_equalto.elementAt(seqidx);
        if (isObviousMember(this_sclvar, this_seqvar))
          return true;
      }
    }
    return false;
  }

  public static boolean isObviousMember(VarInfo sclvar, VarInfo seqvar) {

    VarInfo sclvar_seq = sclvar.isDerivedSequenceMember();
    // System.out.println("Member.isObviousMember being called");
    // System.out.println("sclvar=" + sclvar.name
    //                    + ", sclvar.derived=" + sclvar.derived
    //                    + ", sclvar_seq=" + sclvar_seq);

    if (sclvar_seq == null)
      return false;
    // The scalar is a member of some array.
    if (sclvar_seq == seqvar)
      return true;
    // The scalar is a member of a different array than the sequence.
    // But maybe the relationship is still obvious, so keep checking.

    // If the scalar is a member of a subsequence of the sequence, then
    // the scalar is a member of the full sequence.
    // This is satisfied, for instance, when determining that
    // max(B[0..I]) is an obvious member of B.
    VarInfo sclseqsuper = sclvar_seq.isDerivedSubSequenceOf();
    if (sclseqsuper == seqvar)
      return true;

    // We know the scalar was derived from some array, but not from the
    // sequence variable.  If also not from what the sequence variable was
    // derived from, we don't know anything about membership.
    if (seqvar.isDerivedSubSequenceOf() != sclvar_seq)
      return false;

    // If the scalar is a positional element of the sequence from which
    // the sequence at hand was derived, then any relationship will be
    // (mostly) obvious by comparing the length of the sequence to the
    // index.  By contrast, if the scalar is max(...) or min(...), all bets
    // are off.

    // [Do I need to treat sclvar_seq.derived in {SequenceMin, SequenceMax}
    // specially?]

    // B[I] in B[0..J]
    // I need to compare I to J.  This is a stop-gap that does only
    // a bit of that.
    // Need to completely cover the cases of
    // SequenceInitial and SequenceStringSubscript .

    SequenceStringSubsequence  seqsss = (SequenceStringSubsequence ) seqvar.derived;
    VarInfo seq_index = seqsss.sclvar();
    int seq_shift = seqsss.index_shift;

    if (sclvar.derived instanceof SequenceStringSubscript ) {
      SequenceStringSubscript  sclsss = (SequenceStringSubscript ) sclvar.derived;
      VarInfo scl_index = sclsss.sclvar();
      int scl_shift = sclsss.index_shift;
      if ((scl_index == seq_index)
          && (! ((scl_shift == -1) && (seq_shift == 0))))
        return true;
    } else if (sclvar.derived instanceof SequenceInitial) {
      SequenceInitial sclse = (SequenceInitial) sclvar.derived;
      int scl_index = sclse.index;
      if (scl_index == 0)
        // It might not be true, because the array could be empty;
        // but if the array isn't empty, then it's obvious.
        return true;
    }

    /// I need to test this code!
    // Now do tests over variable name, to avoid invariants like:
    //   header.next in header.~ll~next~
    //   header.next.element in header.~ll~next~.element
    //   header.next in header.next.~ll~next~
    //   return.current in return.current.~ll~next~
    String sclname = sclvar.name;
    String seqname = seqvar.name;
    int llpos = seqname.indexOf("~ll~");
    if (llpos != -1) {
      int tildepos = seqname.indexOf("~", llpos+5);
      if (tildepos != -1) {
        int midsize = tildepos-llpos-4;
        int lastsize = seqname.length()-tildepos-1;
        if (seqname.regionMatches(0, sclname, 0, llpos)
            && (((tildepos == seqname.length() - 1)
                 && (llpos == sclname.length()))
                || (seqname.regionMatches(llpos+4, sclname, llpos, midsize)
                    && seqname.regionMatches(tildepos+1, sclname, tildepos-4, lastsize))))
          return true;
      }
    }

    // int lastdot = sclvar.lastIndexOf(".");
    // if (lastdot != -1) {
    //   if (sclname.substring(0, lastdot).equals(seqname.substring(0, lastdot))
    //       && seqname.substring(lastdot).equals("~ll~" + sclname.substring(lastdot) + "~")) {
    //     return true;
    //   }
    // }

    return false;
  }

  public String repr() {
    double probability = getProbability();
    return "Member" + varNames() + ": "
      + "no_invariant=" + no_invariant
      + "; probability = " + probability;
  }

  public String format() {
    return sclvar().name + " in " + seqvar().name;
  }

  public void add_modified(String [] a, String  i, int count) {
    if (ArraysMDE.indexOf(a, i) == -1) {
      if (debugMember) {
        System.out.println("Member destroyed:  " + format() + " because " + i + " not in " + ArraysMDE.toString(a));
      }
      destroy();
      return;
    }
  }

  protected double computeProbability() {
    if (no_invariant)
      return Invariant.PROBABILITY_NEVER;
    else
      return Invariant.PROBABILITY_JUSTIFIED;
  }

  public boolean isSameFormula(Invariant other)
  {
    Assert.assert(other instanceof Member);
    return true;
  }
}
