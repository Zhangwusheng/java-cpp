package com.aipai.adw.fs.hdfs.filter;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

/**
 * Created by zhangwusheng on 15/11/5.
 */
public class AcceptAllFilter implements PathFilter {
    @Override
    public boolean accept(Path file) {
        return true;
    }
}
