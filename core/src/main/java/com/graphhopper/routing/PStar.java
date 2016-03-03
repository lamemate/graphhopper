package com.graphhopper.routing;

import com.graphhopper.routing.util.*;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.SPTEntry;
import com.graphhopper.util.EdgeIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.PriorityQueue;

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
    private TIntObjectMap<PStarEntry> fromMap;
    private PriorityQueue<PStarEntry> prioQueueOpenSet;
    private PStarEntry currEdge;
    private int to1 = -1;


    public PStar(Graph g, FlagEncoder encoder, Weighting weighting, TraversalMode tMode)
    {
        super(g, encoder, weighting, tMode);
        initCollections(1000);
        StochasticWeightApproximator defaultApprox = new StochasticWeightApproximator(nodeAccess, weighting);
    }

    /**
     * @param approx defines how the distance to goal Node is approximated
     */
    public PStar setApproximation( WeightApproximator approx )
    {
        weightApprox = approx;
        return this;
    }

    protected void initCollections( int size )
    {
        fromMap = new TIntObjectHashMap<PStarEntry>();
        prioQueueOpenSet = new PriorityQueue<PStarEntry>(size);
    }

    @Override
    public Path calcPath(int from, int to)
    {
        checkAlreadyRun();
        to1 = to;

        weightApprox.setGoalNode(to);
        double weightToGoal = weightApprox.approximate(from);
        currEdge = new PStarEntry(EdgeIterator.NO_EDGE, from, 0 + weightToGoal, 0);
        if (!traversalMode.isEdgeBased())
        {
            fromMap.put(from, currEdge);
        }
        return runAlgo();
    }

    private Path runAlgo()
    {
        return null;
    }

    @Override
    protected boolean finished()
    {
        return currEdge.adjNode == to1;
    }

    @Override
    protected Path extractPath()
    {
        return new Path(graph, flagEncoder).setWeight(currEdge.weight).setEdgeEntry(currEdge).extract();
    }

    @Override
    protected SPTEntry createEdgeEntry( int node, double weight )
    {
        throw new IllegalStateException("use PStarEdge constructor directly");
    }

    @Override
    protected boolean isWeightLimitExceeded()
    {
        return currEdge.weight > weightLimit;
    }

    @Override
    public int getVisitedNodes()
    {
        return visitedCount;
    }

    public static class PStarEntry extends SPTEntry
    {
        double weightOfVisitedPath;

        public PStarEntry( int edgeId, int adjNode, double weightForHeap, double weightOfVisitedPath )
        {
            super(edgeId, adjNode, weightForHeap);
            this.weightOfVisitedPath = weightOfVisitedPath;
        }
    }

    @Override
    public String getName()
    {
        return AlgorithmOptions.PSTAR;
    }
}
