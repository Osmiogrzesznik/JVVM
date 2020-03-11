 
package javavendingmachinejvm;

/**
 * Data object representing substantial properties of Snack article stored in
 * Vending Machine. Holds the snack price and name String representation for UI
 purposes. class implementation corresponding to Coin implementation of
 IValuable. Snack differs from its cousin IValuable Coin, we cannot infer
 its description from it's monetary value, thus different constructor
 initialising name as this Object's String representation
 *
 *
 * @author Osmiogrzesznik at TenThousandsSteps.org
 */
public class Snack implements IValuable {

  private int price;
  private String name;

  /**
   * Creates new instance of this object , more strictly following concept of
   * String, Int pair as
   *
   * @param price
   * @param name
   */
  public Snack(String name, int price) {
    this.price = price;
    this.name = name;
  }

  /**
   *
   * @return
   */
  @Override
  public int getValue() {
    return this.price;
  }

  /**
   *
   * @return
   */
  @Override
  public String getDescription() {
    return this.name;
  }

  @Override
  public String toString() {
    return this.getDescription();
  }

}
