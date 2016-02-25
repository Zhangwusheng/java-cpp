package com.aipai.adw.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by zhangwusheng on 15/11/9.
 */
public class AdwScheduleMasterLatch implements Closeable , LeaderLatchListener{
    private static final Logger LOG = LoggerFactory.getLogger ( AdwScheduleMasterLatch.class );

    private String myId;
    private CuratorFramework client;
    private final LeaderLatch leaderLatch;
    private final PathChildrenCache workersCache;
    private final PathChildrenCache tasksCache;

    public AdwScheduleMasterLatch(String myId, String hostAndPort, RetryPolicy retryPolicy){
        LOG.info( myId + ": " +         hostAndPort );

        this.myId = myId;
        this.client = CuratorFrameworkFactory.newClient ( hostAndPort,
                retryPolicy );
        this.leaderLatch = new LeaderLatch(this.client, "/adwschedu/master", myId);
        this.workersCache = new PathChildrenCache(this.client, "/adwschedu/workers", true);
        this.tasksCache = new PathChildrenCache(this.client, "/adwschedu/tasks", true);
    }

    public void startZK(){
        client.start();
    }

    @Override
    public void close ( ) throws IOException {

    }

    @Override
    public void isLeader ( ) {
        while( leaderLatch.hasLeadership ()){
            LOG.info ( "Yes,I am the leader..." );
            try {
                Thread.sleep ( 3000 );
            }
            catch ( InterruptedException e ) {
                e.printStackTrace ( );
            }
        }
    }

    @Override
    public void notLeader ( ) {
        LOG.info ( "notLeader Called" );

        while ( !leaderLatch.hasLeadership () ){
            LOG.info ( "Wow,I forgive the leader..." );
            try {
                Thread.sleep ( 3000);
            }
            catch ( InterruptedException e ) {
                e.printStackTrace ( );
            }

        }

    }

    CuratorListener masterListener = new CuratorListener() {
        public void eventReceived(CuratorFramework client, CuratorEvent event){
            try{
                LOG.info("Event path: " + event.getPath());
                LOG.info ( "Event Type:" + event.getType () );
                LOG.info ( "----------------------------" );
                switch (event.getType()) {
                    case CHILDREN:
                        if(event.getPath().contains("/assign")) {
                            LOG.info("Succesfully got a list of assignments: "
                                    + event.getChildren().size()
                                    + " tasks");
                        /*
                         * Delete the assignments of the absent worker
                         */
                            for(String task : event.getChildren()){
                                LOG.info("getting task:"+task);
                                //deleteAssignment(event.getPath() + "/" + task);
                            }

                        /*
                         * Delete the znode representing the absent worker
                         * in the assignments.
                         */
//                            deleteAssignment(event.getPath());

                        /*
                         * Reassign the tasks.
                         */
//                            assignTasks(event.getChildren());
                        } else {
                            LOG.warn("Unexpected event: " + event.getPath());
                        }

                        break;
                    case CREATE:
                    /*
                     * Result of a create operation when assigning
                     * a task.
                     */
                        if(event.getPath().contains("/assign")) {
                            LOG.info("Task assigned correctly: " + event.getName());
//                            deleteTask(event.getPath().substring(event.getPath().lastIndexOf('-') + 1));
                        }

                        break;
                    case DELETE:
                    /*
                     * We delete znodes in two occasions:
                     * 1- When reassigning tasks due to a faulty worker;
                     * 2- Once we have assigned a task, we remove it from
                     *    the list of pending tasks.
                     */
                        if(event.getPath().contains("/tasks")) {
                            LOG.info("Result of delete operation: " + event.getResultCode() + ", " + event.getPath());
                        } else if(event.getPath().contains("/assign")) {
                            LOG.info("Task correctly deleted: " + event.getPath());
                            break;
                        }

                        break;
                    case WATCHED:
                        // There is no case implemented currently.

                        break;
                    default:
                        LOG.error("Default case: " + event.getType());
                }
            } catch (Exception e) {
                LOG.error("Exception while processing event.", e);
                try{
                    close();
                } catch (IOException ioe) {
                    LOG.error("IOException while closing.", ioe);
                }
            }
        };
    };

    UnhandledErrorListener errorsListener = new UnhandledErrorListener() {
        public void unhandledError(String message, Throwable e) {
            LOG.error("Unrecoverable error: " + message, e);
            try {
                close();
            } catch (IOException ioe) {
                LOG.warn( "Exception when closing.", ioe );
            }
        }
    };

    public void runForMaster()
            throws Exception {
        /*
         * Register listeners
         */
        client.getCuratorListenable().addListener(masterListener);
        client.getUnhandledErrorListenable().addListener(errorsListener);


        /*
         * Start master election
         */
        LOG.info( "Starting master selection: " + myId);
        leaderLatch.addListener(this);
        leaderLatch.start();
    }
}
