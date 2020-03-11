 
package javavendingmachinejvm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Functionally - A distant relative to Scanner, of similar purpose with dynamic
 * approach. Provides set of recursive utility methods for obtaining constrained
 * user input data, parses primitive types and strings using static type parsing
 * methods. Exposes intuitive, human readable usage interface which follows the
 * pattern:
 * <code>ask(String Question, [optional String help]).forDataTypeFollowingGivenConstrains([constrains]);</code>
 * Allows outer invokers to arrange logic flow control around expected input,
 * and to avoid cluttered/nested if statements and try/catch blocks.
 * encapsulates:
 * <ul><li>process of IOException and NumberFormatException handling<li/>
 * <li>validating required input,</li>
 * <li>looping/prompting multiple times for valid input</li>
 * <li>parsing the data</li>
 * <li> reacting to common commands valid on each sub-menu: help, cancel,
 * poweruserModeCode</li>
 *
 * Throws operationCancelledByUserException providing cancel Component of
 * TextUserInterface, tightly coupled, * For example, this code allows a user to
 * read a number from System.in:
 *
 * @author Osmiogrzesznik at TenThousandsSteps.org
 */
public class InputValidator {

  private int POWER_USER_CODE;

  /**
   * configurable input command value that should result in throwing an
   * operationCancelledByUserException
   */
  public String EXITCMD;

  /**
   * configurable input command value that should result in displaying a menu
   * help
   */
  public String HELPCMD;

  /**
   * composes general help applicable to all calls to ask method
   *
   * @return
   */
  public String getGENERALHELP() {
    return "\n"
            + EXITCMD + " - cancel operation\n"
            + HELPCMD + " - prints list of all available commands/options or what input is expected.\n";
  }
  private String ongoingQuestion;
  private String recentUserInput;
  private String ongoingHelp = "";
  private PrintStream out;
  private BufferedReader in;

  /**
   * Creates an instance with configured help/exit commands , output to print
   * messages, input , and special poweruser code
   *
   * @param in input Buffered Reader treated as source of User Input
   * @param out PrintStream to publish messages to
   * @param PowerUserCode command that should result in
   * OperationCancelledByUserException with PowerUser privilege
   * @param ExitCmd command that should result in
   * OperationCancelledByUserException
   * @param HelpCmd command that should result in displaying help text
   */
  public InputValidator(BufferedReader in, PrintStream out, int PowerUserCode, String ExitCmd, String HelpCmd) {
    this.out = out;
    this.in = in;
    this.EXITCMD = ExitCmd;
    this.HELPCMD = HelpCmd;
    this.POWER_USER_CODE = PowerUserCode;
  }

  /**
   * Initiation of question cycle. Chainable method that does not return input
   * itself, instead storing it internally for subsequent validation by
   * finalizing question with method specifying expected/constrained answer.
   *
   * @param newQuestion String presented to user before requesting input
   * @param newHelp String presented to user upon entering <code>HELPCMD</code>.
   * Should provide sufficient indication to user what is expected or what may
   * be possible outcome of corresponding input
   * @return this object. Allows to finish the question definition , by adding
   * chained constraint method ( chaining pattern occurs when methods return an
   * instance of an object allowing for consecutive invocations i.e.
   * <code>ask("How Many").forIntegerBetween(1,10);</code>)
   * @throws OperationCancelledByUserException if user entered command
   * corresponding to cancel action
   */
  public InputValidator ask(String newQuestion, String newHelp) throws OperationCancelledByUserException {
    ongoingHelp = newHelp;
    ongoingQuestion = newQuestion;
    //say(question);
    out.print(newQuestion + " : ");
    try {

      recentUserInput = in.readLine();

    } catch (IOException ex) {
      say("Didn't understand " + forString());
      return ask(newQuestion, newHelp);
    }

    return checkForSpecialCommands();
  }

  /**
   * Overloaded Initiation of question cycle with Question parameter only.
   * Facilitates usage and allows simple questions of not complex nature.
   *
   * @param question String presented to user before requesting input
   * @return this object allowing to finish the question definition , adding
   * chained constraint method ( chaining pattern occurs when methods return an
   * instance of an object allowing for consecutive invocations i.e.
   * <code>a.doA().doB().finalize()</code>
   * @throws OperationCancelledByUserException if user entered command
   * corresponding to cancel action
   */
  public InputValidator ask(String question) throws OperationCancelledByUserException {
    return ask(question, "");
  }

  /**
   * Finalising method - parses input as an integer. If NumberFormatException
   * occurs user is presented with onExceptionMsg, then method recursively
   * repeats the question until success.
   *
   * @param onExceptionMsg String that should clearly indicate what input from
   * user is desired in context that question refers to.
   * @return Integer parsed answer
   * @throws OperationCancelledByUserException if during recursion user entered
   * command corresponding to cancel action
   */
  public int forInteger(String onExceptionMsg) throws OperationCancelledByUserException {
    int parsedIntAnswer;
    try {
      parsedIntAnswer = Integer.parseInt(recentUserInput);
    } catch (NumberFormatException ex) {
      say(onExceptionMsg);
      return ask(ongoingQuestion, ongoingHelp).forInteger(onExceptionMsg);
    }
    return parsedIntAnswer;
  }

  /**
   * Finalising method - parses input as an integer. Provides simplified
   * Overload alternative , where there is no need for additional clarification.
   *
   * @return parsed integer answer
   * @throws OperationCancelledByUserException if during recursion user entered
   * command corresponding to cancel action
   */
  public int forInteger() throws OperationCancelledByUserException {
    return forInteger("This is not a valid integer number. Please provide integer number.");
  }

  /**
   * Finalising method - parses input as an integer. If NumberFormatException
   * occurs, or answer does not meet specified min/max criteria user is
   * presented with onInvalidAnswer String, then method recursively repeats the
   * question until success.
   *
   * @param min minimal valid input value constraint
   * @param max maximal valid input value constraint
   * @param onInvalidAnswer String that should clearly indicate what input from
   * user is desired in context that question refers to.
   * @return Integer parsed answer, falling in range specified by min and max
   * parameters inclusive.
   * @throws OperationCancelledByUserException if during recursion user entered
   * command corresponding to cancel action
   */
  public int forIntegerBetween(int min, int max, String onInvalidAnswer) throws OperationCancelledByUserException {
    int parsedIntAnswer = forInteger(onInvalidAnswer);
    if (parsedIntAnswer > max || parsedIntAnswer < min) {
      say(onInvalidAnswer);
      say("Please Enter integer in range from %d to %d.", min, max);
      return ask(ongoingQuestion, ongoingHelp).forIntegerBetween(min, max, onInvalidAnswer);
    } else {
      return parsedIntAnswer;
    }
  }

  /**
   * Finalising method - parses input as a double. If NumberFormatException
   * occurs user is presented with onExceptionMsg then method recursively
   * repeats the question until success.
   *
   * @param onExceptionMsg String that should clearly indicate what input from
   * user is desired in context that question refers to.
   * @return double parsed answer
   * @throws OperationCancelledByUserException if during recursion user entered
   * command corresponding to cancel action
   */
  public double forDouble(String onExceptionMsg) throws OperationCancelledByUserException {
    double parsedDoubleAnswer;
    try {
      parsedDoubleAnswer = Double.parseDouble(recentUserInput);
    } catch (NumberFormatException ex) {
      say(onExceptionMsg);
      return ask(ongoingQuestion, ongoingHelp).forDouble(onExceptionMsg);
    }
    return parsedDoubleAnswer;

  }

  /**
   * Finalising method - returns a String if the string matches case
   * insensitively an element of String array allowedValues. If answer does not
   * meet specified criteria user is presented with onInvalidAnswer String, then
   * method recursively repeats the question until success. May serve as command
   * based menu
   *
   * @param onInvalidAnswer String that should clearly indicate what input from
   * user is desired in context that question refers to.
   * @param allowedValues
   * @return String meeting the criteria
   * @throws OperationCancelledByUserException
   */
  public String forStringInArray(String onInvalidAnswer, String[] allowedValues) throws OperationCancelledByUserException {
    String answer = forString();
    for (String s : allowedValues) {
      if (s.equalsIgnoreCase(answer)) {
        return answer;
      }
    }
    //if we reached here it means that input is not in allowedValues
    say(onInvalidAnswer);
    return ask(ongoingQuestion, ongoingHelp).forStringInArray(onInvalidAnswer, allowedValues);
  }

  /**
   * Finalising method - accepts variable number of arguments returns a String
   * if the string matches case insensitively one of String arguments passed
   * after onInvalidAnswer. If answer does not meet specified criteria user is
   * presented with onInvalidAnswer String, then method recursively repeats the
   * question until success. Facilitates usage when amount of options is small
   *
   * @param onInvalidAnswer String that should clearly indicate what input from
   * user is desired in context that question refers to.
   * @param allowedValues Variable amount of arguments to check string against.
   * @return String meeting the criteria
   * @throws OperationCancelledByUserException
   */
  public String forStringIn(String onInvalidAnswer, String... allowedValues) throws OperationCancelledByUserException {
    return forStringInArray(onInvalidAnswer, allowedValues);
  }

  /**
   * Finalizing Method - expects user to confirm question by entering Y or N,
   * returning respective boolean value. Utilises other methods to narrow answer
   * to case insesitive letter
   *
   * @return boolean true if answer is yes/y , and false if answer is no/n
   * @throws OperationCancelledByUserException
   */
  public boolean forConfirmation() throws OperationCancelledByUserException {
    String answer = forStringIn("Enter just Yes or No (case insensitive)", "Y", "N", "Yes", "No");

    //Now, when set containing only above options possible
    //is regarded, simple check for first Letter is sufficient.
    boolean booleanAnswer = answer.toUpperCase().startsWith("Y") ? true : false;
    return booleanAnswer;
  }
  //replace for with something else to avoid confusion with forEach ?

  /**
   * Finalising method - simply returns unparsed line of user input
   *
   * @return
   */
  public String forString() {
    return recentUserInput;
  }

  /**
   * prints message to output Printstream, usually User Interface.
   *
   * @param msg message to print
   */
  private void say(String msg) {
    out.println(" " + msg);
  }

  /**
   * prints line of formatted message to output PrintStream, usually User
   * Interface. Delegates formatting to out PrintStream formatting method,
   * adding line break.
   *
   * @param objects parameters to insert into formatString
   * @param formatString String to format to objects
   */
  private void say(String formatString, Object... objects) {
    out.printf("\n" + formatString, objects);
  }

  /**
   * Pipe-like internal method checking answer against special commands.
   * Originates OperationCancelledByUserException if answer is one of the
   * special commands.
   *
   * @return this object.
   * @throws OperationCancelledByUserException if
   */
  private InputValidator checkForSpecialCommands() throws OperationCancelledByUserException {

    if (HELPCMD.equalsIgnoreCase(recentUserInput)) {
      say(ongoingHelp + getGENERALHELP());
      return ask(ongoingQuestion, ongoingHelp);
    } else if (EXITCMD.equalsIgnoreCase(recentUserInput)) {
      throw new OperationCancelledByUserException();
    } else if (isPowerUser()) {
      throw new OperationCancelledByUserException(POWER_USER_CODE);
    }
    return this;
  }

  /**
   * Compares Integer parsed input with PowerUser code and returns boolean. If
   * input is not an integer and NumberFormatException is thrown returns false
   *
   * @return boolean indicating if the entered input is an power user command
   */
  private boolean isPowerUser() {
    try {
      return Integer.parseInt(recentUserInput) == POWER_USER_CODE;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

}
