package hw2;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    // perform T independent experiments on an N-by-N grid
    private int[] results;
    private int T;

    public PercolationStats(int N, int T, PercolationFactory pf) {
        this.results = new int[T];
        this.T = T;

        for (int round = 0; round < T; round++) {
            Percolation perc = pf.make(N);
            int count = 0;
            while (!perc.percolates()) {
                int row = StdRandom.uniform(N);
                int col = StdRandom.uniform(N);
                if (perc.isOpen(row, col)) {
                    continue;
                }
                perc.open(row, col);
                count++;
            }
            results[round] = count / N * N;
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(results);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(results);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLow() {
        return mean() - 1.96 * stddev() / Math.sqrt(T);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHigh() {
        return mean() + 1.96 * stddev() / Math.sqrt(T);
    }
}
