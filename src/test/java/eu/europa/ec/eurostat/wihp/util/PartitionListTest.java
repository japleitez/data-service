package eu.europa.ec.eurostat.wihp.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PartitionListTest {

    @Test
    public void partitionList_test() {
        List<Integer> sourceList = generateListSizeOf(11);
        assertEquals(PartitionList.ofSize(sourceList, 3).toString(), "[[1, 2, 3], [4, 5, 6], [7, 8, 9], [10, 11]]");
        assertEquals(PartitionList.ofSize(sourceList, 2).toString(), "[[1, 2], [3, 4], [5, 6], [7, 8], [9, 10], [11]]");

        assertEquals(PartitionList.ofSize(sourceList, 14).toString(), "[[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]]");

        sourceList = generateListSizeOf(1);
        assertEquals(PartitionList.ofSize(sourceList, 14).toString(), "[[1]]");

        sourceList = Collections.emptyList();
        assertEquals(0, PartitionList.ofSize(sourceList, 14).size());

        sourceList = generateListSizeOf(11);
        assertEquals(PartitionList.ofSize(sourceList, 3).get(2).toString(), "[7, 8, 9]");
    }

    @Test
    public void partitionList_testIndexOfBounds() {
        List<Integer> sourceList = generateListSizeOf(11);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            sourceList.get(100);
        });
    }

    private List<Integer> generateListSizeOf(int listSize) {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i < listSize + 1; i++) {
            list.add(i);
        }
        return list;
    }

}
