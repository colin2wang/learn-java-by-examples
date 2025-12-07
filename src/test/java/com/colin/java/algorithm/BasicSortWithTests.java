package com.colin.java.algorithm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A combined class that implements basic sorting algorithms and includes
 * JUnit 5 tests in the same file.
 */
public class BasicSortWithTests {

    // ========================= SORTING ALGORITHM IMPLEMENTATIONS =========================

    /**
     * Implementation of Bubble Sort algorithm.
     * Time Complexity: O(n²) - worst case
     * Space Complexity: O(1)
     */
    public static void bubbleSort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }

        int n = array.length;
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    // Swap elements
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = true;
                }
            }
            // If no elements were swapped, the array is sorted
            if (!swapped) {
                break;
            }
        }
    }

    /**
     * Implementation of Insertion Sort algorithm.
     * Time Complexity: O(n²) - worst case
     * Space Complexity: O(1)
     */
    public static void insertionSort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }

        int n = array.length;
        for (int i = 1; i < n; i++) {
            int key = array[i];
            int j = i - 1;

            // Move elements greater than key to one position ahead
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }
    }

    /**
     * Implementation of Selection Sort algorithm.
     * Time Complexity: O(n²) - worst case
     * Space Complexity: O(1)
     */
    public static void selectionSort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }

        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            // Find the minimum element in unsorted array
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
            }

            // Swap the found minimum element with the first element
            int temp = array[minIndex];
            array[minIndex] = array[i];
            array[i] = temp;
        }
    }

    /**
     * Implementation of QuickSort algorithm.
     * Time Complexity: O(n log n) - average case, O(n²) - worst case
     * Space Complexity: O(log n)
     */
    public static void quickSort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        quickSortHelper(array, 0, array.length - 1);
    }

    private static void quickSortHelper(int[] array, int low, int high) {
        if (low < high) {
            // Find pivot element such that elements smaller than pivot are on left
            // and elements greater than pivot are on right
            int pivotIndex = partition(array, low, high);

            // Recursively sort the two partitions
            quickSortHelper(array, low, pivotIndex - 1);
            quickSortHelper(array, pivotIndex + 1, high);
        }
    }

    private static int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = low - 1; // index of smaller element

        for (int j = low; j < high; j++) {
            // If current element is smaller than or equal to pivot
            if (array[j] <= pivot) {
                i++;
                // Swap array[i] and array[j]
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }

        // Swap array[i+1] and array[high] (or pivot)
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;

        return i + 1;
    }

    /**
     * Checks if an array is sorted in ascending order.
     */
    public static boolean isSorted(int[] array) {
        if (array == null || array.length <= 1) {
            return true;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        return true;
    }

    // ========================= JUNIT 5 TESTS =========================

    private static final int ARRAY_SIZE = 100;

    @Test
    @DisplayName("Bubble Sort should sort an array correctly")
    void testBubbleSort() {
        int[] unsortedArray = generateRandomArray();
        int[] sortedArray = Arrays.copyOf(unsortedArray, unsortedArray.length);
        Arrays.sort(sortedArray);

        bubbleSort(unsortedArray);
        assertArrayEquals(sortedArray, unsortedArray, "Bubble Sort failed to sort the array correctly");
    }

    @Test
    @DisplayName("Insertion Sort should sort an array correctly")
    void testInsertionSort() {
        int[] unsortedArray = generateRandomArray();
        int[] sortedArray = Arrays.copyOf(unsortedArray, unsortedArray.length);
        Arrays.sort(sortedArray);

        insertionSort(unsortedArray);
        assertArrayEquals(sortedArray, unsortedArray, "Insertion Sort failed to sort the array correctly");
    }

    @Test
    @DisplayName("Selection Sort should sort an array correctly")
    void testSelectionSort() {
        int[] unsortedArray = generateRandomArray();
        int[] sortedArray = Arrays.copyOf(unsortedArray, unsortedArray.length);
        Arrays.sort(sortedArray);

        selectionSort(unsortedArray);
        assertArrayEquals(sortedArray, unsortedArray, "Selection Sort failed to sort the array correctly");
    }

    @Test
    @DisplayName("Quick Sort should sort an array correctly")
    void testQuickSort() {
        int[] unsortedArray = generateRandomArray();
        int[] sortedArray = Arrays.copyOf(unsortedArray, unsortedArray.length);
        Arrays.sort(sortedArray);

        quickSort(unsortedArray);
        assertArrayEquals(sortedArray, unsortedArray, "Quick Sort failed to sort the array correctly");
    }

    @ParameterizedTest
    @MethodSource("edgeCaseProvider")
    @DisplayName("Sorting algorithms should handle edge cases")
    void testEdgeCases(int[] edgeCase) {
        // Create copies of the edge case array
        int[] bubbleSortArray = Arrays.copyOf(edgeCase, edgeCase.length);
        int[] insertionSortArray = Arrays.copyOf(edgeCase, edgeCase.length);
        int[] selectionSortArray = Arrays.copyOf(edgeCase, edgeCase.length);
        int[] quickSortArray = Arrays.copyOf(edgeCase, edgeCase.length);

        // Sort using Arrays.sort for comparison
        int[] expectedSorted = Arrays.copyOf(edgeCase, edgeCase.length);
        Arrays.sort(expectedSorted);

        // Test each sorting algorithm
        bubbleSort(bubbleSortArray);
        insertionSort(insertionSortArray);
        selectionSort(selectionSortArray);
        quickSort(quickSortArray);

        // Verify all sorting algorithms produce the same result
        assertArrayEquals(expectedSorted, bubbleSortArray, "Bubble Sort failed for edge case");
        assertArrayEquals(expectedSorted, insertionSortArray, "Insertion Sort failed for edge case");
        assertArrayEquals(expectedSorted, selectionSortArray, "Selection Sort failed for edge case");
        assertArrayEquals(expectedSorted, quickSortArray, "Quick Sort failed for edge case");
    }

    // Provides edge cases for testing
    private static Stream<Arguments> edgeCaseProvider() {
        return Stream.of(
                Arguments.of((Object) new int[0]),           // Empty array
                Arguments.of((Object) new int[]{1}),          // Single element
                Arguments.of((Object) new int[]{1, 2, 3, 4, 5}), // Already sorted
                Arguments.of((Object) new int[]{5, 4, 3, 2, 1}), // Reverse sorted
                Arguments.of((Object) new int[]{3, 3, 3, 3, 3}), // All elements the same
                Arguments.of((Object) new int[]{-10, 0, 10, -5, 5}) // Mixed positive and negative
        );
    }

    @Test
    @DisplayName("isSorted method correctly identifies sorted and unsorted arrays")
    void testIsSorted() {
        // Test sorted array
        int[] sortedArray = {1, 2, 3, 4, 5};
        assertTrue(isSorted(sortedArray), "isSorted should return true for a sorted array");

        // Test unsorted array
        int[] unsortedArray = {3, 1, 4, 2, 5};
        assertFalse(isSorted(unsortedArray), "isSorted should return false for an unsorted array");

        // Test empty array
        assertTrue(isSorted(new int[0]), "isSorted should return true for an empty array");

        // Test single element array
        assertTrue(isSorted(new int[]{42}), "isSorted should return true for a single element array");
    }

    // Helper method to generate a random array for testing
    private int[] generateRandomArray() {
        Random random = new Random(42); // Fixed seed for reproducibility
        int[] array = new int[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; i++) {
            array[i] = random.nextInt(1000);
        }
        return array;
    }
}