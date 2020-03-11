 
package javavendingmachinejvm;

/**
 * Thrown when an invalid coin is passed to a slotDetection method. The coin is
 * either damaged or forged. There is no denomination registry that matches
 * expected properties of coin inserted Catching should be performed to avoid
 * any untrusted user coin from jamming the mechanical elements.
 *
 * @author Osmiogrzesznik at TenThousandsSteps.org
 */
class noSuchSlotException extends Exception {

  @Override
  public String toString() {
    return "*** INCORRECT INPUT ***\n" + super.toString();
  }
}
