package com.graphhopper.routing.util.probabilistic;

import com.graphhopper.routing.util.FastestWeighting;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PMap;

import java.util.Date;

/**
 * Provides the methods to retrieve probability values for a specific edge according to
 * https://www.cs.sfu.ca/~jpei/publications/prob-path-edbt10-camera.pdf
 * <p>
 * @author Henning Steinke
 */
public class ProbabilisticWeighting extends FastestWeighting
{
    // User paramters
    private final int VALUE;
    private final String VALUE_TYPE;
    private final String VALUE_BOUND;
    private final String BLOCKING_MODE;

    private final FlagEncoder encoder;

    private final EdgeData edgeData;

    public ProbabilisticWeighting( FlagEncoder encoder, PMap pMap, EdgeData edgeData )
    {
        super(encoder);
        this.encoder = encoder;

        if (pMap == null)
        {
            throw new IllegalArgumentException("Weightingmap must not be null!");
        }
        this.VALUE = pMap.getInt("user_value", Integer.MIN_VALUE);
        this.VALUE_TYPE = pMap.get("user_value_type", EdgeEntryValueType.WEATHER_TEMPERATURE.toString());
        this.VALUE_BOUND = pMap.get("user_value_bound", "lower");
        this.BLOCKING_MODE = pMap.get("user_blocking_mode", "block");

        if (edgeData == null)
        {
            throw new IllegalArgumentException("EdgeData must not be null!");
        }
        this.edgeData = edgeData;
    }

    @Override
    public double calcWeight(EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId)
    {
        // get super weight from fastest weighting implementation
        double w = super.calcWeight(edgeState, reverse, prevOrNextEdgeId);

        EdgeEntry edgeEntry = edgeData.getEntryForEdgeId(edgeState.getEdge());
        if (edgeEntry != null)
        {
            EdgeEntryData edgeEntryData = edgeEntry.getClosestSourcesForDateAndValueType(new Date(), EdgeEntryValueType.valueOf(VALUE_TYPE)); // TODO actual date!
            if (edgeEntryData != null)
            {
                // calculate the mean value
                double edgeMeanValue = 0;
                for (EdgeEntryValue entryValueForType : edgeEntryData)
                {
                    edgeMeanValue += entryValueForType.getSource().getProbability() * entryValueForType.getValues().get(EdgeEntryValueType.valueOf(VALUE_TYPE));
                }

                if (("lower".equalsIgnoreCase(VALUE_BOUND) && VALUE >= edgeMeanValue)
                        || ("upper".equalsIgnoreCase(VALUE_BOUND) && VALUE <= edgeMeanValue))
                {
                    // Value meets bound criteria, return super (fastest) weighting
                    return w;
                }
                else
                {
                    // Value did not meet bound criteria, reroute according to blocking mode
                    if ("block".equalsIgnoreCase(BLOCKING_MODE))
                    {
                        return Double.POSITIVE_INFINITY;
                    }
                    return w * 1000; // TODO userdefined? good value?
                }
            }
        }
        // No data found, return super (fastest) weighting
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
        return getName().equalsIgnoreCase(weightingAsStr) && this.encoder.equals(encoder);
    }

    @Override
    public String toString()
    {
        return "PROBABILISTIC";
    }
}
