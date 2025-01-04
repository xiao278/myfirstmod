package kx.myfirstmod.utils;

public class NormalDistribution {
    public static double nextValue(double mean, double stdDev) {
        // Generate two uniform random values in (0, 1)
        double u1 = Math.random();
        double u2 = Math.random();

        // Apply Box-Muller transform
        double z0 = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);

        // Scale and shift by mean and standard deviation
        return z0 * stdDev + mean;
    }
}
