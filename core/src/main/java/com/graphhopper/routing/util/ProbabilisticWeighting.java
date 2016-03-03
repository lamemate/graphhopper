package com.graphhopper.routing.util;

import com.graphhopper.util.EdgeIteratorState;

/**
 * Provides the methods to retrieve probability values for a specific edge according to
 * https://www.cs.sfu.ca/~jpei/publications/prob-path-edbt10-camera.pdf
 * <p>
 * @author Henning Steinke
 */
public class ProbabilisticWeighting implements Weighting
{
    @Override
    public double getMinWeight(double distance) {
        return 0;
    }

    @Override
    public double calcWeight(EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId) {
        return 0;
    }

    @Override
    public FlagEncoder getFlagEncoder() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean matches(String weightingAsStr, FlagEncoder encoder) {
        return false;
    }
}
