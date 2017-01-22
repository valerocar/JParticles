package jparticles.demos;

import jparticles.gui.JAnimationPanel.AnimationData;
import jparticles.gui.JAnimationPanel.J2DAnimation;
import jparticles.gui.JParticlesPanel;
import jparticles.gui.StateToJ2DMap;
import jparticles.maths.calculus.FunctionJet2;
import jparticles.maths.calculus.FunctionJet2Adapter;
import jparticles.particles.ParticleSystem;
import jparticles.particles.potentials.GravityPotential;
import jparticles.particles.potentials.SpatialPotential;
import jparticles.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ParticlesInPotential implements J2DAnimation, Demo{

	private int particleCounter = 0;
	private double[] fireVel = { 0.0, 1.0 };
	private int firedParticle = 0;
	private FunctionJet2 potFunct;
	private BufferedImage image;
	private JParticlesPanel panel;
	private JPanel mainPanel;
	private int particlesCount = 150;
	private ParticleSystem particles = new ParticleSystem(2, particlesCount);
	private SpatialPotential potential;

	private int width = 500;
	private int height = 500;
	private double particleAngle = 0.0;
	private double particleAngleInc = .3;

	double[] g = { 0,-1};
	int spotsCount = 5;
	double[] sigmas = { .1, .1, .1, .2, .2 };
	double[][] centres = { { -0.5, 0.3 }, { 0.0, -0.3 }, { 0. - .5, -.7 },
			{ 0.5, 0.5 }, { 0.8, 0.0 } };
	double theta = 0.0;
	double thetaInc = .1;
	ImageUtils.FunctionWindow fw;

	public ParticlesInPotential() {
		helpString = HelpLoader.loadHelpFile("ParticlesInPotential");

		createPotentialFunction();
		//
		// Creating function and its image.
		//
		fw = new ImageUtils.FunctionWindow(potFunct, -1.0, -1.0, 1.0, 1.0);
		image = new BufferedImage(width, height, 5);
		ImageUtils.getImageFromFunction(fw, width - 100, height - 100, 255.0,
				image);
		//
		// Creating main particlesPanel
		//
		potential = new SpatialPotential(potFunct, particlesCount);
		particles.add(potential);
		GravityPotential gravity = new GravityPotential(2, particlesCount, g);
		particles.add(gravity);
		initilizeParticles();
		particles.setTimeIncrement(.025);
		particles.setMediumViscosity(0.0);
		double aa = (width - 100) / 2.0;
		double bb = (height - 100) / 2.0;
		StateToJ2DMap map = new StateToJ2DMap(aa, bb, aa, bb);
		particles.setStateToJ2DMap(map);
		createMainPanel();
		//
		// Creating and initializing a snake
		//

	}

	public void runInFrame()
	{
		JFrame frame = new JFrame();
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setLocation(0, 0);
		frame.add(mainPanel);
		frame.setVisible(true);
		panel.startAnimation();
		createHelpFrame().setVisible(true);
	}
	public void initilizeParticles() {
		double[] pos = { .0, -1.0 };
		double[] vel = { 0.0, 0.0 };
		for (int i = 0; i < particlesCount; i++) {
			particles.setPosition(i, pos);
		}
		particles.setVelocity(0, vel);
	}

	public void start() {
		panel.startAnimation();
	}

	public void stop() {
		panel.stopAnimation();
	}

	public void destroy() {
	}

	public void createMainPanel() {
		//
		// Creating particles particlesPanel
		//
		panel = particles.createAnimationPanel(width - 100, height - 100, 50);
		panel.setBackgroundImage(image);
		panel.add(new AnimationData(this, null));
		//
		// Creating main particlesPanel
		//
		mainPanel = new JPanel();
		mainPanel.setSize(width, height);
		//
		// Adding toolbars
		//
		mainPanel.add(particles.getPropertiesToolBar(), BorderLayout.NORTH);
		mainPanel.add(panel, BorderLayout.CENTER);
		//
		// Adding main particlesPanel to the applets' content pane
		//

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

	public void updateAimationState(Object userObject) {
		if (particleCounter == 2) {
			firedParticle++;
			double[] pos = { 0.0, 0.3 };
			double[] zero = { 0.0, 0.0 };
			if (firedParticle >= particlesCount) {
				firedParticle = 0;
			}
			fireVel[0] = Math.cos(.05 * particleAngle);
			fireVel[1] = Math.sin(.05 * particleAngle);
			particles.setPosition(firedParticle, pos);
			particles.setVelocity(firedParticle, fireVel);
			particleCounter = 0;
		}
		particleCounter++;
		particleAngle += particleAngleInc;
	}

	public void draw(Graphics2D g, Object userObject) {
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
		new ParticlesInPotential().runInFrame();
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
		panel.startAnimation();
	}

	@Override
	public void stopAnimation() {
		panel.stopAnimation();
	}

	@Override
	public String getName() {
		return "ParticlesInPotential";
	}

	@Override
	public String getHelp() {
		return helpString;
	}
}
