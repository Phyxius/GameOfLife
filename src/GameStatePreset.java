import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Shea Polansky
 * A game state preset, with methods to load the initial grid, set the default viewport, and set the default pixel size
 */
public abstract class GameStatePreset
{
  /**
   * Loads the preset's initial state into the given grid
   * @param output the grid to load into
   */
  public abstract void loadState(boolean[][] output);

  /**
   * @return the preset's default viewport location
   */
  public Point getInitialViewport()
  {
    return new Point(0, 0);
  }

  /**
   * @return the preset's default initial pixel size
   */
  public int getInitialPixelSize()
  {
    return 5;
  }

  /**
   * Helper method to set a series of points to alive
   * Uses int[]'s instead of points for easy inline instantiation
   * @param points a 2D array of the format { {x1, y1}, {x2, y2}, ...}. Each point in the array will be set to true on the grid.
   * @param output the grid to output to.
   */
  protected static void setCellsToAlive(int[][] points, boolean[][] output)
  {
    for (int[] point : points)
    {
      output[point[0] + 1][point[1] + 1] = true;
    }
  }

  /**
   * Parses a string containing a Life 1.06 (Life 1.06 format: http://www.conwaylife.com/wiki/Life_1.06) formatted
   * grid, outputting it onto a given grid with a given offset.
   * Performs normalization so that the smallest x/y values are converted to zero, and the rest of grid moved appropriately.
   * @param content the content to parse
   * @param xOffset the x offset (from x=0). All points will have their x-values added to this.
   * @param yOffset the y offset (from y=0). All points will have their y-values added to this.
   * @param output the grid to output to
   */
  private static void parseLifeString(String content, int xOffset, int yOffset, boolean[][] output)
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

  /**
   * Loads the Life 1.06 formatted file at the given path, at the given offset from zero.
   * @param path the path to the file
   * @param xOffset the x offset
   * @param yOffset the y offset
   * @param output the grid to output to
   */
  static void parseLifeFile(String path, int xOffset, int yOffset, boolean[][] output)
  {
    final InputStream resourceStream = GameStatePreset.class.getResourceAsStream(path);
    parseLifeString(new Scanner(resourceStream).useDelimiter("\\Z").next(), xOffset, yOffset, output);
  }
}
