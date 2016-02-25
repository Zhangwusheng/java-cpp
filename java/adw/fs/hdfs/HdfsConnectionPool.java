package com.aipai.adw.fs.hdfs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.hadoop.fs.*;

/**
 * Created by zhangwusheng on 15/11/4.
 */
public class HdfsConnectionPool {

    private static HdfsConnectionPool thePool = new HdfsConnectionPool ();
    private GenericObjectPoolConfig conf = new GenericObjectPoolConfig ();

    private HdfsConnectionPoolFactory factory = null;
    private GenericObjectPool<FileSystem> pool = null;

    protected HdfsConnectionPool(){
        conf.setMaxTotal ( 8 );
        conf.setBlockWhenExhausted ( false );
        conf.setMaxWaitMillis ( 10*1000 );
        conf.setTestOnBorrow ( true );
        conf.setMinEvictableIdleTimeMillis ( 10*60*1000 );

        factory = new HdfsConnectionPoolFactory ();
        pool = new GenericObjectPool< FileSystem > ( factory , conf );
    }


    public static HdfsConnectionPool get(){

        return thePool;
    }

    public FileSystem getFileSystem() throws Exception{
            return pool.borrowObject ();
    }

    public void returnObject(FileSystem fs){
        pool.returnObject ( fs  );
    }

}
