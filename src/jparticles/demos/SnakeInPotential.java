package jparticles.demos;

import jparticles.gui.JParticlesPanel;
import jparticles.gui.StateToJ2DMap;
import jparticles.maths.calculus.FunctionJet2;
import jparticles.maths.calculus.FunctionJet2Adapter;
import jparticles.particles.ParticleSystem;
import jparticles.particles.constraints.ParticlesNail;
import jparticles.particles.potentials.LinearSnakePotential;
import jparticles.particles.potentials.SpatialPotential;
import jparticles.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SnakeInPotential implements Demo {

	private FunctionJet2 potFunct;
	private BufferedImage image;
	private JParticlesPanel particlesPanel;
	private JPanel mainPanel;
	private int particlesCount = 60;
	private ParticleSystem particles = new ParticleSystem(2, particlesCount);
	private SpatialPotential potential;

	private LinearSnakePotential snakePot = new LinearSnakePotential(2,
			particlesCount);
	{
		snakePot.setGlobalStretchFactor(15.0);
		snakePot.setGlobalBendFactor(10.0);
	}
	private ParticlesNail nail;

	private int width = 500;
	private int height = 500;

	int spotsCount = 4;
	double[] sigmas = { .1, .2, .15, .12 };
	double[][] centres = { { -0.5, 0.3 }, { 0.0, -0.3 }, { 0.0, -.7 },
			{ 0.5, 0.5 } };
	double theta = 0.0;
	double thetaInc = .1;
	ImageUtils.FunctionWindow fw;



	public SnakeInPotential() {
		helpString = HelpLoader.loadHelpFile("SnakeInPotential");

		//
		// Creating function and its image.
		//
		createPotentialFunction();
		fw = new ImageUtils.FunctionWindow(potFunct, -1.0, -1.0, 1.0, 1.0);
		image = new BufferedImage(width, height, 5);
		ImageUtils.getImageFromFunction(fw, width - 100, height - 100, 255.0,
				image);
		//
		// Creating and initializing a snake
		//
		double aa = (width - 100) / 2.0;
		double bb = (height - 100) / 2.0;
		StateToJ2DMap map = new StateToJ2DMap(aa, bb, aa, bb);
		particles.setStateToJ2DMap(map);
		//
		// Creating main particlesPanel
		//
		createMainPanel();

		potential = new SpatialPotential(potFunct, particlesCount);
		particles.add(potential);
		nail = particlesPanel.getNail();
		nail.fix(0);
		nail.fix(particlesCount - 1);
		nail.setParticleColors(particles);
		particles.setVelocityConstraint(nail);
		double[] pos = { .5, .5 };
		particles.setPosition(0, pos);
		pos[0] = -.5;
		particles.setPosition(1, pos);
		particles.setTimeIncrement(.1);
		particles.setMediumViscosity(.5);
		particles.add(snakePot);


	}

	public void createMainPanel() {

		//
		// Creating particles particlesPanel
		//
		particlesPanel = particles.createAnimationPanel(width - 100, height - 100, 50);
		particlesPanel.setBackgroundImage(image);
		//
		// Creating main particlesPanel
		//
		mainPanel = new JPanel();
		mainPanel.setSize(width, height);
		//
		// Adding toolbars
		//
		JSlider sliderS = snakePot.getGSFSlider();
		JSlider sliderB = snakePot.getGBFSlider();
		sliderS.setPreferredSize(new Dimension(130, 30));
		sliderB.setPreferredSize(new Dimension(130, 30));
		sliderS.setValue((int) snakePot.getGlobalStretchFactor());
		sliderB.setValue((int) snakePot.getGlobalBendFactor());
		//JToolBar evolTB = particlesPanel.getEvolveToolBar();
		//evolTB.add(sliderS);
		//evolTB.add(sliderB);
		JPanel slidersPanel = new JPanel();
		slidersPanel.add(sliderS);
		slidersPanel.add(sliderB);
		mainPanel.add(particles.getPropertiesToolBar(), BorderLayout.NORTH);
		mainPanel.add(particlesPanel, BorderLayout.CENTER);
		mainPanel.add(slidersPanel, BorderLayout.SOUTH);
	}

	public void runInFrame()
	{
		JFrame frame = new JFrame();
		frame.setSize(width, height);
		frame.add(mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setLocation(0, 0);
		particlesPanel.startAnimation();
		frame.setVisible(true);
		createHelpFrame().setVisible(true);



	}

	public void createPotentialFunction() {
		potFunct = new FunctionJet2Adapter(2) {
			public void updateValue() {
				value = 0;
				double sigmaSquared;
				double exp;
				double x, y;
				double[] centre;
				for (int i = 0; i < spotsCount; i++) {
					sigmaSquared = sigmas[i] * sigmas[i];
					centre = centres[i];
					x = state[0] - centre[0];
					y = state[1] - centre[1];
					value += Math.exp(-(x * x + y * y) / sigmaSquared);
				}
			}

			public void updateGradient() {
				gradient[0] = 0.0;
				gradient[1] = 0.0;
				double sigmaSquared;
				double x, y;
				double exp;
				double[] centre;
				for (int i = 0; i < spotsCount; i++) {
					sigmaSquared = sigmas[i] * sigmas[i];
					centre = centres[i];
					x = state[0] - centre[0];
					y = state[1] - centre[1];
					exp = Math.exp(-(x * x + y * y) / sigmaSquared);
					gradient[0] -= (2 * exp * x) / sigmaSquared;
					gradient[1] -= (2 * exp * y) / sigmaSquared;
				}
			}

			public void updateHessian(double[] direction) {
				hessian[0] = 0.0;
				hessian[1] = 0.0;
				double f_xx = 0.0;
				double f_xy = 0.0;
				double f_yy = 0.0;
				double exp;
				double sigmaSquared;
				double sigmaFourth;
				double x, y;
				double[] centre;
				for (int i = 0; i < spotsCount; i++) {
					sigmaSquared = sigmas[i] * sigmas[i];
					sigmaFourth = sigmaSquared * sigmaSquared;
					centre = centres[i];
					x = state[0] - centre[0];
					y = state[1] - centre[1];
					exp = Math.exp(-(x * x + y * y) / sigmaSquared);
					f_xx = -2 * exp * (sigmaSquared - 2 * x * x) / sigmaFourth;
					f_yy = -2 * exp * (sigmaSquared - 2 * y * y) / sigmaFourth;
					f_xy = 4 * exp * x * y / sigmaFourth;
					hessian[0] += f_xx * direction[0] + f_xy * direction[1];
					hessian[1] += f_xy * direction[0] + f_yy * direction[1];
				}
			}
		};
	}

	String helpString = "";

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
		new SnakeInPotential().runInFrame();
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
		return "SnakeInPotential";
	}

	@Override
	public String getHelp() {
		return helpString;
	}
}
