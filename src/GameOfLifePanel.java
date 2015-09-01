import javax.swing.*;
import java.awt.*;

/**
 * Shea Polansky
 * Class name and description goes here
 */
public class GameOfLifePanel extends JPanel
{
  private final GameDrawerPanel gameDrawerPanel;
  private boolean isRunning = false;

  public GameOfLifePanel(Runnable operationCompleteCallback)
  {
    //All this GridBagLayout nonsense just to get a GridLayout with uneven cell sizes!
    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.fill = GridBagConstraints.BOTH;
    JScrollBar verticalScrollBar = new JScrollBar(Adjustable.VERTICAL);
    JScrollBar horizontalScrollBar = new JScrollBar(Adjustable.HORIZONTAL);
    gameDrawerPanel = new GameDrawerPanel(horizontalScrollBar, verticalScrollBar, operationCompleteCallback);
    horizontalScrollBar.addAdjustmentListener(e -> gameDrawerPanel.scrollXAbsolute(e.getValue()));
    verticalScrollBar.addAdjustmentListener(e -> gameDrawerPanel.scrollYAbsolute(e.getValue()));
    add(gameDrawerPanel, c);
    c.weighty = 0;
    c.weightx = 0;
    c.gridx = 1;
    c.gridwidth = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.VERTICAL;
    add(verticalScrollBar, c);
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.HORIZONTAL;
    add(horizontalScrollBar, c);
  }

  public void playPause(int threadCount)
  {
    if (isRunning) gameDrawerPanel.stop();
    else gameDrawerPanel.play(threadCount);
    isRunning = !isRunning;
  }

  public void reset()
  {
    new Thread(() -> gameDrawerPanel.resetToRandomState(true)).start();
  }

  public void advanceFrame(int threadCount)
  {
    gameDrawerPanel.advanceFrame(threadCount);
  }

  public void load(GameStatePreset preset)
  {
    gameDrawerPanel.loadState(preset);
  }
}
