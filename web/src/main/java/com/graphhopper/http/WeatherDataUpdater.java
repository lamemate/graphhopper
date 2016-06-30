package com.graphhopper.http;

import com.google.common.collect.Lists;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.probabilistic.*;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.util.EdgeExplorer;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.shapes.BBox;
import de.fu_berlin.agdb.importer.noaa.NOAAImporter;
import de.fu_berlin.agdb.importer.payload.GridMetaData;
import de.fu_berlin.agdb.importer.payload.LocationWeatherData;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

public class WeatherDataUpdater
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final int WORKER_THREADS = 24;

    private final Graph graph;
    private final NodeAccess nodeAccess;
    private final int seconds = 86400;

    private GridData gridData;

    public WeatherDataUpdater( GraphHopper hopper, GridData gridData )
    {
        this.graph = hopper.getGraphHopperStorage();
        this.nodeAccess = graph.getNodeAccess();
        this.gridData = gridData;
    }

    private void feedData( LocationWeatherData data )
    {
        GridMetaData gridMetaData = (GridMetaData) data.getLocationMetaData();
        double gridLat = gridMetaData.getGridLat();
        double gridLon = gridMetaData.getGridLon();
        logger.info("Processing " + new Date(data.getTimestamp()) + ": " + gridLat + "N " + gridLon + "E");

        if (!graph.getBounds().contains(gridLat, gridLon))
        {
            return;
        }

        GridEntryValue gridEntryValue = new GridEntryValue();
        ConcurrentMap<GridEntryValueType, Double> values = new ConcurrentHashMap<GridEntryValueType, Double>(12);
        values.put(GridEntryValueType.WEATHER_CLOUDAGE, data.getCloudage());
        values.put(GridEntryValueType.WEATHER_PRECIPITATION_DEPTH, data.getPrecipitationDepth());
        values.put(GridEntryValueType.WEATHER_WINDCHILL, data.getWindChill());
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

        BBox bBox = new BBox(gridLon - 0.125, gridLon + 0.125, gridLat - 0.125, gridLat + 0.125);

        GridEntry existingGridEntry = gridData.getEntryForBoundingBox(bBox);

        if (existingGridEntry == null) // No mapping yet
        {
            TIntSet edges = new TIntHashSet(8000); // totally guessed value

            EdgeExplorer explorer = graph.createEdgeExplorer();

            for (int nodeIndex = 0; nodeIndex < graph.getNodes(); nodeIndex++)
            {
                EdgeIterator edgeIterator = explorer.setBaseNode(nodeIndex);
                while (edgeIterator.next())
                {
                    if (bBox.contains(nodeAccess.getLat(nodeIndex), nodeAccess.getLon(nodeIndex)))
                    {
                        edges.add(edgeIterator.getEdge());
                    }
                }
            }

            GridEntry gridEntry = new GridEntry(bBox, edges);
            gridEntry.updateWithGridEntryDataForDate(gridEntryData, new Date(data.getTimestamp()));

            gridData.updateWithGridEntry(gridEntry);
        } else // update data
        {
            existingGridEntry.updateWithGridEntryDataForDate(gridEntryData, new Date(data.getTimestamp()));
            gridData.updateWithGridEntry(existingGridEntry);
        }
    }

    private final AtomicBoolean running = new AtomicBoolean(false);

    public void start()
    {
        if (running.get())
        {
            return;
        }

        running.set(true);
        new Thread("WeatherDataUpdater" + seconds)
        {
            @Override
            public void run()
            {
                logger.info("Fetching data every " + seconds + " seconds");
                while (running.get())
                {
                    try
                    {
                        logger.info("Fetching new weather data");
                        List<LocationWeatherData> locationWeatherDataList = new NOAAImporter()
                                .getWeatherDataForLocationsRespectingTimeout(null);
                        for (List<LocationWeatherData> partition : Lists.partition(locationWeatherDataList, locationWeatherDataList.size() / WORKER_THREADS))
                        {
                            new Thread(new PartitionWorker(partition)).start();
                        }
                        try
                        {
                            Thread.sleep(seconds * 1000);
                        } catch (InterruptedException e)
                        {
                            logger.info("WeatherDataUpdater thread stopped");
                            break;
                        }
                    } catch (Exception e)
                    {
                        logger.error("Problem while fetching weather data");
                    }
                }
            }
        }.start();
    }

    public void stop()
    {
        running.set(false);
    }

    private class PartitionWorker implements Runnable
    {
        private List<LocationWeatherData> weatherData;

        public PartitionWorker( List<LocationWeatherData> weatherData )
        {
            this.weatherData = weatherData;
            Collections.shuffle(weatherData);
        }

        @Override
        public void run()
        {
            for (LocationWeatherData data : weatherData)
            {
                feedData(data);
            }
        }
    }
}
