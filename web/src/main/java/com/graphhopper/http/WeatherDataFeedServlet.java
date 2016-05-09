package com.graphhopper.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import de.fu_berlin.agdb.importer.payload.LocationWeatherData;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WeatherDataFeedServlet extends GraphHopperServlet
{
    @Inject
    private ObjectMapper mapper;

    @Inject
    private WeatherDataUpdater updater;

    @Override
    protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        System.out.println("YEAH data");
        try {
            LocationWeatherData locationWeatherData = mapper.readValue(req.getInputStream(), LocationWeatherData.class);
            System.out.println("Feed data received: " + locationWeatherData);
            updater.feedData(locationWeatherData);
        } catch (Exception e) {
            resp.setStatus(HttpStatus.BAD_REQUEST_400);
            JSONObject exception = new JSONObject();
            exception.put("error", e.getCause());
            exception.put("message", e.getMessage());
            exception.write(resp.getWriter());
        }
    }
}
