package com.graphhopper.routing.util.probabilistic;

import com.graphhopper.routing.util.FastestWeighting;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.Weighting;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PMap;
import com.sun.tools.doclint.Checker;

import java.util.Date;

/**
 * Provides the methods to retrieve probability values for a specific edge according to
 * https://www.cs.sfu.ca/~jpei/publications/prob-path-edbt10-camera.pdf
 * <p>
 * @author Henning Steinke
 */
public class ProbabilisticWeighting extends FastestWeighting
{
    private final FlagEncoder encoder;

    private PMap pMap;
    private final EdgeData edgeData;

    public ProbabilisticWeighting( FlagEncoder encoder, PMap pMap, EdgeData edgeData )
    {
        super(encoder);
        this.encoder = encoder;
        this.pMap = pMap;
        this.edgeData = edgeData;
    }

    @Override
    public double calcWeight(EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId)
    {
        String valueBound = pMap.get("value_bound", "lower");
        EdgeEntry edgeEntry = edgeData.getEntryForEdgeId(edgeState.getEdge());

        // TODO get value for time and attribute and do magic

        double w = super.calcWeight(edgeState, reverse, prevOrNextEdgeId);
        return w;
    }

    @Override
    public FlagEncoder getFlagEncoder()
    {
        return encoder;
    }

    @Override
    public String getName()
    {
        return "probabilistic";
    }

    @Override
    public boolean matches(String weightingAsStr, FlagEncoder encoder)
    {
        if (getName().equalsIgnoreCase(weightingAsStr) && this.encoder.equals(encoder))
        {
            return true;
        }
        return false;
    }

    @Override
    public String toString()
    {
        return "PROBABILISTIC";
    }
}
