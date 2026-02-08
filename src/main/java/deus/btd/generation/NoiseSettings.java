package deus.btd.generation;

public final class NoiseSettings {
    public double xScale;
    public double zScale;
    public double exponent;
    public double fuzzPercentage;

    public NoiseSettings(
        double xScale,
        double zScale,
        double exponent,
        double fuzzPercentage
    ) {
        this.xScale = xScale;
        this.zScale = zScale;
        this.exponent = exponent;
        this.fuzzPercentage = fuzzPercentage;
    }

    public NoiseSettings withScale(double x, double z) {
        this.xScale = x;
        this.zScale = z;
        return this;
    }

    public NoiseSettings withExponent(double exponent) {
        this.exponent = exponent;
        return this;
    }

    public NoiseSettings withFuzz(double fuzzPercentage) {
        this.fuzzPercentage = fuzzPercentage;
        return this;
    }
}
