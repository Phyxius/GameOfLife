import javax.swing.*;

/**
 * Shea Polansky
 * Class name and description goes here
 */
public class Main
{
  public static void main(String[] args)
  {
    try
    {
      UIManager.setLookAndFeel(
          UIManager.getSystemLookAndFeelClassName());
    }
    catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException ignored)
    {
    }

    SwingUtilities.invokeLater(() -> new MainUI().setVisible(true));
  }
}
