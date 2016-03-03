package com.graphhopper.storage.index;

/**
 * This implementation implements an m-tree go partition the graph according to
 * https://www.cs.sfu.ca/~jpei/publications/prob-path-edbt10-camera.pdf
 * <p>
 * @author Henning Steinke
 */
public class HierarchicalIndexTree implements HierarchicalPartitionIndex
{
    @Override
    public boolean loadExisting()
    {
        return false;
    }

    @Override
    public HierarchicalPartitionIndex create(long byteCount)
    {
        return null;
    }

    @Override
    public void flush()
    {

    }

    @Override
    public void close()
    {

    }

    @Override
    public boolean isClosed()
    {
        return false;
    }

    @Override
    public long getCapacity()
    {
        return 0;
    }
}
