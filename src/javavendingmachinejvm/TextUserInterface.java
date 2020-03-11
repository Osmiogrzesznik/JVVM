 
package javavendingmachinejvm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Osmiogrzesznik at TenThousandsSteps.org
 */
class TextUserInterface {

  private final BufferedReader in;
  private final PrintStream out;
  private final JavaVirtualVendingMachine JVVM;
  private final InputValidator inputValidator;
  private boolean isRunning;

  private final int POWER_USER_CODE = 10976;
  private final String DEFAULT_POWER_USER_PASS = "admin";
  public static final String PANEL_CANCEL_BUTTON_CODE = "Cancel";
  public static final String PANEL_OPTIONAL_BUTTON_CODE = "Help";
  public static final String GENERALHELP
          = "\n"
          + PANEL_CANCEL_BUTTON_CODE + " - Cancel Operation\n"
          + PANEL_OPTIONAL_BUTTON_CODE + " - Prints list of all available commands/options/ or what input is expected.\n";
  private String POWER_USER_PASS = DEFAULT_POWER_USER_PASS;

  TextUserInterface(InputStream PanelInputStream, PrintStream out, JavaVirtualVendingMachine vendingMachine) {
    this.out = out;
    this.in = new BufferedReader(new InputStreamReader(PanelInputStream));
//to provide real experience everything should be repgrogrammed to react to non-blocking inputStream
    this.JVVM = vendingMachine;
    inputValidator = new InputValidator(in, out, POWER_USER_CODE, PANEL_CANCEL_BUTTON_CODE, PANEL_OPTIONAL_BUTTON_CODE);
  }

  public void say(Object toSay) {
    out.println("\n" + toSay);
  }

  public void say(String toSay, Object... os) {
    say(String.format(toSay, os));
  }

  public InputValidator ask(String question) throws OperationCancelledByUserException {
    return ask(question, "");
  }

  public InputValidator ask(String question, String help) throws OperationCancelledByUserException {
    return inputValidator.ask(question, help);
  }

  private void promptForEnter() {
    try {
      in.readLine();
    } catch (IOException ex) {
      say(ex.toString());
    }
  }

  void setObservableOutputs(ObservableOutputList coinOutput, ObservableOutputList snackOutput) {
    OutputObserver coinOutputListener = new OutputObserver("__｡O◯___ CLINK!!!", 1, out);
    OutputObserver snackOutputListener = new OutputObserver("   (づ｡◕‿‿◕｡)づ    RATTLE!!!", 2, out);
    coinOutput.addObserver(coinOutputListener);
    snackOutput.addObserver(snackOutputListener);
  }

  public void start() {
    isRunning = true;
    say("At Any time just type:" + GENERALHELP);
    say("Machine has been set up. Please Press Enter to start dispensing");
    promptForEnter();
    while (isRunning) {

      try {

        SnackSelectionMenu();

      } catch (OperationCancelledByUserException exception) {
        if (exception.issuedByPowerUser) {
          PowerUserValidation();
        }
      } catch (IndexOutOfBoundsException ex) {
        say("Still somehow you managed to select non-existing snack. There is no snack within snackRegister: %s", ex.getMessage());
      } finally {
        JVVM.dispenseChange();
      }
    }
  }

//??? maybe good
  public void SnackSelectionMenu() throws OperationCancelledByUserException {
    String snackTable = stringifyItemTable();
    String helpnote = snackTable + "sorry, There is no snack with such code.";

    say(snackTable);

    int selectedID = ask("Please Enter Snack Code")
            .forIntegerBetween(1, JVVM.getSnackSelectionSize(), helpnote);

    --selectedID; // user is presented with 1 indexed selection. elements are 0 indexed. Decreasing for further calculation

    int maxQTY = JVVM.getSnackQuantity(selectedID);
    if (maxQTY < 1) {
      say("*** OUT OF STOCK ***");
      return;
    }
    QuantityMenu(selectedID, maxQTY);
  }

  private void QuantityMenu(int snackId, int maxQTY) throws OperationCancelledByUserException {//you can subclass this exception to create cancel order mechanism 
    //separately from exiting from application 
    int price = JVVM.getSnackPrice(snackId);
    String snackName = JVVM.getSnackName(snackId);
    int selQTY = 1;//initialise it to 1 so user doesn't have to type manually 1 if there is only one item in JVVM #UX

    if (maxQTY > 1) {
      String msg = String.format("There is %d %s items on stock.", maxQTY, snackName);

      selQTY = ask(msg + "How many would You like?")
              .forIntegerBetween(1, maxQTY, "Sorry, i cannot dispense this amount");
    }

    int requiredCredit = selQTY * price;

    if (requiredCredit > JVVM.getCredit()) {
      insertCoinsMenu(requiredCredit);
    }

    try {
      JVVM.dispenseSnackQuantityAndReturnChange(snackId, selQTY);
    } catch (InsufficientFundsException ex) {
      say("Still somehow you managed to give not enough money. Please insert " + requiredCredit);
      insertCoinsMenu(requiredCredit);
    } catch (OutOfStockException ex) {
      say("Sorry, full amount requested cannot be dispensed, this item ran out of stock");
    }

    Collection SnacksOutput = JVVM.getSnacksOutputOpening();
    Collection CoinsOutput = JVVM.getCoinsOutputOpening();

    say("Here's what in the snacks output:\n " + SnacksOutput);
    say("Here's what in the coins output:\n %s", CoinsOutput);

    say(" Enjoy your %s !!!", snackName);
    say("press Enter");
    promptForEnter();
    SnacksOutput.clear();
    CoinsOutput.clear();
  }

  public void insertCoinsMenu(int priceTotal) throws OperationCancelledByUserException {
    say("Okay, total cost calculated is %.2f", ((double) priceTotal) / 100);
    if (JVVM.getUseCorrectChangeState()) {
      say("sorry, i have no change.Please make sure you use correct change");
    }
    while (JVVM.getCredit() < priceTotal) {
      double d = ask("Insert coin")
              .forDouble("please enter coin denomination Manually. For example: \"0.05\" - inserts one 5p coin");

      int internal_int = (int) (d * 100);
      Coin c = new Coin(internal_int);
      JVVM.insertCoin(c);
    }

  }

  private void PowerUserValidation() {
    try {
      say("in order to access advanced admin options "
              + "open your Machine and connect the input device to usb maintenance port");

      String enteredPassword
              = ask("Password:").forString();

      if (POWER_USER_PASS.equals(enteredPassword)) {
        powerUserOptions();
      } else {
        say("ACCESS DENIED!!!");
      }
    } catch (OperationCancelledByUserException ex) {
      say("Going back to Standard User Menu");
    }
  }

  private void powerUserOptions() throws OperationCancelledByUserException {
    say(stringifyCoinTable());
    String cmdsHelp = "\n$ PASSWD - change Password"
            + "\n SHOW - show Income/Losses and Coins Storage"
            + "\n REGITEM - register new item"
            + "\n SYSEXIT - turn Machine off ";

    while (isRunning) {
      String userInput = ask("Enter command:", cmdsHelp).forString();
      switch (userInput.toUpperCase()) {
        case "$ PASSWD":
          changePassword();
          break;
        case "SHOW":
          say(stringifyCoinTable());
          break;

        case "SYSEXIT":
          say("This will turn off and reset the Java Virtual Vending Machine, resulting in data loss!!!");

          boolean confirmation
                  = ask("Are you Sure to exit the system (Y/N)?").forConfirmation();
          if (confirmation) {
            isRunning = false;
            return;
          }
          break;
        default:
          say("unrecognized command : %s", userInput);
//TODO : warn that password will be reset and prompt for confirmation
      }
    }
  }

  /**
   * @param newPOWER_USER_PASS the POWER_USER_PASS to set
   */
  private void setPOWER_USER_PASS(String newPOWER_USER_PASS) {
    this.POWER_USER_PASS = newPOWER_USER_PASS;
  }

  private void changePassword() throws OperationCancelledByUserException {
    setPOWER_USER_PASS(ask("Enter new Password:").forString());
  }

  public String stringifyItemTable() {

    List storageData = JVVM.getSnackRegister();
    StringBuilder sb = new StringBuilder();

    sb
            .append("+------------------------------------------+\n")
            .append("+       100000 Steps healthy snacks        +\n")
            .append("+------+-----------------+--------+--------+\n")
            .append("| CODE | SNACK           | PRICE  | QTY    |\n")
            .append("+------+-----------------+--------+--------+\n")
            .append(stringifyValuablesTable("| %-4d | %-15s | %-6.2f | %-6d |\n", storageData))
            .append("+------+-----------------+--------+--------+\n")
            .append(String.format(
                    "| CREDIT:           %-6.2f                 |\n", (float) JVVM.getCredit() / 100));
    if (JVVM.getUseCorrectChangeState()) {
      sb.append("|        *** USE CORRECT CHANGE ***        |\n");
    }
    sb.append("+------------------------------------------+");

    return sb.toString();
  }

  public String stringifyCoinTable() {
    List<ISlotController> storageData = JVVM.getCoinRegister();
    StringBuilder sb = new StringBuilder();
    int TotalCash = JVVM.getTotalCash();

    sb
            .append("+------------------------------------------+\n")
            .append("| CODE | DENOMINATION    | VALUE  | QTY    |\n")
            .append("+------+-----------------+--------+--------+\n")
            .append(stringifyValuablesTable("| %-4d | %-15s | %-6.2f | %-6d |\n", storageData))
            .append("+------+-----------------+--------+--------+\n")
            .append(String.format("+ CREDIT:           %-6.2f                 +\n", (float) JVVM.getCredit() / 100))
            .append("+------+-----------------+--------+--------+\n")
            .append(String.format("+ TOTAL CASH:       %-6.2f                 +\n", (float) TotalCash / 100))
            .append("+------+-----------------+--------+--------+\n")
            .append(String.format("+ PROFIT/LOSSES:    %-6.2f                 +\n", (float) JVVM.getTotalIncome() / 100))
            .append("+------------------------------------------+");
    return sb.toString();
  }

  public String stringifyValuablesTable(String formatString, List<ISlotController> storage) {
    //jvvm should have getSlotsLength;
    //this method jere should loop calling that method 
    // in order to decouple jvvm and tui

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < storage.size(); i++) {
      sb.append(String.format(formatString,
              i + 1,
              storage.get(i).getDescription(),
              (float) storage.get(i).getValue() / 100,
              storage.get(i).getQuantity()
      )
      );
    }
    return sb.toString();
  }

}
