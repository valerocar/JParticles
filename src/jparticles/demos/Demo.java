package jparticles.demos;

import javax.swing.*;

/**
 * Created by valeroc on 1/20/17.
 */
public interface Demo
{
    public void initialize();
    public JPanel getMainPanel();
    public void startAnimation();
    public void stopAnimation();
    public String getName();
    public String getHelp();
}
