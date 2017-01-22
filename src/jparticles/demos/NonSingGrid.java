package jparticles.demos;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jparticles.gui.JParticlesPanel;
import jparticles.particles.ParticleSystem;
import jparticles.particles.constraints.ParticlesNail;
import jparticles.particles.potentials.NonSingGridPotential;


public class NonSingGrid implements Demo {
	private int width = 500;
	private int height = 500;
	// Grid dimensions
	private int cols = 20;
	private int rows = 20;
	// Parameters used for the deformation of the grid
	private double ACB = 0.0;
	private double BCB = (ACB - 2) / 4.0;
	// This slider is used to deform the lower boundary of the grid
	JSlider boundarySlider;
	JPanel mainPanel = new JPanel();
	ParticleSystem particles = new ParticleSystem(2, cols * rows);
	{
		particles.setTimeIncrement(.1);
		particles.setMediumViscosity(0.2);
		particles.setJ2DDrawableParticles(true);
	}
	int[][] gridIndices;
	NonSingGridPotential gridPot = new NonSingGridPotential(2, cols, rows);
	{
		gridIndices = gridPot.getGridIndices();
		gridPot.setGlobalBendFactor(10.0);
		gridPot.setGlobalStretchFactor(10.0);
		gridPot.setGlobalStretchFactor(1.5);
		gridPot.setLocalVolumeValue(.025);
		gridPot.setNonSingStiffness(0.0);
		particles.add(gridPot);
	}
	JParticlesPanel particlesPanel = particles.createAnimationPanel(512, 512, 50);
	ParticlesNail nail = particlesPanel.getNail();
	{
		particles.setVelocityConstraint(nail);
		gridPot.fixBorders(nail);

		gridPot.setFrame(-.5, .5, -.5, .8, particles);
		deformBoundary();
		particles.setVelocityConstraint(nail);
		nail.setParticleColors(particles);
	}
	JToolBar toolBar = new JToolBar();
	public String helpString;

	public NonSingGrid() {
		helpString = HelpLoader.loadHelpFile("NonSingGrid");

		gridPot.setGBFSliderScale(0.1);
		createToolBar();
		mainPanel = new JPanel();
		mainPanel.setSize(width, height);
		//JToolBar et = particlesPanel.getEvolveToolBar();
		//et.add(particles.getPropertiesToolBar());

		JPanel propertiesPanel = new JPanel();
		propertiesPanel.add(particles.getPropertiesToolBar());
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(particlesPanel,BorderLayout.CENTER);
		mainPanel.add(propertiesPanel, BorderLayout.SOUTH);
		mainPanel.add(toolBar, BorderLayout.NORTH);
	}

	public void runInFrame()
	{
		JFrame frame = new JFrame();
		frame.setSize(width, height);
		frame.add(mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setLocation(0, 0);
		frame.setVisible(true);
		createHelpFrame().setVisible(true);
		particlesPanel.startAnimation();
	}

	void createToolBar() {
		boundarySlider = new JSlider();
		toolBar.add(boundarySlider);
		JSlider nonSingSlider = gridPot.getNonSingFactorSlider();
		nonSingSlider.setValue(0);
		gridPot.setNonSingSliderScale(3.0);
		toolBar.add(nonSingSlider);
		boundarySlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSlider slider = (JSlider) evt.getSource();
				ACB = ((double) slider.getValue() - 50) / 16.0;
				BCB = (ACB - 2) / 4.0;
				deformBoundary();
				particlesPanel.repaint();
			}
		});
	}


	void deformBoundary() {
		double[] pos = new double[2];
		double[] ppos;
		for (int i = 0; i < cols; i++) {
			ppos = particles.getPosition(gridIndices[i][0]);
			pos[0] = ppos[0];
			pos[1] = -ACB * pos[0] * pos[0] + BCB;
			particles.setPosition(gridIndices[i][0], pos);
		}
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
	public static void main(String[] args) {
		new NonSingGrid().runInFrame();
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
		return "NonSingGrid";
	}

	@Override
	public String getHelp() {
		return helpString;
	}
}
