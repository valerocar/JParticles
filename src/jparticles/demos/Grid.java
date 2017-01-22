package jparticles.demos;

import java.awt.*;

import javax.swing.*;

import jparticles.gui.JParticlesPanel;
import jparticles.gui.StateToJ2DMap;
import jparticles.particles.ParticleSystem;
import jparticles.particles.constraints.ParticlesNail;
import jparticles.particles.potentials.LinearGridPotential;


public class Grid implements Demo {
	
	private JParticlesPanel particlesPanel;    
    private int particlesCountW = 20;
    private int particlesCountH = 20;
    private ParticleSystem particles;    
    private LinearGridPotential gridPot;    
    private int fps = 100;
    private ParticlesNail nail;
    private int width = 500, height = 500;
    public String helpString = "";
    private JPanel mainPanel = new JPanel();


    public Grid()
    {
        //
        // Creating and initializing particle system
        //
        particles = new ParticleSystem(2, particlesCountW*particlesCountH);          
        gridPot = new LinearGridPotential(2,particlesCountW,particlesCountH);
        
        
        
        
        particles.setMediumViscosity(0.05);
        particles.setTimeIncrement(.1);        
        gridPot.setGlobalBendFactor(10.0);
        gridPot.setGlobalStretchFactor(10.0);
        gridPot.setFrame(-.9,.9,-.9,.9,particles);
        particles.add(gridPot);
        double a = (width-100)/2.0;
        double b = (height-100)/2.0;
        StateToJ2DMap map = new StateToJ2DMap(a,b,a,b);
        particles.setStateToJ2DMap(map);
        //
        // Creating main particlesPanel
        //
        createMainPanel();         
        //
        // Adding velocity constraints
        //         
        nail = particlesPanel.getNail(); 
        gridPot.fixBorders(nail);
        nail.setParticleColors(particles);
        particles.setVelocityConstraint(nail);
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
        createHelpFrame().setVisible(true);
        particlesPanel.startAnimation();
    }
          
    public void createMainPanel()
    {
        helpString = HelpLoader.loadHelpFile("Grid");
        particlesPanel = particles.createAnimationPanel(width-100,height-100,fps);
        particlesPanel.setHistorySize(1); 
        particlesPanel.setHistoryState(0);
        particlesPanel.storeCurrentState();
        particles.setJ2DDrawableParticles(true);
        //
        // Creating toolbars
        //
        JSlider sliderS = gridPot.getGSFSlider();                     
        JSlider sliderB = gridPot.getGBFSlider();

        sliderS.setPreferredSize(new Dimension(130,30));
        sliderB.setPreferredSize(new Dimension(130,30));        
        sliderS.setValue((int)gridPot.getGlobalStretchFactor());
        sliderB.setValue((int)gridPot.getGlobalBendFactor());
        JToolBar evolTB = particlesPanel.getEvolveToolBar();
        evolTB.add(sliderS);
        evolTB.add(sliderB);
        JPanel slidersPanel = new JPanel();
        slidersPanel.add(sliderS);
        slidersPanel.add(sliderB);
        //
        // Creating main particlesPanel
        //
        mainPanel.setSize(width,height);
        mainPanel.add(particles.getPropertiesToolBar(),BorderLayout.NORTH);
        mainPanel.add(particlesPanel,BorderLayout.CENTER);
        mainPanel.add(slidersPanel,BorderLayout.SOUTH);
    }


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
        return "Grid";
    }

    @Override
    public String getHelp() {
        return helpString;
    }

    public static void main(String[] args)
    {
        Grid demo = new Grid();
        demo.runInFrame();
    }
}
