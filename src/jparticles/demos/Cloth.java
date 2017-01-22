package jparticles.demos;

import jparticles.gui.JParticlesPanel;
import jparticles.maths.calculus.QuadraticCGMinimizer;
import jparticles.particles.ParticleSystem;
import jparticles.particles.constraints.ParticlesNail;
import jparticles.particles.potentials.GravityPotential;
import jparticles.particles.potentials.LinearGridPotential;
import jparticles.particles.potentials.LinksPotential;
import jparticles.particles.potentials.ParticlesPotentialTerm;

import javax.swing.*;
import java.awt.*;

public class Cloth implements Demo {
	int cols =10;
    int rows =10;
    String helpString = "";
    
    ParticleSystem particles = new ParticleSystem(2,cols*rows);
    {
        particles.setTimeIncrement(.1);
        particles.setMediumViscosity(0.0);
        particles.setJ2DDrawableParticles(true);
    }
    int [][] gridIndices;
    LinearGridPotential gridPot = new LinearGridPotential(2,cols,rows);
    {
        gridIndices = gridPot.getGridIndices();
        gridPot.setGlobalBendFactor(00.0);
        gridPot.setGlobalStretchFactor(15.0);
        particles.add(gridPot);
    }
    JParticlesPanel particlesPanel = particles.createAnimationPanel(512,512,50);
    ParticlesNail nail = particlesPanel.getNail();
    {
        particles.setVelocityConstraint(nail);
        gridPot.fixBorders(nail);
        gridPot.setFrame(-.5,.5,-.5,.5,particles);
        particles.setVelocityConstraint(nail);
        nail.setParticleColors(particles);
    }
    QuadraticCGMinimizer qm;
    ParticlesPotentialTerm nonSingPot;
    
    int width = 500, height = 500;
    JTextPane helpPane;
    public Cloth()
    {
        helpString = HelpLoader.loadHelpFile("Cloth");

        qm = new QuadraticCGMinimizer();
        nonSingPot = createNonSingPart(gridPot.getGridIndices());
        qm.set(particles);
        qm.setFilter(nail);
        qm.minimizeFunction();
        nail.releaseAll();
        nail.fix(gridIndices[0][rows-1]);
        nail.fix(gridIndices[cols-1][rows-1]);
        nail.fix(gridIndices[cols-1][0]);
        nail.setParticleColors(particles);
        
        particles.add(nonSingPot);

        JToolBar toolBar = new JToolBar();
        gridPot.setGBFSliderScale(0.1);
        gridPot.setGSFSliderScale(0.1);
        double [] g =
        {0.0,-0.1};
        particles.add(new GravityPotential(2,cols*rows,g));
        toolBar.add(gridPot.getGBFSlider());
        toolBar.add(gridPot.getGSFSlider());

        //getContentPane().add(toolBar,BorderLayout.SOUTH);
    }

    @Override
    public void initialize() {

    }

    public void runInFrame()
    {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLocation(0, 0);
        frame.setVisible(true);
        //createMainPanel();
        frame.add(particlesPanel);
        particlesPanel.startAnimation();


        createHelpPane();
        JFrame  helpFrame = new JFrame();
        helpFrame.add(helpPane);
        helpFrame.setSize(500,500);
        helpFrame.setLocation(505,0);
        frame.setVisible(true);
        helpFrame.setVisible(true);
        helpFrame.setVisible(true);

        particlesPanel.startAnimation();
    }
    
    ParticlesPotentialTerm createNonSingPart(int [][] g)
    {
        int [] I = new int[4*(cols-1)*(rows-1)];
        int count = 0;
        for(int i = 0; i < (cols-1); i++)
        {
            for(int j = 0; j < (rows-1); j++)
            {
                I[count++] = g[i][j];
                I[count++] = g[i+1][j+1];
                I[count++] = g[i][j+1];
                I[count++] = g[i+1][j];
            }
        }
        LinksPotential pot = new LinksPotential(particles,I,.1);
        pot.setConstraintsCeofficients(50.0);
        pot.setDampingsCoefficients(0.0);
        return pot;
    }


    private void createHelpPane()
    {
        helpPane = new JTextPane();
        helpPane.setContentType("text/html");
        helpPane.setText(this.helpString);
        helpPane.setMargin(new Insets(5,20,5,20));
    }



    public JPanel getMainPanel()
    {
        return particlesPanel;
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
        return "Cloth";
    }

    @Override
    public String getHelp() {
        return helpString;
    }

    public static void main(String[] args) {

        Cloth demo = new Cloth();
        demo.runInFrame();
	}

}
