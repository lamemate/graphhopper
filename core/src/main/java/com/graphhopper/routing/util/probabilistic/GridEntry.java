package com.graphhopper.routing.util.probabilistic;

import com.graphhopper.util.shapes.BBox;
import gnu.trove.TIntCollection;
import gnu.trove.set.TIntSet;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GridEntry
{
    // Bounding box for the grid
    private final BBox boundingBox;

    // All the edge ids belonging to the grid
    private final Set<Integer> edges;

    // Map of list of available values
    private ConcurrentMap<Date, GridEntryData> values;

    public GridEntry( BBox boundingBox, Set<Integer> edges )
    {
        this.boundingBox = boundingBox;
        this.edges = edges;
        this.values = new ConcurrentHashMap<Date, GridEntryData>();
    }

    public boolean containsEdgeId( int id )
    {
        return edges.contains(id);
    }

    /**
     * Returns GridEntryData closest to a given time and corresponding GridEntryValueType.
     *
     * @param date
     * @param gridEntryValueType
     * @return
     */
    public GridEntryData getClosestSourcesForDateAndValueType( final Date date, GridEntryValueType gridEntryValueType )
    {
        long minDiff = Long.MAX_VALUE;
        Date closestDate = date;

        for (Date keyDate : values.keySet())
        {
            final long diff = Math.abs(keyDate.getTime() - date.getTime());
            if (diff <= minDiff && values.get(keyDate).containsGridEntryValue(gridEntryValueType))
            {
                minDiff = diff;
                closestDate = keyDate;
            }
        }
        return values.get(closestDate);
    }

    public void updateWithGridEntryDataForDate( GridEntryData gridEntryData, Date date )
    {
        this.values.put(date, gridEntryData);
        // cleanup old entries on each update
        for (Date keyDate : this.values.keySet())
        {
            if (keyDate.before(new Date()))
            {
                this.values.remove(keyDate);
            }
        }
    }

    public void updateValues( ConcurrentMap<Date, GridEntryData> values )
    {
        this.values.putAll(values);
    }

    public BBox getBoundingBox()
    {
        return boundingBox;
    }

    public Set<Integer> getEdges()
    {
        return edges;
    }

    public ConcurrentMap<Date, GridEntryData> getValues()
    {
        return values;
    }

    public void setValues( ConcurrentMap<Date, GridEntryData> values )
    {
        this.values = values;
    }

    @Override
    public boolean equals( Object o )
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GridEntry gridEntry = (GridEntry) o;

        return boundingBox.equals(gridEntry.boundingBox);

    }

    @Override
    public int hashCode()
    {
        return boundingBox.hashCode();
    }
}

