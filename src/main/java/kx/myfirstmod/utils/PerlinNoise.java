package kx.myfirstmod.utils;

public class PerlinNoise {
    // lower the scale the slower the smoother the change
    public static double noise1D(double x, double changeRate) {
        return Math.sin(changeRate * x) + Math.sin(Math.PI * changeRate * x / 2);
    }

    public static double noise1DPositive(double x, double changeRate) {
        return (noise1D(x, changeRate) / 2) + 1;
    }
}
