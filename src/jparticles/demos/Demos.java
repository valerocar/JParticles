package jparticles.demos;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by valeroc on 20/01/17.
 */
public class Demos implements ChangeListener{
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JTextPane helpPane = new JTextPane();
    private int width = 1000;
    private int height = 600;
    private int selected = 0;


    private Demo [] demos = {new Springs(), new Cloth(), new Grid(),
            new NonSingGrid(), new ParticlesInPotential(),new Snake(), new SnakeInPotential()};

    public Demos()
    {
        helpPane.setContentType("text/html");

        for(int i = 0; i < demos.length; i++)
        {
            Demo demo = demos[i];
            tabbedPane.addTab(demo.getName(),demo.getMainPanel());
        }
        demos[selected].startAnimation();
        helpPane.setText(demos[selected].getHelp());
        helpPane.setSize(500,600);
        helpPane.setMargin(new Insets(5,20,5,20));


        tabbedPane.addChangeListener(this);

        JFrame mainFrame = new JFrame();
        mainFrame.setLayout(new GridLayout(1,2));
        mainFrame.add(tabbedPane);
        mainFrame.add(helpPane);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(width, height);

        mainFrame.setVisible(true);




    }

    public static void main(String[] args) {
        new Demos();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        for(int i = 0; i < demos.length; i++) {
            demos[i].stopAnimation();
        }
        selected = tabbedPane.getSelectedIndex();
        Demo demo = demos[selected];
        demo.startAnimation();
        helpPane.setText(demo.getHelp());
    }
}
