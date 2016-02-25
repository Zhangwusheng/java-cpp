package com.aipai.adw.fs.hdfs;

import org.apache.hadoop.fs.PathFilter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangwusheng on 15/11/3.
 */
public class HdfsFileMonitorTest {
    public static void main ( String[] args ) throws Exception {

        CountDownLatch latch = new CountDownLatch ( 1 );
        // 监控目录
        String directory = "/tmp/test/d1";


        System.out.println ("---------------------" );
        // 轮询间隔 5 秒
        long interval = TimeUnit.SECONDS.toMillis(10);
        // 创建一个文件观察器用于处理文件的格式
        PathFilter filter = null;
//        FileFilr filter = new RegexFileFilter ("^.*t(\\d+)\\\\t(\\d+)\\\\[tT]est(-\\d+)?\\.java$");
//      filter = FileFilterUtils.and(
//                FileFilterUtils.fileFileFilter(),FileFilterUtils.suffixFileFilter(".txt"));
        HdfsFileAlterationObserver observer = new HdfsFileAlterationObserver(directory);
        //设置文件变化监听器
        observer.addListener(new HdfsFileListener());
        HdfsFileAlterationMonitor monitor = new HdfsFileAlterationMonitor(interval,observer);
        monitor.start();

        int i = 3;
        while(i>0){
            latch.await (30,TimeUnit.SECONDS);
            System.out.println ( "--------  i="+i );
            i=i-1;
        }


        monitor.stop ();
    }
}