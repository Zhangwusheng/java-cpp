package com.aipai.adw.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by zhangwusheng on 15/11/11.
 */
public class ZooKeeperLeaderElectionAgent implements LeaderLatchListener,LeaderElectionAgent {
    private static final Logger LOG = LoggerFactory.getLogger ( ZooKeeperLeaderElectionAgent.class );

    ZooKeeperLeaderElectionAgent(String id){
        //
        myId = id;
        hostAndPort="192.168.1.199:31810";
        retryPolicy = new ExponentialBackoffRetry (10,10  );
        client = CuratorFrameworkFactory.newClient ( hostAndPort,retryPolicy );
        leaderLatch = new LeaderLatch(client, "/adwschedu/master");
        leaderLatch.addListener(this);
//        start ( );
    }

    @Override
    public void start ( ) {
        try {
            client.start ();
            leaderLatch.start();
        }
        catch ( Exception e ) {
            e.printStackTrace ( );
        }
    }

    @Override
    public LeaderElectable getMasterInstance ( ) {
        return masterInstance;
    }

    @Override
    public void setMasterInstance ( LeaderElectable instance ) {
        this.masterInstance = instance;
    }

    @Override
    public void stop ( ) {
        try {
            leaderLatch.close ();
        }
        catch ( IOException e ) {
            e.printStackTrace ( );
        }
        client.close ();

    }

    public static enum LeadershipStatus
    {
        LEADER,NOT_LEADER;
    }

    LeaderElectable masterInstance;
    String myId;
    String hostAndPort;//="192.168.1.199:31810";
    RetryPolicy retryPolicy; //= new ExponentialBackoffRetry (10,10  );
    CuratorFramework client ;
    private LeadershipStatus status = LeadershipStatus.NOT_LEADER;


    LeaderLatch leaderLatch = null;




    @Override
    public void isLeader ( ) {
//        synchronized(this) {
            // could have lost leadership by now.
            if ( !leaderLatch.hasLeadership ( ) ) {
                return;
            }

            LOG.info ( "We have gained leadership" );
            updateLeadershipStatus ( true );

//        }
    }

    private void updateLeadershipStatus(boolean isLeader) {
        LOG.info ( "updateLeadershipStatus:"+isLeader );
        if (isLeader && status == LeadershipStatus.NOT_LEADER) {
            status = LeadershipStatus.LEADER;
            masterInstance.electedLeader();
        } else if (!isLeader && status == LeadershipStatus.LEADER) {
            LOG.info ( "=====+++++++++++++++" );
            status = LeadershipStatus.NOT_LEADER;
            masterInstance.revokedLeadership();
        }
    }
    @Override
    public void notLeader ( ) {
//        synchronized(this) {
        // could have lost leadership by now.
        if ( leaderLatch.hasLeadership ( ) ) {
            LOG.info ( "----****************" );
            return;
        }

        LOG.info ( "We have ***** Not*** leadership" );
        updateLeadershipStatus ( false );

//        }

    }
}
