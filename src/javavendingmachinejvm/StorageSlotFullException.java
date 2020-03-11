 
package javavendingmachinejvm;

/**
 * responsible for informing higher structures of slot capacity state
 *
 * @author Osmiogrzesznik at TenThousandsSteps.org
 */
public class StorageSlotFullException extends Exception {

  private final String storageInfo;

  /**
   * Creates a new instance of <code>StorageSlotFullException</code> without
   * detail message.
   *
   * @param storageInfo
   */
  public StorageSlotFullException(String storageInfo) {
    super(storageInfo);
    this.storageInfo = storageInfo;
  }

  /**
   *
   * @return
   */
  @Override
  public String toString() {
    return "*** STORAGE SLOT IS FULL ***\n" + super.toString();
  }

  @Override
  public String getMessage() {
    return super.getMessage() + getStorageInfo(); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * @return the storageInfo
   */
  public String getStorageInfo() {
    return storageInfo;
  }
}
