package eu.europa.ec.eurostat.wihp.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public final class PartitionList<T> extends AbstractList<List<T>> {

    private final List<T> list;
    private final int partitionListSize;

    private PartitionList(List<T> list, int partitionListSize) {
        this.list = new ArrayList<>(list);
        this.partitionListSize = partitionListSize;
    }

    public static <T> PartitionList<T> ofSize(List<T> list, int partitionListSize) {
        return new PartitionList<>(list, partitionListSize);
    }

    @Override
    public List<T> get(int index) {
        int start = index * partitionListSize;
        int end = Math.min(start + partitionListSize, list.size());
        if (start > end) {
            throw new IndexOutOfBoundsException("PartitionList: requested index " + index + " is out of the list size : " + (size() - 1));
        }
        return new ArrayList<>(list.subList(start, end));
    }

    @Override
    public int size() {
        return (int) Math.ceil((double) list.size() / (double) partitionListSize);
    }
}
