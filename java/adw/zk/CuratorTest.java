package com.aipai.adw.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangwusheng on 15/11/9.
 */
public class CuratorTest {

    public void justTest(String id){
        CountDownLatch latch = new CountDownLatch ( 1 );
        String myId = id;
        String hostAndPort="192.168.1.199:31810";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry (10,10  );

        AdwScheduleMasterLatch master = new AdwScheduleMasterLatch (myId,hostAndPort,retryPolicy  );
        master.startZK ();

        System.out.println ("starting zookeeper..." );

        try {
            master.runForMaster ();
        }
        catch ( Exception e ) {

            System.out.println ("-----------@@@@@@@@@@@@@" );
            e.printStackTrace ( );
        }

//        synchronized(latch)
        {
            try {
                while(true) {
                    latch.await ( 30, TimeUnit.SECONDS );
                }
            }
            catch ( InterruptedException e ) {
                e.printStackTrace ( );
            }

        }
        System.out.println ("exiting thread..." );

    }


    public static void main ( String[] args ) {
        CuratorTest t = new CuratorTest ();
        t.justTest ( args[0] );
    }

}
