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

    private final double edgePenaltyFactor = 5.0;

    private final GridData gridData;

    public ProbabilisticWeighting( FlagEncoder encoder, PMap pMap, GridData gridData )
    {
        super(encoder);

        if (pMap == null)
        {
            throw new IllegalArgumentException("Weightingmap must not be null!");
        }
        this.VALUE = pMap.getInt("user_value", Integer.MIN_VALUE);
        this.VALUE_TYPE = pMap.get("user_value_type", GridEntryValueType.WEATHER_TEMPERATURE.toString());
        this.VALUE_BOUND = pMap.get("user_value_bound", "lower");
        this.BLOCKING_MODE = pMap.get("user_blocking_mode", "block");

        if (gridData == null)
        {
            throw new IllegalArgumentException("GridData must not be null!");
        }
        this.gridData = gridData;
    }

    @Override
    public double calcWeight( EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId )
    {
        // get super weight from fastest weighting implementation
        double w = super.calcWeight(edgeState, reverse, prevOrNextEdgeId);

        GridEntry gridEntry = gridData.getGridEntryForEdgeId(edgeState.getEdge());
        if (gridEntry != null)
        {
            GridEntryData gridEntryData = gridEntry.getClosestSourcesForDateAndValueType(new Date(), GridEntryValueType.valueOf(VALUE_TYPE)); // TODO actual date!
            if (gridEntryData != null)
            {
                // calculate the mean value
                double edgeMeanValue = gridEntryData.calculateMeanValueForGridEntryValueType(GridEntryValueType.valueOf(VALUE_TYPE));

                if (("lower".equalsIgnoreCase(VALUE_BOUND) && VALUE <= edgeMeanValue)
                        || ("upper".equalsIgnoreCase(VALUE_BOUND) && VALUE >= edgeMeanValue))
                {
                    // Value meets bound criteria, return super (fastest) weighting
                    return w;
                } else
                {
                    // Value did not meet bound criteria, reroute according to blocking mode
                    if ("block".equalsIgnoreCase(BLOCKING_MODE))
                    {
                        return Double.POSITIVE_INFINITY;
                    }
                    return w * edgePenaltyFactor * (1 + Math.abs(VALUE - edgeMeanValue)); // TODO userdefined? good value?
                }
            }
        }
        // No data found, return super (fastest) weighting
        return w;
    }

    @Override
    public String getName()
    {
        return "probabilistic";
    }
}
