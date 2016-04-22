package com.graphhopper.routing.util.probabilistic;

import com.graphhopper.util.shapes.BBox;
import gnu.trove.map.TMap;
import gnu.trove.set.TIntSet;

import java.util.Date;

public class EdgeEntry
{
    // Bounding box for the grid
    private final BBox boundingBox;

    // All the edge ids belonging to the grid
    private final TIntSet edges;

    // Map of map of map containing the grid values
    private TMap<Date, TMap<EdgeEntrySource, TMap<EdgeEntryValueType, Integer>>> values;

    public EdgeEntry( BBox boundingBox, TIntSet edges )
    {
        this.boundingBox = boundingBox;
        this.edges = edges;
    }

    public boolean containsEdgeId(int id)
    {
        return edges.contains(id);
    }

    public BBox getBoundingBox()
    {
        return boundingBox;
    }

    public TIntSet getEdges()
    {
        return edges;
    }

    public TMap<Date, TMap<EdgeEntrySource, TMap<EdgeEntryValueType, Integer>>> getValues()
    {
        return values;
    }

    public void setValues( TMap<Date, TMap<EdgeEntrySource, TMap<EdgeEntryValueType, Integer>>> values )
    {
        this.values = values;
    }
}

