# JParticles Tutorial


## Introduction
JParticles  is java library to simulate the evolution of particles systems (see next section). You will need JRE1.8 (or higher) installed on your machine to run the programs in this tutorial.

## Particle Systems


A particle system is a collection of point masses whose motions obey Newton's second law of motion, i.e the acceleration of a given particle is equal to the force on that particle divided by its mass. In the JParticles, a particle system is represented as an object of the class ParticleSystem. For example, if we want to construct a system of ten particles in two-dimensional space, we use the code:

```markdown
particles = new ParticleSystem(2,10);
```

By default the position and velocities of the particles in a a system are all set to zero. To change the position and velocity of a specified particle, say particle number three, we use code as the following:

```java
double [] position = {0.5,0.3}; 
double [] velocity = {1.0,2.0}; 
particles.setPosition(3,position); 
particles.setVelocity(3,velocity);
```

Having set the initial state of the particles, we  start the motion of the particle system by calling:

```java
particles.evolve();
```

This method evolves the system to the end of a time step whose value can be obtained by using the method

```java
particles.getTimeIncrement();
```

and set by using the method

```java
particles.setTimeIncrement(double);
```

By default, the only forces acting on the particle system are viscous forces, whose intensity is measured by the number


```java
particles.getMediumViscosity();
```

which can be set by the method

```java
particles.setMediumViscosity(double);
```

If we want to use a Swing component to set the viscosity and the time increment we can call:

```java
particles.getPropertiesToolBar();
```

To render the evolution of the particles, we create a JParticlesPanel object by calling

```java
ParticlesPanel panel = particles.createAnimationPanel(int width,int height,int fps);
```

where fps is the animation rate in frames per second. The panel can be used to easily render and manipulate particles systems by using code like

```java
JFrame frame = new JFrame(512,512); 
frame.getContentPane.add(pane,BorderLayOut.CENTER); 
```

```java
frame.show(); 
panel.startAnimation();
```
To get a swing component to control the evolution of the system, we use the code:

```java
JToolBar evolToolBar = panel.getEvolutionToolBar();
```

For an example of an applet using the above concepts see: ParticleApplet. The source code for this applet can be found in ParticlesApplet.java









You can use the [editor on GitHub](https://github.com/valerocar/JParticles/edit/gh-pages/index.md) to maintain and preview the content for your website in Markdown files.

Whenever you commit to this repository, GitHub Pages will run [Jekyll](https://jekyllrb.com/) to rebuild the pages in your site, from the content in your Markdown files.

### Markdown

Markdown is a lightweight and easy-to-use syntax for styling your writing. It includes conventions for

```markdown
Syntax highlighted code block

# Header 1
## Header 2
### Header 3

- Bulleted
- List

1. Numbered
2. List

**Bold** and _Italic_ and `Code` text

[Link](url) and ![Image](src)
```

For more details see [GitHub Flavored Markdown](https://guides.github.com/features/mastering-markdown/).

### Jekyll Themes

Your Pages site will use the layout and styles from the Jekyll theme you have selected in your [repository settings](https://github.com/valerocar/JParticles/settings). The name of this theme is saved in the Jekyll `_config.yml` configuration file.

### Support or Contact

Having trouble with Pages? Check out our [documentation](https://docs.github.com/categories/github-pages-basics/) or [contact support](https://github.com/contact) and we’ll help you sort it out.
