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
}