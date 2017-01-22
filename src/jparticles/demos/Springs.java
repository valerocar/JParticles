package jparticles.demos;

import java.awt.*;

import javax.swing.*;

import jparticles.gui.JParticlesPanel;
import jparticles.gui.StateToJ2DMap;
import jparticles.particles.ParticleSystem;
import jparticles.particles.constraints.ParticlesNail;
import jparticles.particles.potentials.LinearSpringPotential;

public class Springs implements Demo
{
    private final JScrollPane helpPane;
    private int width = 500;
	private int height = 500;
	private int fps = 30;

	private int ringParticlesCount = 8;
	private int particlesCount = 2 * ringParticlesCount; // Two rings in the
															// particle system
	private ParticleSystem particles;
	private ParticlesNail nail; // Used to fix the particles in the outer ring

	private LinearSpringPotential springs;

	private JParticlesPanel particlesPanel;
	private JPanel mainPanel;
	String helpString = "";


	public Springs()
	{
		helpString = HelpLoader.loadHelpFile("Springs");

		//
		// Creating and initializing particle system
		//
		particles = new ParticleSystem(2, particlesCount);
		particles.setMediumViscosity(0.0);
		particles.setTimeIncrement(.1);
		double aa = (width - 100) / 2.0;
		double bb = (height - 100) / 2.0;
		StateToJ2DMap map = new StateToJ2DMap(aa, bb, aa, bb);
		particles.setStateToJ2DMap(map);
		this.setParticlesInitialState();
		this.createSprings();
		particles.add(springs);
		//
		// Creating main particlesPanel
		//
		this.createMainPanel();
		//
		// Fixing particles in the outer ring
		//
		nail = particlesPanel.getNail();
		particles.setVelocityConstraint(nail);
		for (int i = 0; i < ringParticlesCount; i++)
			nail.fix(i);
		nail.setParticleColors(particles);
        helpPane = createHelpPane();
	}

	@Override
	public void initialize() {
		this.setParticlesInitialState();
	}

	public void runInFrame()
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setLocation(0, 0);
		frame.setVisible(true);
		//createMainPanel();
		frame.add(mainPanel);
		particlesPanel.startAnimation();


		JFrame  helpFrame = new JFrame();
		helpFrame.add(helpPane);
		helpFrame.setSize(500,500);
		helpFrame.setLocation(505,0);
		frame.setVisible(true);
		helpFrame.setVisible(true);
	}

	public void setParticlesInitialState()
	{
		double[] zero =
		{ 0.0, 0.0 };
		double[] position = new double[2];
		double angle = 0.0;
		double angleIncrement = 2 * Math.PI / ringParticlesCount;
		double radius = 0.7;
		double radiusIncrement = 0.0;
		for (int i = 0; i < ringParticlesCount; i++)
		{
			particles.setPosition(i, zero);
			position[0] = radius * Math.cos(angle);
			position[1] = radius * Math.sin(angle);
			particles.setPosition(i, position);
			angle += angleIncrement;
			radius += radiusIncrement;
		}
	}

	public void createSprings()
	{
		// Creating a springs potential
		springs = new LinearSpringPotential(2, particlesCount);
		// Add inner and outer rings with springs
		int particleA = 0;
		int particleB = 0;
		int springIndex = 0;
		for (int i = 0; i < ringParticlesCount; i++)
		{
			particleA = i;
			particleB = i + ringParticlesCount;
			springs.setParticlePair(springIndex, particleA, particleB);
			springIndex++;
		}
		// Create springs circle for the inner ring
		int count = ringParticlesCount - 1;
		for (int i = 0; i < count; i++)
		{
			particleA = ringParticlesCount + i;
			particleB = ringParticlesCount + i + 1;
			springs.setParticlePair(springIndex, particleA, particleB);
			springIndex++;
		}
		springs.setParticlePair(springIndex, particleB, ringParticlesCount);
		springs.setGlobalStiffnessFactor(10.0);
	}

	public void createMainPanel()
	{
		//
		// Creating ParticlesPanel
		//
		particlesPanel = particles.createAnimationPanel(width - 100,
				height - 100, fps);
		particlesPanel.setHistorySize(1);
		particlesPanel.setHistoryState(0);
		particlesPanel.storeCurrentState();

		mainPanel = new JPanel();
		mainPanel.setSize(width, height);
		//
		// Creating toolbars
		//
		JSlider slider = springs.getGSFSlider();
		springs.setGSFSliderScale(0.2);
		slider.setValue((int) (5.0 * springs.getGlobalStiffnessFactor()));
		//JToolBar evolTB = particlesPanel.getEvolveToolBar();
		//evolTB.add(slider);
		mainPanel.add(particles.getPropertiesToolBar(), BorderLayout.NORTH);
		mainPanel.add(particlesPanel, BorderLayout.CENTER);
		mainPanel.add(slider, BorderLayout.SOUTH);
	}

	public void startAnimation()
	{
		particlesPanel.startAnimation();
	}

	public void stopAnimation()
	{
		particlesPanel.stopAnimation();
	}

	@Override
	public String getName() {
		return "Springs";
	}

	@Override
	public String getHelp() {
		return helpString;
	}

	private JScrollPane createHelpPane()
	{
		JTextPane helpPane = new JTextPane();
		helpPane.setContentType("text/html");
		helpPane.setText(this.helpString);
		helpPane.setMargin(new Insets(5,20,5,20));

		JScrollPane scrollPane = new JScrollPane(helpPane);
		return scrollPane;
	}



	public JPanel getMainPanel()
    {
        return mainPanel;
    }
    JScrollPane getHelpPane()
    {
        return helpPane;
    }
	public static void main(String[] args)
	{

		Springs demo = new Springs();
		demo.runInFrame();
	}
}
