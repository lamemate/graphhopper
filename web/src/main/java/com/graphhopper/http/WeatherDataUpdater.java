package com.graphhopper.http;

import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.probabilistic.*;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.util.EdgeExplorer;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.shapes.BBox;
import de.fu_berlin.agdb.importer.payload.GridMetaData;
import de.fu_berlin.agdb.importer.payload.LocationWeatherData;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.locks.Lock;

public class WeatherDataUpdater
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Graph graph;
    private final NodeAccess nodeAccess;
    private final Lock writeLock;
    private final int seconds = 150;

    private GridData gridData;

    public WeatherDataUpdater( GraphHopper hopper, GridData gridData, Lock writeLock )
    {
        this.graph = hopper.getGraphHopperStorage();
        this.nodeAccess = graph.getNodeAccess();
        this.gridData = gridData;
        this.writeLock = writeLock;
    }

    public void feedData(LocationWeatherData data)
    {
        writeLock.lock();
        try {
            feedLocked(data);
        } finally {
            writeLock.unlock();
        }
    }

    private void feedLocked(LocationWeatherData data)
    {
        GridMetaData gridMetaData = (GridMetaData) data.getLocationMetaData();
        double gridLat = gridMetaData.getGridLat();
        double gridLon = gridMetaData.getGridLon();

        BBox bBox = new BBox(gridLon - 0.125, gridLon + 0.125, gridLat - 0.125, gridLat + 0.125);
        TIntSet edges = new TIntHashSet(8000); // totally guessed value

        EdgeExplorer explorer = graph.createEdgeExplorer();

        for(int nodeIndex = 0; nodeIndex < graph.getNodes(); nodeIndex++)
        {
            EdgeIterator edgeIterator = explorer.setBaseNode(nodeIndex);
            while (edgeIterator.next())
            {
                if(bBox.contains(nodeAccess.getLat(nodeIndex), nodeAccess.getLon(nodeIndex)))
                {
                    edges.add(edgeIterator.getEdge());
                }
            }
        }

        GridEntry gridEntry = new GridEntry(bBox, edges);

        GridEntryValue gridEntryValue = new GridEntryValue();
        TMap<GridEntryValueType, Double> values = new THashMap<GridEntryValueType, Double>(12);
        values.put(GridEntryValueType.WEATHER_CLOUDAGE, data.getCloudage());
        values.put(GridEntryValueType.WEATHER_PRECIPITATION_DEPTH, data.getPrecipitationDepth());
        values.put(GridEntryValueType.WEATHER_WINDCHILL, data.getWindChill());
        values.put(GridEntryValueType.WEATHER_WIND_SPEED, data.getWindSpeed());
        values.put(GridEntryValueType.WEATHER_ATMOSPHERE_HUMIDITY, data.getAtmosphereHumidity());
        values.put(GridEntryValueType.WEATHER_ATMOSPHERE_PRESSURE, data.getAtmospherePressure());
        values.put(GridEntryValueType.WEATHER_TEMPERATURE, data.getTemperature());
        values.put(GridEntryValueType.WEATHER_MAXIMUM_WIND_SPEED, data.getMaximumWindSpeed());
        values.put(GridEntryValueType.WEATHER_SUNSHINE_DURATION, data.getSunshineDuration());
        values.put(GridEntryValueType.WEATHER_SNOW_HEIGHT, data.getSnowHeight());
        values.put(GridEntryValueType.WEATHER_TEMPERATURE_HIGH, data.getTemperatureHigh());
        values.put(GridEntryValueType.WEATHER_TEMPERATURE_LOW, data.getTemperatureLow());
        gridEntryValue.setValues(values);
        gridEntryValue.setSource(new GridEntrySource("NOAA", 1)); // TODO get from JSON

        GridEntryData gridEntryData = new GridEntryData();
        gridEntryData.updateWithGridEntryValue(gridEntryValue);

        gridEntry.updateWithGridEntryDataForDate(gridEntryData, new Date(data.getTimestamp()));

        gridData.updateWithGridEntry(gridEntry);
    }
}
