 
package javavendingmachinejvm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Virtual Implementation of ISlotController interface. Wrapper and adapter
 * around ArrayBlockingQueue, chosen due to its to similarity in behaviour and a
 * sufficient ready implementation. Physical storage slots would have restricted
 * capacity and motors to move content up to Output opening. Convenient
 * representation of this behaviour is Queue data type, as elements follow FIFO
 * principle.
 *
 * In real world scenario it would control hardware parts, activating series of
 * digital outputs and reading sensors digital inputs while instead of array
 * inside use of simple integer counter state would be sufficient.
 *
 *
 * @author Osmiogrzesznik at TenThousandsSteps.org
 */
public class ValuablesSlot implements ISlotController {

  private final IValuable itemType;
  private ArrayBlockingQueue<IValuable> storageSlot;
  private final int slotCapacity;
  private final Collection jointToOutputContainer;

  @Override
  public int size() {
    return storageSlot.size();
  }

  /**
   * Creates internal representation of physical Slot - ArrayBlockingQueue
   * setting its maximum capacity, and adds <b>initAmount</b> of multiple
   * references of object passed as IValuable <b>itemType</b>
   *
   * @param itemType properties of hold article, must conform to IValuable
   * interface as data will be accessed only through ISlotController interface
   * @param initAmount the amount of articles inserted into slot at registering.
   * Must be smaller than <b>slotCapacity</b>
   * @param slotCapacity number of elements that this Slot can hold
   * @param jointToOutputContainer Collection that can be used to monitor
   * dispensed articles
   */
  public ValuablesSlot(IValuable itemType, int initAmount, int slotCapacity, Collection jointToOutputContainer) {
    this.itemType = itemType;
    this.slotCapacity = slotCapacity;
    this.jointToOutputContainer = jointToOutputContainer;
    storageSlot = new ArrayBlockingQueue<IValuable>(slotCapacity);
    for (int i = 1; i <= initAmount && i <= slotCapacity; i++) {
      storageSlot.add(itemType);
    }

  }

  /**
   *
   * @return string property of hold article,
   */
  @Override
  public String getDescription() {
    return itemType.getDescription();
  }

  /**
   *
   * @return value property of Hold Article
   */
  @Override
  public int getValue() {
    return itemType.getValue();
  }

  /**
   *
   * @return Quantity of Articles being hold
   */
  @Override
  public int getQuantity() {
    return storageSlot.size();
  }

  /**
   * tries to remove last element of underlying queue and pass to
   * jointToOutputContainer, if no element is present (ArrayBlockingQueue
   * NoSuchElement exception) OutOfStockException is thrown. By hiding the
   * original exception system remains unaware of actual implementation. Real
   * World scenario would involve boolean comparison of slot sensor inputs.
   *
   * @throws OutOfStockException if wrapped queue is Empty
   */
  @Override
  public void ReleaseItem() throws OutOfStockException {
    try {
      jointToOutputContainer.add(storageSlot.remove());
    } catch (NoSuchElementException e) {
      throw new OutOfStockException();
    }
  }

  /**
   * Tries to insert element to underlying ArrayBlockingQueue, on Failure The
   * element inserted is being redirected to jointToOutputContainer of this
   * instance, and StorageSlotFullException is thrown afterwards.
   *
   * @param article article inserted
   * @return True on successful operation , or
   * @throws StorageSlotFullException if slot is full, and cannot accept more
   * articles (i.ex. sensor next to slot opening is active). It means that there
   * was an attempt to exceed capacity of underlying ArrayBlockingQueue. The
   * element inserted is being redirected to jointOutput of this instance
   */
  @Override
  public boolean Accept(IValuable article) throws StorageSlotFullException {
    try {
      storageSlot.add(article);
    } catch (IllegalStateException e) {
      jointToOutputContainer.add(article);
      throw new StorageSlotFullException(this.toString());
    }
    return true;
  }

  /**
   *
   * @return boolean indicating whether instance contains any article.
   */
  @Override
  public boolean isEmpty() {
    return storageSlot.isEmpty();

  }

  @Override
  public String toString() {
    return String.format("item:%s stock:%d/%d", itemType, storageSlot.size(), slotCapacity);
  }

}
