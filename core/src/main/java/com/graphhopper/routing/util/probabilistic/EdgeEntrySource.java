package com.graphhopper.routing.util.probabilistic;

public class EdgeEntrySource
{
    private final String source;

    private double probability;

    public EdgeEntrySource(String source, double probability)
    {
        if (source == null || source.isEmpty())
        {
            throw new IllegalArgumentException("Source value must not be null or empty");
        }
        if (probability < 0 || probability > 1)
        {
            throw new IllegalArgumentException("Probability value must be between 0 and 1");
        }
        this.source = source;
        this.probability = probability;
    }

    public String getSource()
    {
        return source;
    }

    public double getProbability()
    {
        return probability;
    }

    public void setProbability( double probability )
    {
        this.probability = probability;
    }
}
