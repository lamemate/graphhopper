package com.graphhopper.routing.util.probabilistic;

import gnu.trove.map.TMap;

public class GridEntryValue
{
    private GridEntrySource source;

    private TMap<GridEntryValueType, Double> values;

    public void updateValues(TMap<GridEntryValueType, Double> values)
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

    public TMap<GridEntryValueType, Double> getValues()
    {
        return values;
    }

    public void setValues( TMap<GridEntryValueType, Double> values )
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
