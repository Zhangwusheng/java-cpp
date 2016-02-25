package com.aipai.adw.fs.hdfs;

/**
 * Created by zhangwusheng on 15/11/4.
 */

import org.apache.hadoop.fs.FileStatus;

/**
 * A listener that receives events of file system modifications.
 * <p>
 * Register {@link HdfsFileAlterationListener}s with a {@link org.apache.commons.io.monitor.FileAlterationObserver}.
 *
 * @see org.apache.commons.io.monitor.FileAlterationObserver
 * @version $Id: FileAlterationListener.java 1304052 2012-03-22 20:55:29Z ggregory $
 * @since 2.0
 */
public interface HdfsFileAlterationListener {

    /**
     * File system observer started checking event.
     *
     * @param observer The file system observer
     */
    void onStart(final HdfsFileAlterationObserver observer);

    /**
     * Directory created Event.
     *
     * @param directory The directory created
     */
    void onDirectoryCreate(final FileStatus directory);

    /**
     * Directory changed Event.
     *
     * @param directory The directory changed
     */
    void onDirectoryChange(final FileStatus directory);

    /**
     * Directory deleted Event.
     *
     * @param directory The directory deleted
     */
    void onDirectoryDelete(final FileStatus directory);

    /**
     * File created Event.
     *
     * @param file The file created
     */
    void onFileCreate(final FileStatus file);

    /**
     * File changed Event.
     *
     * @param file The file changed
     */
    void onFileChange(final FileStatus file);

    /**
     * File deleted Event.
     *
     * @param file The file deleted
     */
    void onFileDelete(final FileStatus file);

    /**
     * File system observer finished checking event.
     *
     * @param observer The file system observer
     */
    void onStop(final HdfsFileAlterationObserver observer);
}

