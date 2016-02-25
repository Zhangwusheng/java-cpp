package com.aipai.adw.fs.hdfs;

import org.apache.commons.io.IOCase;
import org.apache.hadoop.fs.FileStatus;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zhangwusheng on 15/11/4.
 */
public class FileStatusComparator implements Comparator<FileStatus> {
    public static final Comparator<FileStatus> NAME_COMPARATOR = new FileStatusComparator();

    /** Whether the comparison is case sensitive. */
    private final IOCase caseSensitivity;

    /**
     * Construct a case sensitive file name comparator instance.
     */
    public FileStatusComparator() {
        this.caseSensitivity = IOCase.SENSITIVE;
    }

    public FileStatusComparator(IOCase caseSensitivity) {
        this.caseSensitivity = caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity;
    }
    /**
     * Sort an array of files.
     * <p>
     * This method uses {@link java.util.Arrays#sort(Object[], Comparator)}
     * and returns the original array.
     *
     * @param files The files to sort, may be null
     * @return The sorted array
     * @since 2.0
     */
    public FileStatus[] sort(FileStatus... files) {
        if (files != null) {
            Arrays.sort ( files, this );
        }
        return files;
    }

    /**
     * Sort a List of files.
     * <p>
     * This method uses {@link java.util.Collections#sort(java.util.List , Comparator)}
     * and returns the original list.
     *
     * @param files The files to sort, may be null
     * @return The sorted list
     * @since 2.0
     */
    public List<FileStatus> sort(List<FileStatus> files) {
        if (files != null) {
            Collections.sort ( files, this );
        }
        return files;
    }

    public int compare(FileStatus file1, FileStatus file2) {
        return caseSensitivity.checkCompareTo ( file1.getPath ( ).getName ( ), file2.getPath ( ).getName ( ) );
    }

    /**
     * String representation of this file comparator.
     *
     * @return String representation of this file comparator
     */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}