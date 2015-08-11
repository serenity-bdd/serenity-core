package net.thucydides.core.util;

/**
 * Utility class for implementing equals().
 * @author johnsmart
 *
 */
public final class EqualsUtils {

    private EqualsUtils() {}
    
    public static boolean areEqual(final boolean aThis, final boolean aThat){
      return aThis == aThat;
    }

    public static boolean areEqual(final char aThis, final char aThat){
      return aThis == aThat;
    }

    public static boolean areEqual(final long aThis, final long aThat){
      return aThis == aThat;
    }

    public static boolean areEqual(final float aThis, final float aThat){
      return Float.floatToIntBits(aThis) == Float.floatToIntBits(aThat);
    }

    public static boolean areEqual(final double aThis, final double aThat){
      return Double.doubleToLongBits(aThis) == Double.doubleToLongBits(aThat);
    }

    public static boolean areEqual(final Object aThis, final Object aThat){
      if (aThis == null) {
          return aThat == null;
      } else {
          return aThis.equals(aThat);
      }
    }
  }
   