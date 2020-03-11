 
package javavendingmachinejvm;

/**
 *
 * @author Osmiogrzesznik <Osmiogrzesznik.name at TenThousandsSteps.org>
 */
public interface IOutputObserver {

  /**
   * @param OutputName observable identifier
   * @param valuable new object appeared in observable
   */
  void update(String OutputName, Object valuable);

}
