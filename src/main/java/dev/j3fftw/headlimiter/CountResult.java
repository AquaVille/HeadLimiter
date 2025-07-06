package dev.j3fftw.headlimiter;

import javax.annotation.Nonnull;
import java.util.Map;

public class CountResult {

    private final int total;
    private final Map<String, Integer> counts;

    protected CountResult(int total, Map<String, Integer> counts) {
        this.total = total;
        this.counts = counts;
    }

    public int getTotal() {
        return total;
    }

    @Nonnull
    public Map<String, Integer> getCounts() {
        return counts;
    }
}
