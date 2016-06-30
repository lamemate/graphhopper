package com.graphhopper.routing.util.probabilistic;

public class GridEntrySource
{
    private final String source;

    private double probability;

    public GridEntrySource( String source, double probability )
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

    @Override
    public boolean equals( Object o )
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GridEntrySource that = (GridEntrySource) o;

        return source.equals(that.source);

    }

    @Override
    public int hashCode()
    {
        return source.hashCode();
    }
}
