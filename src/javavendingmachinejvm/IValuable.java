 
package javavendingmachinejvm;

/**
 * Interface of Data objects representing substantial properties of any valuable
 * article stored in Vending Machine. Serves as an abstraction of objects having
 * a scalar value(price,denomination etc.) and String representation for UI
 * purposes.
 *
 * @author Osmiogrzesznik at TenThousandsSteps.org
 */
public interface IValuable {

  /**
   *
   * @return internal calculation value as an Integer type to avoid rounding
   * errors, and reduce amount of memory consumed
   */
  int getValue();

  /**
   *
   * @return human readable String for any UI.
   */
  String getDescription();

}
