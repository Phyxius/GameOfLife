import java.util.Random;

/**
 * Created by Shea on 2015-08-30.
 * Utilities class
 */
class Util
{
  private Util()
  {
  }

  public static final Random RANDOM = new Random();

  /**
   * Clamps a given value to be at most max, and at least min.
   * Assumes min < max
   * @param value the value to clamp
   * @param min the minimum value
   * @param max the maximum value
   * @return min if value < min, max if value > max, value otherwise
   */
  public static int clampInteger(int value, int min, int max)
  {
    return Math.min(max, Math.max(min, value));
  }
}
