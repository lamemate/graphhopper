package com.graphhopper.routing.util.probabilistic;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GridEntryValue
{
    private GridEntrySource source;

    private ConcurrentMap<GridEntryValueType, Double> values;

    public GridEntryValue()
    {
        this.values = new ConcurrentHashMap<GridEntryValueType, Double>();
    }

    public void updateValues( ConcurrentMap<GridEntryValueType, Double> values )
    {
        this.values.putAll(values);
    }

    public GridEntrySource getSource()
    {
        return source;
    }

    public void setSource( GridEntrySource source )
    {
        this.source = source;
    }

    public ConcurrentMap<GridEntryValueType, Double> getValues()
    {
        return values;
    }

    public void setValues( ConcurrentMap<GridEntryValueType, Double> values )
    {
        this.values = values;
    }

    @Override
    public boolean equals( Object o )
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GridEntryValue that = (GridEntryValue) o;

        return source.equals(that.source);

    }

    @Override
    public int hashCode()
    {
        return source.hashCode();
    }
}
