package com.graphhopper.routing.util.probabilistic;

import com.graphhopper.util.shapes.BBox;

import java.util.LinkedHashSet;
import java.util.Set;

public class GridData
{
    private Set<GridEntry> entries;

    public GridData()
    {
        this.entries = new LinkedHashSet<>(1500); // about the amount of grids
    }

    public GridEntry getEntryForBoundingBox( BBox boundingBox )
    {
        synchronized (entries)
        {
            for (GridEntry entry : entries)
            {
                if (boundingBox.equals(entry.getBoundingBox()))
                {
                    return entry;
                }
            }
            return null;
        }

    }

    public GridEntry getGridEntryForEdgeId( int id )
    {
        synchronized (entries)
        {
            for (GridEntry entry : entries)
            {
                if (entry.containsEdgeId(id))
                {
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
