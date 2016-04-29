package com.graphhopper.routing.util.probabilistic;

import gnu.trove.map.TMap;

public class EdgeEntryValue
{
    private EdgeEntrySource source;

    private TMap<EdgeEntryValueType, Double> values;

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
}
