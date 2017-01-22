/*
 * DrawingPanel.java
 *
 * Created on 20 March 2001, 08:48
 */

package jparticles.gui;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 * @author  Carlos Valero
 * @version
 */
import java.util.*;

/** A extended JPanel which can handle JDrawingData.
 * @see jparticles.gui.JDrawingPanel.JDrawingData
 */
public class JDrawingPanel extends javax.swing.JPanel
{        
/** This interface is used by JDrawingPanel to abstract the objects which can
 * be drawn in it.
 */
    static public interface J2DDrawableObject
    {
/** Draws something in a Graphics2D context. The userObject may be used to pass
 * external data to this method.
 * @param g a g3d context
 * @param userObject a user object
 */
        public void draw(Graphics2D g, Object userObject);
    }
/** This class is used to pair a J2DDrawableObject with a user object.
 */
    static public class DrawingData
    {
/** The J2DDrawable object wich will be painted.
 */
        public J2DDrawableObject drawable;
/** A user object with may be used to get external information in the paint method.
 *
 */
        public Object userObject;
/** Creates a new DrawingData object.
 */
        public DrawingData() {}
/** Creates a new DrawingData object.
 * @param drawable a J2DDrawableObject object
 * @param userObject a user object.
 */
        public DrawingData(J2DDrawableObject drawable, Object userObject)
        {
            this.drawable = drawable;
            this.userObject = userObject;
        }
    }
    
/** The getName of the panel.
 */
    public String name = "no-getName";
    public int imPosX, imPosY;
    //
    // Private variables starte here
    //    
    private BufferedImage backgroundImage = null;
    private Dimension preferredSize;
    private Vector drawingDataVector = new Vector();
    private DrawingData drawingData;
    private J2DDrawableObject drawable;
    private int drawingDataCount;    
    /** Creates new DrawingPanel of the specified dimensions and black background
     * @param hSize the width of the panel
     * @param vSize the height of the panel
 */
    public JDrawingPanel(int width,int height)
    {
        this.preferredSize = new Dimension(width,height) ;
        setBackground(Color.black);
        this.setPreferredSize(preferredSize);        
    }
    
/** Sets the given image as the background.
 * @param image a buffered image
 */
    public void setBackgroundImage(BufferedImage image)
    {
        backgroundImage = image;
        this.repaint();
    }
    public void setImagePosition(int x, int y)
    {
        imPosX = x;
        imPosY = y;
    }
/** Adds a drawing data pair.
 * @param drawingData a drawingData object
 */
    public void add(DrawingData drawingData)
    {
        drawingDataVector.add(drawingData);
        drawingDataCount = drawingDataVector.size();
        repaint();
    }    
/** Removes the given DrawingData object.
 * @param drawingData a DrawingData object
 */
    public boolean remove(DrawingData drawingData)
    {
        boolean out;
        out = drawingDataVector.remove(drawingData);
        drawingDataCount = drawingDataVector.size();
        repaint();
        return out;
    }
/** Sets the i-th DrawingData object to drawingData.
 * @param drawingData a DrawingData object
 * @param i the index of the DrawingData object.
 */
    public void set(int i, DrawingData drawingData)
    {
        drawingDataVector.set(i,drawingData);
        repaint();
    }
    
/** Paints the sequence of DrawingData objects.
 * @param g a g3d context
 */
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;                
        super.paintComponent(g2);
        if(backgroundImage != null)
        {
            g2.drawImage(backgroundImage,imPosX,imPosY,this);
        }
        if (drawingDataCount > 0)
        {
            for(int i = 0; i < drawingDataCount; i++)
            {
                drawingData = (DrawingData)drawingDataVector.get(i);
                drawable = drawingData.drawable;
                if(drawable != null) drawable.draw(g2,drawingData.userObject);
            }
        }
    }
    public  int getDrawableObjectsCount()
    {
        return drawingDataVector.size();
    }
   
}
