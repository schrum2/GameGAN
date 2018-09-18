/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.util.random;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class creates a random number generator that stores the current seed
 * value so the program can be resumed with the current seed saved.
 *
 * @author Jacob
 */
public class ResumableRandom extends Random {

	/**
	 * autogenerated serial version ID
	 */
	private static final long serialVersionUID = 8857335572195241681L;

	/**
	 * creates a random number generator using the seed parameter, is inherited
	 * from java.util
	 * 
	 * @param seed:
	 *            long value used to seed random number generator
	 */
	public ResumableRandom(long seed) {
		super(seed);
	}

	/**
	 * creates a random number generator inherited from java.util
	 */
	public ResumableRandom() {
		super();
	}

	/**
	 * Code for troubleshooting
	 */
	// @Override
	// public int nextInt(int x) {
	// int result = super.nextInt(x);
	// System.out.println("Random int " + result);
	// return result;
	// }
	//
	// @Override
	// public double nextDouble(){
	// double result = super.nextDouble();
	// System.out.println("Random double " + result);
	// return result;
	// }
	/**
	 * Accesses and hard sets the seed of the resumable random number generator
	 * 
	 * @param newSeed:
	 *            long value that acts as new seed
	 */
	public void hardSetSeed(long newSeed) {
		try {
			// gets seed field from the resumable random
			Field field = this.getClass().getSuperclass().getDeclaredField("seed");
			// hardcodes field so the seed value is always accessible
			field.setAccessible(true);
			// casts the seed of the resumable random to an Atomiclong
			AtomicLong seed = (AtomicLong) field.get(this);
			// resets seed of resumable random number generator to the long
			// parameter newSeed
			seed.set(newSeed);
			// these exceptions should not occur and if so, they crash the
			// program
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			System.exit(1);
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
			System.exit(1);
		} catch (NoSuchFieldException ex) {
			ex.printStackTrace();
			System.exit(1);
		} catch (SecurityException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Performs same operation as hardSetSeed method to access the seed of the
	 * random number generator, but instead returns the seed currently being
	 * used for the random number generator.
	 * 
	 * @return: current long seed value in random number generator
	 */
	public long getSeed() {
		try {
			Field field = this.getClass().getSuperclass().getDeclaredField("seed");
			field.setAccessible(true);
			AtomicLong seed = (AtomicLong) field.get(this);
			return seed.get();
			// these exceptions should not occur and if so, they crash the
			// program
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			System.exit(1);
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
			System.exit(1);
		} catch (NoSuchFieldException ex) {
			ex.printStackTrace();
			System.exit(1);
		} catch (SecurityException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		// if the try and catch clauses fail, returns 0. Random number
		// generators never have seed value so client can
		// know code is broken if 0 is returned. Should never occur
		// theoretically.
		return 0;
	}

	public static void main(String[] args) {
		ResumableRandom r1 = new ResumableRandom(0);

		System.out.println("seed: " + r1.getSeed());
		System.out.println(r1.nextInt(100) + ":" + r1.getSeed());
		System.out.println(r1.nextInt(100) + ":" + r1.getSeed());
		System.out.println(r1.nextInt(100) + ":" + r1.getSeed());
		System.out.println(r1.nextInt(100) + ":" + r1.getSeed());
		System.out.println(r1.nextInt(100) + ":" + r1.getSeed());
		System.out.println("----------------------");

		ResumableRandom r2 = new ResumableRandom(0);

		System.out.println(r2.nextInt(100) + ":" + r2.getSeed());
		System.out.println(r2.nextInt(100) + ":" + r2.getSeed());
		System.out.println(r2.nextInt(100) + ":" + r2.getSeed());
		System.out.println(r2.nextInt(100) + ":" + r2.getSeed());
		System.out.println(r2.nextInt(100) + ":" + r2.getSeed());
		System.out.println("----------------------");

		r1 = new ResumableRandom(0);

		System.out.println(r1.nextInt(100) + ":" + r1.getSeed());
		long seed = r1.getSeed();
		r2.hardSetSeed(seed);
		System.out.println(r2.nextInt(100) + ":" + r2.getSeed());
		seed = r2.getSeed();
		r1.hardSetSeed(seed);
		System.out.println(r1.nextInt(100) + ":" + r1.getSeed());
		seed = r1.getSeed();
		r2.hardSetSeed(seed);
		System.out.println(r2.nextInt(100) + ":" + r2.getSeed());
		seed = r2.getSeed();
		r1.hardSetSeed(seed);
		System.out.println(r1.nextInt(100) + ":" + r1.getSeed());
		System.out.println("----------------------");

	}
}
