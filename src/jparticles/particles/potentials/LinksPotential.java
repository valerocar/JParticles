/*
 * TrianglesPotential.java
 *
 * Created on 01 May 2002, 12:02
 */

package jparticles.particles.potentials;

import jparticles.particles.ParticleSystem;
import javax.swing.JFrame;
import jparticles.gui.JParticlesPanel;

/**
 *
 * @author  carlos
 */
public class LinksPotential extends ConstraintPotential
{
    private double [] dist;
    private int [] links;
    private ParticleSystem particles;
    private int particleDim;
    private int particlesCount;
    private double value;
    private double stiffness = 50.0;
    
    // Helping vars
    double diff, diffDir;
    double diff_diffDir;
    int indexA, indexB;
    int particleA, particleB;
    double dAB;
    public LinksPotential(ParticleSystem particles, int[] links)
    {
        super(particles,links.length/2);
        this.links =  links;
        this.state = particles.getState();
        particleDim = particles.getParticleDimension();
        particlesCount = particles.getParticlesCount();
        dist = new double[constraintsCount];
        ComputeInitialDistances();
    }
    public LinksPotential(ParticleSystem particles, int[] links,double linksDistance)
    {
        super(particles,links.length/2);
        this.links =  links;
        this.state = particles.getState();
        particleDim = particles.getParticleDimension();
        particlesCount = particles.getParticlesCount();
        dist = new double[constraintsCount];
        int count = 0;
        for(int i = 0; i < constraintsCount; i++)
        {
            dist[count++]  =linksDistance;
        }
    }
    private void ComputeInitialDistances()
    {
        int count = 0;
        for(int i = 0; i < constraintsCount; i++)
        {
            dist[count++]  =computeDistance( links[2*i], links[2*i+1]);
        }
    }
    
    private double computeDistance(int particleA, int particleB)
    {
        double dAB = 0.0;
        indexA = particleA*particleDim;
        indexB = particleB*particleDim;
        for(int j = 0; j < particleDim; j++)
        {
            diff = state[indexB++]-state[indexA++];
            dAB += diff*diff;
        }
        dAB = Math.sqrt(dAB);
        return dAB;
    }
    double getLocalValue(int i)
    {
        return computeDistance( links[2*i],links[2*i+1])-dist[i];
    }
    void addMultiplyLocalGradient(int i, double coeff, double [] vector)
    {
        particleA = links[2*i];
        particleB = links[2*i+1];
        dAB = computeDistance(particleA,particleB);
        indexA = particleA*particleDim;
        indexB = particleB*particleDim;
        if(dAB != 0)
        {
            for(int k = 0; k < particleDim; k++)
            {
                diff = (state[indexB]-state[indexA])/dAB;
                vector[indexA] -= coeff*diff;
                vector[indexB] += coeff*diff;
                indexA++;
                indexB++;
            }
        }
    }
    double getDotLocalGradient(int i, double [] vector)
    {        
        particleA = links[2*i];
        particleB = links[2*i+1];
        dAB = computeDistance(particleA,particleB);
        indexA = particleA*particleDim;
        indexB = particleB*particleDim;
        
        double out = 0;
        if(dAB != 0)
        {
            for(int k = 0; k < particleDim; k++)
            {
                diff = (state[indexB]-state[indexA])/dAB;                
                out  -= vector[indexA]*diff;
                out += vector[indexB]*diff;
                indexA++;
                indexB++;
            }
        }
        return out;
    }
    void addMultiplyLocalHessian(int i, double coeff, double [] direction, double [] vector)
    {                
        particleA = links[2*i];
        particleB = links[2*i+1];
        dAB = computeDistance(particleA,particleB);
        if(dAB != 0)
        {
            indexA = particleA*particleDim;
            indexB = particleB*particleDim;
            double cc;
            for(int k = 0; k < particleDim; k++)
            {
                diff = (state[indexB]-state[indexA])/dAB;
                diffDir = (direction[indexB]-direction[indexA])/dAB;
                diff_diffDir = 0;
                for(int j = 0; j < particleDim; j++)  diff_diffDir += diff*diffDir;
                cc = coeff*(diffDir-diff_diffDir*diff);
                vector[indexA] -= cc;
                vector[indexB] += cc;
                indexA++;
                indexB++;
            }
        }              
    }
    static public void main(String [] args)
    {
        int pcount = 10;
        ParticleSystem particles = new ParticleSystem(2,pcount);
        double x = -.3;
        double dx = -2*x/(pcount-1);
        for(int i = 0; i < pcount; i++)
        {
            particles.setPosition(i,x,0);
            x += dx;
        }
        JParticlesPanel panel = particles.createAnimationPanel(500,500,100);
        
        int [] links = new int[2*(pcount-1)];
        for(int i = 0; i < (pcount-1); i++)
        {
            links[2*i] = i;
            links[2*i+1] = i+1;
        }
        
        LinksPotential pot = new LinksPotential(particles,links);
        pot.setConstraintsCeofficients(20);
        pot.setDampingsCoefficients(5);
        particles.setTimeIncrement(.1);
        particles.setMediumViscosity(0);
        
        particles.add(pot);
        particles.setVelocityConstraint(panel.getNail());
        JFrame frame = new JFrame();
        frame.getContentPane().add(panel);
        frame.setSize(500,500);
        frame.show();
        panel.startAnimation();
    }
}
