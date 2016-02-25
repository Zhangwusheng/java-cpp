package com.aipai.adw.zk;

/**
 * Created by zhangwusheng on 15/11/11.
 */
public interface LeaderElectionAgent {
    LeaderElectable getMasterInstance();
    void setMasterInstance(LeaderElectable instatnce);
    void start();
    void stop() ;// to avoid noops in implementations.
}
