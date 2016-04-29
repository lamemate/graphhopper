package com.graphhopper.routing.util.probabilistic;

import java.util.ArrayList;

public class EdgeEntryData extends ArrayList<EdgeEntryValue>
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
}
