package com.graphhopper.routing;

import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.routing.util.WeightApproximator;
import com.graphhopper.routing.util.Weighting;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.SPTEntry;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class implements the P* algorithm according to
 * https://www.cs.sfu.ca/~jpei/publications/prob-path-edbt10-camera.pdf
 * <p>
 * @author Henning Steinke
 */
public class PStar extends AbstractRoutingAlgorithm
{
    private WeightApproximator weightApprox;
    private int visitedCount;


    public PStar(Graph g, FlagEncoder encoder, Weighting weighting, TraversalMode tMode)
    {
        super(g, encoder, weighting, tMode);
    }

    @Override
    protected boolean finished()
    {
        return false;
    }

    @Override
    protected Path extractPath()
    {
        return null;
    }

    @Override
    protected boolean isWeightLimitExceeded()
    {
        return false;
    }

    @Override
    public Path calcPath(int from, int to)
    {
        return null;
    }

    @Override
    public int getVisitedNodes()
    {
        return 0;
    }

    public static class PStarEntry extends SPTEntry
    {
        public PStarEntry( int edgeId, int adjNode, double weightForHeap, double weightOfVisitedPath )
        {
            super(edgeId, adjNode, weightForHeap);
        }
    }

    @Override
    public String getName()
    {
        return AlgorithmOptions.PSTAR;
    }
}
