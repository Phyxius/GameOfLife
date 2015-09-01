import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.CyclicBarrier;

/**
 * Shea Polansky
 * Class name and description goes here
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

  private void onUpdateComplete()
  {
    if (synchronizationBarrier == null) return;
    boolean[][] temp = gameState;
    gameState = nextGameState;
    nextGameState = temp;
    System.out.println("Update");
    repaint(0);
    if (advanceSingleFrame)
    {
      stop();
      operationCompleteCallback.run();
    }
  }

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

  public void advanceFrame(int threadCount)
  {
    if (synchronizationBarrier != null) return;
    advanceSingleFrame = true;
    play(threadCount);
  }

  private int getVisibleGridHeight()
  {
    return getHeight() / pixelSize;
  }

  private int getVisibleGridWidth()
  {
    return getWidth() / pixelSize;
  }

  public Dimension getScreenGridSize()
  {
    return new Dimension(getVisibleGridWidth(), getVisibleGridHeight());
  }

  public void scrollRelative(int xAmount, int yAmount)
  {
    pixelOffsetX = Util.clampInteger(pixelOffsetX + xAmount, 0, GAME_SIZE - getScreenGridSize().width);
    pixelOffsetY = Util.clampInteger(pixelOffsetY + yAmount, 0, GAME_SIZE - getScreenGridSize().height);
    updateScrollBars();
    repaint();
  }

  public void scrollAbsolute(int x, int y)
  {
    pixelOffsetX = x;
    pixelOffsetY = y;
    updateScrollBars();
    repaint();
  }

  public void scrollXAbsolute(int x)
  {
    pixelOffsetX = x;
    repaint();
  }

  public void scrollYAbsolute(int y)
  {
    pixelOffsetY = y;
    repaint();
  }

  public Point screenToGridCoordinates(Point p)
  {
    return new Point((p.x + pixelOffsetX) / pixelSize, (p.y + pixelOffsetY) / pixelSize);
  }

  public void setZoom(int newSize)
  {
    pixelSize = Util.clampInteger(newSize, MIN_PIXEL_SIZE, MAX_PIXEL_SIZE);
  }

  private class ResizeHandler extends ComponentAdapter
  {
    @Override
    public void componentResized(ComponentEvent componentEvent)
    {
      updateScrollBars();
    }
  }

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
