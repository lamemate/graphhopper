package com.graphhopper.routing.util.probabilistic;

import gnu.trove.map.TMap;

public class EdgeEntryValue
{
    private EdgeEntrySource source;

    private TMap<EdgeEntryValueType, Double> values;

    public void updateValues(TMap<EdgeEntryValueType, Double> values)
    {
        this.values.putAll(values);
    }

    public EdgeEntrySource getSource()
    {
        return source;
    }

    public void setSource( EdgeEntrySource source )
    {
        this.source = source;
    }

    public TMap<EdgeEntryValueType, Double> getValues()
    {
        return values;
    }

    public void setValues( TMap<EdgeEntryValueType, Double> values )
    {
        this.values = values;
    }

    @Override
    public boolean equals( Object o )
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EdgeEntryValue that = (EdgeEntryValue) o;

        return source.equals(that.source);

    }

    @Override
    public int hashCode()
    {
        return source.hashCode();
    }
}
