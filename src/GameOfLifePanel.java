import javax.swing.*;
import java.awt.*;

/**
 * Shea Polansky
 * GameOfLifePanel: Contains a GameDrawerPanel and its associated scroll bars
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

  /**
   * Plays/pauses the Life simulation.
   * If it is currently running, it will be stopped.
   * If it is currently stopped, it is started with the specified number of threads.
   * @param threadCount the number of threads to use to simulate Life
   */
  public void playPause(int threadCount)
  {
    if (isRunning) gameDrawerPanel.stop();
    else gameDrawerPanel.play(threadCount);
    isRunning = !isRunning;
  }

  /**
   * Resets the simulation to a new random state
   */
  public void reset()
  {
    new Thread(() -> gameDrawerPanel.resetToRandomState(true)).start();
  }

  /**
   * instructs the simulation to advance a single step
   * @param threadCount the number of threads to use to simulate Life
   */
  public void advanceFrame(int threadCount)
  {
    gameDrawerPanel.advanceFrame(threadCount);
  }

  /**
   * loads a game state from a preset
   * @param preset the preset to load
   */
  public void load(GameStatePreset preset)
  {
    gameDrawerPanel.loadState(preset);
  }
}
