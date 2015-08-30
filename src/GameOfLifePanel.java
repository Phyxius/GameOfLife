import javax.swing.*;
import java.awt.*;

/**
 * Shea Polansky
 * Class name and description goes here
 */
public class GameOfLifePanel extends JPanel
{

    private final JScrollBar horizontalScrollBar;
    private final JScrollBar verticalScrollBar;
    private final GameDrawerPanel gameDrawerPanel;

    public GameOfLifePanel()
    {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.BOTH;
        gameDrawerPanel = new GameDrawerPanel();
        add(gameDrawerPanel, c);
        c.weighty = 0;
        c.weightx = 0;
        c.gridx = 1;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.VERTICAL;
        verticalScrollBar = new JScrollBar(Adjustable.VERTICAL);
        add(verticalScrollBar, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.HORIZONTAL;
        horizontalScrollBar = new JScrollBar(Adjustable.HORIZONTAL);
        add(horizontalScrollBar, c);
    }

    public void playPause()
    {

    }
    public void reset()
    {

    }
    public void advanceFrame()
    {

    }

    @Override
    public Dimension getMaximumSize()
    {
        return new Dimension(20000, 20000);
    }
}
