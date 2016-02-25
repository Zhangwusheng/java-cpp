package com.aipai.adw.fs.hdfs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

/**
 * Created by zhangwusheng on 15/11/4.
 * 注意FileSystem不是线程安全的。
 * 这个类设计目的是为了在一个线程里面监控某个目录的文件是否存在，然后分发某些事件。
 */
public class HdfsConnectionPoolFactory extends BasePooledObjectFactory<FileSystem> {
    static final Log LOG = LogFactory.getLog ( HdfsConnectionPoolFactory.class );

    @Override
    public FileSystem create ( ) throws Exception {
        Configuration conf = new Configuration (  );
        FileSystem fs = FileSystem.get ( conf );
        return fs;
    }

    @Override
    public PooledObject< FileSystem > wrap ( FileSystem obj ) {
        return new DefaultPooledObject< FileSystem > ( obj );
    }

    @Override
    public PooledObject< FileSystem > makeObject ( ) throws Exception {
        return super.makeObject ( );
    }

    @Override
    public void destroyObject ( PooledObject< FileSystem > p ) throws Exception {
        p.getObject ().close ();
    }

    @Override
    public boolean validateObject ( PooledObject< FileSystem > p ) {
        try {
            Path tmpPath = new Path ( "/tmp" );
            boolean b = p.getObject ().exists ( tmpPath );
            return b;
        }
        catch ( IOException e ) {
            e.printStackTrace ();
            return false;
        }
    }

    @Override
    public void activateObject ( PooledObject< FileSystem > p ) throws Exception {

    }

    @Override
    public void passivateObject ( PooledObject< FileSystem > p ) throws Exception {
    }
}
