package daikon.inv.sequenceScalar;

import daikon.*;

import java.lang.reflect.*;

// I think this is likely to disappear, except possibly as a place to keep
// common data like minimum and maximum.

public class SequenceScalarFactory {


  // Adds the appropriate new Invariant objects to the specified Invariants
  // collection.
  public static void instantiate(PptSlice ppt) {
    boolean seq_first;

    {
      VarInfo vi0 = ppt.var_infos[0];
      VarInfo vi1 = ppt.var_infos[1];
      if (vi0.type.isArray() && (!vi1.type.isArray())) {
        seq_first = true;
      } else if ((!vi0.type.isArray()) && (vi1.type.isArray())) {
        seq_first = false;
      } else {
        return;
      }
    }

    new Member(ppt, seq_first);
  }

  private SequenceScalarFactory() {
  }

}
