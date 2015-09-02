import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.CyclicBarrier;

/**
 * Shea Polansky
 * GameDrawerPanel: draws a Life simulation, manages updates for the simulation, and provides
 * an external interface to control the simulation
 */
public class GameDrawerPanel extends JPanel
{
  public static final int GAME_SIZE = 10000;
  public static final int MIN_GRID_SIZE = 5;
  public static final int MIN_PIXEL_SIZE = 1;
  public static final int MAX_PIXEL_SIZE = 50;
  private int pixelSize = 20, pixelOffsetX = 0, pixelOffsetY = 0;
  private boolean[][] gameState = new boolean[GAME_SIZE + 2][GAME_SIZE + 2]; //account for border cells, [x][y]
  private boolean[][] nextGameState = new boolean[gameState.length][gameState[0].length];
  private boolean advanceSingleFrame = false;
  private final JScrollBar horizontalScrollBar, verticalScrollBar;
  private final Runnable operationCompleteCallback;
  private CyclicBarrier synchronizationBarrier;
  private UpdateThread[] updateThreads;

  public GameDrawerPanel(JScrollBar horizontalScrollBar, JScrollBar verticalScrollBar, Runnable operationCompleteCallback)
  {
    MouseHandler m = new MouseHandler();
    addMouseListener(m);
    addMouseMotionListener(m);
    addMouseWheelListener(m);
    addComponentListener(new ResizeHandler());
    this.horizontalScrollBar = horizontalScrollBar;
    this.verticalScrollBar = verticalScrollBar;
    this.operationCompleteCallback = operationCompleteCallback;
    getActionMap().put("Scroll Up", new RunnableActionAdapter(() -> scrollRelative(0, -pixelSize)));
    getInputMap().put(KeyStroke.getKeyStroke("UP"), "Scroll Up");
    getActionMap().put("Scroll Down", new RunnableActionAdapter(() -> scrollRelative(0, pixelSize)));
    getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "Scroll Down");
    getActionMap().put("Scroll Left", new RunnableActionAdapter(() -> scrollRelative(-pixelSize, 0)));
    getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "Scroll Left");
    getActionMap().put("Scroll Right", new RunnableActionAdapter(() -> scrollRelative(pixelSize, 0)));
    getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "Scroll Right");
    getActionMap().put("Page Up", new RunnableActionAdapter(() -> scrollRelative(0, -getHeight())));
    getInputMap().put(KeyStroke.getKeyStroke("PAGE_UP"), "Page Up");
    getActionMap().put("Page Down", new RunnableActionAdapter(() -> scrollRelative(0, getHeight())));
    getInputMap().put(KeyStroke.getKeyStroke("PAGE_DOWN"), "Page Down");
    updateScrollBars();
    resetToRandomState(false);
  }

  /**
   * Sets the scroll bars to have the correct position, visible amount, min, and max
   */
  private void updateScrollBars()
  {
    horizontalScrollBar.setValues(pixelOffsetX, getVisibleGridWidth(), 0, GAME_SIZE - getVisibleGridWidth());
    verticalScrollBar.setValues(pixelOffsetY, getVisibleGridHeight(), 0, GAME_SIZE - getVisibleGridHeight());
  }

  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension(800, 600);
  }

  /**
   * Resets the game state to a random one, with each cell having a 50/50 chance to be alive/dead
   * @param triggerCallback If true, the operationCompleteCallback will be called after the reset is complete
   */
  public void resetToRandomState(boolean triggerCallback)
  {
    for (int i = 1; i <= GAME_SIZE; i++)
    {
      for (int j = 1; j <= GAME_SIZE; j++)
      {
        gameState[i][j] = Util.RANDOM.nextBoolean();
      }
    }
    repaint();
    if (triggerCallback) operationCompleteCallback.run();
  }

  /**
   * Kills every cell, leaving with a blank slate
   */
  private void resetToBlankState()
  {
    for (int i = 1; i <= GAME_SIZE; i++)
    {
      for (int j = 1; j <= GAME_SIZE; j++)
      {
        gameState[i][j] = false;
      }
    }
    repaint();
  }

  /**
   * Loads the given preset's game state, initial viewport, and pixel size
   * @param preset the preset to load
   */
  public void loadState(GameStatePreset preset)
  {
    resetToBlankState();
    preset.loadState(gameState);
    scrollAbsolute(preset.getInitialViewport().x, preset.getInitialViewport().y);
    setZoom(preset.getInitialPixelSize());
  }

  @Override
  protected void paintComponent(Graphics graphics)
  {
    super.paintComponent(graphics);
    graphics.clearRect(0, 0, getWidth(), getHeight());
    graphics.setColor(Color.black);
    for (int i = -1; i <= getVisibleGridWidth() + 1; i++)
    {
      for (int j = -1; j <= getVisibleGridHeight() + 1; j++)
      {
        int curGridX = i + pixelOffsetX / pixelSize;
        int curGridY = j + pixelOffsetY / pixelSize;
        if (curGridX < 0 || curGridY < 0) continue;
        if (gameState[curGridX + 1][curGridY + 1])
        {
          int pixelX = i * pixelSize - pixelOffsetX % pixelSize;
          int pixelY = j * pixelSize - pixelOffsetY % pixelSize;
          if (pixelSize == 1) graphics.drawLine(pixelX, pixelY, pixelX, pixelY); //should be slightly faster than fillRect
          else graphics.fillRect(pixelX, pixelY, pixelSize, pixelSize);
        }
      }
    }
    if (pixelSize >= MIN_GRID_SIZE)
    {
      graphics.setColor(Color.gray);
      for (int i = 0; i <= getVisibleGridWidth() + 1; i++)
      {
        int pixelX = i * pixelSize - pixelOffsetX % pixelSize;
        graphics.drawLine(pixelX, 0, pixelX, getHeight());
      }
      for (int i = 0; i <= getVisibleGridHeight() + 1; i ++)
      {
        int pixelY = i * pixelSize - pixelOffsetY % pixelSize;
        graphics.drawLine(0, pixelY, getWidth(), pixelY);
      }
    }
  }

  /**
   * Callback for handling swapping of next/last game states, repaints, and (if necessary) automatic stopping of the simulation
   */
  private void onUpdateComplete()
  {
    if (synchronizationBarrier == null) return;
    boolean[][] temp = gameState;
    gameState = nextGameState;
    nextGameState = temp;
    repaint(0);
    if (advanceSingleFrame)
    {
      stop();
      operationCompleteCallback.run();
    }
  }

  /**
   * Starts the simulation using the given thread count
   * Does nothing if the simulation is currently running
   * @param threadCount the number of threads to use
   */
  public void play(int threadCount)
  {
    if (synchronizationBarrier != null) stop();
    synchronizationBarrier = new CyclicBarrier(threadCount, this::onUpdateComplete);
    updateThreads = new UpdateThread[threadCount];
    for (int i = 0; i < updateThreads.length; i++)
    {
      updateThreads[i] = new UpdateThread(gameState, nextGameState, threadCount, i, synchronizationBarrier);
      updateThreads[i].start();
    }
  }

  /**
   * Stops the simulation, signalling the threads to stop at their earliest convenience.
   * Does nothing if the simulation is not running.
   */
  public void stop()
  {
    if (synchronizationBarrier == null) return;
    for (UpdateThread thread : updateThreads)
    {
      thread.end();
    }
    synchronizationBarrier.reset();
    updateThreads = null;
    synchronizationBarrier = null;
    advanceSingleFrame = false;
  }

  /**
   * Runs the simulation for a single frame using the specified number of threads
   * @param threadCount the number of threads to use
   */
  public void advanceFrame(int threadCount)
  {
    if (synchronizationBarrier != null) return;
    advanceSingleFrame = true;
    play(threadCount);
  }

  /**
   * @return the height of the visible portion of the grid
   */
  private int getVisibleGridHeight()
  {
    return getHeight() / pixelSize;
  }

  /**
   * @return the width of the visible portion of the grid
   */
  private int getVisibleGridWidth()
  {
    return getWidth() / pixelSize;
  }

  /**
   * @return the dimensions of the visible portion of the grid
   */
  public Dimension getScreenGridSize()
  {
    return new Dimension(getVisibleGridWidth(), getVisibleGridHeight());
  }

  /**
   * Scrolls the x and y offsets by the specified amount
   * @param xAmount the amount to move in the x direction
   * @param yAmount the amount to move in the y direction
   */
  public void scrollRelative(int xAmount, int yAmount)
  {
    pixelOffsetX = Util.clampInteger(pixelOffsetX + xAmount, 0, GAME_SIZE - getScreenGridSize().width);
    pixelOffsetY = Util.clampInteger(pixelOffsetY + yAmount, 0, GAME_SIZE - getScreenGridSize().height);
    updateScrollBars();
    repaint();
  }

  /**
   * Sets the screen position to the specified position
   * @param x the x to set
   * @param y the y to set
   */
  public void scrollAbsolute(int x, int y)
  {
    pixelOffsetX = x;
    pixelOffsetY = y;
    updateScrollBars();
    repaint();
  }

  /**
   * Sets the horizontal position to the specified value
   * @param x the value to set
   */
  public void scrollXAbsolute(int x)
  {
    pixelOffsetX = x;
    repaint();
  }

  /**
   * Sets the vertical position to the specified value
   * @param y the position to set
   */
  public void scrollYAbsolute(int y)
  {
    pixelOffsetY = y;
    repaint();
  }

  /**
   * Converts a point in screen coordinates to the closest grid coordinate
   * @param p the point to convert
   * @return the converted point
   */
  public Point screenToGridCoordinates(Point p)
  {
    return new Point((p.x + pixelOffsetX) / pixelSize, (p.y + pixelOffsetY) / pixelSize);
  }

  /**
   * Sets the zoom level to the specified size, clamped to be between MIN_PIXEL_SIZE and MAX_PIXEL_SIZE
   * @param newSize new size of a single pixel
   */
  public void setZoom(int newSize)
  {
    pixelSize = Util.clampInteger(newSize, MIN_PIXEL_SIZE, MAX_PIXEL_SIZE);
  }

  /**
   * Updates the scroll bar values when the window is resized
   */
  private class ResizeHandler extends ComponentAdapter
  {
    @Override
    public void componentResized(ComponentEvent componentEvent)
    {
      updateScrollBars();
    }
  }

  /**
   * Handles mouse input (scrolling/zooming/cell toggling)
   */
  private class MouseHandler extends MouseInputAdapter
  {
    Point currentPoint = new Point();

    @Override
    public void mousePressed(MouseEvent mouseEvent)
    {
      currentPoint = mouseEvent.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent)
    {
      super.mouseReleased(mouseEvent);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
      int newSize = pixelSize + e.getWheelRotation();
      setZoom(newSize);
      updateScrollBars();
      repaint();
    }
    @Override
    public void mouseDragged(MouseEvent mouseEvent)
    {
      scrollRelative(currentPoint.x - mouseEvent.getX(), currentPoint.y - mouseEvent.getY());
      currentPoint = mouseEvent.getPoint();
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
      if (synchronizationBarrier != null) return;
      Point gridPoint = screenToGridCoordinates(e.getPoint());
      gameState[gridPoint.x + 1][gridPoint.y + 1] = !gameState[gridPoint.x + 1][gridPoint.y + 1];
      repaint();
    }
  }
}
