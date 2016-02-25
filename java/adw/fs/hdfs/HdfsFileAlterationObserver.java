package com.aipai.adw.fs.hdfs;


import com.aipai.adw.fs.hdfs.filter.HiddenFileFilter;
import org.apache.commons.io.IOCase;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;


public class HdfsFileAlterationObserver implements Serializable {
    public static final FileStatus[] EMPTY_FILE_ARRAY = new FileStatus[0];
    public static final PathFilter HIDDEN_PATH_FILTER = new HiddenFileFilter ();

    private final List<HdfsFileAlterationListener> listeners = new CopyOnWriteArrayList<HdfsFileAlterationListener> ();
    private final FileEntry rootEntry;
    private final PathFilter fileFilter;
    private final Comparator<FileStatus> comparator;
    private String rootDirectoryName;

    /**
     * Construct an observer for the specified directory.
     *
     * @param directoryName the name of the directory to observe
     */
    public HdfsFileAlterationObserver ( String directoryName ) {
        this(directoryName,(PathFilter)null,(IOCase)null);
    }

    /**
     * Construct an observer for the specified directory and file filter.
     *
     * @param directoryName the name of the directory to observe
     * @param fileFilter The file filter or null if none
     */
    public HdfsFileAlterationObserver ( String directoryName, PathFilter fileFilter ) {
        this(directoryName,fileFilter,(IOCase)null);
    }

    /**
     * Construct an observer for the specified directory, file filter and
     * file comparator.
     *
     * @param directoryName the name of the directory to observe
     * @param fileFilter The file filter or null if none
     * @param caseSensitivity  what case sensitivity to use comparing file names, null means system sensitive
     */
    public HdfsFileAlterationObserver ( String directoryName, PathFilter fileFilter, IOCase caseSensitivity ) {
        Path p = new Path ( directoryName );
        FileStatus status = null;
        FileSystem dfs = null;
        try {
            dfs = HdfsConnectionPool.get ().getFileSystem ();
            status =  dfs.getFileStatus ( p );
        }
        catch ( Exception e ) {
            e.printStackTrace ( );
        }
        finally {
            if( dfs != null){
            HdfsConnectionPool.get ().returnObject ( dfs );
            }
        }

        this.rootEntry = new FileEntry(status);
        this.fileFilter = fileFilter == null? HIDDEN_PATH_FILTER:fileFilter;
        this.comparator = FileStatusComparator.NAME_COMPARATOR;
    }

    /**
     * Return the directory being observed.
     *
     * @return the directory being observed
     */
    public FileStatus getDirectory() {
        return rootEntry.getFile();
    }

    /**
     * Return the fileFilter.
     *
     * @return the fileFilter
     * @since 2.1
     */
    public PathFilter getFileFilter() {
        return fileFilter;
    }

    /**
     * Add a file system listener.
     *
     * @param listener The file system listener
     */
    public void addListener(final HdfsFileAlterationListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Remove a file system listener.
     *
     * @param listener The file system listener
     */
    public void removeListener(final HdfsFileAlterationListener listener) {
        if (listener != null) {
            while (listeners.remove(listener)) {
            }
        }
    }

    /**
     * Returns the set of registered file system listeners.
     *
     * @return The file system listeners
     */
    public Iterable<HdfsFileAlterationListener> getListeners() {
        return listeners;
    }

    /**
     * Initialize the observer.
     *
     * @throws Exception if an error occurs
     */
    public void initialize() throws Exception {
        rootEntry.refresh(rootEntry.getFile());
        FileStatus[] files = listFiles(rootEntry.getFile());
        FileEntry[] children = files.length > 0 ? new FileEntry[files.length] : FileEntry.EMPTY_ENTRIES;
        for (int i = 0; i < files.length; i++) {
            children[i] = createFileEntry(rootEntry, files[i]);
        }
        rootEntry.setChildren(children);
    }

    /**
     * Final processing.
     *
     * @throws Exception if an error occurs
     */
    public void destroy() throws Exception {
    }

    /**
     * Check whether the file and its chlidren have been created, modified or deleted.
     */
    public void checkAndNotify() {

        /* fire onStart() */
        for (HdfsFileAlterationListener listener : listeners) {
            listener.onStart(this);
        }

        /* fire directory/file events */
        FileStatus rootFile = rootEntry.getFile();
        boolean exists = false;
        FileSystem dfs = null;
        try {
            dfs = HdfsConnectionPool.get ().getFileSystem ();
            exists = dfs.exists ( rootFile.getPath () );
        }
        catch ( Exception e ) {
            e.printStackTrace ( );
        }
        finally {
            if( dfs != null) {
                HdfsConnectionPool.get ( ).returnObject ( dfs );
            }
        }

        if (exists) {
            checkAndNotify(rootEntry, rootEntry.getChildren(), listFiles(rootFile));
        } else if (rootEntry.isExists()) {
            checkAndNotify(rootEntry, rootEntry.getChildren(),EMPTY_FILE_ARRAY);
        } else {
            // Didn't exist and still doesn't
        }

        /* fire onStop() */
        for (HdfsFileAlterationListener listener : listeners) {
            listener.onStop(this);
        }
    }

    /**
     * Compare two file lists for files which have been created, modified or deleted.
     *
     * @param parent The parent entry
     * @param previous The original list of files
     * @param files  The current list of files
     */
    private void checkAndNotify(FileEntry parent, FileEntry[] previous, FileStatus[] files) {
        int c = 0;
        FileEntry[] current = files.length > 0 ? new FileEntry[files.length] : FileEntry.EMPTY_ENTRIES;
        for (FileEntry entry : previous) {
            while (c < files.length && comparator.compare(entry.getFile(), files[c]) > 0) {
                current[c] = createFileEntry(parent, files[c]);
                doCreate(current[c]);
                c++;
            }
            if (c < files.length && comparator.compare(entry.getFile(), files[c]) == 0) {
                doMatch(entry, files[c]);
                checkAndNotify(entry, entry.getChildren(), listFiles(files[c]));
                current[c] = entry;
                c++;
            } else {
                checkAndNotify(entry, entry.getChildren(), EMPTY_FILE_ARRAY);
                doDelete(entry);
            }
        }
        for (; c < files.length; c++) {
            current[c] = createFileEntry(parent, files[c]);
            doCreate(current[c]);
        }
        parent.setChildren(current);
    }

    /**
     * Create a new file entry for the specified file.
     *
     * @param parent The parent file entry
     * @param file The file to create an entry for
     * @return A new file entry
     */
    private FileEntry createFileEntry(FileEntry parent, FileStatus file) {
        FileEntry entry = parent.newChildInstance(file);
        entry.refresh(file);
        FileStatus[] files = listFiles(file);
        FileEntry[] children = files.length > 0 ? new FileEntry[files.length] : FileEntry.EMPTY_ENTRIES;
        for (int i = 0; i < files.length; i++) {
            children[i] = createFileEntry(entry, files[i]);
        }
        entry.setChildren(children);
        return entry;
    }

    /**
     * Fire directory/file created events to the registered listeners.
     *
     * @param entry The file entry
     */
    private void doCreate(FileEntry entry) {
        for (HdfsFileAlterationListener listener : listeners) {
            if (entry.isDirectory()) {
                listener.onDirectoryCreate(entry.getFile());
            } else {
                listener.onFileCreate(entry.getFile());
            }
        }
        FileEntry[] children = entry.getChildren();
        for (FileEntry aChildren : children) {
            doCreate(aChildren);
        }
    }

    /**
     * Fire directory/file change events to the registered listeners.
     *
     * @param entry The previous file system entry
     * @param file The current file
     */
    private void doMatch(FileEntry entry, FileStatus file) {
        if (entry.refresh(file)) {
            for (HdfsFileAlterationListener listener : listeners) {
                if (entry.isDirectory()) {
                    listener.onDirectoryChange(file);
                } else {
                    listener.onFileChange(file);
                }
            }
        }
    }

    /**
     * Fire directory/file delete events to the registered listeners.
     *
     * @param entry The file entry
     */
    private void doDelete(FileEntry entry) {
        for (HdfsFileAlterationListener listener : listeners) {
            if (entry.isDirectory()) {
                listener.onDirectoryDelete(entry.getFile());
            } else {
                listener.onFileDelete(entry.getFile());
            }
        }
    }

    /**
     * List the contents of a directory
     *
     * @param file The file to list the contents of
     * @return the directory contents or a zero length array if
     * the empty or the file is not a directory
     */
    private FileStatus[] listFiles(FileStatus file) {
        FileStatus[] children = null;
        if (file.isDirectory()) {
            FileSystem dfs = null;

            try {
                 dfs = HdfsConnectionPool.get ().getFileSystem ();
                children = fileFilter == null ? dfs.listStatus ( file.getPath () )
                        : dfs.listStatus ( file.getPath (),fileFilter );
            }
            catch ( Exception e ) {
                e.printStackTrace ( );
            }
            finally {
                if( dfs != null ) {
                    HdfsConnectionPool.get ( ).returnObject ( dfs );
                }
            }
        }
        if (children == null) {
            children = EMPTY_FILE_ARRAY;
        }
        if (comparator != null && children.length > 1) {
            Arrays.sort ( children, comparator );
        }
        return children;
    }

    /**
     * Provide a String representation of this observer.
     *
     * @return a String representation of this observer
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append("[file='");
        builder.append(getDirectory().getPath());
        builder.append('\'');
        if (fileFilter != null) {
            builder.append(", ");
            builder.append(fileFilter.toString());
        }
        builder.append(", listeners=");
        builder.append(listeners.size());
        builder.append("]");
        return builder.toString();
    }

}
