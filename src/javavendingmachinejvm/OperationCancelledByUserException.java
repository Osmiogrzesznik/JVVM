 
package javavendingmachinejvm;

/**
 * TextUserInterface Exception indicating that cancellation of current
 * transaction process has been cancelled by user. used in Flow control. When
 * instantiated with parameter indicates cancellation has been performed by
 * poweruser.
 *
 * @author Osmiogrzesznik at TenThousandsSteps.org
 */
public class OperationCancelledByUserException extends Exception {

  boolean issuedByPowerUser = false;

  /**
   *
   */
  public OperationCancelledByUserException() {
    super();
  }

  /**
   *
   * @param o
   */
  public OperationCancelledByUserException(Object o) {
    super();
    issuedByPowerUser = true;
  }

}
