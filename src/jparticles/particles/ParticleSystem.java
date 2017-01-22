/*
 * ParticleSystem.java
 *
 * Created on 09 May 2001, 13:00
 */

package jparticles.particles;

/**
 *
 * @author  carlosv
 * @version
 */
import jparticles.maths.calculus.*;
import jparticles.maths.linearalgebra.*;

import java.util.Random;
import java.util.Vector;
import jparticles.particles.potentials.*;
import java.awt.*;
import java.awt.event.*;
import jparticles.gui.*;
import jparticles.gui.JAnimationPanel.*;
import javax.swing.*;

/**
 * This class is used to simulate the dynamics of a system of point masses. We
 * assume that the forces on the particles are derived from a global potential
 * function. The reason for this is the following. The increments in velocity
 * and position of the system are obtained by solving a system of linear
 * equations (in fact, we are using an implicit Euler integrator). The matrix
 * asocciated with this system of equations is symmetric if we hold to the
 * assumption that the forces on the system are derived from a global potential,
 * which allows us to use efficient numerical algorithms (the conjugate gradient
 * method to be specific).
 */
public class ParticleSystem extends FunctionJet2Adapter implements J2DAnimation
{
	public enum EvolveMode
	{
		EULER, EULER_IMPLICIT, VELOCITY_VERLET, PURE_MINIMISATION
	};

	protected EvolveMode evolveMode = EvolveMode.EULER_IMPLICIT;

	private Random rand = new Random();
	private double randSigma = 0.0;
	private int randStep = 0;
	private int randStepCount = 0;

	protected boolean particlesJ2DDrawable = true;
	/**
	 * Determines if the potentials are drawable. This variable has value true
	 * if you want the draw method of this object to paint the potentials in a
	 * Java2D g3d context, and has the value false otherwise.
	 */
	protected boolean potentialsJ2DDrawable = true;
	/**
	 * The dimension of the space in which the particle system lives.
	 */
	protected int particleDim = 0;
	/**
	 * The number of particles in the system.
	 */
	protected int particlesCount = 0;
	/**
	 * The array of colors for the particles in the system. The i-th element in
	 * the array describes the color of the i-th particle in the system.
	 */
	public Color[] particleColors;

	/**
	 * The position of the particle system. We have that position = (x_1, ...,
	 * x_n), where x_i is the position of the i-th particle.
	 */
	protected double[] position;
	/**
	 * The velocity of the particle system. We have that velocity = (v_1, ...,
	 * v_n), where v_i is the velocity of the i-th particle.
	 */
	protected double[] velocity;
	/**
	 * This operator is used to impose velocity constraints on the particle
	 * system. The constraint is obtained applying the operator to the velocity
	 * of the system.
	 * 
	 */
	protected LinearOperator velocityFilter = null;

	protected ConjGradSolver solverForImpEuler = new ConjGradSolver();
	//
	// Private variables start here
	//
	protected double[] mass;
	protected double mediumViscosity;
	protected double timeInc = 0.01;
	protected double timeIncSq = 0.0001;
	protected double[] positionInc;
	protected double[] velocityInc;

	protected Vector<ParticlesPotentialTerm> potentialTerms = new Vector<ParticlesPotentialTerm>();

	protected ParticlesPotentialTerm term;
	protected int potentialTermsCount;

	protected double[] impEulerB;
	protected LinearOperator impEulerOp = null;
	protected StateToJ2DMap stateToJ2DMap;
	protected StateToJ2DMap currentStateToJ2DMap;
	protected JToolBar propertiesToolBar;
	protected JTextField viscosityField;
	protected JTextField timeIncField;

	// Helping variables
	protected double[][] positions;
	protected double[][] velocities;
	protected double[] nonFilteredVelocity;
	protected int[] java2Dcoord = new int[2];
	protected String infoText = "no-info";

	protected int pCenterX = 3;
	protected int pCenterY = 3;
	protected int pSizeX = 6;
	protected int pSizeY = 6;

	/** Holds value of property j3DDrawableParticles. */
	private boolean j3DDrawableParticles = false;

	/** Holds value of property j3DDrawablePotentials. */
	private boolean j3DDrawablePotentials = false;

	/** Used for velocity verlet */
	private double[] velocityAtHalfStep;

	protected double zeroTol = 1.0e-3;

	protected double zeroTolSq = zeroTol * zeroTol;

	protected MyVector[] velocityVectors;
	protected MyVector[] positionVectors;
	protected MyVector[] negForceVectors;

	/**
	 * Creates a system of particles with a specified number of particles in a
	 * space of a specified dimension.
	 * 
	 * @param particleDim
	 *            dimension of the space in which the particles live
	 * @param particlesCount
	 *            number of particles in the system
	 */
	public ParticleSystem(int particleDim, int particlesCount)
	{
		super(particleDim * particlesCount);
		this.particleDim = particleDim;
		this.particlesCount = particlesCount;

		particleColors = new Color[particlesCount];
		mass = new double[baseDimension];
		position = new double[baseDimension];
		velocityAtHalfStep = new double[baseDimension];

		state = position;
		positions = new double[particlesCount][particleDim];
		velocities = new double[particlesCount][particleDim];
		velocity = new double[baseDimension];
		nonFilteredVelocity = new double[baseDimension];
		positionInc = new double[baseDimension];
		velocityInc = new double[baseDimension];
		impEulerB = new double[baseDimension];

		for (int i = 0; i < particlesCount; i++)
		{
			particleColors[i] = Color.orange;
		}
		for (int i = 0; i < baseDimension; i++)
		{
			mass[i] = 1.0;
			mediumViscosity = 1.0;
			position[i] = 0.0;
			velocity[i] = 0.0;
		}
		createImpEulerOp();
		solverForImpEuler.set(impEulerOp);

		constructPropertiesToolBar();
		stateToJ2DMap = new StateToJ2DMap(256.0, 256.0, 256.0, 256.0);

		velocityVectors = new MyVector[particlesCount];
		positionVectors = new MyVector[particlesCount];
		negForceVectors = new MyVector[particlesCount];

		for (int i = 0; i < particlesCount; i++)
		{
			int index = i * particleDim;
			velocityVectors[i] = new MyVector(particleDim, true);
			velocityVectors[i].setBaseData(velocity, index);

			positionVectors[i] = new MyVector(particleDim, true);
			positionVectors[i].setBaseData(position, index);

			negForceVectors[i] = new MyVector(particleDim, true);
			negForceVectors[i].setBaseData(gradient, index);
		}
	}

	/**
	 * Creates a panel for drawing and animating this particle system, which is
	 * of the desired dimensions and runs at the desired number of frames per
	 * second.
	 * 
	 * @return animation panel for particle system
	 * @param width
	 *            the width of the panel
	 * @param height
	 *            the height of the panel
	 * @param framesPerSecond
	 *            the number of frames per second
	 */
	public JParticlesPanel createAnimationPanel(int width, int height,
			int framesPerSecond)
	{
		JParticlesPanel out = new JParticlesPanel(this, width, height,
				framesPerSecond);
		return out;
	}

	//
	// SET-GET METHODS START HERE
	//

	/**
	 * Sets the value of potentialsJ2DDrawable.
	 * 
	 * @param condition
	 *            true of false
	 */
	public void setJ2DDrawableParticles(boolean condition)
	{
		particlesJ2DDrawable = condition;
	}

	/**
	 * Sets ths value of particlesJ2DDrawable.
	 * 
	 * @param condition
	 *            true of false
	 */
	public void setJ2DDrawablePotentials(boolean condition)
	{
		potentialsJ2DDrawable = condition;
	}

	/**
	 * Returns particleDim.
	 * 
	 * @return particleDim
	 */
	public int getParticleDimension()
	{
		return particleDim;
	}

	/**
	 * Returns the number of particles in the system.
	 * 
	 * @return number of particles
	 */
	public int getParticlesCount()
	{
		return particlesCount;
	}

	/**
	 * Sets the color for the i-th particle.
	 * 
	 * @param i
	 *            the particle number
	 * @param color
	 *            a color
	 */
	public void setParticleColor(int i, Color color)
	{
		particleColors[i] = color;
	}

	/**
	 * Sets the viscosity of the medium in which the particles move. This
	 * imposes a velocity dependent force on the particles.
	 * 
	 * @param viscosity
	 *            The viscosity of the medium
	 */
	public void setMediumViscosity(double viscosity)
	{
		mediumViscosity = viscosity;
		viscosityField.setText(String.valueOf(mediumViscosity));
	}

	/**
	 * Returns the viscosity of the medium.
	 * 
	 * @return the viscosity of the medium
	 */
	public double getMediumViscosity()
	{
		return mediumViscosity;
	}

	/**
	 * Sets the position of a given particle.
	 * 
	 * @param i
	 *            particle number
	 * @param particlePosition
	 *            position of the particle
	 */
	public void setPosition(int i, double[] particlePosition)
	{
		int k = particleDim * i;
		for (int j = 0; j < particleDim; j++)
			position[k + j] = particlePosition[j];
	}

	public void setPosition(int i, double x, double y)
	{
		int k = particleDim * i;
		position[k] = x;
		position[k + 1] = y;
	}

	public void setPosition(int i, double x, double y, double z)
	{
		int k = particleDim * i;
		position[k] = x;
		position[k + 1] = y;
		position[k + 2] = z;
	}

	/**
	 * Sets the position of all the particles in the system
	 * 
	 * @param position
	 *            position = (x_1, ..., x_n), where x_i is the position of
	 *            particle i.
	 */
	public void setPosition(double[] position)
	{
		for (int i = 0; i < baseDimension; i++)
			this.position[i] = position[i];
	}

	/**
	 * Gets the position of the i-th particle in the system.
	 * 
	 * @param i
	 *            particle number
	 * @return partilce position
	 */
	public double[] getPosition(int i)
	{
		double[] pos = positions[i];
		int k = particleDim * i;
		for (int j = 0; j < particleDim; j++)
			pos[j] = position[k + j];
		return pos;
	}

	/**
	 * Gets the position of the particle system.
	 * 
	 * @return positions of the particle system
	 */
	public double[] getPosition()
	{
		return position;
	}

	/**
	 * Sets the velocity of a particle
	 * 
	 * @param i
	 *            particle number
	 * @param particleVelocity
	 *            velocity for the given particle number
	 */
	public void setVelocity(int i, double[] particleVelocity)
	{
		int k = particleDim * i;
		for (int j = 0; j < particleDim; j++)
			velocity[k + j] = particleVelocity[j];
	}

	/**
	 * Sets the velocity the particles in the system.
	 * 
	 * @param velocity
	 *            velocity = (v_1,...,v_n), where v_i is the velocity of
	 *            particle i.
	 */
	public void setVelocity(double[] velocity)
	{
		for (int i = 0; i < baseDimension; i++)
			this.velocity[i] = velocity[i];
	}

	public void setVelocity(int i, double x, double y)
	{
		int k = particleDim * i;
		velocity[k] = x;
		velocity[k + 1] = y;
	}

	public void setVelocity(int i, double vx, double vy, double vz)
	{
		int k = particleDim * i;
		velocity[k] = vx;
		velocity[k + 1] = vy;
		velocity[k + 2] = vz;
	}

	/**
	 * Gets the velocity of a particle.
	 * 
	 * @return velocity of the particles in the system
	 * @param i
	 *            particle number
	 */
	public double[] getVelocity(int i)
	{
		double[] vel = velocities[i];
		int k = particleDim * i;
		for (int j = 0; j < particleDim; j++)
			vel[j] = velocity[k + j];
		return vel;
	}

	/**
	 * Gets the velocity the particle system.
	 * 
	 * @return velocity of the particles in the system
	 */
	public double[] getVelocity()
	{
		return velocity;
	}

	/**
	 * Sets the time increment for the system.
	 * 
	 * @param timeInc
	 *            the time increment
	 * @see jparticles.particles.ParticleSystem#evolve
	 */
	public void setTimeIncrement(double timeInc)
	{
		this.timeInc = timeInc;
		timeIncSq = timeInc * timeInc;
		timeIncField.setText(String.valueOf(timeInc));
	}

	/**
	 * Gets the time increment of the system.
	 * 
	 * @see jparticles.particles.ParticleSystem#evolve
	 * @return the time increment
	 */
	public double getTimeIncrement()
	{
		return timeInc;
	}

	//
	// END OF SET-GET METHODS
	//
	/**
	 * Adds a potential term to the particle system.
	 * 
	 * @param term
	 *            a potential term for the particle system
	 */
	public void add(ParticlesPotentialTerm term)
	{
		term.setState(position);
		potentialTerms.add(term);
		potentialTermsCount = potentialTerms.size();
	}

	/**
	 * Remove the potential term for the particle system.
	 * 
	 * @param term
	 *            a potential term for the particle system
	 */
	public void remove(ParticlesPotentialTerm term)
	{
		potentialTerms.remove(term);
		potentialTermsCount = potentialTerms.size();
	}

	/**
	 * Updates the value for the total potential energy of the system.
	 */
	public void updateValue()
	{
		super.updateValue();
		value = 0.0;
		for (int i = 0; i < potentialTermsCount; i++)
		{
			term = (ParticlesPotentialTerm) potentialTerms.get(i);
			term.setState(state);
			value += term.getValue();
		}
	}

	/**
	 * Updates the gradient of the total potential energy for the system.
	 */
	public void updateGradient()
	{
		super.updateGradient();
		for (int i = 0; i < baseDimension; i++)
			gradient[i] = 0.0;
		for (int i = 0; i < potentialTermsCount; i++)
		{
			term = (ParticlesPotentialTerm) potentialTerms.get(i);
			term.setState(state);
			term.setTotalGradient(gradient);
			term.addGradient();
		}
	}

	/**
	 * Updates the hessian of the total potential energy of the system, in the
	 * given direction.
	 * 
	 * @param direction
	 *            a direction
	 */
	public void updateHessian(double[] direction)
	{
		super.updateHessian(direction);
		for (int i = 0; i < baseDimension; i++)
			hessian[i] = 0.0;
		for (int i = 0; i < potentialTermsCount; i++)
		{
			term = (ParticlesPotentialTerm) potentialTerms.get(i);
			term.setState(state);
			term.setTotalHessian(hessian);
			term.addHessian(direction);
		}
	}

	protected void updateImpEulerB()
	{
		for (int i = 0; i < baseDimension; i++)
		{
			impEulerB[i] = 0.0;
		}
		setState(position);
		updateGradient();
		updateHessian(velocity);

		for (int i = 0; i < baseDimension; i++)
		{
			impEulerB[i] -= timeInc
					* (gradient[i] + mediumViscosity * velocity[i] + timeInc
							* hessian[i]);
		}
	}

	private void createImpEulerOp()
	{
		impEulerOp = new LinearOperator()
		{
			public void transform(double[] in, double[] out)
			{
				for (int i = 0; i < baseDimension; i++)
				{
					out[i] = 0.0;
				}
				setState(position);
				updateHessian(in);
				for (int i = 0; i < baseDimension; i++)
				{
					out[i] += mass[i] * in[i] + timeInc * mediumViscosity
							* in[i] + timeIncSq * hessian[i];
				}
			}

			public int getBaseDimension()
			{
				return baseDimension;
			}

			public int getTargetDimension()
			{
				return baseDimension;
			}
		};
	}

	/**
	 * Changes the current position and velocity to the position and velocity at
	 * the end of one time step.
	 * 
	 * @see jparticles.particles.ParticleSystem#setTimeIncrement
	 * @see jparticles.particles.ParticleSystem#getTimeIncrement
	 */
	public void evolve()
	{
		if ((randSigma != 0.0) && (randStep >= 0 ))
		{
			if(randStepCount > randStep)
			{
				randomizeVelocities();
				randStepCount=0;
			}
			randStepCount++;
		}
		if (evolveMode == EvolveMode.EULER_IMPLICIT)
		{
			this.evolveEulerImp();
		}
		else if (evolveMode == EvolveMode.VELOCITY_VERLET)
		{
			evolveVelocityVerlet();
		}
		else if (evolveMode == EvolveMode.EULER)
		{
			evolveEuler();
		}

	}

	private void randomizeVelocities()
	{
		for (int i = 0; i < velocity.length; i++)
		{
			velocity[i] = randSigma*rand.nextGaussian();
		}
		
	}

	private void evolveEulerImp()
	{
		this.updateImpEulerB();
		solverForImpEuler.solve(impEulerB, velocityInc);
		for (int i = 0; i < baseDimension; i++)
		{
			position[i] += timeInc * (velocity[i] + velocityInc[i]);
			velocity[i] += velocityInc[i];
		}
	}

	private void evolveEuler()
	{
		this.setState(position);
		updateGradient();
		double accel;
		for (int i = 0; i < baseDimension; i++)
		{
			position[i] += timeInc * velocity[i];
			accel = (timeInc / mass[i])
					* (-gradient[i] - mediumViscosity * velocity[i]);
			velocity[i] = accel;
		}
	}

	private void evolveVelocityVerlet()
	{
		double accel, accelNext;
		double timeIncSq = timeInc * timeInc;

		this.setState(position);
		this.updateGradient();
		for (int i = 0; i < baseDimension; i++)
		{
			accel = (timeInc / mass[i])
					* (-gradient[i] - mediumViscosity * velocity[i]);
			position[i] = position[i] + velocity[i] * timeInc + .5 * accel
					* timeIncSq;
		}

		for (int i = 0; i < baseDimension; i++)
		{
			accel = (timeInc / mass[i])
					* (-gradient[i] - mediumViscosity * velocity[i]);
			velocityAtHalfStep[i] = velocity[i] + .5 * accel * timeInc;
		}

		this.setPosition(position); // The new position
		this.updateGradient(); // the new gradient
		for (int i = 0; i < baseDimension; i++)
		{
			accelNext = (timeInc / mass[i])
					* (-gradient[i] - mediumViscosity * velocity[i]);
			velocity[i] = velocityAtHalfStep[i] + .5 * accelNext * timeInc;
		}
	}

	/**
	 * Updates the animation state of the particle system by making a call to
	 * evolve.
	 * 
	 * @param userObject
	 *            Not used.
	 */
	public void updateAimationState(Object userObject)
	{
		evolve();
	}

	/**
	 * This method draws the particle system and its potential terms in a
	 * Graphics2D context.
	 * 
	 * @param g
	 *            the g3d context
	 * @param userObject
	 *            a StateToJ2DMap object
	 * @see jparticles.gui.StateToJ2DMap
	 */
	public void draw(Graphics2D g, Object userObject)
	{
		if (userObject != null)
			currentStateToJ2DMap = (StateToJ2DMap) userObject;
		else
			currentStateToJ2DMap = stateToJ2DMap;
		int k = 0;
		int x, y;
		if (particlesJ2DDrawable)
		{
			for (int i = 0; i < particlesCount; i++)
			{
				currentStateToJ2DMap.mapStateToJ2D(getPosition(i), java2Dcoord);
				g.setColor(particleColors[i]);
				g.fillOval(java2Dcoord[0] - pCenterX,
						java2Dcoord[1] - pCenterY, pSizeX, pSizeY);
			}
		}
		if (potentialsJ2DDrawable)
		{
			for (int i = 0; i < potentialTermsCount; i++)
			{
				term = (ParticlesPotentialTerm) potentialTerms.get(i);
				term.draw(g, currentStateToJ2DMap);
			}
		}
	}

	/**
	 * Imposes a velocity constraint on the velocity of the particles system.
	 * This constraint is given by the condition that the velocity of the system
	 * v = (v_1,...,v_2) lies in the subspace
	 * 
	 * <PRE>
	 * range(projectionMap).
	 * </PRE>
	 * 
	 * The projection map passed as the argument to this method must satisfy
	 * 
	 * <PRE>
	 * projectionMap*projectionMap = projectionMap,
	 * </PRE>
	 * 
	 * which is actually the definition of a projection map. The method does not
	 * make any checks on the validity of this assumption and the user is
	 * responsible to ensure that it holds.
	 * 
	 * @param projectionMap
	 *            a projection map
	 */
	public void setVelocityConstraint(LinearOperator projectionMap)
	{
		this.velocityFilter = projectionMap;
		solverForImpEuler.setFilter(projectionMap);
		filterVelocities();
	}

	/**
	 * Filters the velocities of the system by applying the velocity filter.
	 */
	public void filterVelocities()
	{
		if (velocityFilter != null)
		{
			System.arraycopy(velocity, 0, nonFilteredVelocity, 0, baseDimension);
			velocityFilter.transform(nonFilteredVelocity, velocity);
		}
	}

	/**
	 * Returns the index of the closest particle to a given point. If there are
	 * several particles at the same distance to the given point, then the first
	 * one found will be returned.
	 * 
	 * @param pos
	 *            a point in a space of dimension particleDims
	 * @return the index of a particle closest to the given point
	 */
	public int getNearestParticle(double[] pos)
	{
		double radiusSq;
		double minRadiusSq = 0.0;
		double coordInc;
		int indexOut = 0;
		for (int j = 0; j < particleDim; j++)
		{
			coordInc = position[j] - pos[j];
			minRadiusSq += coordInc * coordInc;
		}
		int particleIndex = particleDim;
		for (int i = 1; i < particlesCount; i++)
		{
			radiusSq = 0.0;
			for (int j = 0; j < particleDim; j++)
			{
				coordInc = position[particleIndex] - pos[j];
				radiusSq += coordInc * coordInc;
				particleIndex++;
			}
			if (radiusSq < minRadiusSq)
			{
				minRadiusSq = radiusSq;
				indexOut = i;
			}
		}
		return indexOut;
	}

	/**
	 * Gets a swing component used to set the viscosity and time step.
	 * 
	 * @return Swing component
	 */
	public JToolBar getPropertiesToolBar()
	{
		return propertiesToolBar;
	}

	private void constructPropertiesToolBar()
	{
		propertiesToolBar = new JToolBar();
		viscosityField = new JTextField(Double.toString(getMediumViscosity()),
				4);
		timeIncField = new JTextField(Double.toString(getTimeIncrement()), 4);
		JLabel viscosityLabel = new JLabel("Viscosity = ");
		JLabel timeIncLabel = new JLabel("Time Inc = ");
		viscosityField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				JTextField viscosityField = (JTextField) evt.getSource();
				String stringValue = viscosityField.getText();
				setMediumViscosity(Double.valueOf(stringValue).doubleValue());
			}
		});
		timeIncField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				JTextField timeIncField = (JTextField) evt.getSource();
				String stringValue = timeIncField.getText();
				setTimeIncrement(Double.valueOf(stringValue).doubleValue());
			}
		});
		propertiesToolBar.add(viscosityLabel);
		propertiesToolBar.add(viscosityField);
		propertiesToolBar.add(timeIncLabel);
		propertiesToolBar.add(timeIncField);
	}

	public StateToJ2DMap getStateToJ2DMap()
	{
		return stateToJ2DMap;
	}

	public void setStateToJ2DMap(StateToJ2DMap map)
	{
		stateToJ2DMap = map;
	}

	public void setParticleSize(int width, int height)
	{
		pCenterX = width / 2;
		pCenterY = height / 2;
		pSizeX = width;
		pSizeY = height;
	}

	public ParticlesPotentialTerm getPotential(int i)
	{
		return (ParticlesPotentialTerm) potentialTerms.get(i);
	}

	public boolean isParticleStable(int i)
	{
		if (evolveMode == EvolveMode.PURE_MINIMISATION)
		{
			return (negForceVectors[i].dot(negForceVectors[i]) < zeroTolSq);
		}
		return ((velocityVectors[i].dot(velocityVectors[i]) < zeroTolSq) && (negForceVectors[i]
				.dot(negForceVectors[i]) < zeroTolSq));
	}

	public boolean isStable()
	{
		for (int i = 0; i < particlesCount; i++)
		{
			if (!isParticleStable(i))
			{
				return false;
			}
		}
		return true;
	}

	public void setRandomisedData(double randSigma, int randStep)
	{
		this.randSigma = randSigma;
		this.randStep = randStep;
	}

	public long evolveUntilStable(long itersMax)
	{
		long iters = 0;
		boolean isNotStable = !this.isStable();
		while (isNotStable && (iters < itersMax))
		{
			this.evolve();
			isNotStable = !this.isStable();
			iters++;
		}
		if (isNotStable)
		{
			return -1;
		}
		return iters;
	}

	public EvolveMode getEvolveMode()
	{
		return evolveMode;
	}

	public void setEvolveMode(EvolveMode evolveMode)
	{
		this.evolveMode = evolveMode;
	}

}