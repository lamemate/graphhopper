package com.graphhopper.routing.util;

import com.graphhopper.storage.NodeAccess;

/**
 * Approximates the distance to the goal node by weighting the stochastic estimate according to
 * https://www.cs.sfu.ca/~jpei/publications/prob-path-edbt10-camera.pdf
 * <p>
 * @author Henning Steinke
 */
public class StochasticWeightApproximator implements WeightApproximator
{
    private final NodeAccess nodeAccess;
    private final Weighting weighting;

    public StochasticWeightApproximator( NodeAccess nodeAccess, Weighting weighting )
    {
        this.nodeAccess = nodeAccess;
        this.weighting = weighting;
    }

    @Override
    public double approximate(int fromNode)
    {
        return 0;
    }

    @Override
    public void setGoalNode(int to)
    {

    }

    @Override
    public WeightApproximator duplicate()
    {
        return new StochasticWeightApproximator(nodeAccess, weighting);
    }
}
