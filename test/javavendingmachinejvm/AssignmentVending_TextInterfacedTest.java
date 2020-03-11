 
package javavendingmachinejvm;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Osmiogrzesznik <Osmiogrzesznik.name at TenThousandsSteps.org>
 */
public class AssignmentVending_TextInterfacedTest {

  private final ByteArrayOutputStream mockStdOutBytes = new ByteArrayOutputStream();
  private final PrintStream originalStdOut = System.out;

  TextUserInterface TUI;
  ObservableOutputList observableCoinOutput = new ObservableOutputList("Coin Opening");
  ObservableOutputList observableSnackOutput = new ObservableOutputList("Snack Opening");
  JavaVirtualVendingMachine JVVM = new JavaVirtualVendingMachine(observableCoinOutput, observableSnackOutput, 50, 10);

  public AssignmentVending_TextInterfacedTest() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
    JVVM = new JavaVirtualVendingMachine(observableCoinOutput, observableSnackOutput, 50, 10);
  }

  @After
  public void tearDown() throws IOException {

    mockStdOutBytes.flush();
    observableCoinOutput.clear();
    observableSnackOutput.clear();
    JVVM = null;
  }

//-------------------------------------------------------------- U T I L S --------------------------------------------
  public void fullJVVMSetup() {
    JVVM.registerSnack(new Snack("Crisps", 75), 45);
    JVVM.registerSnack(new Snack("Mars Bar", 70), 45);
    JVVM.registerSnack(new Snack("Coca cola", 100), 45);
    JVVM.registerSnack(new Snack("Eugenia", 50), 45);
    JVVM.registerSnack(new Snack("Water", 85), 45);
    coinOnlyJVVMSetup();

  }

  public void coinOnlyJVVMSetup() {
    JVVM.registerValidCoin(TrustedCoin.GBP_0p05, 20);
    JVVM.registerValidCoin(TrustedCoin.GBP_0p10, 20);
    JVVM.registerValidCoin(TrustedCoin.GBP_0p20, 20);
    JVVM.registerValidCoin(TrustedCoin.GBP_0p50, 20);
    JVVM.registerValidCoin(TrustedCoin.GBP_1p00, 10);
  }

  public InputStream mockInput(String scenario) {
    byte[] scenarioBytes = scenario.getBytes();
    InputStream mockedInputStream = new ByteArrayInputStream(scenarioBytes);
    return mockedInputStream;
  }

  //-------------------------------------------------------------- T E S T S -----------------------------------
  @Test(expected = InsufficientFundsException.class)
  public void TC1_declineIfNotPaid() throws OutOfStockException, InsufficientFundsException {
    try {
      fullJVVMSetup();// prepare machine
// No money inserted, attempt to dispense should result in an Exception
      JVVM.dispenseSnackQuantityAndReturnChange(0, 1);// Exception is OK
      fail(" machine dispensed item not paid for");//FAIL exception not present
    } catch (InsufficientFundsException ex) {
      if (!JVVM.getSnacksOutputOpening().isEmpty()) {
        fail("machine dispensed item not paid for after throwing exception");//FAIL exception thrown, but snack wa dispensed
        return;
      }
      throw ex;//Exception is OK 
    }
  }

  @Test(expected = OutOfStockException.class)
  public void TC2_declineIfOutOfStock() throws OutOfStockException, InsufficientFundsException {
//one snack quantity capacity
    JVVM = new JavaVirtualVendingMachine(observableCoinOutput, observableSnackOutput, 50, 1);
    coinOnlyJVVMSetup();
//cheap dummy snack QTY  = 1 
    JVVM.registerSnack(new Snack("dummy", 5), 45);

// insert money, dispense 1 snack            QTY = 1-1 = 0           OK
    JVVM.insertCoin(TrustedCoin.GBP_1p00);
    JVVM.dispenseSnackQuantityAndReturnChange(0, 1);
// insert money, dispense 1 snack            QTY = 0-1 =-1          ERROR EXPECTED
    JVVM.insertCoin(TrustedCoin.GBP_1p00);
    JVVM.dispenseSnackQuantityAndReturnChange(0, 1); // this line should throw OutOfStockException
// if ERROR not occurred:
    fail(" machine did not declined dispensing item which is out of stock");
  }

  @Test
  public void TC3_display_OutOfStock_and_decline_proceeding() throws OperationCancelledByUserException, InsufficientFundsException, OutOfStockException, IOException {

    PrintStream mockStdOut = new PrintStream(mockStdOutBytes);
    JVVM = new JavaVirtualVendingMachine(observableCoinOutput, observableSnackOutput, 50, 1);
    coinOnlyJVVMSetup();
    JVVM.registerSnack(new Snack("dummy", 5), 45);

    JVVM.insertCoin(TrustedCoin.GBP_0p05);
    JVVM.dispenseSnackQuantityAndReturnChange(0, 1);
    JVVM.insertCoin(TrustedCoin.GBP_0p05);
    InputStream mockedInputStream = mockInput("1\n1");
    TextUserInterface tui = new TextUserInterface(mockedInputStream, mockStdOut, JVVM);

    tui.SnackSelectionMenu();//function should return on outofstock selection
    mockedInputStream.close();
    //System.out.println(mockStdOutBytes.toString());
    assertTrue(mockStdOutBytes.toString().trim().endsWith("*** OUT OF STOCK ***"));//if this is true function displayed msg and returned
  }

  @Test
  public void TC4_reject_Invalid_Denominations() throws InsufficientFundsException, OutOfStockException, IOException {
    PrintStream mockStdOut = new PrintStream(mockStdOutBytes);
    JVVM = new JavaVirtualVendingMachine(observableCoinOutput, observableSnackOutput, 50, 10);//three items only
    coinOnlyJVVMSetup();
    JVVM.registerSnack(new Snack("dummy", 120), 45);
    List<Coin> invalidCoins = new ArrayList();
    invalidCoins.add(new Coin(0));
    invalidCoins.add(new Coin(-1));
    invalidCoins.add(new Coin(200));

    invalidCoins.forEach(JVVM::insertCoin);//functional method referencing
    assertTrue(observableCoinOutput.containsAll(invalidCoins));

  }

  @Test
  public void TC5_testDispense_If_AllOK() throws IOException, OperationCancelledByUserException {
    fullJVVMSetup();
    InputStream mockedInputStream = mockInput("\n1\n10\n1\n1\n1\n1\n1\n1\n1\n0.50\n");
    TextUserInterface tui = new TextUserInterface(mockedInputStream, System.out, JVVM);
    System.out.println("Starting!");

    String expResult = "CrispsCrispsCrispsCrispsCrispsCrispsCrispsCrispsCrispsCrisps";
    final StringBuilder resultSB = new StringBuilder();

    observableSnackOutput.addObserver((String OutputName, Object valuable) -> {
      resultSB.append(valuable.toString());
    });
//
//    System.out.print(tui.ask("ddssd").forString());
//    System.out.print(tui.ask("ddssd").forString());
//    System.out.print(tui.ask("ddssd").forString());
    // mockedInputStream.close();
    //System.out.println(reader.readLine());
    tui.SnackSelectionMenu();
    System.out.println(observableSnackOutput);
    //TextUserInterface instance = null;
    //InputValidator result = instance.ask(question);
    assertEquals(expResult, resultSB.toString());
    // TODO review the generated test code and remove the default call to fail.
  }

}
