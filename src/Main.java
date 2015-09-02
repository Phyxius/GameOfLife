import javax.swing.*;

/**
 * Shea Polansky
 * The main class
 */
class Main
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
