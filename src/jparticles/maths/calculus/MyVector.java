package jparticles.maths.calculus;


import java.util.Random;

/**
 * A vector Class.
 */
public class MyVector implements Cloneable
{
	protected int dimension;
	private double[] array;
	protected boolean byReference = false;
	protected int begin = 0;



    /**
	 * Is byReference is true, the vector uses a pointer to an external array.
	 * Otherwise the vector owns its array.
	 * 
	 * @param dimension
	 * @param byReference
	 */
	public MyVector(int dimension, boolean byReference)
	{
		this.dimension = dimension;
		this.byReference = byReference;

		if (!byReference)
		{
			this.array = new double[dimension];
		}
	}

	public void incrementCoord(int i, double inc)
	{
		this.setCoord(i, this.getCoord(i)+inc);
	}
	public double x()
	{
		return getCoord(0);
	}

	public double y()
	{
		return getCoord(1);
	}

	public double z()
	{
		return getCoord(2);
	}

	public void setCoords(double... coords)
	{
		for (int i = 0; i < coords.length; i++)
		{
			this.setCoord(i, coords[i]);
		}
	}

	public MyVector(boolean byReference)
	{
		this(0, byReference);
	}

	public void setTo(int index, MyVector in)
	{
		int inDim = in.dimension;
		for (int i = 0; i < inDim; i++)
		{
			setCoord(index + i, in.getCoord(i));
		}
	}

	public MyVector(double x0, double x1, double x2, double x3)
	{
		this(4, false);
		this.setCoord(0, x0);
		this.setCoord(1, x1);
		this.setCoord(2, x2);
		this.setCoord(3, x3);
	}

	public MyVector(double x0, double x1, double x2)
	{
		this(3, false);
		this.setCoord(0, x0);
		this.setCoord(1, x1);
		this.setCoord(2, x2);
	}

	public MyVector(double x0, double x1, double x2, double x3, double x4)
	{
		this(5, false);
		this.setCoord(0, x0);
		this.setCoord(1, x1);
		this.setCoord(2, x2);
		this.setCoord(3, x3);
		this.setCoord(4, x4);
	}

	/**
	 * ByReference is false.
	 * 
	 * @param dimension
	 */
	public MyVector(int dimension)
	{
		this(dimension, false);
	}

	/**
	 * ByReference is true
	 */
	public MyVector()
	{
		this(0, true);
	}

	public MyVector(MyVector direction)
	{
		this(direction.dimension());
		this.setEqual(direction);
	}

	/**
	 * Creates the concatenated vector (u,v)
	 * 
	 * @param u
	 * @param v
	 */
	public MyVector(MyVector u, MyVector v)
	{
		this(u.dimension + v.dimension());
		int coord = 0;
		for (int i = 0; i < u.dimension; i++)
		{
			this.setCoord(coord++, u.getCoord(i));
		}
		for (int i = 0; i < v.dimension; i++)
		{
			this.setCoord(coord++, v.getCoord(i));
		}
	}

	/**
	 * Only work if byReference is true
	 * 
	 * @param dimension
	 */
	public void setDimension(int dimension)
	{
		if (byReference)
		{
			this.dimension = dimension;
		}
		else
		{
			Log.error("Can't assign dimension to non-reference vector");
			MathException.error(this, MathException.BYREFERENCE_ERROR,
					"Can't assign dimension to non-reference vector");
		}
	}

	/**
	 * Only works if byReference is true.
	 * 
	 * @param array
	 * @param begin
	 */
	public void setBaseData(double[] array, int begin)
	{
		if (byReference)
		{
			this.array = array;
			this.begin = begin;
		}
		else
		{
			Log.error("This vector is not byReference");
			System.exit(-1);
		}
	}

	/**
	 * Only works if byReference is true
	 * 
	 * @param v
	 * @param begin
	 */
	public void setBaseData(MyVector v, int begin)
	{

		if (byReference)
		{
			this.array = v.array;
			this.begin = begin + v.begin;
		}
		else
		{
			System.out.println("This vector is not byReference");
			System.exit(-1);
		}
	}

	/**
	 * 
	 * @return The underlying base array.
	 */
	public double[] getBaseArray()
	{
		return array;
	}

	/**
	 * Copy the values of the array to this vector.
	 * 
	 * @param array
	 */
	public void setEqualTo(double[] array)
	{
		for (int i = 0; i < dimension; i++)
			this.setCoord(i, array[i]);
	}

	/**
	 * 
	 * @param i
	 * @return The i-th entry of this vector.
	 */
	final public double getCoord(int i)
	{
		return array[begin + i];
	}

	public final void setToRandomOnDomain(double[] domain, Random rand)
	{

		for (int i = 0; i < dimension; i++)
		{
			array[begin + i] = domain[2 * i] + rand.nextDouble()
					* (domain[2 * i + 1] - domain[2 * i]);
		}
	}

	/**
	 * 
	 * @param i
	 * @param value
	 *            Sets the i-th entry of the vector to value.
	 */
	final public void setCoord(int i, double value)
	{
		array[begin + i] = value;
	}

	/**
	 * 
	 * @param v
	 * @return True if the values of this vector equals those of v. False
	 *         otherwise.
	 */
	final public boolean isEqualTo(MyVector v)
	{
		if (v.dimension() != dimension)
			return false;
		for (int i = 0; i < dimension; i++)
		{
			if (v.getCoord(i) != array[begin + i])
				return false;
		}
		return true;
	}

	/**
	 * this *= functions
	 * 
	 * @param scalar
	 */
	final public void multiplyBy(double scalar)
	{
		for (int i = 0; i < dimension; i++)
			array[begin + i] *= scalar;
	}

	/**
	 * this += vector
	 * 
	 * @param vector
	 */
	final public void add(MyVector vector)
	{
		for (int i = 0; i < dimension; i++)
			array[begin + i] += vector.getCoord(i);
	}

	/**
	 * this -= vector
	 * 
	 * @param vector
	 */
	final public void substract(MyVector vector)
	{
		for (int i = 0; i < dimension; i++)
			array[begin + i] -= vector.getCoord(i);
	}

	/**
	 * this = v+width
	 * 
	 * @param v
	 * @param w
	 */
	final public void setToSum(MyVector v, MyVector w)
	{
		for (int i = 0; i < dimension; i++)
			array[begin + i] = v.getCoord(i) + w.getCoord(i);
	}

	final public void setToSum(MyVector... vs)
	{
		this.setToZero();
		for (int i = 0; i < vs.length; i++)
		{
			this.add(vs[i]);
		}
	}
	
	

	/**
	 * this = v-w
	 * 
	 * @param v
	 * @param w
	 */
	final public void setToSubstraction(MyVector v, MyVector w)
	{
		for (int i = 0; i < dimension; i++)
			array[begin + i] = v.getCoord(i) - w.getCoord(i);
	}

	/**
	 * this += functions*vector
	 * 
	 * @param scalar
	 * @param vector
	 */
	final public void addScalarTimes(double scalar, MyVector vector)
	{
		for (int i = 0; i < dimension; i++)
			array[i + begin] += scalar * vector.getCoord(i);
	}

	/**
	 * this = functions*vector
	 * 
	 * @param scalar
	 * @param vector
	 */
	final public void setToScalarTimes(double scalar, MyVector vector)
	{
		for (int i = 0; i < dimension; i++)
			array[i + begin] = scalar * vector.getCoord(i);
	}

	/**
	 * Sets all the entries of this vector equal to zero.
	 */
	final public void setToZero()
	{
		for (int i = 0; i < dimension; i++)
			array[begin + i] = 0.0;
	}

	/**
	 * Dot products with the given vector
	 * 
	 * @param vector
	 * @return this.vector
	 */
	final public double dot(MyVector vector)
	{
		double dot = 0.0;
		for (int i = 0; i < dimension; i++)
			dot += array[begin + i] * vector.getCoord(i);
		return dot;
	}

	/**
	 * 
	 * @return The Euclidean norm of the vector.
	 */
	final public double norm()
	{
		return Math.sqrt(this.dot(this));
	}

	/**
	 * Normalizes this vector.
	 * 
	 * @return The norm of the vector before normalization.
	 */
	final public double normalize()
	{
		double norm = this.norm();
		if (norm != 0.0)
		{
			multiplyBy(1.0 / norm);
		}
		else
		{
			Log.error("Can't normalise zero vector!");
		}
		return norm;
	}

	/**
	 * 
	 * @return True is all the entries are zero. False otherwise.
	 */
	final public boolean isZero()
	{
		for (int i = 0; i < dimension; i++)
		{
			if (array[begin + i] != 0)
				return false;
		}
		return true;
	}

	/**
	 * 
	 * @return True if the underlying array is by reference. False otherwise.
	 */
	final public boolean isByReference()
	{
		return byReference;
	}

	/**
	 * Sets the i-th entry of the vector to 1.0 and the rest to 0.0.
	 * 
	 * @param i
	 */
	final public void setToBasisElement(int i)
	{
		this.setToZero();
		array[begin + i] = 1.0;
	}

	/**
	 * 
	 * @return The dimension of the vector.
	 */
	final public int dimension()
	{
		return dimension;
	}

	/**
	 * Sets the entries of this vector equal to those of v.
	 * 
	 * @param v
	 */
	final public void setEqual(MyVector v)
	{
		System.arraycopy(v.array, v.begin, this.array, this.begin, dimension);
		/*
		 * for(int i = 0; i < dimension; i++) array[i+begin]=v.getCoord(i);
		 */
	}

	/**
	 * 
	 * @return The entry with the highest absolute value
	 */
	final public double computeMaxNorm()
	{
		double max = 0;
		double val;
		for (int i = 0; i < dimension; i++)
		{
			val = Math.abs(array[begin + i]);
			if (val > max)
				max = val;
		}
		return max;
	}

	/**
	 * 
	 * @return Sum of the absolute value entries
	 */
	final public double computeMinkowskiNorm()
	{
		double minkNorm = Math.abs(array[begin]);
		for (int i = 1; i < dimension; i++)
		{
			minkNorm += Math.abs(array[begin + i]);
		}
		return minkNorm;
	}

	public String toString()
	{
		String out = "";
		if (dimension == 1)
		{
			out = Double.toString(array[begin]);
		}
		else
		{
			int count = dimension - 1;
			int i;

			out += "[";
			for (i = 0; i < count; i++)
			{
				out += Double.toString(array[begin + i]) + ",";
			}
			out += Double.toString(array[begin + i]) + "]";
		}
		return out;
	}

	/**
	 * 
	 * @return The smallest coordinate value of this vector.
	 */
	public double computeMinCoordinateValue()
	{
		double min = this.getCoord(0);
		double val;
		for (int i = 1; i < dimension; i++)
		{
			val = this.getCoord(i);
			if (val < min)
				min = val;
		}
		return min;
	}

	/**
	 * 
	 * @return The largest coordinate value of this vector.
	 */
	public double computeMaxCoordinateValue()
	{
		double max = this.getCoord(0);
		double val;
		for (int i = 1; i < dimension; i++)
		{
			val = this.getCoord(i);
			if (val > max)
				max = val;
		}
		return max;
	}

	public Object clone() throws CloneNotSupportedException
	{
		MyVector o = (MyVector) super.clone();
		if (!byReference)
		{
			o.array = (double[]) this.array.clone();
		}
		return o;
	}

	/**
	 * this = sum_i scalars[i]*vectors[i]
	 * 
	 * @param scalars
	 * @param vectors
	 */
	public void setToLinearCombination(MyVector scalars, MyVector[] vectors)
	{
		int count = scalars.dimension();
		this.setToZero();
		for (int i = 0; i < count; i++)
		{
			this.addScalarTimes(scalars.getCoord(i), vectors[i]);
		}
	}

	/**
	 * this = alpha p + beta q + gama r
	 * 
	 * @param alpha
	 * @param beta
	 * @param gamma
	 * @param p
	 * @param q
	 * @param r
	 */
	public void setToLinearCombination(double alpha, double beta, double gamma,
			MyVector p, MyVector q, MyVector r)
	{
		this.setToZero();
		this.addScalarTimes(alpha, p);
		this.addScalarTimes(beta, q);
		this.addScalarTimes(gamma, r);

	}

	/**
	 * this = alpa p + beta q
	 * 
	 * @param alpha
	 * @param beta
	 * @param p
	 * @param q
	 */
	public void setToLinearCombination(double alpha, double beta, MyVector p,
			MyVector q)
	{
		this.setToZero();
		this.addScalarTimes(alpha, p);
		this.addScalarTimes(beta, q);
	}

	/**
	 * 
	 * @param v
	 * @return the angle that this makes with v
	 */
	public double getAngleWith(MyVector v)
	{
		double thisNrm = this.norm();
		double vNrm = v.norm();
		double dot = this.dot(v);
		if ((thisNrm != 0) && (vNrm != 0))
		{
			return Math.acos(dot / (thisNrm * vNrm));
		}
		else
		{
			Log.error("Can't compute angle with zero vector, returning zeroQ");
		}
		return 0.0;
	}

	/**
	 * Sets the entries to random values distributed as a gaussian with mean 0
	 * and variance 1
	 * 
	 * @param rand
	 *            TODO
	 * 
	 */
	public void setToRandomGaussian(Random rand)
	{
		for (int i = 0; i < dimension; i++)
		{
			setCoord(i, rand.nextGaussian());
		}
	}

	/**
	 * 
	 * @param normal
	 *            Normal vector
	 * @param projectionOut
	 *            = projection of this onto plane orthogonal to normal.
	 */
	final public void projectToNormalPlane(MyVector normal,
			MyVector projectionOut)
	{
		MyVector n = new MyVector(normal.dimension());
		n.setEqual(normal);
		n.normalize();

		double s = this.dot(n);
		projectionOut.setEqual(this);
		projectionOut.addScalarTimes(-s, n);
	}

	final public void projectToNormalPlane(MyVector direction)
	{
		MyVector n = new MyVector(direction);
		n.normalize();

		double s = this.dot(n);
		this.addScalarTimes(-s, n); // Eliminating normal component
	}

	/**
	 * Copies the entries of this vector into the given array (starting at the
	 * first entry of the array)
	 * 
	 * @param f
	 */
	final public void copyToFloatArray(float[] f)
	{
		for (int i = 0; i < dimension; i++)
		{
			f[i] = (float) getCoord(i);
		}
	}

	/**
	 * This returns the index where the vector begins (when byReference = true).
	 * 
	 * @return
	 */
	public int getBegin()
	{
		return begin;
	}

	
	MyVector negative()
	{
		MyVector out = new MyVector(dimension);
		out.multiplyBy(-1.0);
		return out;
	}
	static public void main(String[] args)
	{
		MyVector v = new MyVector(2);
		v.incrementCoord(0, 2);
		v.incrementCoord(1, 3);
		v.incrementCoord(1, 7);
		System.out.println(v);
	}

	public static double distance(MyVector p, MyVector q)
	{
		double dstSq = 0.0;
		double diff;
		int count = p.dimension();
		for (int i = 0; i < count; i++)
		{
			diff = p.getCoord(i) - q.getCoord(i);
			dstSq += diff * diff;
		}
		return Math.sqrt(dstSq);
	}
	public static double distanceSquared(MyVector p, MyVector q)
	{
		double dstSq = 0.0;
		double diff;
		int count = p.dimension();
		for (int i = 0; i < count; i++)
		{
			diff = p.getCoord(i) - q.getCoord(i);
			dstSq += diff * diff;
		}
		return dstSq;
	}


    public double getMaxCoordValue() {
        double maxCoordValue = getCoord(0);
        for (int i = 1; i < dimension; i++) {
            if(getCoord(i) > maxCoordValue) maxCoordValue = getCoord(i);
        }
        return maxCoordValue;
    }

    public double getMinCoordValue() {
        double minCoordValue = getCoord(0);
        for (int i = 1; i < dimension; i++) {
            if(getCoord(i) < minCoordValue) minCoordValue = getCoord(i);
        }
        return minCoordValue;
    }


}
