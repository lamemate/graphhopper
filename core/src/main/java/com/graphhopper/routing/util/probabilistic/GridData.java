package com.graphhopper.routing.util.probabilistic;

import com.graphhopper.util.shapes.BBox;
import com.sun.beans.util.Cache;

import java.util.LinkedHashSet;
import java.util.Set;

public class GridData
{
    private Set<GridEntry> entries;

    private GridEntry lastResult;

    public GridData()
    {
        this.entries = new LinkedHashSet<>(1500); // about the amount of grids
    }

    public GridEntry getEntryForBoundingBox( BBox boundingBox )
    {
        synchronized (entries)
        {
            if (lastResult != null && boundingBox.equals(lastResult.getBoundingBox()))
            {
                return lastResult;
            }
            for (GridEntry entry : entries)
            {
                if (boundingBox.equals(entry.getBoundingBox()))
                {
                    lastResult = entry;
                    return entry;
                }
            }
            return null;
        }

    }

    public GridEntry getGridEntryForEdgeId( int id )
    {
        if (lastResult != null && lastResult.containsEdgeId(id))
        {
            return lastResult;
        }
        synchronized (entries)
        {
            for (GridEntry entry : entries)
            {
                if (entry.containsEdgeId(id))
                {
                    lastResult = entry;
                    return entry;
                }
            }
            return null;
        }

    }

    public void updateWithGridEntry( GridEntry gridEntry )
    {
        synchronized (entries)
        {
            if (!entries.add(gridEntry))
            {
                for (GridEntry entry : entries)
                {
                    if (entry.equals(gridEntry))
                    {
                        entry.updateValues(gridEntry.getValues());
                    }
                }
            }
        }

    }
}
