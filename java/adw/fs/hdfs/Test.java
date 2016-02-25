package com.aipai.adw.fs.hdfs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangwusheng on 15/11/4.
 */
public class Test {
    public static void main ( String[] args ) {

        CountDownLatch latch = new CountDownLatch ( 1 );
        int i = 3;
        while(i>0){
            try {
                latch.await (30, TimeUnit.SECONDS);
            }
            catch ( InterruptedException e ) {
                e.printStackTrace ( );
            }
            System.out.println ( "--------  i="+i );
            i=i-1;
        }
    }
}
