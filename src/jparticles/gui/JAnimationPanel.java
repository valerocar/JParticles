/*
 * AnimationPanel.java
 *
 * Created on 11 May 2001, 15:14
 */

package jparticles.gui;

import java.awt.event.*;
import java.util.*;
import java.awt.*;

/** A JDrawingPanel wich can handle JAnimationData objects.
 * @author carlosv
 * @version 1.0
 */



public class JAnimationPanel extends JDrawingPanel implements ActionListener
{
    private CallBack callBack = null;
/** This interface is used by JAnimation to abstract the objects which can
 * be animated in it.
 *
 */
    public interface J2DAnimation extends J2DDrawableObject
    {
    /** Updates the animation state. The userObject can be used to pass external
     * data to this method.
     * @param userObject a user supplied object
     */
        public void updateAimationState(Object userObject);
    }
/** Has value true if the animation is froze, and false otherwise.
 */    
    public boolean frozen = false;
/** Used to pass animation data to a JAnimationPanel.
 */    
    static public class AnimationData
    {
/** A J2DAnimation object.
 */        
        public J2DAnimation animation;
/** A user's object.
 */        
        public Object userObject;
/** Creates the AnimationData with the specified data.
 * @param animation a J2DAnimation object
 * @param userObject a user's object
 */        
        public AnimationData(J2DAnimation animation, Object userObject)
        {
            this.animation = animation;
            this.userObject = userObject;
        }
    }
    
    Vector animationVector = new Vector();
    int animationsCount = 0;
    AnimationData animationData;
    J2DAnimation animation;
    Graphics2D g2;
    
    javax.swing.Timer timer;
    int delay;
    int fps;
    
/** Creates a JAnimation panel of the given size and with the given animation
 * rate.
 * @param hSize the width of the panel
 * @param vSize the height of the panel
 * @param fps the number of frames per second
 */    
    public JAnimationPanel(int hSize,int vSize,int fps)
    {
        super(hSize,vSize);
        this.fps = fps;
        delay = (fps > 0) ? (1000 / fps) : 100;
        timer = new javax.swing.Timer(delay,this);
    }
/** Starts the animation.
 */    
    public  void startAnimation()
    {
        if (frozen)
        {
            //Do nothing.  The user has requested that we
            //stop changing the image.
        }
        else
        {
            //Start animating!
            timer.start();
        }
    }
/** Stops the animation.
 */    
    public  void stopAnimation()
    {
        timer.stop();
    }
    
/** Sets the animation rate.
 * @param fps frames per second
 */    
    public  void setFramesPerSecond(int fps)
    {
        this.fps = fps;
        delay = (fps > 0) ? (1000 / fps) : 100;
        timer.setDelay(delay);
    }
    
/** Adds animation data to the panel.
 * @param animationData the animation data
 */    
    public  void add(AnimationData animationData)
    {
        animationVector.add(animationData);
        animationsCount = animationVector.size();
    }
    
    public void actionPerformed(java.awt.event.ActionEvent evt)
    {
        for(int i = 0; i < animationsCount; i++)
        {
            animationData = (AnimationData)animationVector.get(i);
            animation = animationData.animation;
            if(animation != null) animation.updateAimationState(animationData.userObject);
            if(callBack != null) callBack.method.run(callBack.object);
        }
        repaint();
    }
    public void paintComponent(Graphics g)
    {
        g2 = (Graphics2D)g;
        super.paintComponent(g);
        if (animationsCount > 0)
        {
            for(int i = 0; i < animationsCount; i++)
            {
                animationData = (AnimationData)animationVector.get(i);
                animation = animationData.animation;
                if(animation != null)  animation.draw(g2,animationData.userObject);
            }
        }
    }
    public boolean isFocusTrasversable() 
    {
        return true;
    }    
    public void setCallBack(CallBack callBack)
    {
        this.callBack = callBack;
    }
}
