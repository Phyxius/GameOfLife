import java.util.Random;

/**
 * Created by Shea on 2015-08-30.
 * Class name and description go here.
 */
public class Util
{
  private Util() throws Exception
  {
    throw new Exception();
  }

  public static final Random RANDOM = new Random();

  public static int clampInteger(int value, int min, int max)
  {
    return Math.min(max, Math.max(min, value));
  }
}
