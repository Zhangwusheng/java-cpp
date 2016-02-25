package com.aipai.adw.fs.hdfs;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileStatus;

import java.io.File;

/**
 * Created by zhangwusheng on 15/11/3.
 */
public class HdfsFileListener implements HdfsFileAlterationListener {
    static final Log log = LogFactory.getLog ( HdfsFileListener.class );

    /**
     * 文件创建执行
     */
    @Override
    public void onFileCreate(FileStatus file) {
        log.info("[新建文件]:" + file.getPath ().toUri ().toString ());
    }

    /**
     * 文件创建修改
     */
    @Override
    public void onFileChange(FileStatus file) {
        log.info("[修改]:" + file.getPath ().toUri ().toString ( ));
    }

    /**
     * 文件删除
     */
    @Override
    public void onFileDelete(FileStatus file) {
        log.info ( "[删除文件]:" + file.getPath ( ).toUri ( ).toString ());
    }

    /**
     * 目录创建
     */
    @Override
    public void onDirectoryCreate(FileStatus directory) {
        log.info("[新建目录]:" + directory.getPath ( ).toUri ( ).toString ( ));
    }

    /**
     * 目录修改
     */
    @Override
    public void onDirectoryChange(FileStatus directory) {
        log.info("[修改目录]:" + directory.getPath ().toUri ( ).toString ( ));
    }

    /**
     * 目录删除
     */
    @Override
    public void onDirectoryDelete(FileStatus directory) {
        log.info("[删除目录]:" + directory.getPath ().toUri ().toString());
    }

    @Override
    public void onStart(HdfsFileAlterationObserver observer) {
    }

    @Override
    public void onStop(HdfsFileAlterationObserver observer) {
    }

}
