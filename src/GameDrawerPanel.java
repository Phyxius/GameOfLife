import javax.swing.*;
import java.awt.*;

/**
 * Shea Polansky
 * Class name and description goes here
 */
public class GameDrawerPanel extends JPanel
{
  public static final int GAME_SIZE = 10000;
  private final boolean[][] gameState = new boolean[GAME_SIZE + 2][GAME_SIZE + 2]; //account for border cells
  public GameDrawerPanel()
  {

  }

  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension(800, 600);
  }

  public void resetToRandomState()
  {
    for (int i = 0; i < GAME_SIZE; i++)
    {
      for (int j = 0; j < GAME_SIZE; j++)
      {
        gameState[i][j] = Util.RANDOM.nextBoolean();
      }
    }
  }

  @Override
  protected void paintComponent(Graphics graphics)
  {

  }
}
