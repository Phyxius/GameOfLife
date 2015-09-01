/**
 * Shea Polansky
 * Class name and description goes here
 */
public abstract class GameStatePreset
{
  public abstract void loadState(boolean[][] output);

  protected static void setCellsToAlive(int[][] points, boolean[][] output)
  {
    for (int[] point : points)
    {
      output[point[0]][point[1]] = true;
    }
  }
}
