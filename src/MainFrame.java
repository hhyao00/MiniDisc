import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    private MinDiscPanel discPanel;
    private JPanel controlPanel;

    public MainFrame(){

        super.setTitle("Minimum Enclosing Disc");
        setSize(1000, 700);
        Container contentPane = getContentPane();
        ((JComponent) contentPane).setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel panel =  new JPanel();
        panel.setLayout(new BorderLayout());
        discPanel = new MinDiscPanel();
        panel.add(discPanel, BorderLayout.CENTER);

        controlPanel = new JPanel();
        setupControlPanel();
        panel.add(controlPanel, BorderLayout.EAST);


        add(panel);

        validate();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }


    private void setupControlPanel(){

        controlPanel.setLayout(new GridLayout(10, 0));

        JButton inButton = new JButton("Random");
        inButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                discPanel.start();
            }
        });
        controlPanel.add(inButton);
    }

}
