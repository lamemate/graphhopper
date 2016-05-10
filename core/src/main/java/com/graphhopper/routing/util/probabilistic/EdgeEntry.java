package com.graphhopper.routing.util.probabilistic;

import com.graphhopper.util.shapes.BBox;
import gnu.trove.TIntCollection;
import gnu.trove.map.TMap;
import gnu.trove.set.TIntSet;

import java.util.Date;

public class EdgeEntry
{
    // Bounding box for the grid
    private final BBox boundingBox;

    // All the edge ids belonging to the grid
    private final TIntCollection edges;

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

    public void updateWithEdgeEntryDataForDate(EdgeEntryData edgeEntryData, Date date)
    {
        this.values.put(date, edgeEntryData);
        // cleanup old entries on each update
        for (Date keyDate : this.values.keySet())
        {
            if (keyDate.before(new Date()))
            {
                this.values.remove(keyDate);
            }
        }
    }

    public void updateValues(TMap<Date, EdgeEntryData> values)
    {
        this.values.putAll(values);
    }

    public BBox getBoundingBox()
    {
        return boundingBox;
    }

    public TIntCollection getEdges()
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

    @Override
    public boolean equals( Object o )
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EdgeEntry edgeEntry = (EdgeEntry) o;

        return boundingBox.equals(edgeEntry.boundingBox);

    }

    @Override
    public int hashCode()
    {
        return boundingBox.hashCode();
    }
}

