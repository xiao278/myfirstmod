package kx.myfirstmod;

public class BinomialDistribution {

    // Function to perform binomial sampling
    public static int sample(int n, double p) {
        int successes = 0;

        for (int i = 0; i < n; i++) {
            // Perform a single Bernoulli trial
            if (Math.random() < p) {
                successes++;
            }
        }

        return successes;
    }

}
