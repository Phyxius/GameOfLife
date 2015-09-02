import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Shea Polansky
 * Helper class to transform Runnables (and therefore lambdas) into Actions
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
