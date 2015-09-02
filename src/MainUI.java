import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Shea Polansky
 * The top level portion of the user interface.
 * Contains several buttons, a drop down for preset loading, a spinner for thread count selection,
 * and the main simulator panel.
 */
public class MainUI extends JFrame
{

  public static final String PLAY_TEXT = "Play";
  public static final String PAUSE_TEXT = "Pause";
  public static final int BUTTON_PADDING = 5;
  public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
  private final JButton playPauseButton, nextButton, resetButton, loadButton;
  private final JSpinner threadSpinner;
  private final GameOfLifePanel gamePanel;
  private final JComboBox<GameStatePreset> loadBox;

  public MainUI()
  {
    super("Conway's Game of Life");
    setLayout(new BorderLayout());
    gamePanel = new GameOfLifePanel(this::toggleUIEnabled);
    add(gamePanel);
    JPanel buttonPanel = new JPanel();
    BoxLayout box = new BoxLayout(buttonPanel, BoxLayout.X_AXIS);
    buttonPanel.setLayout(box);
    playPauseButton = new JButton(PLAY_TEXT);
    playPauseButton.addActionListener(this::playPause);
    buttonPanel.add(playPauseButton);
    addPadding(buttonPanel);
    threadSpinner = new JSpinner(new SpinnerNumberModel(AVAILABLE_PROCESSORS, 1, AVAILABLE_PROCESSORS * 2, 1));
    threadSpinner.setMaximumSize(threadSpinner.getPreferredSize());
    buttonPanel.add(threadSpinner);
    buttonPanel.add(new JLabel(" threads"));
    addPadding(buttonPanel);
    nextButton = new JButton("Next");
    nextButton.addActionListener(this::advanceFrame);
    buttonPanel.add(nextButton);
    addPadding(buttonPanel);
    resetButton = new JButton("Reset");
    resetButton.addActionListener(this::reset);
    buttonPanel.add(resetButton);
    buttonPanel.add(Box.createHorizontalGlue());
    loadBox = new JComboBox<>(GameStatePresets.getPresets());
    loadBox.setMaximumSize(loadBox.getPreferredSize());
    buttonPanel.add(loadBox);
    addPadding(buttonPanel);
    loadButton = new JButton("Load");
    loadButton.addActionListener(e -> gamePanel.load((GameStatePreset)loadBox.getSelectedItem()));
    buttonPanel.add(loadButton);
    add(buttonPanel, BorderLayout.SOUTH);

    pack();
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  /**
   * Helper method to add padding to a panel that uses a BoxLayout
   * @param buttonPanel the panel to add padding to
   */
  private void addPadding(JPanel buttonPanel)
  {
    buttonPanel.add(Box.createHorizontalStrut(BUTTON_PADDING));
  }

  /**
   * Calls reset on the gamePanel, and disables the UI while it waits for the operation to finish
   * ActionListener target
   * @param e ignored
   */
  private void reset(ActionEvent e)
  {
    toggleUIEnabled();
    playPauseButton.setEnabled(false);
    gamePanel.reset();
  }

  /**
   * Toggles the UI, and the game state
   * ActionListener target
   * @param e ignored
   */
  private void playPause(ActionEvent e)
  {
    toggleUIEnabled();
    gamePanel.playPause((int) threadSpinner.getValue());
  }

  /**
   * Turns the UI off, and signals the gamePanel to advance a single frame
   * @param e ignored
   */
  private void advanceFrame(ActionEvent e)
  {
    toggleUIEnabled();
    gamePanel.advanceFrame((int) threadSpinner.getValue());
  }

  /**
   * Toggles various elements of the UI to on/off, toggles the text of the play button to play/paused,
   * and makes sure it is enabled.
   */
  private void toggleUIEnabled()
  {
    boolean isPaused = playPauseButton.getText().equals(PLAY_TEXT);
    playPauseButton.setText(isPaused ? PAUSE_TEXT : PLAY_TEXT);
    playPauseButton.setEnabled(true);
    for (JComponent c : new JComponent[] {nextButton, resetButton, loadButton, threadSpinner, loadBox})
    {
      c.setEnabled(!isPaused);
    }
  }
}
