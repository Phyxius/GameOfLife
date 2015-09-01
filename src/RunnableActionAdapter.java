import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Shea Polansky
 * Class name and description goes here
 */
public class RunnableActionAdapter extends AbstractAction
{
  private final Runnable runnable;

  public RunnableActionAdapter(Runnable runnable)
  {
    this.runnable = runnable;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    runnable.run();
  }
}
