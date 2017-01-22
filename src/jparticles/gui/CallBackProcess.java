/*
 * CallBackProcess.java
 *
 * Created on 10 December 2001, 18:56
 */

package jparticles.gui;

import java.awt.event.*;

/**
 *
 * @author  carlos
 * @version
 */
public class CallBackProcess extends CallBack implements ActionListener
{    
    private int framesPerSecond;
    private javax.swing.Timer timer;
    private int delay;
    public CallBackProcess(Method method, Object object, int framesPerSecond)
    {
        super(method,object);
        this.framesPerSecond = framesPerSecond;
        delay = (framesPerSecond > 0) ? (1000 / framesPerSecond) : 100;
        timer = new javax.swing.Timer(delay,this);
    }
    public void actionPerformed(java.awt.event.ActionEvent actionEvent)
    {
        method.run(object);
    }
    /** Getter for property framesPerSecond.
     * @return Value of property framesPerSecond.
     */
    public int getFramesPerSecond()
    {
        return framesPerSecond;
    }
    /** Setter for property framesPerSecond.
     * @param framesPerSecond New value of property framesPerSecond.
     */
    public void setFramesPerSecond(int framesPerSecond)
    {
        this.framesPerSecond = framesPerSecond;
        delay = (framesPerSecond > 0) ? (1000 / framesPerSecond) : 100;
        timer.setDelay(delay);
    }
    /** Starts the animation.
     */
    public  void start()
    {
        timer.start();
    }
    /** Stops the animation.
     */
    public  void stop()
    {
        timer.stop();
    }
}
