/*
 * JDrawingWindow.java
 *
 * Created on February 26, 2002, 5:57 PM
 */

package jparticles.gui;

/**
 *
 * @author  carlos
 */
public class JDrawingWindow extends JDrawingFrame
{
    JDrawingPanel panel;    
    public JDrawingWindow(int width, int height)
    {
        super(width,height);
        panel = new JDrawingPanel(width,height);
        this.add(panel);
    }
    public JDrawingWindow()
    {
        this(512,512);
    }
    public void add(JDrawingPanel.J2DDrawableObject drawable)
    {
        panel.add(new JDrawingPanel.DrawingData(drawable,null));
    }
}
