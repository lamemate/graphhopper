package com.graphhopper.routing.util.probabilistic;

import com.graphhopper.util.shapes.BBox;
import gnu.trove.set.hash.THashSet;

import java.util.Set;

public class EdgeData
{
    private Set<EdgeEntry> entries;

    public EdgeData()
    {
        this.entries = new THashSet<EdgeEntry>(1500); // about the amount of grids
    }

    public EdgeEntry getEntryForBoundingBox(BBox boundingBox)
    {
        for ( EdgeEntry entry : entries)
        {
            if (entry.getBoundingBox().equals(boundingBox))
            {
                return entry;
            }
        }
        return null;
    }

    public EdgeEntry getEntryForEdgeId(int id)
    {
        for (EdgeEntry entry : entries)
        {
            if (entry.containsEdgeId(id))
            {
                return entry;
            }
        }
        return null;
    }

    public void updateWithEdgeEntry(EdgeEntry edgeEntry)
    {
        if (!entries.add(edgeEntry))
        {
            for (EdgeEntry entry : entries)
            {
                if (entry.equals(edgeEntry))
                {
                    entry.updateValues(edgeEntry.getValues());
                }
            }
        }
    }
}
