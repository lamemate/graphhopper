/*
 *  Licensed to GraphHopper GmbH under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for 
 *  additional information regarding copyright ownership.
 * 
 *  GraphHopper GmbH licenses this file to you under the Apache License, 
 *  Version 2.0 (the "License"); you may not use this file except in 
 *  compliance with the License. You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.probabilistic.GridData;
import com.graphhopper.util.CmdArgs;
import com.graphhopper.util.TranslationMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Peter Karich
 */
public class DefaultModule extends AbstractModule
{
    private final Logger logger = LoggerFactory.getLogger(getClass());
    protected final CmdArgs args;
    private GraphHopper graphHopper;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public DefaultModule( CmdArgs args )
    {
        this.args = CmdArgs.readFromConfigAndMerge(args, "config", "graphhopper.config");
    }

    public GraphHopper getGraphHopper()
    {
        if (graphHopper == null)
            throw new IllegalStateException("createGraphHopper not called");

        return graphHopper;
    }

    /**
     * @return an initialized GraphHopper instance
     */
    protected GraphHopper createGraphHopper( CmdArgs args )
    {
        GraphHopper tmp = new GraphHopper() {

            @Override
            public GHResponse route( GHRequest request )
            {
                lock.readLock().lock();
                try {
                    return super.route(request);
                } finally {
                    lock.readLock().unlock();
                }
            }
        }.forServer().init(args);
        tmp.importOrLoad();
        logger.info("loaded graph at:" + tmp.getGraphHopperLocation()
                + ", source:" + tmp.getOSMFile()
                + ", flagEncoders:" + tmp.getEncodingManager()
                + ", class:" + tmp.getGraphHopperStorage().toDetailsString());
        return tmp;
    }

    @Override
    protected void configure()
    {
        try
        {
            graphHopper = createGraphHopper(args);
            bind(GraphHopper.class).toInstance(graphHopper);
            bind(TranslationMap.class).toInstance(graphHopper.getTranslationMap());

            long timeout = args.getLong("web.timeout", 3000);
            bind(Long.class).annotatedWith(Names.named("timeout")).toInstance(timeout);
            boolean jsonpAllowed = args.getBool("web.jsonp_allowed", false);
            if (!jsonpAllowed)
                logger.info("jsonp disabled");

            bind(Boolean.class).annotatedWith(Names.named("jsonp_allowed")).toInstance(jsonpAllowed);

            bind(RouteSerializer.class).toInstance(new SimpleRouteSerializer(graphHopper.getGraphHopperStorage().getBounds()));

            final WeatherDataUpdater updater = new WeatherDataUpdater(getGraphHopper(), GridData.getInstance());
            bind(WeatherDataUpdater.class).toInstance(updater);
            updater.start();
            bind(ObjectMapper.class).toInstance(createMapper());
        } catch (Exception ex)
        {
            throw new IllegalStateException("Couldn't load graph", ex);
        }
    }

    public static ObjectMapper createMapper()
    {
        return new ObjectMapper();
    }
}
