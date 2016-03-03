package com.graphhopper.routing.util;

/**
 * Approximates the distance to the goal node by weighting the stochastic estimate according to
 * https://www.cs.sfu.ca/~jpei/publications/prob-path-edbt10-camera.pdf
 * <p>
 * @author Henning Steinke
 */
public class StochasticWeightApproximator implements WeightApproximator
{
    @Override
    public double approximate(int fromNode) {
        return 0;
    }

    @Override
    public void setGoalNode(int to) {

    }

    @Override
    public WeightApproximator duplicate() {
        return null;
    }
}
