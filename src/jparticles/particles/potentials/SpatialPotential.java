/*
 * SpatialPotential.java
 *
 * Created on 11 July 2001, 09:00
 */

package jparticles.particles.potentials;

import jparticles.maths.calculus.FunctionJet2;
import jparticles.utils.ImageUtils;

import java.awt.image.BufferedImage;

/**
 * This class is used to create forces on particles which arise from a potential
 * in their underlying space. One can think of the potential as hilly terrain
 * throug which the particles slide.
 * 
 * @author carlos
 * @version 1.0
 * 
 */

public class SpatialPotential extends ParticlesPotentialTerm
{
	protected FunctionJet2 spatialPotential;
	protected int particleDim;
	protected int particlesCount;

	private double[] particleState;
	private double[] particleDir;

	/**
	 * Creates a spatial potential from the given second order function jet, for
	 * a system with a specified number of particles.
	 * 
	 * @param potential
	 *            a second order jet
	 * 
	 * @param particlesCount
	 *            the number of particles in the system
	 * 
	 */
	public SpatialPotential(FunctionJet2 potential, int particlesCount)
	{
		spatialPotential = potential;
		this.particlesCount = particlesCount;
		particleDim = potential.getBaseDimension();
		particleState = new double[particleDim];
		particleDir = new double[particleDim];
	}

	public SpatialPotential(int particleDim, int particlesCount)
	{
		this.particlesCount = particlesCount;
		this.particleDim = particleDim;
		particleState = new double[particleDim];
		particleDir = new double[particleDim];
	}

	public void setPotential(FunctionJet2 potential)
	{
		spatialPotential = potential;
	}

	public double getValue()
	{
		double value = 0.0;
		int k = 0;
		for (int i = 0; i < particlesCount; i++)
		{
			for (int j = 0; j < particleDim; j++)
				particleState[j] = state[k + j];
			spatialPotential.setState(particleState);
			spatialPotential.updateValue();
			value += spatialPotential.getValue();
			k += particleDim;
		}
		return value;
	}

	public void addGradient()
	{
		double[] particleGrad;
		int k = 0;
		for (int i = 0; i < particlesCount; i++)
		{
			for (int j = 0; j < particleDim; j++)
				particleState[j] = state[k + j];
			spatialPotential.setState(particleState);
			spatialPotential.updateGradient();
			particleGrad = spatialPotential.getGradient();
			for (int j = 0; j < particleDim; j++)
				totalGradient[k + j] += particleGrad[j];
			k += particleDim;
		}
	}

	public void addHessian(double[] direction)
	{
		double[] particleHess;
		int k = 0;
		for (int i = 0; i < particlesCount; i++)
		{
			for (int j = 0; j < particleDim; j++)
			{
				particleState[j] = state[k + j];
				particleDir[j] = direction[k + j];
			}
			spatialPotential.setState(particleState);
			spatialPotential.updateHessian(particleDir);
			particleHess = spatialPotential.getHessian();
			for (int j = 0; j < particleDim; j++)
				totalHessian[k + j] += particleHess[j];
			k += particleDim;
		}
	}

	public BufferedImage constructImage(int xMin, int xMax, int yMin, int yMax, int width,
			int height)
	{
		ImageUtils.FunctionWindow fw = new ImageUtils.FunctionWindow(
				spatialPotential, xMin, yMin, xMax, yMax);
		BufferedImage image = new BufferedImage(width, height, 5);
		ImageUtils.getImageFromFunction(fw, width - 100, height - 100, 255.0,
				image);
		return image;
	}
}
