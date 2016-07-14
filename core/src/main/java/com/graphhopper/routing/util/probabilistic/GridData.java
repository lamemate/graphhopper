package com.graphhopper.routing.util.probabilistic;

import com.graphhopper.util.shapes.BBox;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GridData
{
    private static GridData instance = new GridData();

    private GridData()
    {
        this.entries = new LinkedHashSet<>();
        this.edgeCache = new ConcurrentHashMap<>();
    }

    public static GridData getInstance()
    {
        return instance;
    }

    private Set<GridEntry> entries;

    private GridEntry lastResult;

    private ConcurrentMap<Integer, GridEntry> edgeCache;

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
        return edgeCache.get(id);
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
            else
            {
                for (int edge : gridEntry.getEdges())
                {
                    edgeCache.put(edge, gridEntry);
                }
            }
        }

    }
}
