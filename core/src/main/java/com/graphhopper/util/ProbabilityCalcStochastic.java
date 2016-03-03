package com.graphhopper.util;

import com.graphhopper.util.shapes.BBox;
import com.graphhopper.util.shapes.GHPoint;

/**
 * @author Henning Steinke
 */
public class ProbabilityCalcStochastic implements DistanceCalc
{
    @Override
    public BBox createBBox( double lat, double lon, double radiusInMeter )
    {
        return null;
    }

    @Override
    public double calcCircumference( double lat )
    {
        return 0;
    }

    @Override
    public double calcDist( double fromLat, double fromLon, double toLat, double toLon )
    {
        return 0;
    }

    @Override
    public double calcNormalizedDist( double dist )
    {
        return 0;
    }

    @Override
    public double calcDenormalizedDist( double normedDist )
    {
        return 0;
    }

    @Override
    public double calcNormalizedDist( double fromLat, double fromLon, double toLat, double toLon )
    {
        return 0;
    }

    @Override
    public boolean validEdgeDistance( double r_lat_deg, double r_lon_deg, double a_lat_deg, double a_lon_deg, double b_lat_deg, double b_lon_deg )
    {
        return false;
    }

    @Override
    public double calcNormalizedEdgeDistance( double r_lat_deg, double r_lon_deg, double a_lat_deg, double a_lon_deg, double b_lat_deg, double b_lon_deg )
    {
        return 0;
    }

    @Override
    public GHPoint calcCrossingPointToEdge( double r_lat_deg, double r_lon_deg, double a_lat_deg, double a_lon_deg, double b_lat_deg, double b_lon_deg )
    {
        return null;
    }
}
