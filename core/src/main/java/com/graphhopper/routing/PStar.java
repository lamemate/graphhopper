package com.graphhopper.routing;

import com.graphhopper.routing.util.*;
import com.graphhopper.routing.util.probabilistic.StochasticWeightApproximator;
import com.graphhopper.storage.Graph;

/**
 * This class implements the P* algorithm according to
 * https://www.cs.sfu.ca/~jpei/publications/prob-path-edbt10-camera.pdf
 * <p>
 * @author Henning Steinke
 */
public class PStar extends AStar
{
    public PStar(Graph g, FlagEncoder encoder, Weighting weighting, TraversalMode tMode)
    {
        super(g, encoder, weighting, tMode);
        initCollections(1000);
        StochasticWeightApproximator defaultApprox = new StochasticWeightApproximator(nodeAccess, weighting);
        setApproximation(defaultApprox);
    }

}
