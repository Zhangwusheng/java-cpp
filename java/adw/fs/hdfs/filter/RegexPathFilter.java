package com.aipai.adw.fs.hdfs.filter;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

/**
 * Created by zhangwusheng on 15/11/5.
 */
class RegexPathFilter implements PathFilter {

    private final String regex;
    public RegexPathFilter(String regex) {
        this.regex = regex;
    }

    @Override
    public boolean accept(Path path) {
        return path.toString().matches(regex);
    }

}