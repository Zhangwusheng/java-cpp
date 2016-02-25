package com.aipai.adw.fs.hdfs;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import java.io.Serializable;

/**
 * Borrowed From commons-pool. For Hadoop Files Adaptation.
 * */
public class FileEntry implements Serializable {

    static final FileEntry[] EMPTY_ENTRIES = new FileEntry[0];

    private final FileEntry parent;
    private FileEntry[] children;
    private final FileStatus file;
    private String name;
    private boolean exists;
    private boolean directory;
    private long lastModified;
    private long length;

    /**
     * Construct a new monitor for a specified {@link FileStatus}.
     *
     * @param file The file being monitored
     */
    public FileEntry(FileStatus file) {
        this((FileEntry)null, file);
    }

    /**
     * Construct a new monitor for a specified {@link FileStatus}.
     *
     * @param parent The parent
     * @param file The file being monitored
     */
    public FileEntry(FileEntry parent, FileStatus file) {
        if (file == null) {
            throw new IllegalArgumentException("File is missing");
        }
        this.file = file;
        this.parent = parent;
        this.name = file.getPath ().getName ();
    }

    /**
     * Refresh the attributes from the {@link FileStatus}, indicating
     * whether the file has changed.
     * <p>
     * This implementation refreshes the <code>name</code>, <code>exists</code>,
     * <code>directory</code>, <code>lastModified</code> and <code>length</code>
     * properties.
     * <p>
     * The <code>exists</code>, <code>directory</code>, <code>lastModified</code>
     * and <code>length</code> properties are compared for changes
     *
     * @param file the file instance to compare to
     * @return {@code true} if the file has changed, otherwise {@code false}
     */
    public boolean refresh(FileStatus file) {

        // cache original values
        boolean origExists       = exists;
        long    origLastModified = lastModified;
        boolean origDirectory    = directory;
        long    origLength       = length;

        // refresh the values
        name  = file.getPath().getName ();
        FileSystem dfs = null;

        try {
            dfs = HdfsConnectionPool.get().getFileSystem ();
            exists = dfs.exists ( file.getPath () );
        }
        catch ( Exception e ) {
            e.printStackTrace ();
            exists = false;
        }
        finally {
            if( dfs != null ) {
                HdfsConnectionPool.get ( ).returnObject ( dfs );
            }
        }

        directory    = exists ? file.isDirectory() : false;
        lastModified = exists ? file.getModificationTime ()  : 0;
        length       = exists && !directory ? file.getLen () : 0;

        return exists != origExists ||
                lastModified != origLastModified ||
                directory != origDirectory ||
                length != origLength;
    }

    /**
     * Create a new child instance.
     * <p>
     * Custom implementations should override this method to return
     * a new instance of the appropriate type.
     *
     * @param file The child file
     * @return a new child instance
     */
    public FileEntry newChildInstance(FileStatus file) {
        return new FileEntry(this, file);
    }

    /**
     * Return the parent entry.
     *
     * @return the parent entry
     */
    public FileEntry getParent() {
        return parent;
    }

    /**
     * Return the level
     *
     * @return the level
     */
    public int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    /**
     * Return the directory's files.
     *
     * @return This directory's files or an empty
     * array if the file is not a directory or the
     * directory is empty
     */
    public FileEntry[] getChildren() {
        return children != null ? children : EMPTY_ENTRIES;
    }

    /**
     * Set the directory's files.
     *
     * @param children This directory's files, may be null
     */
    public void setChildren(FileEntry[] children) {
        this.children = children;
    }

    /**
     * Return the file being monitored.
     *
     * @return the file being monitored
     */
    public FileStatus getFile() {
        return file;
    }

    /**
     * Return the file name.
     *
     * @return the file name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the file name.
     *
     * @param name the file name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the last modified time from the last time it
     * was checked.
     *
     * @return the last modified time
     */
    public long getLastModified() {
        return lastModified;
    }

    /**
     * Return the last modified time from the last time it
     * was checked.
     *
     * @param lastModified The last modified time
     */
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Return the length.
     *
     * @return the length
     */
    public long getLength() {
        return length;
    }

    /**
     * Set the length.
     *
     * @param length the length
     */
    public void setLength(long length) {
        this.length = length;
    }

    /**
     * Indicate whether the file existed the last time it
     * was checked.
     *
     * @return whether the file existed
     */
    public boolean isExists() {
        return exists;
    }

    /**
     * Set whether the file existed the last time it
     * was checked.
     *
     * @param exists whether the file exists or not
     */
    public void setExists(boolean exists) {
        this.exists = exists;
    }

    /**
     * Indicate whether the file is a directory or not.
     *
     * @return whether the file is a directory or not
     */
    public boolean isDirectory() {
        return directory;
    }

    /**
     * Set whether the file is a directory or not.
     *
     * @param directory whether the file is a directory or not
     */
    public void setDirectory(boolean directory) {
        this.directory = directory;
    }
}

