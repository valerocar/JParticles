package jparticles.demos;

import java.awt.*;

import javax.swing.*;

import jparticles.gui.JParticlesPanel;
import jparticles.gui.StateToJ2DMap;
import jparticles.particles.ParticleSystem;
import jparticles.particles.constraints.ParticlesNail;
import jparticles.particles.potentials.LinearSnakePotential;


public class Snake implements Demo{
	private JPanel mainPanel;
    private int fps = 50;
    private JParticlesPanel particlesPanel;    
    private int particlesCount = 30;
    private ParticleSystem particles;    
    private LinearSnakePotential snakePot;
    private int width = 500;
    private int height = 500;    
    private ParticlesNail nail;    

    public Snake()
    {
        helpString = HelpLoader.loadHelpFile("Snake");

        //
        // Creating and initializing particle system
        //
        particles =  new ParticleSystem(2,particlesCount);        
        this.setParticlesInitialState();  
        
        particles.setMediumViscosity(1.0);
        particles.setTimeIncrement(0.5);
        double a = (width-100)/2.0;
        double b = (height-100)/2.0;
        StateToJ2DMap map = new StateToJ2DMap(a,b,a,b);
        particles.setStateToJ2DMap(map);
        snakePot  = new LinearSnakePotential(2,particlesCount);        
        snakePot.setGlobalStretchFactor(10.0);
        snakePot.setGlobalBendFactor(10.0);
        particles.add(snakePot);
        //
        // Createing the main particlesPanel
        //        
        createMainPanel();                 
        //
        // Adding nail constraints
        //                
        nail = particlesPanel.getNail();
        particles.setVelocityConstraint(nail);
        nail = particlesPanel.getNail();
        nail.fix(0);
        nail.fix((particlesCount-1)/3);
        nail.fix(2*(particlesCount-1)/3);
        nail.fix(particlesCount-1);        
        particles.setVelocityConstraint(nail);
        nail.setParticleColors(particles);
    }

    public void runInFrame()
    {
        JFrame frame = new JFrame();
        frame.setSize(width,height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLocation(0, 0);
        frame.add(mainPanel);
        frame.setVisible(true);
        particlesPanel.startAnimation();
        createHelpFrame().setVisible(true);
    }
    
    public void setParticlesInitialState()
    {        
        double [] pos = {-.5,.0};
        particles.setPosition(0,pos);
        pos[0] = -.25;
        pos[1] = .5;
        particles.setPosition((particlesCount-1)/3,pos);
        pos[0] = .25;
        pos[1] = -.5;
        particles.setPosition(2*(particlesCount-1)/3,pos);        
        pos[0] = .5;
        pos[1] = .0;
        particles.setPosition(particlesCount-1,pos);
    }
    
    public void createMainPanel()
    {        

        //
        // Creating particles particlesPanel
        //
        particlesPanel = particles.createAnimationPanel(width-100,height-100,fps);
        particlesPanel.setHistorySize(1); 
        particlesPanel.setHistoryState(0);
        particlesPanel.storeCurrentState();
        //
        // Creating main particlesPanel
        //
        mainPanel = new JPanel();;
        mainPanel.setSize(width,height);
        //
        // Adding toolbars
        //
        JSlider sliderS = snakePot.getGSFSlider();             
        JSlider sliderB = snakePot.getGBFSlider();        
        sliderS.setPreferredSize(new Dimension(130,30));
        sliderB.setPreferredSize(new Dimension(130,30));        
        sliderS.setValue((int)snakePot.getGlobalStretchFactor());
        sliderB.setValue((int)snakePot.getGlobalBendFactor());
        //JToolBar evolTB = particlesPanel.getEvolveToolBar();
        //evolTB.add(sliderS);
        //evolTB.add(sliderB);
        JPanel slidersPanel = new JPanel();
        slidersPanel.add(sliderS);
        slidersPanel.add(sliderB);
        JToolBar propTB = particles.getPropertiesToolBar();
        mainPanel.add(propTB,BorderLayout.NORTH);
        mainPanel.add(particlesPanel,BorderLayout.CENTER);
        mainPanel.add(slidersPanel,BorderLayout.SOUTH);
    }

    private String helpString = "";


    private JFrame createHelpFrame()
    {
        JTextPane helpPane = new JTextPane();
        helpPane.setContentType("text/html");
        helpPane.setText(this.helpString);
        helpPane.setMargin(new Insets(5,20,5,20));

        JScrollPane scrollPane = new JScrollPane(helpPane);
        JFrame  helpFrame = new JFrame();
        helpFrame.add(scrollPane);
        helpFrame.setSize(500,500);
        helpFrame.setLocation(505,0);
        helpFrame.setVisible(true);

        return helpFrame;
    }
    
    public static void main(String[] args) {
		new Snake().runInFrame();
	}


    @Override
    public void initialize() {

    }

    @Override
    public JPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    public void startAnimation() {
        particlesPanel.startAnimation();
    }

    @Override
    public void stopAnimation() {
        particlesPanel.stopAnimation();
    }

    @Override
    public String getName() {
        return "Snake";
    }

    @Override
    public String getHelp() {
        return helpString;
    }
}
