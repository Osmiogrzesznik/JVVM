 
package javavendingmachinejvm;

import java.util.List;

/**
 * interface for controlling the dispensing process
 *
 * @author Osmiogrzesznik at TenThousandsSteps.org
 */
public interface ISlotController extends IValuable {

  /**
   * controls the mechanism responsible for releasing the article
   *
   * @throws javavendingmachinejvm.OutOfStockException
   */
  public void ReleaseItem() throws OutOfStockException;

  /**
   *
   * @param article data object representing stored article
   * @return boolean upon successful insertion (keeps coherent with
   * ArrayBlockingQueue interface)
   * @throws StorageSlotFullException if slot is full, and cannot accept more
   * articles (i.ex. sensor next to slot opening is active)
   */
  public boolean Accept(IValuable article) throws StorageSlotFullException;

  /**
   *
   * @return boolean corresponding to particular implementation
   */
  public boolean isEmpty();

  /**
   *
   * @return remaining quantity of article being stored. Implementation may
   * involve simple
   */
  int getQuantity();

  /**
   *
   * @return amount of items on stock
   */
  int size();
}
