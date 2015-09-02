import java.awt.*;
import java.util.ArrayList;

/**
 * Shea Polansky
 * GameStatePresets: Container class for various preset game states
*/
public class GameStatePresets
{
  /**
   * Private because this class has no non-static members
   */
  private GameStatePresets()
  {

  }

  /**
   * Finds all inner classes of this one that extend GameStatePreset and instantiates one each.
   * @return an array of GameStatePresets
   */
  public static GameStatePreset[] getPresets()
  {
    ArrayList<GameStatePreset> presets = new ArrayList<>();
    for (Class<?> c : GameStatePresets.class.getDeclaredClasses())
    {
      try
      {
        presets.add((GameStatePreset) c.newInstance());
      }
      catch (InstantiationException | IllegalAccessException | ClassCastException ignored)
      {

      }
    }
    return presets.toArray(new GameStatePreset[presets.size()]);
  }

  /**
   * from: http://conwaylife.com/wiki/3-engine_Cordership_gun
   * A gun that fires corderships
   */
  public static class ShipGun extends GameStatePreset
  {
    @Override
    public void loadState(boolean[][] output)
    {
      parseLifeFile("3enginecordershipgun_106.lif", 0, 0, output);
    }

    @Override
    public int getInitialPixelSize()
    {
      return 2;
    }

    @Override
    public String toString()
    {
      return "Ship Gun";
    }
  }

  /**
   * from: http://www.conwaylife.com/wiki/Turing_machine
   * A turing machine. Sadly, the Universal Turing Machine did not fit on this board.
   */
  public static class TuringMachine extends GameStatePreset
  {
    @Override
    public void loadState(boolean[][] output)
    {
      parseLifeFile("turingmachine_106.lif",0, 0, output);
    }

    @Override
    public int getInitialPixelSize()
    {
      return 1;
    }

    @Override
    public String toString()
    {
      return "Turing Machine";
    }
  }

  /**
   * from: http://conwaylife.com/wiki/Ring_of_fire
   * A 2-period oscillator
   */
  public static class RingOfFire extends GameStatePreset
  {
    @Override
    public int getInitialPixelSize()
    {
      return 20;
    }

    @Override
    public void loadState(boolean[][] output)
    {
      parseLifeFile("ringoffire_106.lif", 1, 1, output);
    }

    @Override
    public String toString()
    {
      return "Ring of Fire";
    }
  }

  /**
   * from: http://conwaylife.com/wiki/3-engine_Cordership_rake
   * A rake (a ship that leaves a trail of more ships)
   */
  public static class CordershipRake extends GameStatePreset
  {
    @Override
    public void loadState(boolean[][] output)
    {
      parseLifeFile("3enginecordershiprake_106.lif", 100, 100, output);
    }

    @Override
    public int getInitialPixelSize()
    {
      return 1;
    }

    @Override
    public String toString()
    {
      return "Cordership Rake";
    }
  }

  /**
   * from: http://conwaylife.com/wiki/Fermat_prime_calculator
   * A calculator of Fermat primes. The pattern destroys a section of itself when a Fermat prime is found.
   * a number N is tested for Fermat primality around generation 120N-550, and the pattern is rigged to completely
   * self-destruct if a Fermat prim larger than 65537 (the largest known) is found.
   */
  public static class FermatPrimeCalculator extends GameStatePreset
  {
    @Override
    public void loadState(boolean[][] output)
    {
      parseLifeFile("fermatprimecalculator_106.lif", 100, 100, output);
    }

    @Override
    public Point getInitialViewport()
    {
      return new Point(100, 100);
    }

    @Override
    public int getInitialPixelSize()
    {
      return 1;
    }

    @Override
    public String toString()
    {
      return "Fermat Prime Calculator";
    }
  }

  /**
   * An empty board.
   */
  public static class Blank extends GameStatePreset
  {
    @Override
    public void loadState(boolean[][] output)
    {

    }

    @Override
    public String toString()
    {
      return "Blank";
    }
  }
}
