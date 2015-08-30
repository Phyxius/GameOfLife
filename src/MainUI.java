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
    private final JButton playPauseButton, nextButton;
    private final GameOfLifePanel gamePanel;

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
        nextButton = new JButton("Next");
        nextButton.addActionListener(e -> gamePanel.advanceFrame());
        buttonPanel.add(nextButton);
        buttonPanel.add(Box.createHorizontalGlue());
        JComboBox<String> loadBox = new JComboBox<>(new String[] {"Test 1", "Test 2"});
        loadBox.setMaximumSize(loadBox.getPreferredSize());
        buttonPanel.add(loadBox);
        buttonPanel.add(Box.createHorizontalStrut(BUTTON_PADDING));
        JButton loadButton = new JButton("Load");
        buttonPanel.add(loadButton);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void playPause(ActionEvent e)
    {
        if (playPauseButton.getText().equals(PLAY_TEXT))
        {
            playPauseButton.setText(PAUSE_TEXT);
            nextButton.setEnabled(false);
        }
        else
        {
            playPauseButton.setText(PLAY_TEXT);
            nextButton.setEnabled(true);
        }
        gamePanel.playPause();
    }
}
