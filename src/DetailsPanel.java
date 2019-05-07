import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DetailsPanel extends JPanel {

    private String ROUTINE = "Routine: ", SET_P = "Set P of points; n = ";
    private String
            MINIDISC = "\t\t\t- MiniDisc: P (i = ", MINIDISC2 = ")",
            MINIDISC_ONE = "\t\t\t- MiniDiscOnePoint ",
            MINIDISC_TWO = "\t\t\t- MiniDiscTwoPoints ";


    private JLabel routineLabel, setLabel,
            r1Label, r2Label, r3Label;
    private int n, r1, r2, r3;

    public DetailsPanel(){
        setLayout(new GridLayout(18, 1));
        setBorder(new EmptyBorder(3, 7, 3, 1));
        add(new JLabel("  "));
        add(new JSeparator(SwingConstants.HORIZONTAL));
        addLabels();

        revalidate();
        repaint();
    }

    private void addLabels(){

        setLabel = new JLabel(SET_P + "0");
        add(setLabel);

        routineLabel = new JLabel(ROUTINE);
        add(routineLabel);

        r1Label = new JLabel(MINIDISC + r1 + MINIDISC2);
        r2Label = new JLabel(MINIDISC_ONE + r2);
        r3Label = new JLabel(MINIDISC_TWO + r3);
        add(r1Label);
        add(r2Label);
        add(r3Label);
    }

    public void clear(){

        setLabel.setText(SET_P + "0");

        // reset routine strings
        r1 = 0;
        r2 = 0;
        r3 = 0;
        r1Label.setText(MINIDISC + r1 + MINIDISC2);
        r2Label.setText(MINIDISC_ONE + r2);
        r3Label.setText(MINIDISC_TWO + r3);
        routineLabel.setText(ROUTINE);
    }


    public void updateRoutine(String routine, int id){
        switch (id){
            case 1:
                r1++; break;
            case 2:
                r2++; break;
            case 3:
                r3++; break;
        }
        r1Label.setText(MINIDISC + r1 + MINIDISC2);
        r2Label.setText(MINIDISC_ONE + r2);
        r3Label.setText(MINIDISC_TWO + r3);
        routineLabel.setText(ROUTINE + routine);
    }

    public void totalPoints(int numPoints){
        setLabel.setText(SET_P + numPoints);
    }
}
