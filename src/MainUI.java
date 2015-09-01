import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by Shea on 2015-08-27.
 * Class name and description go here.
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

  private void addPadding(JPanel buttonPanel)
  {
    buttonPanel.add(Box.createHorizontalStrut(BUTTON_PADDING));
  }

  private void reset(ActionEvent e)
  {
    toggleUIEnabled();
    playPauseButton.setEnabled(false);
    gamePanel.reset();
  }

  private void playPause(ActionEvent e)
  {
    toggleUIEnabled();
    gamePanel.playPause((int) threadSpinner.getValue());
  }

  private void advanceFrame(ActionEvent e)
  {
    toggleUIEnabled();
    gamePanel.advanceFrame((int) threadSpinner.getValue());
  }

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
