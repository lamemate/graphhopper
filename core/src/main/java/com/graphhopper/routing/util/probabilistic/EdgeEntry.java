package com.graphhopper.routing.util.probabilistic;

import com.graphhopper.util.shapes.BBox;
import gnu.trove.map.TMap;
import gnu.trove.set.TIntSet;

import java.util.Date;
import java.util.List;

public class EdgeEntry
{
    // Bounding box for the grid
    private final BBox boundingBox;

    // All the edge ids belonging to the grid
    private final TIntSet edges;

    // Map of list of available values
    private TMap<Date, EdgeEntryData> values;

    public EdgeEntry( BBox boundingBox, TIntSet edges )
    {
        this.boundingBox = boundingBox;
        this.edges = edges;
    }

    public boolean containsEdgeId(int id)
    {
        return edges.contains(id);
    }

    /**
     * Returns EdgeEntryData closest to a given time and corresponding EdgeEntryValueType.
     *
     * @param date
     * @param edgeEntryValueType
     * @return
     */
    public EdgeEntryData getClosestSourcesForDateAndValueType( final Date date, EdgeEntryValueType edgeEntryValueType)
    {
        long minDiff = Long.MAX_VALUE;
        Date closestDate = date;

        for (Date keyDate : values.keySet())
        {
            final long diff = Math.abs(keyDate.getTime() - date.getTime());
            if (diff <= minDiff && values.get(keyDate).containsEdgeEntryValue(edgeEntryValueType))
            {
                minDiff = diff;
                closestDate = keyDate;
            }
        }
        return values.get(closestDate);
    }

    public BBox getBoundingBox()
    {
        return boundingBox;
    }

    public TIntSet getEdges()
    {
        return edges;
    }

    public TMap<Date, EdgeEntryData> getValues()
    {
        return values;
    }

    public void setValues( TMap<Date, EdgeEntryData> values )
    {
        this.values = values;
    }
}

