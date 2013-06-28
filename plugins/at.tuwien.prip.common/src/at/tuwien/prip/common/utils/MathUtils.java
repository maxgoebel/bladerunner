/*******************************************************************************
 * Copyright (c) 2013 Max Göbel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Max Göbel - initial API and implementation
 ******************************************************************************/
package at.tuwien.prip.common.utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import at.tuwien.prip.common.datastructures.SparseMatrix2;


/**
 * 
 * Common calculations from probability, matrix, etc.
 * 
 * @author max
 *
 */
public class MathUtils
{

	/**
	 *
	 */
	public static class _Gaussian
	{
		/**
		 * return phi(x) = standard Gaussian pdf
		 * @param x
		 * @return
		 */
		public static double phi(double x) 
		{
			return Math.exp(-x*x / 2) / Math.sqrt(2 * Math.PI);
		}

		/**
		 * return phi(x, mu, signma) = Gaussian pdf with mean mu and stddev sigma
		 * @param x
		 * @param mu
		 * @param sigma
		 * @return
		 */
		public static double phi(double x, double mu, double sigma) 
		{
			return phi((x - mu) / sigma) / sigma;
		}

		/**
		 * return Phi(z) = standard Gaussian cdf using Taylor approximation
		 * @param z
		 * @return
		 */
		public static double Phi(double z)
		{
			if (z < -8.0) return 0.0;
			if (z >  8.0) return 1.0;
			double sum = 0.0, term = z;
			for (int i = 3; sum + term != sum; i += 2) {
				sum  = sum + term;
				term = term * z * z / i;
			}
			return 0.5 + sum * phi(z);
		}

		/**
		 * return Phi(z, mu, sigma) = Gaussian cdf with mean mu and stddev sigma
		 * @param z
		 * @param mu
		 * @param sigma
		 * @return
		 */
		public static double Phi(double z, double mu, double sigma) 
		{
			return Phi((z - mu) / sigma);
		}

		// test client
		public static void main(String[] args)
		{
			double z     = 0;//Double.parseDouble(args[0]);
			double mu    = 0;//Double.parseDouble(args[1]);
			double sigma = 1;//Double.parseDouble(args[2]);
			System.out.println(Phi(z, mu, sigma));
		}

	}//MathUtils$_Gaussian

	/**
	 *
	 * @author max
	 *
	 */
	public static class _Statistics 
	{

		/**
		 * 
		 * Calculate the standard deviation of a list of values.
		 * 
		 * @param values
		 * @return
		 */
		public static double getStandardDeviation (List<Double> values) 
		{
			if (values.size()==0) return -1d;
			double mean = getMean(values);
			return Math.sqrt( getSquareSum(values)/values.size() - mean*mean);
		}
		
		/**
		 * 
		 * @param values
		 * @return
		 */
		public static double getSum (List<Double> values) 
		{
			if (values.size()==0) return -1d;
			double sum = 0;
			for (Double value : values)
				sum+=value;
			return sum;
		}

		/**
		 * 
		 * @param values
		 * @return
		 */
		public static double getSquareSum (List<Double> values)
		{
			if (values.size()==0) return -1d;
			double squaresum = 0;
			for (Double value : values)
				squaresum += value*value;
			return squaresum;
		}

		/**
		 * 
		 * @param values
		 * @return
		 */
		public static double getMean (List<Double> values) 
		{
			if (values.size()==0) return -1d;
			return getSum(values)/values.size();
		}
		
		/**
		 * 
		 * Calculate the z-test of a sample wrt to a population.
		 *  z = (mean(x) - x) / (std / sqrt(|population|)),
		 *  where (std / sqrt(|population|) is the standard error
		 *  of the distributions, SE.
		 * 
		 * @param x
		 * @param population
		 * @return
		 */
		public static double getZScore (Double x, List<Double> population) 
		{
			double denom = getStandardDeviation(population) / (Math.log(population.size()));
			double mean  = getMean(population);
			return (x-mean)/denom;
		}

		/**
		 * Normalize a list of doubles by dividing each
		 * member of the list by the maximum value in the list.
		 * @param input
		 * @return the normalized list
		 */
		public static List<Double> normalize (List<Double> input) 
		{
			// find max value
			double max = input.get(0);
			for (Double in : input) {
				if (in>max) max = in;
			}
			List<Double> result = new LinkedList<Double> ();
			for (Double in : input) {
				result.add(in/max);
			}
			return result;
		}
		
	}//MathUtils$_Statistics

	
	public static class _Convert 
	{

		/**
		 *
		 * @param input
		 * @return
		 */
		public static List<Double> convertInt2DoubleList (Collection<Integer> input)
		{
			List<Double> output = new LinkedList<Double> () ;
			for (int i : input) {
				output.add((double)i);
			}
			return output;
		}

        /**
         * Method to round a double value to the given precision.
         *
         * @param <b>val </b> The double to be rounded
         * @param <b>precision </b> Rounding precision
         *
         * @return <b>double</b> The rounded value
         */
        public static double round(double val, int precision) 
        {
          // Multiply by 10 to the power of precision and add 0.5 for rounding up
          // Take the nearest integer smaller than this value
          val = Math.floor((val * Math.pow(10, precision)) + 0.5);

          // Divide it by 10**precision to get the rounded value
          return val / Math.pow(10, precision);
        }

	}//MathUtils$_Convert

	/**
	 *
	 * @author max
	 *
	 */
	public static class _Vector
	{

		/**
		 * Compute the inner product (dot product) between two vectors of double.
		 * 
		 * @param a
		 * @param b
		 * @return the inner product, a.b
		 */
		public static int dotProduct (Vector<Double> a, Vector<Double> b)
		{
			int sum=0;
			for (int i=0; i<a.size(); i++)
				sum += a.get(i) * b.get(i);
			return sum;
		}

		/**
		 * Compute the inner product of square root of two vectors.
		 * 
		 * @param a
		 * @param b
		 * @return
		 */
		public static double sqrtDotProduct (Vector<Double> a, Vector<Double> b)
		{
			double sum=0;
			for (int i=0; i<a.size(); i++)
				sum += Math.sqrt(a.get(i) * b.get(i));
			return sum;
		}

	}//MathUtils$_Vector

	/**
	 * Calculate the factorial of an integer N: N!
	 * @param N
	 * @return
	 */
	public static long factorial(int N)
	{
		long n = 1;
		for (int i = 1; i <= N; i++) {
			n = n * i;
		}
		return n;
    }
	
	/**
	 * 
	 * Calculates a dot product between two vector instances.
	 *
	 *
	 */
	public static double dotProd(double[] a, double[] b)
	{
		double result = 0;

		assert a.length==b.length;
		for (int i=0; i<a.length; i++) {
			result += a[i] * b[i];
		}

		return result;
	}

	/**
	 *
	 * @author max
	 *
	 */
	public static class _Matrix
	{

		/**
		 * Build a square matrix with values defaulted to 'value'.
		 * @param dim
		 * @param value
		 * @return
		 */
		public static double[][] buildDefaultSquareMatrix (int dim, double value) 
		{
			double[][] M = new double[dim][dim];
			for (int i=0; i<M.length; i++) {
				for (int j=0; j<M[0].length; j++) {
					M[i][j]=value;
				}
			}
			return M;
		}

		/**
		 * 
		 * The identity matrix or unit matrix of size n is the n-by-n square
		 * matrix with ones on the main diagonal and zeros elsewhere.
		 * 
		 * @param dim
		 * @return the n-dimensional identity matrix
		 */
		public static double[][] buildIdentityMatrix (int n) 
		{
			double[][] I = new double[n][n];
			for (int i=0; i<I.length; i++) {
				for (int j=0; j<I[0].length; j++) {
					if (i==j) {
						I[i][j]=1;
					} else {
						I[i][j]=0;
					}
				}
			}
			return I;
		}
		
		/**
		 * Frobenius inner product.
		 * 
		 * @param a
		 * @param b
		 * @return
		 */
		public static double dotProd(
				SparseMatrix2<Integer,Integer,Double> a, 
				SparseMatrix2<Integer, Integer,Double> b) 
		{
			double result = 0d;

			assert a.size()==b.size();
			for (int i=0; i<a.size(); i++) {
				for (int j=0; j<b.size(); j++) {
					result += a.get(i, j) * b.get(i, j);
				}
			}

			return result;
		}

//		/**
//		 * 
//		 * Write a matrix to xml.
//		 * 
//		 * Format:
//		 * 
//		 * <matrix>
//		 * 		<dim>n:m</dim>
//		 * 		<row> 0 1 0 0 1 0 </row>
//		 * 		<row> 1 0 0 0 0 1 </row>
//		 * ...
//		 * ...
//		 * </matrix>
//		 * 
//		 * @param m, the matrix to be written
//		 * @param uri, the file location to write to
//		 */
//		public static void writeMatrix (double[][] m, URI uri) {
//			int dimA = m.length;
//			int dimB = m[0].length;
//			
//			XMLDocument doc = new XMLDocument();
//			XMLObject root = doc.getRoot();
//			
//			XMLObject matrixRoot = root.addChild("matrix", null);
//			matrixRoot.addChild("dim", dimA+":"+dimB);
//			
//			
//			for (int i=0; i<m.length; i++) {
//				StringBuffer row = new StringBuffer();
//				for (int j=0; j<m[i].length; j++) {
//					row.append(m[i][j]+" ");
//				}
//				matrixRoot.addChild("row", row.toString().substring(0, row.length()-1));
//			}
//			
//			doc.write(uri);
//		}
//


		
//		/**
//		 * 
//		 * Parse a matrix from a XML file at a given URI. 
//		 * 
//		 * Format:
//		 * 
//		 * <matrix>
//		 * 		<dim>n:m</dim>
//		 * 		<row> 0 1 0 0 1 0 </row>
//		 * 		<row> 1 0 0 0 0 1 </row>
//		 * ...
//		 * ...
//		 * </matrix>
//		 * 
//		 * @param uri
//		 * @return
//		 */
//		public static double[][] parseMatrix (URI uri) {
//			
//			XMLDocument doc = new XMLDocument();
//			Document dom = doc.parse2document(uri);
//			Element root = dom.getDocumentElement();
//			
//			String dims = getNamedChildElements(root, "dim").get(0).getNodeValue();
//			String[] m_dims = dims.split(":");
//			
//			double[][] result = new double[Integer.parseInt(m_dims[0])][Integer.parseInt(m_dims[1])] ;
//			
//			List<Element> rows = getNamedChildElements(root, "row");
//			for (int i=0; i<rows.size(); i++) {
//				String s = rows.get(i).getNodeValue();
//				String[] vals = s.split("\\s");
//				for (int j=0; j<vals.length; j++) {
//					result[i][j] = Double.parseDouble(vals[j]);
//				}
//			}
//			return result;
//		}
	}//MathUtils$_Matrix

} //MathUtils
