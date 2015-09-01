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

  public static class SliderPreset extends GameStatePreset
  {
    @Override
    public void loadState(boolean[][] output)
    {

    }

    @Override
    public String toString()
    {
      return "Sliders";
    }
  }

  //from: http://conwaylife.com/wiki/3-engine_Cordership_gun
  public static class ShipGun extends GameStatePreset
  {
    @Override
    public void loadState(boolean[][] output)
    {
      parseLifeFile("3enginecordershipgun_106.lif", output);
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
      parseLifeFile("turingmachine_106.lif", output);
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
}
