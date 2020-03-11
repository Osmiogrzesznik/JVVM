 
package javavendingmachinejvm;

/**
 *
 * @author Osmiogrzesznik at TenThousandsSteps.org
 */
public class OutOfStockException extends Exception {

  /**
   *
   */
  public OutOfStockException() {
  }

  /**
   *
   * @return
   */
  @Override
  public String toString() {
    return "*** OUT OF STOCK ***" + super.toString();
  }

}
