package com.aipai.adw.fs.local;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangwusheng on 15/11/3.
 */
public class FileMonitorTest {
    public static void main ( String[] args ) throws Exception {

        CountDownLatch latch = new CountDownLatch ( 1 );
        // 监控目录
        String directory = "/Users/zhangwusheng/t1";

        File f = new File(directory);
        File[] all = f.listFiles ();
        for(File ff:all){
            System.out.println (ff.getName () );
        }

        System.out.println ("---------------------" );
        // 轮询间隔 5 秒
        long interval = TimeUnit.SECONDS.toMillis(10);
        // 创建一个文件观察器用于处理文件的格式
        FileFilter filter = null;
//        FileFilter filter = new RegexFileFilter ("^.*t(\\d+)\\\\t(\\d+)\\\\[tT]est(-\\d+)?\\.java$");
//      filter = FileFilterUtils.and(
//                FileFilterUtils.fileFileFilter(),FileFilterUtils.suffixFileFilter(".txt"));
        FileAlterationObserver observer = new FileAlterationObserver(directory, filter);
        //设置文件变化监听器
        observer.addListener(new FileListener());
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval,observer);
        monitor.start();

        while(true){
            latch.await (60,TimeUnit.SECONDS);
            System.out.println ( "--------" );
        }
    }
}