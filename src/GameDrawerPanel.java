import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Shea Polansky
 * Class name and description goes here
 */
public class GameDrawerPanel extends JPanel
{
  public static final int GAME_SIZE = 10000;
  private int pixelSize = 20, pixelOffsetX = 0, pixelOffsetY = 0;
  private final boolean[][] gameState = new boolean[GAME_SIZE + 2][GAME_SIZE + 2]; //account for border cells
  public GameDrawerPanel()
  {
    MouseHandler m = new MouseHandler();
    addMouseListener(m);
    addMouseMotionListener(m);
    addMouseWheelListener(m);
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
    for (int i = -1; i <= getWidth() / pixelSize; i++)
    {
      for (int j = -1; j <= getHeight() / pixelSize; j++)
      {
        int curGridX = i + pixelOffsetX / pixelSize;
        int curGridY = j + pixelOffsetY / pixelSize;
        if (curGridX < 0 || curGridY < 0) continue;
        if (gameState[curGridX + 1][curGridY + 1])
        {
          graphics.fillRect(i * pixelSize - pixelOffsetX % pixelSize, j * pixelSize - pixelOffsetY % pixelSize, pixelSize, pixelSize);
        }
      }
    }
    if (pixelSize >= 5)
    {
      graphics.setColor(Color.gray);
      for (int i = getWidth() - (pixelOffsetX % pixelSize); i >= 0; i-= pixelSize)
      {
        graphics.drawLine(i, 0, i, getHeight());
      }
      for (int i = getHeight() - pixelOffsetY % pixelSize; i >= 0; i-= pixelSize)
      {
        graphics.drawLine(0, i, getWidth(), i);
      }
    }
  }
  public Dimension getScreenGridSize()
  {
    return new Dimension(getWidth() / pixelSize, getHeight() / pixelSize);
  }
  public void scroll(int xAmount, int yAmount)
  {
    pixelOffsetX = Util.clampInteger(pixelOffsetX + xAmount, 0, GAME_SIZE - getScreenGridSize().width);
    pixelOffsetY = Util.clampInteger(pixelOffsetY + yAmount, 0, GAME_SIZE - getScreenGridSize().height);
    repaint();
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
      repaint();
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent)
    {
      scroll(currentPoint.x - mouseEvent.getX(), currentPoint.y - mouseEvent.getY());
      currentPoint = mouseEvent.getPoint();
    }
  }
}
