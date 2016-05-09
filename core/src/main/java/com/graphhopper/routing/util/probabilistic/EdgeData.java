package com.graphhopper.routing.util.probabilistic;

import com.graphhopper.util.shapes.BBox;
import gnu.trove.set.hash.THashSet;

public class EdgeData extends THashSet<EdgeEntry>
{
    public EdgeEntry getEntryForBoundingBox(BBox boundingBox)
    {
        for ( EdgeEntry entry : this)
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
        for (EdgeEntry entry : this)
        {
            if (entry.containsEdgeId(id))
            {
                return entry;
            }
        }
        return null;
    }

}
