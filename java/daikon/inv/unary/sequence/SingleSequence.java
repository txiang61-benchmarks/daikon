package daikon.inv.unary.sequence;

import daikon.*;
import daikon.inv.*;
import daikon.inv.unary.*;
import daikon.inv.binary.twoSequence.*;
import daikon.derive.binary.SequenceSubsequence;
import daikon.suppress.*;
import utilMDE.*;
import org.apache.log4j.Category;
import java.util.*;

/**
 * Invariants on a single sequence (integer or floating point).
 **/
public abstract class SingleSequence
  extends UnaryInvariant
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20020801L;

  protected SingleSequence(PptSlice ppt) {
    super(ppt);
    // System.out.println("Created SingleSequence invariant " + this + " at " + ppt);
  }

  public VarInfo var() {
    return ppt.var_infos[0];
  }

  private static final SuppressionFactory[] suppressionFactories =
    new SuppressionFactory[] {
      SubsetImpliedSuppressionFactory.getInstance()
      // SelfSuppressionFactory.getInstance()
    };

  public SuppressionFactory[] getSuppressionFactories() {
    return suppressionFactories;
  }

  /**
   * Suppression for f(a[0..j]) if f(a[]) or f(b[]) && a \subseq b
   * holds.  Used in all child classes of SingleSequence unless they
   * override.
   **/

  public static class SubsetImpliedSuppressionFactory extends SuppressionFactory {

    public static final Category debug =
      Category.getInstance("daikon.suppress.factories.SubsetImpliedSuppressionFactory");

    private static final SubsetImpliedSuppressionFactory theInstance =
      new SubsetImpliedSuppressionFactory();

    public static SuppressionFactory getInstance() {
      return theInstance;
    }

    private Object readResolve() {
      return theInstance;
    }


    public SuppressionLink generateSuppressionLink (Invariant arg) {
      SingleSequence inv = (SingleSequence) arg;

      if (debug.isDebugEnabled()) {
        debug.debug ("Attempting derived subsequence for: " + inv.repr());
        debug.debug ("  in ppt " + inv.ppt.parent.name);
      }

      VarInfo orig = inv.var().isDerivedSubSequenceOf();
      if (orig != null) {
        if (debug.isDebugEnabled()) {
          debug.debug ("  with orig " + orig.name.name());
        }


        // Since we know f(a[i..j]) is true, searching for f(a[]) is
        // sufficient, since it cannot be the case that !f(a[]).
        SuppressionTemplate template = new SuppressionTemplate();
        template.invTypes = new Class[] {inv.getClass()}; // Has to be same type as inv
        template.varInfos = new VarInfo[][] {new VarInfo[] {orig}};


        inv.ppt.parent.fillSuppressionTemplate (template);
        if (template.filled) {
          SingleSequence  suppressor = (SingleSequence) template.results[0];
          if (debug.isDebugEnabled()) {
            debug.debug ("  Successful template fill for " + inv.format());
            debug.debug ("             with              " + suppressor.format());
          }
          if (suppressor.isSameFormula(inv)) {
            if (debug.isDebugEnabled()) {
              debug.debug ("  Generating link");
            }
            return linkFromTemplate (template, inv);
          } else {
            if (debug.isDebugEnabled()) {
              debug.debug ("  But no link made");
            }
          }
        }
      }

      // Now try finding subset invariants for all VarInfos.  Don't
      // bother with checking < relationships between
      // SequenceSubsequence variables here, because if the <
      // relationships hold, there should exist SubSequence
      // invariants.  That is, we don't bother checking things like
      // A[0..i] subseq A[0..j] etc. because the subsequence
      // invariants should exist.
      VarInfo thisVar = inv.var();
      for (int iVarInfos = 0; iVarInfos < inv.ppt.parent.var_infos.length; iVarInfos++) {
        // Try to find other varInfos, such that the following hold:
        // If inv is f(thisVar) then there's also f(otherVar)
        // subsequence(thisVar, otherVar)

        VarInfo otherVar = inv.ppt.parent.var_infos[iVarInfos];
        if (otherVar.type == thisVar.type && otherVar != thisVar) {
          if (debug.isDebugEnabled()) {
            debug.debug ("  Possibly interesting var: " + otherVar.name.name());
          }
          // Two things to check: obvious subsets, like A[0..i-1] subseq A[0..i]
          // and non obvious ones detected dynamically
          if (thisVar.isDerivedSubSequenceOf() != null && otherVar.isDerivedSubSequenceOf() != null
              && thisVar.isDerivedSubSequenceOf() == otherVar.isDerivedSubSequenceOf()) {
            SequenceSubsequence leftDer = (SequenceSubsequence) thisVar.derived;
            SequenceSubsequence rightDer = (SequenceSubsequence) otherVar.derived;
            if (leftDer.from_start == rightDer.from_start &&
                leftDer.sclvar() == rightDer.sclvar() &&
                (leftDer.from_start ?
                 (rightDer.index_shift - leftDer.index_shift >= 0) :
                 (rightDer.index_shift - leftDer.index_shift <= 0))
                ) {
              SuppressionTemplate similarTemplate = new SuppressionTemplate();
              similarTemplate.invTypes = new Class[] {inv.getClass()};
              similarTemplate.varInfos = new VarInfo[][] {new VarInfo[] {otherVar}};
              inv.ppt.parent.fillSuppressionTemplate (similarTemplate);
              if (similarTemplate.filled &&
                  similarTemplate.results[0].isSameFormula(inv)) {
                if (debug.isDebugEnabled()) {
                  debug.debug ("  Filling with obvious subset");
                }
                return linkFromTemplate (similarTemplate, inv);
              }
            }
          }

          // Now search for non obvious results
          SuppressionTemplate similarTemplate = new SuppressionTemplate();
          similarTemplate.invTypes = new Class[] {inv.getClass()};
          similarTemplate.varInfos = new VarInfo[][] {new VarInfo[] {otherVar}};
          inv.ppt.parent.fillSuppressionTemplate (similarTemplate);
          if (similarTemplate.filled &&
              similarTemplate.results[0].isSameFormula(inv)) {
            // Success in finding f(otherVar)
            // Now have to show that thisVar subsequence otherVar
            SuppressionTemplate subseqTemplate = new SuppressionTemplate();
            subseqTemplate.varInfos = new VarInfo[][] {new VarInfo[] {thisVar, otherVar}};

            subseqTemplate.invTypes = new Class[] {SubSequence.class};
            inv.ppt.parent.fillSuppressionTemplate (similarTemplate);
            if (subseqTemplate.filled) {
              // Possible success in finding thisVar subSeq otherVar
              SubSequence subSeqInv = (SubSequence) subseqTemplate.results[0];

              VarInfo transThisVar = subseqTemplate.transforms[0][0];
              // Second transformed var in first invariant
              if ((subSeqInv.var1_in_var2 && subSeqInv.var1() == transThisVar) ||
                  (subSeqInv.var2_in_var1 && subSeqInv.var2() == transThisVar)) {
                List suppressors = new ArrayList();
                suppressors.add (similarTemplate.results[0]);
                suppressors.add (subseqTemplate.results[0]);
                // Now we have to add both invariants to the suppressor
                return new SuppressionLink (this,
                                            inv,
                                            suppressors);
              }
            }

            subseqTemplate.resetResults();
            subseqTemplate.invTypes = new Class[] {SubSequenceFloat.class};
            inv.ppt.parent.fillSuppressionTemplate (similarTemplate);
            if (subseqTemplate.filled) {
              // Possible success in finding thisVar subSeq otherVar
              SubSequenceFloat subSeqInv = (SubSequenceFloat) subseqTemplate.results[0];

              VarInfo transThisVar = subseqTemplate.transforms[0][0];
              // Second transformed var in first invariant
              if ((subSeqInv.var1_in_var2 && subSeqInv.var1() == transThisVar) ||
                  (subSeqInv.var2_in_var1 && subSeqInv.var2() == transThisVar)) {
                List suppressors = new ArrayList();
                suppressors.add (similarTemplate.results[0]);
                suppressors.add (subseqTemplate.results[0]);
                // Now we have to add both invariants to the suppressor
                if (debug.isDebugEnabled()) {
                  debug.debug ("  Filling with non obvious subset");
                }

                return new SuppressionLink (this,
                                            inv,
                                            suppressors);
              }
            }
          }
        }
      }
      return null;
    }
  }

}
