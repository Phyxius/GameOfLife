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
  private final JButton playPauseButton, nextButton, resetButton, loadButton;
  private final JSpinner threadSpinner;
  private final GameOfLifePanel gamePanel;
  private final JComboBox<GameStatePreset> loadBox;

  public MainUI()
  {
    super("Conway's Game of Life");
    setLayout(new BorderLayout());
    gamePanel = new GameOfLifePanel();
    add(gamePanel);
    JPanel buttonPanel = new JPanel();
    BoxLayout box = new BoxLayout(buttonPanel, BoxLayout.X_AXIS);
    buttonPanel.setLayout(box);
    playPauseButton = new JButton(PLAY_TEXT);
    playPauseButton.addActionListener(this::playPause);
    buttonPanel.add(playPauseButton);
    buttonPanel.add(Box.createHorizontalStrut(BUTTON_PADDING));
    threadSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Runtime.getRuntime().availableProcessors(), 1));
    threadSpinner.setMaximumSize(threadSpinner.getPreferredSize());
    buttonPanel.add(threadSpinner);
    buttonPanel.add(new JLabel(" threads"));
    buttonPanel.add(Box.createHorizontalStrut(BUTTON_PADDING));
    nextButton = new JButton("Next");
    nextButton.addActionListener(e -> gamePanel.advanceFrame());
    buttonPanel.add(nextButton);
    buttonPanel.add(Box.createHorizontalStrut(BUTTON_PADDING));
    resetButton = new JButton("Reset");
    resetButton.addActionListener(e -> gamePanel.reset());
    buttonPanel.add(resetButton);
    buttonPanel.add(Box.createHorizontalGlue());
    loadBox = new JComboBox<>(GameStatePresets.getPresets());
    loadBox.setMaximumSize(loadBox.getPreferredSize());
    buttonPanel.add(loadBox);
    buttonPanel.add(Box.createHorizontalStrut(BUTTON_PADDING));
    loadButton = new JButton("Load");
    loadButton.addActionListener(e -> gamePanel.load((GameStatePreset)loadBox.getSelectedItem()));
    buttonPanel.add(loadButton);

    add(buttonPanel, BorderLayout.SOUTH);

    pack();
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  private void playPause(ActionEvent e)
  {
    boolean isPaused = playPauseButton.getText().equals(PLAY_TEXT);
    playPauseButton.setText(isPaused ? PAUSE_TEXT : PLAY_TEXT);
    for (JComponent c : new JComponent[] {nextButton, resetButton, loadButton, threadSpinner, loadBox})
    {
      c.setEnabled(!isPaused);
    }
    gamePanel.playPause((int) threadSpinner.getValue());
  }
}
