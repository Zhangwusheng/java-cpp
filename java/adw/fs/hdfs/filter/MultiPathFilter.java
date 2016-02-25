package com.aipai.adw.fs.hdfs.filter;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwusheng on 15/11/5.
 */
public class MultiPathFilter implements PathFilter {
    private List<PathFilter> filters;

    public MultiPathFilter() {
        this.filters = new ArrayList<PathFilter> ();
    }

    public MultiPathFilter(List<PathFilter> filters) {
        this.filters = filters;
    }

    public void add(PathFilter one) {
        filters.add(one);
    }

    public boolean accept(Path path) {
        for (PathFilter filter : filters) {
            if (filter.accept(path)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[");
        for (PathFilter f: filters) {
            buf.append(f);
            buf.append(",");
        }
        buf.append("]");
        return buf.toString();
    }
}
