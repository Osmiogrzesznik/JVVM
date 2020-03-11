 
package javavendingmachinejvm;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main Class of Application, instantiates and prepares
 * JavaVirtualVendingMachine and TextInterface modules
 *
 * @author Osmiogrzesznik at TenThousandsSteps.org3
 */
public class AssignmentVending_TextInterfaced {

  /**
   * @param args
   * @throws StorageSlotFullException
   * @throws javavendingmachinejvm.InsufficientFundsWarning
   * @throws javavendingmachinejvm.OutOfStockException
   */
  public static void main(String[] args) {

    //Prepare components and Assemble JVVM
    ObservableOutputList coinOutput = new ObservableOutputList("Coin Opening");
    ObservableOutputList snackOutput = new ObservableOutputList("Snack Opening");
    JavaVirtualVendingMachine JVVM = new JavaVirtualVendingMachine(coinOutput, snackOutput, 50, 10);
    TextUserInterface textUserInterface = new TextUserInterface(System.in, System.out, JVVM);
    textUserInterface.setObservableOutputs(coinOutput, snackOutput);

    //Create Machine Default Snack Slot Managers 
    //TODO: should be stored in separate configuration file to ship machines with custom initial setups
    JVVM.registerSnack(new Snack("Crisps", 75), 45);
    JVVM.registerSnack(new Snack("Mars Bar", 70), 45);
    JVVM.registerSnack(new Snack("Coca cola", 100), 45);
    JVVM.registerSnack(new Snack("Eugenia", 50), 45);
    JVVM.registerSnack(new Snack("Water", 85), 45);

    //Create Machine Default Coin Slot Managers 
    //TODO: should be stored in separate configuration file to ship machines with custom initial setups
    JVVM.registerValidCoin(TrustedCoin.GBP_0p05, 20);
    JVVM.registerValidCoin(TrustedCoin.GBP_0p10, 20);
    JVVM.registerValidCoin(TrustedCoin.GBP_0p20, 20);
    JVVM.registerValidCoin(TrustedCoin.GBP_0p50, 20);
    JVVM.registerValidCoin(TrustedCoin.GBP_1p00, 10);
    
    textUserInterface.start();

  }
}
