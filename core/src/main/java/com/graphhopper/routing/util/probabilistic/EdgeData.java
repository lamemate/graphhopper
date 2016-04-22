package com.graphhopper.routing.util.probabilistic;

import com.graphhopper.util.shapes.BBox;

import java.util.ArrayList;

public class EdgeData extends ArrayList<EdgeEntry>
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
