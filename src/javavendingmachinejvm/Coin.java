 
package javavendingmachinejvm;

/**
 * Class used only on purpose to imitate coin insertion mechanism. In opposition
 * to Coin enum this class can be instantiated with values provided by user,
 * thus being untrusted Real world scenario would not need objects of this
 * class, or at least instantiation would be performed upon collecting sensor
 * data.
 *
 * @author Osmiogrzesznik at TenThousandsSteps.org
 */
public class Coin implements IValuable {

  private final int value;

  /**
   *
   * @param value
   */
  public Coin(int value) {
    this.value = value;
  }

  /**
   *
   * @return
   */
  @Override
  public int getValue() {
    return this.value;
  }

  /**
   *
   * @return
   */
  @Override
  public String getDescription() {
    return String.format("Possibly fake £%.2f coin ", (double) value / 100);
  }

  @Override
  public String toString() {
    return getDescription(); //To change body of generated methods, choose Tools | Templates.
  }

}
