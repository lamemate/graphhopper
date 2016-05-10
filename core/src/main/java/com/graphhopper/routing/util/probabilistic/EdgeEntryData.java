package com.graphhopper.routing.util.probabilistic;

import gnu.trove.set.hash.THashSet;

import java.util.Set;

public class EdgeEntryData
{
    private Set<EdgeEntryValue> entries;

    public EdgeEntryData()
    {
        this.entries = new THashSet<EdgeEntryValue>();
    }

    public boolean containsEdgeEntryValue(EdgeEntryValueType edgeEntryValueType)
    {
        for (EdgeEntryValue value : entries)
        {
            if (value.getValues().containsKey(edgeEntryValueType))
            {
                return true;
            }
        }
        return false;
    }

    public void updateWithEdgeEntryValue( EdgeEntryValue edgeEntryValue )
    {
        if (!entries.add(edgeEntryValue))
        {
            for (EdgeEntryValue value : entries)
            {
                if (value.equals(edgeEntryValue))
                {
                    value.updateValues(edgeEntryValue.getValues());
                }
            }
        }
    }
}
