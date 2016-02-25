package com.aipai.adw.zk;

/**
 * Created by zhangwusheng on 15/11/11.
 */
public interface LeaderElectable {
    void electedLeader();
    void revokedLeadership();

}
