 
package javavendingmachinejvm;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * Observer Design Pattern / Callback Design Pattern attached to Vending Machine
 * Virtual Outputs , allowing to decouple code executed in TextUserInterface
 * Module, and avoid polling the Machine Outputs for changes. This together with
 * re-routing object storage in JavaVending Machine helps to emulate real
 * vending machine experience, where machine may dispense invalid coin back to
 * user at any time and after performing internal operation machine finally
 * dispenses item. This ArrayList can be used in VendingMachine.
 *
 *
 * @author Osmiogrzesznik at TenThousandsSteps.org
 * @param <ItemType>
 */
public class ObservableOutputList<ItemType> extends
        ArrayList<ItemType> {

  //Observable {
  private String OutputName;

  /**
   *
   * @param OutputName
   */
  public ObservableOutputList(String OutputName) {
    this.OutputName = OutputName;
  }

  private ArrayList<IOutputObserver> observers = new ArrayList();

  /**
   * sends the message containing the last item and this Output's name to all
   * Observers by invoking their update method
   */
  public void notifyObservers() {
    if (!super.isEmpty()) {//send msg only if there is last item
      ItemType lastItem = super.get(super.size() - 1);

      for (IOutputObserver observer : observers) {
        observer.update(OutputName, lastItem);
      }
    }
  }

  /**
   * adds Observer to this Object broadcast list
   *
   * @param obsrvr
   */
  public void addObserver(IOutputObserver obsrvr) {
    observers.add(obsrvr);
  }

  /**
   * adds item of <code>ItemType</code> to this Collection, and notifies all
   * Observers about the change;
   *
   * @param item
   * @return true on success
   */
  public boolean add(ItemType item) {

    boolean output = super.add(item);
    if (output) {
      notifyObservers();
    }
    return output;
  }

}
