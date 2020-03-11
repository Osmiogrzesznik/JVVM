 
package javavendingmachinejvm;

/**
 * Thrown on attempt to dispense a snack without sufficient credit, Ensures that
 * even in case of faulty interface logic snack is not dispensed. Other
 * additional measures should be taken to provide good user experience.
 *
 * @author Osmiogrzesznik at TenThousandsSteps.org
 */
class InsufficientFundsException extends Exception {

  @Override
  public String toString() {
    return "*** INSUFFICIENT FUNDS ***\n" + super.toString();
  }
}
