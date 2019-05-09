import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MainFrame extends JFrame {

    private MinDiscPanel discPanel;
    private JPanel controlPanel, detailsPanel;

    private boolean isStopped = false;


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

        String pause = "Stop (Pause)";
        JButton button, playButton = new JButton(pause);


        button = new JButton("Compute MiniDisc");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                discPanel.normal();

                isStopped = false;
                playButton.setText(pause);
            }
        });
        controlPanel.add(button);


        button = new JButton("Freehand (dynamic)");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                discPanel.freehand();

                isStopped = false;
                playButton.setText(pause);
            }
        });
        controlPanel.add(button);


        JPanel randomPanel = new JPanel();
        randomPanel.setLayout(new GridLayout(1, 2));

        JTextField numPoints = new JTextField("300");
        randomPanel.add(numPoints);

        button = new JButton("Random");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!numPoints.getText().isEmpty()){
                    discPanel.random((int)Double.parseDouble(numPoints.getText()));

                    isStopped = false;
                    playButton.setText(pause);
                }
            }
        });
        randomPanel.add(button);

        controlPanel.add(randomPanel);


        JLabel label = new JLabel("Animation speed");
        controlPanel.add(label);

        JSlider slider = new JSlider(JSlider.HORIZONTAL, 50, 170, 170);
        slider.setInverted(true);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = slider.getValue();
                discPanel.setTimerDelay(value);
            }
        });

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2, 1));
        p.setBorder(new EmptyBorder(5, 7, 0, 1));
        p.add(label);
        p.add(slider);
        controlPanel.add(p);


        label = new JLabel(" ");
        controlPanel.add(label);

        button = new JButton("Shuffle");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                discPanel.shuffle();
                JOptionPane.showMessageDialog(null, "Finished shuffle");

            }
        });
        controlPanel.add(button);


        // ------ Animation control ------ //

        // playButton = new JButton("Stop (Pause)");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!isStopped){
                    discPanel.timerStop();
                    playButton.setText("Resume");
                    isStopped = true;
                } else if (isStopped){
                    discPanel.timerStart();
                    playButton.setText("Stop (Pause)");
                    isStopped = false;
                }
            }
        });
        controlPanel.add(playButton);

//        button = new JButton("Resume");
//        button.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                discPanel.timerStart();
//            }
//        });
//        controlPanel.add(button);

        button = new JButton("Clear/Reset");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                discPanel.clear();
                playButton.setText(pause);
            }
        });
        controlPanel.add(button);
//        controlPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
    }

    private class SliderListener implements ChangeListener {

        public JSlider slider = new JSlider(JSlider.HORIZONTAL, 200, 30, 10);

        public void stateChanged(ChangeEvent e) {
            int value = slider.getValue();
            discPanel.setTimerDelay(value);
        }
    }
}
