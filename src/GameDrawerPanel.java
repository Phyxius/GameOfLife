import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Shea Polansky
 * Class name and description goes here
 */
public class GameDrawerPanel extends JPanel
{
  public static final int GAME_SIZE = 10000;
  public static final int MIN_GRID_SIZE = 5;
  private int pixelSize = 20, pixelOffsetX = 0, pixelOffsetY = 0;
  private final boolean[][] gameState = new boolean[GAME_SIZE + 2][GAME_SIZE + 2]; //account for border cells
  private final JScrollBar horizontalScrollBar, verticalScrollBar;
  public GameDrawerPanel(JScrollBar horizontalScrollBar, JScrollBar verticalScrollBar)
  {
    MouseHandler m = new MouseHandler();
    addMouseListener(m);
    addMouseMotionListener(m);
    addMouseWheelListener(m);
    addComponentListener(new ResizeHandler());
    this.horizontalScrollBar = horizontalScrollBar;
    this.verticalScrollBar = verticalScrollBar;
    updateScrollBars();
    resetToRandomState();
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

  public void resetToRandomState()
  {
    for (int i = 1; i <= GAME_SIZE; i++)
    {
      for (int j = 1; j <= GAME_SIZE; j++)
      {
        gameState[i][j] = Util.RANDOM.nextBoolean();
      }
    }
    repaint();
  }

  @Override
  protected void paintComponent(Graphics graphics)
  {
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
      pixelSize = Util.clampInteger(pixelSize + e.getWheelRotation(), 1, 40);
      updateScrollBars();
      repaint();
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent)
    {
      scrollRelative(currentPoint.x - mouseEvent.getX(), currentPoint.y - mouseEvent.getY());
      currentPoint = mouseEvent.getPoint();
    }
  }
}
