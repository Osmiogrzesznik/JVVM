 
package javavendingmachinejvm;

import java.io.PrintStream;

/**
 * Observer for ObservableOutputList , displays text message(sound) on receiving
 * update about observable
 *
 * @author Osmiogrzesznik at TenThousandsSteps.org
 * @param <T>
 */
//clink
public class OutputObserver implements IOutputObserver {

  private final String sound;
  private final int tabs;
  private final PrintStream out;

  /**
   * Message to display
   *
   * @param Sound
   */
  public OutputObserver(String Sound, int tabs, PrintStream out) {
    this.sound = Sound;
    this.tabs = tabs;
    this.out = out;
  }

  /**
   * @param OutputName observable identifier
   * @param valuable new object appeared in observable
   */
  @Override
  public void update(String OutputName, Object valuable) {
    StringBuilder sb = new StringBuilder("\t");
    sb.append(sound);
    for (int i = 0; i <= tabs; i++) {
      sb.append("\t");
    }

    sb.append(String.format("----%s---- landed in %s.", valuable, OutputName));
    out.println(sb);
  }

}
