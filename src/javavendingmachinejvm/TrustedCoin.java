 
package javavendingmachinejvm;

/**
 *
 * @author Osmiogrzesznik at TenThousandsSteps.org
 */
/**
 * TrustedCoin types as a Constant enum better represent coin types(they rarely
 change). These are used as default coin set to register on initialisation;
 *
 * In real world scenario these objects would store the required properties
 * (weight, diameter) or set of data corresponding to expected outputs of
 * hardware coin validation sensors.
 *
 * implementation of Common IValuable interface helps to treat inserted coins as
 * they would have been of the same type
 */
enum TrustedCoin implements IValuable {
//how machine would reject the coin not listed here,
  //the question is can i create TrustedCoin that is not Listed here
  GBP_0p05(5),
  GBP_0p10(10),
  GBP_0p20(20),
  GBP_0p50(50),
  GBP_1p00(100);
  /**
   * TrustedCoin.value is represented internally as an integer, to avoid rounding
 errors, and reduce amount of memory consumed
   */
  final int value;

  private TrustedCoin(int value) {
    this.value = value;
  }

  /**
   *
   * @return internal integer used for calculations
   */
  @Override
  public int getValue() {
    return this.value;
  }

  /**
   * @return human readable TrustedCoin representation. Until scaling globally value
 may remain expressed in GBP notation
   */
  @Override
  public String getDescription() {
    return String.format("£%.2f Coin", (double) value / 100);
  }

  /**
   * @return human readable String for any UI. Overridden to achieve similar
   * expected behaviour in Collections
   */
  @Override
  public String toString() {
    return this.getDescription();
  }

}
