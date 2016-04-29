package com.graphhopper.routing.util.probabilistic;

import com.graphhopper.routing.util.FastestWeighting;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        int value = pMap.getInt("user_value", 0);
        final String valueType = pMap.get("user_value_type", EdgeEntryValueType.WEATHER_TEMPERATURE.toString());
        String valueBound = pMap.get("user_value_bound", "lower");
        String blockingMode = pMap.get("user_bound_mode", "block"); // set edge weight to +infinity

        // get super weight from fastest weighting implementation
        double w = super.calcWeight(edgeState, reverse, prevOrNextEdgeId);

        EdgeEntry edgeEntry = edgeData.getEntryForEdgeId(edgeState.getEdge());

        EdgeEntryData edgeEntryData = edgeEntry.getClosestSourcesForDateAndValueType(new Date(), EdgeEntryValueType.valueOf(valueType)); // TODO actual date!
        // calculate the mean value
        double edgeMeanValue = 0;
        for (EdgeEntryValue entryValueForType : edgeEntryData)
        {
            edgeMeanValue += entryValueForType.getSource().getProbability() * entryValueForType.getValues().get(EdgeEntryValueType.valueOf(valueType));
        }

        if (("lower".equalsIgnoreCase(valueBound) && value >= edgeMeanValue)
                || ("upper".equalsIgnoreCase(valueBound) && value <= edgeMeanValue))
        {
            return w;
        }
        else
        {
            if ("block".equalsIgnoreCase(blockingMode))
            {
                return Double.POSITIVE_INFINITY;
            }
            return w * 1000; // TODO userdefined?
        }
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
