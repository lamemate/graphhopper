package com.graphhopper.routing.util.probabilistic;

import gnu.trove.set.hash.THashSet;

import java.util.Set;

public class GridEntryData
{
    private Set<GridEntryValue> entries;

    public GridEntryData()
    {
        this.entries = new THashSet<GridEntryValue>();
    }

    public boolean containsGridEntryValue( GridEntryValueType gridEntryValueType )
    {
        synchronized (entries)
        {
            for (GridEntryValue value : entries)
            {
                if (value.getValues().containsKey(gridEntryValueType))
                {
                    return true;
                }
            }
            return false;
        }

    }

    public void updateWithGridEntryValue( GridEntryValue gridEntryValue )
    {
        synchronized (entries)
        {
            if (!entries.add(gridEntryValue))
            {
                for (GridEntryValue value : entries)
                {
                    if (value.equals(gridEntryValue))
                    {
                        value.updateValues(gridEntryValue.getValues());
                    }
                }
            }
        }

    }

    public double calculateMeanValueForGridEntryValueType(GridEntryValueType type)
    {
        double mean = 0;
        for (GridEntryValue value : entries)
        {
            mean += value.getSource().getProbability() * value.getValues().get(type);
        }
        return mean;
    }
}
