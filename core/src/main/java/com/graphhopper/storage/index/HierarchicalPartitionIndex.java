package com.graphhopper.storage.index;

import com.graphhopper.storage.Storable;

/**
 * Partitions to graph to speed up P* algorithm according to
 * https://www.cs.sfu.ca/~jpei/publications/prob-path-edbt10-camera.pdf
 * <p>
 * @author Henning Steinke
 */
public interface HierarchicalPartitionIndex extends Storable<HierarchicalPartitionIndex>
{
}
