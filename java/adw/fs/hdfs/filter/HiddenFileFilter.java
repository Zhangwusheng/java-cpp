package com.aipai.adw.fs.hdfs.filter;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

/**
 * Created by zhangwusheng on 15/11/5.
 */
public class HiddenFileFilter implements PathFilter {
    public boolean accept(Path p){
        String name = p.getName();
        return !name.startsWith("_") && !name.startsWith(".");
    }
};