package com.graphhopper.routing.util.probabilistic;

import gnu.trove.set.hash.THashSet;

public class EdgeEntryData extends THashSet<EdgeEntryValue>
{
    public boolean containsEdgeEntryValue(EdgeEntryValueType edgeEntryValueType)
    {
        for (EdgeEntryValue value : this)
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
        if (!super.add(edgeEntryValue))
        {
            for (EdgeEntryValue value : this)
            {
                if (value.equals(edgeEntryValue))
                {
                    value.updateValues(edgeEntryValue.getValues());
                }
            }
        }
    }
}
