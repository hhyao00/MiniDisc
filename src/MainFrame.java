import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    private MinDiscPanel discPanel;
    private JPanel controlPanel, detailsPanel;

    public MainFrame(){

        super.setTitle("Minimum Enclosing Disc");
        setSize(1000, 700);
        Container contentPane = getContentPane();
        ((JComponent) contentPane).setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel panel =  new JPanel();
        panel.setLayout(new BorderLayout());
        discPanel = new MinDiscPanel();
        discPanel.setBorder(new EmptyBorder(3, 5, 3, 2));
        panel.add(discPanel, BorderLayout.CENTER);

        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(2, 1));

        controlPanel = new JPanel();
        setUpControlPanel();
        //panel.add(controlPanel, BorderLayout.EAST);

        detailsPanel = new DetailsPanel();
        discPanel.receiveDetailsPanel(detailsPanel);

        pane.add(controlPanel);
        pane.add(detailsPanel);
        panel.add(pane, BorderLayout.EAST);
        add(panel);

        validate();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }



    /** Control options for actions to prefrom */
    private void setUpControlPanel(){

        controlPanel.setLayout(new GridLayout(0, 1));
        controlPanel.setBorder(new EmptyBorder(5, 5, 3, 1));

        // ------ MiniDisc options ------ //

        JButton button, playButton;


        button = new JButton("Compute MiniDisc");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                discPanel.normal();
            }
        });
        controlPanel.add(button);


        button = new JButton("Freehand (dynamic)");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                discPanel.freehand();
            }
        });
        controlPanel.add(button);


        JPanel randomPanel = new JPanel();
        randomPanel.setLayout(new GridLayout(1, 2));

        JTextField numPoints = new JTextField("  30");
        randomPanel.add(numPoints);

        button = new JButton("Random");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!numPoints.getText().isEmpty())
                    discPanel.random((int)Double.parseDouble(numPoints.getText()));
            }
        });
        randomPanel.add(button);

        controlPanel.add(randomPanel);


        JLabel label = new JLabel(" ");
        controlPanel.add(label);



        // ------ Animation control ------ //

        playButton = new JButton("Stop (Pause)");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                discPanel.timerStop();
            }
        });
        controlPanel.add(playButton);

        button = new JButton("Resume");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                discPanel.timerStart();
            }
        });
        controlPanel.add(button);

        button = new JButton("Clear/Reset");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                discPanel.clear();
            }
        });
        controlPanel.add(button);
//        controlPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
    }

}
