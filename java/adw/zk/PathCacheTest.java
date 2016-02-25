package com.aipai.adw.zk;

//import org.apache

import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangwusheng on 15/11/3.
 */
public class PathCacheTest {
    public static void main ( String[] args ) {
        try {
            PathChildrenCache cache = null;
            CuratorFramework cluster = CuratorFrameworkFactory.newClient (
                    "192.168.1.199:31810",
                    new BoundedExponentialBackoffRetry (100,60000,8  ) );
            cluster.start();

            final CountDownLatch latch = new CountDownLatch(1);
            cache = new PathChildrenCache(cluster, "/test", true);
            cache.getListenable().addListener
                    (
                            new PathChildrenCacheListener ()
                            {
                                @Override
                                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception
                                {
                                    System.out.println ( event.getType () );

                                    if( event.getType () == PathChildrenCacheEvent.Type.CHILD_ADDED){
                                        System.out.println ( event.getData ().getPath () + " added" );
                                    }
                                    else if( event.getType () == PathChildrenCacheEvent.Type.CONNECTION_SUSPENDED){
                                        System.out.println ( "suspended" );
                                    }
                                    else if( event.getType () == PathChildrenCacheEvent.Type.CHILD_REMOVED){
                                        System.out.println ( event.getData ().getPath () + " removed" );
                                    }
                                    else if( event.getType () == PathChildrenCacheEvent.Type.CHILD_UPDATED){
                                        System.out.println ( event.getData ().getPath () + " updated" );
                                        String s = event.getData ().toString ();
                                        System.out.println ( " New data is:" + s );
                                    }
                                    else if( event.getType () == PathChildrenCacheEvent.Type.CONNECTION_SUSPENDED){
                                        System.out.println ( "suspended" );
                                    }
                                }
                            }
                    );
            cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

            while(true) {

                System.out.println ( "-----------------" );

                latch.await ( 10, TimeUnit.SECONDS );
                List<ChildData> data = cache.getCurrentData ();
                for( ChildData item: data){
                    System.out.println (item.getPath () );

                }
            }


        }
        catch ( Exception e ) {
            e.printStackTrace ( );
        }
        System.out.println ("In Main" );

//        PathChildrenCache pcc = new PathChildrenCache (  );
    }
}
