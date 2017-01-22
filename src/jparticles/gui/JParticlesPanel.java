/*
 * ParticlesPanel.java
 *
 * Created on 15 September 2001, 13:58
 */

package jparticles.gui;

/**
 *
 * @author  carlosv
 * @version
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import jparticles.particles.*;
import jparticles.particles.constraints.*;

/** This class is used to display and manipulate particle systems.
 * @see jparticles.particles.ParticleSystem
 */
public class JParticlesPanel extends JAnimationPanel implements MouseListener, MouseMotionListener, KeyListener
{
    private ParticleSystem particles = null;
    private int selectedParticle = -1;
    private double [] particlePos;
    private StateToJ2DMap stateToJ2DMap;    
    
    private J2DDrawableObject mousePointer;
    private int [] mousePos = new int[2];
    private ParticlesNail nail;
    private double [] mouseNormalizedPos = new double[2];
    
    private double hCenter;
    private double vCenter;    
    private boolean frozen = false;
    
    private JToolBar evolveToolBar;
    private JToolBar historyToolBar;
       
    private JLabel currenLabel;
    private int currentInHistory = 0;
    private int historySize;    
    private double [][] posHistory;
    private double [][] velHistory;
    // Helping variables
    double [] partPos;
    double [] j2dparam;
    
/** Create a JParticlesPanel with the specified parameters.
 * @param particles a ParticleSystem object
 * @param width the width of the panel
 * @param height the height of the panel
 * @param fps animation rate in frames per second
 */        
    public JParticlesPanel(ParticleSystem particles, int width, int height, int fps)
    {
        super(width, height, fps);
        this.particles = particles;
        partPos= new double[particles.getParticleDimension()];
        createMousePointer();
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        this.add(new DrawingData(mousePointer, mousePos));        
        stateToJ2DMap = particles.getStateToJ2DMap();        
        j2dparam = stateToJ2DMap.getParameters();
        this.add(new AnimationData(particles, null));
        nail = new ParticlesNail(particles.getParticleDimension(),particles.getParticlesCount());
        createEvolveToolBar();
        createHistoryToolBar();
    }
        
    public void setStateToJ2DMap(StateToJ2DMap map)
    {
        stateToJ2DMap = map;
    }
    private void createMousePointer()
    {
        mousePointer = new J2DDrawableObject()
        {
            int[]  java2Dcoord = new int[2];
            public void draw(Graphics2D g, Object obj)
            {
                int [] pos = (int []) obj;
                g.setColor(Color.white);
                g.drawRect(pos[0]-5,pos[1]-5,10,10);
                if(selectedParticle >= 0)
                {
                    particlePos = particles.getPosition(selectedParticle);
                    stateToJ2DMap.mapStateToJ2D(particlePos,java2Dcoord);
                    g.fillRect(java2Dcoord[0]-4,java2Dcoord[1]-4,8,8);
                }
            }
        };
    }
    public void mouseReleased(java.awt.event.MouseEvent evt)
    {
    }
    public void mouseEntered(java.awt.event.MouseEvent evt)
    {
        requestFocus();
    }
    public void mouseClicked(java.awt.event.MouseEvent evt)
    {
    }
    public void mousePressed(java.awt.event.MouseEvent evt)
    {
        requestFocus();
        mousePos[0]= evt.getX();
        mousePos[1]= evt.getY();
        setPointerNode();
        if(SwingUtilities.isRightMouseButton(evt))
        {
            if(selectedParticle >= 0)
            {
                if(!nail.isFixed(selectedParticle))
                {
                    nail.fix(selectedParticle);
                    particles.setParticleColor(selectedParticle,nail.getFixedColor());
                    particles.filterVelocities();
                }
                else
                {
                    nail.release(selectedParticle);
                    particles.setParticleColor(selectedParticle,nail.getFreeColor());
                    particles.filterVelocities();
                }
                
            }
        }
        repaint();
        
    }
    public void mouseExited(java.awt.event.MouseEvent evt)
    {
    }
    public void mouseDragged(java.awt.event.MouseEvent evt)
    {
        if(SwingUtilities.isLeftMouseButton(evt))
        {
            if(selectedParticle > -1)
            {
                mousePos[0]= evt.getX();
                mousePos[1]= evt.getY();
                stateToJ2DMap.mapJ2DtoState(mousePos,partPos);
                particles.setPosition(selectedParticle,partPos);
            }
        }
    }
    public void mouseMoved(java.awt.event.MouseEvent evt)
    {
        mousePos[0]= evt.getX();
        mousePos[1]= evt.getY();
        repaint();
    }
    public void keyReleased(java.awt.event.KeyEvent evt)
    {
    }
    public void keyPressed(java.awt.event.KeyEvent evt)
    {
        char key = evt.getKeyChar();
        if(key == ' ')
        {
            if(frozen)
            {
                frozen = false;
                startAnimation();
                //remove(mousePointer,mousePos);
            }
            else
            {
                stopAnimation();
                frozen = true;
                //add(mousePointer,mousePos);
            }
        }
        if(key == 'k')
        {            
            j2dparam[0] += 4.0;
            repaint();
        }
        if(key == 'j')
        {            
            j2dparam[0] -= 4.0;
            repaint();
        }
        if(key == 'i')
        {            
            j2dparam[1] -= 4.0;
            repaint();
        }
        if(key == 'm')
        {            
            j2dparam[1] += 4.0;
            repaint();
        }
        if(key == 'z')
        {
            j2dparam[2] += 4.0;
            j2dparam[3] += 4.0;
        }
        if(key == 'x')
        {
            j2dparam[2] -= 4.0;
            j2dparam[3] -= 4.0;
        }
    }
    public void keyTyped(java.awt.event.KeyEvent evt)
    {
    }
    void setPointerNode()
    {
        stateToJ2DMap.mapJ2DtoState(mousePos,partPos);
        selectedParticle = particles.getNearestParticle(partPos);
    }
/** Returns the index of the particle after having selected it with the mouse
 * by clicking on it.
 * @return a particle index
 */    
    public int getSelectedParticle()
    {
        return selectedParticle;
    }
/** Returns the particle system which this panel is displaying and controlling.
 * @return a ParticleSystem object
 */    
    public ParticleSystem getParticleSystem()
    {
        return particles;
    }
/** Gets a ParticleNail constraint which can be controlled by the panel.
 * By right clicking on a particle the user can alternate the state of the
 * selected particle between fixed an released. This fixed particles can then
 * be dragged using the mouse.
 * @return a ParticlesNail object
 */    
    public ParticlesNail getNail()
    {
        if(particles == null)
        {
            System.out.println("You must set a particle system first");
            System.exit(-1);
        }
        return nail;
    }
/** Gets a swing component to control the evolution of the particle system.
 * @return A JToolBar object
 */    
    public JToolBar getEvolveToolBar()
    {
        return evolveToolBar;
    }
/** Gets a swing component used to memorize and recall states of the underlying
 * particle system.
 * @return a JToolBar object
 */    
    public JToolBar getHistoryToolBar()
    {
        return historyToolBar;
    }
    private void createHistoryToolBar()
    {
        historyToolBar = new JToolBar();
        currenLabel = new JLabel("0", SwingConstants.CENTER);        
        String path = "/jparticles/resources/icons/";
        JButton saveButton = new JButton("store");
        JButton recallButton = new JButton("recall");
        JButton nextButton = new JButton();
        JButton previousButton = new JButton();
                
        previousButton.setIcon(new ImageIcon(getClass().getResource(path+"Back16.gif")));        
        nextButton.setIcon(new ImageIcon(getClass().getResource(path+"Forward16.gif")));
                
        historyToolBar.add(recallButton);
        historyToolBar.add(saveButton);
        historyToolBar.add(previousButton);        
        historyToolBar.add(currenLabel);
        historyToolBar.add(nextButton);
        
        nextButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {         
                if(currentInHistory == historySize-1) return;
                currentInHistory++;
                currenLabel.setText(String.valueOf(currentInHistory));
            }
        });
        previousButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {                
                if(currentInHistory == 0) return;                
                currentInHistory--;
                currenLabel.setText(String.valueOf(currentInHistory));
            }
        });
        saveButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {                
                storeCurrentState();
            }
        });
        recallButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {                
                recallStateInHistory(currentInHistory);
            }
        });
    }
    private void createEvolveToolBar()
    {
        evolveToolBar = new JToolBar();
        JButton stopButton;
        JButton playButton;
        JButton evolveBackButton;
        JButton stepForwardButton;               
        String path = "/jparticles/resources/icons/";
        stopButton = new JButton();
        playButton = new JButton();
        evolveBackButton = new JButton();
        stepForwardButton = new JButton();
                
        stopButton.setIcon(new ImageIcon(getClass().getResource(path+"Stop16.gif")));
        playButton.setIcon(new ImageIcon(getClass().getResource(path+"Play16.gif")));
        stepForwardButton.setIcon(new ImageIcon(getClass().getResource(path+"StepForward16.gif")));        
        
        evolveToolBar.add(playButton);
        evolveToolBar.add(stopButton);        
        evolveToolBar.add(stepForwardButton);        
        
        playButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                startAnimation();
            }
        });
        stopButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                stopAnimation();
            }
        });
        stepForwardButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                particles.evolve();
                repaint();
            }
        });
    }
/** Sets the size of the memory for the panel. The panel can store states of the
 * particle system.
 * @param historySize the memory size
 */    
    public void setHistorySize(int historySize)
    {
        this.historySize = historySize;
        int baseDim = particles.getBaseDimension();
        posHistory = new double[historySize][baseDim];
        velHistory = new double[historySize][baseDim];
    }
/** Memorizes the current state of the particle system.
 */    
    public void storeCurrentState()
    {
        double [] mpos = posHistory[currentInHistory];
        double [] mvel = velHistory[currentInHistory];
        double [] ppos = particles.getPosition();
        double [] pvel = particles.getVelocity();
        int count = particles.getBaseDimension();        
        for(int i = 0 ; i < count; i++) 
        {
            mpos[i] = ppos[i];
            mvel[i] = pvel[i];
        }
    }
/** Recalls the the given state for the given memory number.
 * @param memoryNumber the memory number
 */    
    public void recallStateInHistory(int memoryNumber)
    {
        stopAnimation();
        double [] mpos = posHistory[currentInHistory];
        double [] mvel = velHistory[currentInHistory];
        particles.setPosition(mpos);
        particles.setVelocity(mvel);
        this.repaint();
    }
/** Sets the memory number which other methods related to state memory will be
 * dealing with.
 * @param memoryNumber the memory number
 */    
    public void setHistoryState(int memoryNumber)
    {
        currentInHistory = memoryNumber;
    }        
}


