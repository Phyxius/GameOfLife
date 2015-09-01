import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Shea Polansky
 * Class name and description goes here
 */
public abstract class GameStatePreset
{
  public abstract void loadState(boolean[][] output);

  public Point getInitialViewport()
  {
    return new Point(0, 0);
  }

  public int getInitialPixelSize()
  {
    return 5;
  }

  protected static void setCellsToAlive(int[][] points, boolean[][] output)
  {
    for (int[] point : points)
    {
      output[point[0] + 1][point[1] + 1] = true;
    }
  }

  //Life 1.06 format: http://www.conwaylife.com/wiki/Life_1.06
  protected static void parseLifeString(String content, int xOffset, int yOffset, boolean[][] output)
  {
    String[] lines = content.replace("\r","").split("\n");
    if (!lines[0].equals("#Life 1.06")) throw new IllegalArgumentException();
    ArrayList<Point> points = new ArrayList<>();
    for (int i = 1; i < lines.length; i++)
    {
      String[] line = lines[i].split(" ");
      points.add(new Point(Integer.parseInt(line[0]), Integer.parseInt(line[1])));
    }
    int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
    for (Point point : points)
    {
      if (point.x < minX) minX = point.x;
      if (point.y < minY) minY = point.y;
    }
    for (Point point : points)
    {
      output[point.x - minX + xOffset + 1][point.y - minY + yOffset + 1] = true;
    }
  }

  protected static void parseLifeFile(String path, int xOffset, int yOffset, boolean[][] output)
  {
    final InputStream resourceStream = GameStatePreset.class.getResourceAsStream(path);
    parseLifeString(new Scanner(resourceStream).useDelimiter("\\Z").next(), xOffset, yOffset, output);
  }
}
