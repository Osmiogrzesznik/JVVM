 
package javavendingmachinejvm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * JavaVirtualVendingMachine is independent module , storing and dispensing
 * IValuables and performing business logic, keeping track of overall difference
 * between invested capital value of products, and income gained during
 * operation. Vending Machine is additionally responsible for validation of
 * monetary input, and keeping track of coins inserted and not spend, referred
 * to as a <code>credit</code>, as well as calculating change to dispense after
 * successful transaction. Two Public Lists serve as access points to served
 * IValuables, number of higher level methods control internal objects to
 * perform dispensing,accepting and registering new products, as well as
 * accessing retail data. Machine follows the strategy employed by other Vending
 * Machines, if there is no possibility to dispense correct change using its
 * change pool, machine saves the undispensed change remainder in credit -
 * available to user on next purchase, while setting to state of requiring usage
 * of correct change to active Machine remains in
 * <code>useCorrectChangeState</code> until purchases will top up the missing
 * coins in change pool.
 *
 * @author Osmiogrzesznik at TenThousandsSteps.org
 */
class JavaVirtualVendingMachine {

  private boolean useCorrectChangeState;
  int MAX_SNACK_CAPACITY;
  private final int MAX_COIN_CAPACITY;
  /**
   * integer field keeping the total of all coins stored;
   */
  private int totalCash = 0;
  /**
   * integer field keeping the difference between purchase value and the
   * invested Value of initial articles, expressed in pence
   */
  private int totalIncome = 0;
  /**
   * integer field holding the value of inserted coins that have not been spend
   * yet on dispensed content, expressed in pence
   */
  private int credit = 0;
  /**
   * list storing dispensed change coins, set private to prevent clients form
   * setting to null
   */
  private Collection coinsOutputOpening;
  /**
   * list storing dispensed snacks, private to prevent clients form setting to
   * null
   */
  private Collection snacksOutputOpening;
  /**
   * List of references to <code>ISlotController</code>s employing List index as
   * an ID mechanism used to access the served articles
   */
  private final List<ISlotController> snackRegister = new ArrayList<ISlotController>();
  /**
   * List of references to <code>ISlotController</code>s employing List index as
   * an ID mechanism used to access, accept and dispense stored coins
   */
  private final List<ISlotController> coinRegister = new ArrayList<ISlotController>();

  /**
   *
   * Instantiates this object with two outputs and determines the maximum
   * capacity for slots. Ideally Machine itself should not keep any data about
   * products already dispensed, and this Collections serve as virtual
   * representations of outputs that machine should not control in any other way
   * than using their add method (dispensing into them). Further responsibility
   * for collecting/ clearing the content buffered in list is left to Modules/
   * objects interested in these outputs, and these should understand exposed
   * API and utilisation of produced objects.
   *
   * @param coinsOutputOpening collection storing dispensed change
   * @param snacksOutputOpening collection storing dispensed snack articles
   * @param maxCoinCapacity maximum default coin capacity
   * @param maxSnackCapacity maximum default snack capacity
   */
  public JavaVirtualVendingMachine(Collection coinsOutputOpening, Collection snacksOutputOpening, int maxCoinCapacity, int maxSnackCapacity) {
    this.MAX_COIN_CAPACITY = maxCoinCapacity;
    this.MAX_SNACK_CAPACITY = maxSnackCapacity;
    this.coinsOutputOpening = coinsOutputOpening;
    this.snacksOutputOpening = snacksOutputOpening;
  }

  /**
   * @return the difference between purchases and the invested Value of initial
   * stored content, expressed in pence
   */
  public int getTotalIncome() {
    return totalIncome;
  }

  /**
   * @return the value of inserted coins that will be considered in next
   * purchase but have not been spend yet on dispensed content - expressed in
   * pennies
   */
  public int getCredit() {
    return credit;
  }

  /**
   * @return the total of all coins stored;
   */
  int getTotalCash() {
    return totalCash;
  }

  /**
   * @return the coinsOutputOpening
   */
  public Collection getCoinsOutputOpening() {
    return coinsOutputOpening;
  }

  /**
   * @return the snacksOutputOpening
   */
  public Collection getSnacksOutputOpening() {
    return snacksOutputOpening;
  }

  /**
   * @param snackId index of snack at register
   * @return returns the item property corresponding to getter name,
   */
  int getSnackQuantity(int snackId) {
    return snackRegister.get(snackId).getQuantity();
  }

  /**
   * @param snackId index of snack at register
   * @return returns the item property corresponding to getter name,
   */
  int getSnackPrice(int snackId) {
    return snackRegister.get(snackId).getValue();
  }

  /**
   * @param snackId index of snack at register
   * @return returns the item property corresponding to getter name,
   */
  String getSnackName(int snackId) {
    return snackRegister.get(snackId).getDescription();
  }

  /**
   * @return returns the amount of registered retail items
   */
  int getSnackSelectionSize() {
    return snackRegister.size();
  }

  /**
   * todo: should it be a list of strings or islotcontryrollers should override
   * toString Method of Object, in such a way that each element in this way
   * returning here this
   *
   * @return
   */
  public List<ISlotController> getCoinRegister() {

    return coinRegister;
  }

  public List<ISlotController> getSnackRegister() {
    return snackRegister;
  }

  /**
   *
   * @param snack snack data Object
   * @param investedValue price invested in Snack, not a selling price
   */
  public void registerSnack(Snack snack, int investedValue) {

    ISlotController valuablesSlot = new ValuablesSlot(snack, MAX_SNACK_CAPACITY, MAX_SNACK_CAPACITY, snacksOutputOpening);
    totalIncome -= MAX_SNACK_CAPACITY * investedValue;
    snackRegister.add(valuablesSlot);

  }

  /**
   * TODO:move on top of class registers TrustedCoin as a Valid denomination by
   * instantiating ValuablesSlot assigned to its coin.
   *
   * @param nwCoinType set of properties of coin being stored
   * @param initAmount amount of items physically loaded in storage slot
   * @param slotCapacity max
   * @throws StorageSlotFullException
   */
  public void registerValidCoin(TrustedCoin nwCoinType, int initAmount) {

    ISlotController valuablesSlot = new ValuablesSlot(nwCoinType, initAmount, MAX_COIN_CAPACITY, coinsOutputOpening);
// why decreasing income? Because company leaves valuable assets unsupervised,
//or in other words invests capital in initial change  
    //totalIncome -= initAmount * nwCoinType.getValue();
    totalCash += initAmount * nwCoinType.getValue();
    coinRegister.add(valuablesSlot);
  }

  /**
   * Determines the slot responsible for holding the inserted type, if no slot
   * is found throws invalidCoinException.
   *
   * Real world implementation, if not mechanical in nature, should involve
   * comparison of digital sensor activation pattern stored in TrustedCoin Enum
   * against current state of digital sensors on insertion. TrustedCoin would
   * then represent set of booleans or weight size, texture, magnetic properties
   * of object inserted checked against the article type info stored on
   * register.
   *
   * For the purpose of manual denomination input method required, this
   * implementation checks only for denomination, expressed as value of
   * Ivaluable.
   *
   * TODO: 1.Should i leave the responsibility to ISlotController to reject
   * coin? - how then to pass the coin to next slots without slots accidentaly
   * being copied into output on rejection? 2.ISSUE: two channel communication -
   * Error throw here is almost like value returned .
   *
   * @param coin inserted coin
   * @return slot responsible for storing the type of coin
   * @throws noSuchSlotException if respective slot was not found in
   * coinRegister
   */
  private ISlotController validateCoin(IValuable coin) throws noSuchSlotException {
    for (ISlotController slot : coinRegister) {
      if (slot.getValue() == coin.getValue()) {
        return slot; //first slot that matches
      }
    }
    throw new noSuchSlotException(); // there is no such slot
  }

  /**
   * Validates and passes the coin to appropriate slot, rejected coin is
   * redirected to coin output. Follows real world behaviour - user is not
   * notified about the reason of rejected coin. TODO: 1. ISSUE :
   * re-implementing forwarding mechanism that already exists in ISlotController
   *
   * @param coin coin inserted
   * @return true on coin being accepted false if coin had to be forwarded
   */
  public boolean insertCoin(IValuable coin) {
    // lets say that coin is valid but slot is full, should we rethrow to inform user interface and user of these alternatives
    // or should we keep it real - you never know why your coin is being rejected
    try {
      //try to identify coin slot
      ISlotController slot = validateCoin(coin);
      slot.Accept(coin); //try to accept the coin
      credit += coin.getValue();
      totalCash += coin.getValue();
    } catch (noSuchSlotException | StorageSlotFullException e) {
      coinsOutputOpening.add(coin);//if slot is full or coin is fake spit the coin out through opening
      return false;

    }

    return true;//coin was accepted by some slot
  }

  /**
   * Dispenses requestedQuantity of snack with index snackCode most important
   * and secured method, delivering main functionality . Does not return any
   * value. Sends the message to chosen slotController with index of snackId,
   * looping until either required qty is met or OutOfStockException occurs. On
   * each successful iteration credit is decreased by snack price, and vice
   * versa TotalProfit is increased to cover invested negative value. At the end
   * of operation or when there is no more snacks machine attempts to dispense
   * change. In real world scenario method would need to dispense only one snack
   * a time or block the thread in a loop to wait until all snacks are
   * dispensed. Snacks are dispensed by slots to output configured at
   * instantiation.
   *
   * @param snackCode snackregister index
   * @param requestedQuantity
   * @throws InsufficientFundsException if credit is not covering the cost of
   * snack.
   * @throws OutOfStockException if dispensing from empty slot
   */
  public void dispenseSnackQuantityAndReturnChange(int snackCode, int requestedQuantity) throws InsufficientFundsException, OutOfStockException, IndexOutOfBoundsException {
    ISlotController snackSlot = snackRegister.get(snackCode); // possible IndexOutOfBOund here!!!
    int price = snackSlot.getValue();
    if (credit < snackSlot.getValue() * requestedQuantity) {
      throw new InsufficientFundsException();
    }
    for (int i = 0; i < requestedQuantity; i++) {
      try {
        snackSlot.ReleaseItem();
        credit -= price;
        totalIncome += price;
      } catch (OutOfStockException ex) {
        dispenseChange();
        throw ex;
      }
    }

    dispenseChange();
  }

  /**
   * TODO: Loops Through register !!!!! Should sort them before or arrange the
   * references in temporary array in descending order—NOBODY SAID IT IS SORTED,
   * cant assume it. However, it would be to much - irrelevant boiler plate code
   * especially in case when user has no possibility to register new Coins
   */
  public void dispenseChange() {
    int currentDenomination;
    ISlotController currentSlot;
    boolean noNegativeChangeLeftAfterDispensingThisCoin;

    //System.out.println("Change to give in coins:" + changeleft);
    for (int i = coinRegister.size() - 1; i >= 0 && credit > 0; i--) { //for each of
      //{100,50,20,10,5} until either we've reached smallest denomination or already dispensed change needed
      currentSlot = coinRegister.get(i);
      currentDenomination = currentSlot.getValue();

      do {
        noNegativeChangeLeftAfterDispensingThisCoin = currentDenomination <= credit;
        if (noNegativeChangeLeftAfterDispensingThisCoin) {
          try {
            currentSlot.ReleaseItem();
            credit -= currentDenomination;
            totalCash -= currentDenomination;
          } catch (OutOfStockException e) {
            useCorrectChangeState = true;
          }
        }
      } while (!currentSlot.isEmpty() && noNegativeChangeLeftAfterDispensingThisCoin);

    }
  }

  /**
   * loops through coin slots to determine if change dispensing algorithm will
   * have enough denominations
   *
   * @return true if any of slots is Empty
   */
  public boolean getUseCorrectChangeState() {
    //checks for state only if last operation set it to True
    useCorrectChangeState = false;
    for (ISlotController slot : coinRegister) {
      if (slot.isEmpty()) {
        useCorrectChangeState = true;
      }
    }

    return useCorrectChangeState;
  }

}
