import java.awt.*;
import java.util.ArrayList;

/**
 * Shea Polansky
 * Class name and description goes here
 */
public class GameStatePresets
{
  private GameStatePresets()
  {

  }

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

  //from: http://conwaylife.com/wiki/3-engine_Cordership_gun
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

  //from: http://www.conwaylife.com/wiki/Turing_machine
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

  //from: http://conwaylife.com/wiki/Ring_of_fire
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

  //from: http://conwaylife.com/wiki/3-engine_Cordership_rake
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

  //from: http://conwaylife.com/wiki/Fermat_prime_calculator
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
