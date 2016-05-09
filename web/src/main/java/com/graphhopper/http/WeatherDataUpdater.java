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

    private EdgeData edgeData;

    public WeatherDataUpdater( GraphHopper hopper, EdgeData edgeData, Lock writeLock )
    {
        this.graph = hopper.getGraphHopperStorage();
        this.nodeAccess = graph.getNodeAccess();
        this.edgeData = edgeData;
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

        EdgeEntry edgeEntry = new EdgeEntry(bBox, edges);

        EdgeEntryValue edgeEntryValue = new EdgeEntryValue();
        TMap<EdgeEntryValueType, Double> values = new THashMap<EdgeEntryValueType, Double>(12);
        values.put(EdgeEntryValueType.WEATHER_CLOUDAGE, data.getCloudage());
        values.put(EdgeEntryValueType.WEATHER_PRECIPITATION_DEPTH, data.getPrecipitationDepth());
        values.put(EdgeEntryValueType.WEATHER_WINDCHILL, data.getWindChill());
        values.put(EdgeEntryValueType.WEATHER_WIND_SPEED, data.getWindSpeed());
        values.put(EdgeEntryValueType.WEATHER_ATMOSPHERE_HUMIDITY, data.getAtmosphereHumidity());
        values.put(EdgeEntryValueType.WEATHER_ATMOSPHERE_PRESSURE, data.getAtmospherePressure());
        values.put(EdgeEntryValueType.WEATHER_TEMPERATURE, data.getTemperature());
        values.put(EdgeEntryValueType.WEATHER_MAXIMUM_WIND_SPEED, data.getMaximumWindSpeed());
        values.put(EdgeEntryValueType.WEATHER_SUNSHINE_DURATION, data.getSunshineDuration());
        values.put(EdgeEntryValueType.WEATHER_SNOW_HEIGHT, data.getSnowHeight());
        values.put(EdgeEntryValueType.WEATHER_TEMPERATURE_HIGH, data.getTemperatureHigh());
        values.put(EdgeEntryValueType.WEATHER_TEMPERATURE_LOW, data.getTemperatureLow());
        edgeEntryValue.setValues(values);
        edgeEntryValue.setSource(new EdgeEntrySource("NOAA", 1)); // TODO get from JSON

        EdgeEntryData edgeEntryData = new EdgeEntryData();
        edgeEntryData.updateWithEdgeEntryValue(edgeEntryValue);

        edgeEntry.updateWithEdgeEntryDataForDate(edgeEntryData, new Date(data.getTimestamp()));

        edgeData.updateWithEdgeEntry(edgeEntry);
    }
}
