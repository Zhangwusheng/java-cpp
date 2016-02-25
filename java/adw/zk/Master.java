package com.aipai.adw.zk;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by zhangwusheng on 15/11/11.
 */
public class Master implements LeaderElectable {
    private static final Logger LOG = LoggerFactory.getLogger ( LeaderElectable.class );

    ScheduledExecutorService masterMessageThread;
    ScheduledExecutorService workerMessageThread;
    @Override
    public void electedLeader ( ) {

        if( workerMessageThread != null ){
            LOG.info ( "exiting worker thread..." );
            workerMessageThread.shutdown ();
        }

        String threadName = "electedLeader";
        ThreadFactory threadFactory = new ThreadFactoryBuilder ().setDaemon(true).setNameFormat(threadName).build();
        masterMessageThread = Executors.newSingleThreadScheduledExecutor ( threadFactory );

        masterMessageThread.scheduleAtFixedRate ( new Runnable ( ) {
            @Override
            public void run ( ) {
                LOG.info ( "Yes,I am the leader" );

            }
        } ,5000,5000, TimeUnit.MILLISECONDS);

    }

    @Override
    public void revokedLeadership ( ) {
        if( masterMessageThread != null ){
            LOG.info ( "exiting master thread..." );
            masterMessageThread.shutdown ();
        }

        String threadName = "revokedLeadership";
        ThreadFactory threadFactory = new ThreadFactoryBuilder ().setDaemon(true).setNameFormat(threadName).build();
        workerMessageThread = Executors.newSingleThreadScheduledExecutor ( threadFactory );

        workerMessageThread.scheduleAtFixedRate ( new Runnable ( ) {
            @Override
            public void run ( ) {
                LOG.info ( "Yes,I am the worker" );

            }
        } ,5000,5000, TimeUnit.MILLISECONDS);


        //while

    }

    public static void main ( String[] args ) {

        CountDownLatch latn = new CountDownLatch ( 1 );
        Master master = new Master ();
        ZooKeeperLeaderElectionAgent agent = new ZooKeeperLeaderElectionAgent ( args[0] );
        agent.setMasterInstance ( master );
        agent.start();

        try {
            latn.await (10,TimeUnit.MINUTES);
        }
        catch ( InterruptedException e ) {
            e.printStackTrace ( );
        }

        System.out.println ("------ Exiting ......" );
    }
}
