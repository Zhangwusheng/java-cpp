package com.aipai.adw.fs.hdfs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.hadoop.fs.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangwusheng on 15/11/4.
 */
public class HdfsConnectionPoolTest {
    static final Log LOG = LogFactory.getLog ( HdfsConnectionPoolTest.class );

    public static void main ( String[] args ) {
        GenericObjectPoolConfig conf = new GenericObjectPoolConfig ();
        conf.setMaxTotal ( 8 );
        conf.setBlockWhenExhausted ( false );
        conf.setMaxWaitMillis ( 10*1000 );
        conf.setTestOnBorrow ( true );
        conf.setMinEvictableIdleTimeMillis ( 10*60*1000 );

        HdfsConnectionPoolFactory factory = new HdfsConnectionPoolFactory ();
        GenericObjectPool<FileSystem> pool = new GenericObjectPool< FileSystem > ( factory );


        CountDownLatch latch = new CountDownLatch ( 1 );
        for(int i = 0;i<100;i++) {
            LOG.info ( "-------------------" );
            try {
                FileSystem fs = pool.borrowObject ( );

                LOG.info ( fs );
                Path path = new Path ( "/" );
                FileStatus[] statuc = fs.listStatus ( path );

                for ( FileStatus status : statuc ) {
                    LOG.info ( status.getPath () );
                }

                pool.returnObject ( fs );
            }
            catch ( Exception e ) {
                e.printStackTrace ( );
            }

            try {
                latch.await (10, TimeUnit.SECONDS);
            }
            catch ( InterruptedException e ) {
                e.printStackTrace ( );
            }
        }
    }
}
