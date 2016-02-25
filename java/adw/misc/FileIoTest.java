package com.aipai.adw.misc;

//import org.apache.commons.cli.*;
//import org.apache.commons.cli.Options;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.*;
//import org.apache.hadoop.security.Credentials;
//import org.apache.hadoop.security.UserGroupInformation;
//import org.apache.hadoop.util.GenericOptionsParser;
//import org.apache.hadoop.conf.Configuration;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.oro.text.regex.*;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormatter;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.io.*;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;



/**
 * Created by zws on 7/9/15.
 */
public class FileIoTest {


    private CountDownLatch countDownLatch = new CountDownLatch(3);
    private volatile boolean running = true;
    private BlockingQueue<String> dataQueue = new LinkedBlockingQueue<String>();
    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    private int BUF_SIZE = 65536;
    private DateTime dt = DateTime.now(DateTimeZone.getDefault());
    private StringBuffer sbFilePath = new StringBuffer();
    private Properties prop = new Properties();
    KafkaProducer<String, String> producer = null;
    boolean syncKafka = false;
    String topic = "mytopic";
//    private CommandLine commandLine;

//    private Configuration conf = new Configuration();


    public ByteBuffer deepCopy(ByteBuffer source, ByteBuffer target) {

        int sourceP = source.position();
        int sourceL = source.limit();


        if (null == target) {
            target = ByteBuffer.allocate(source.remaining());
        }
        target.put(source);
        target.flip();


        source.position(sourceP);
        source.limit(sourceL);
        return target;
    }

    public void waitForTermination() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testData() {

        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.211:24000,192.168.1.212:24000, 192.168.1.213:24000");
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        producer = new KafkaProducer<String, String>(prop);


        for (int t = 0; t < 2; t++) {

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    while (running) {
                        try {
                            final String dataStr = dataQueue.poll(1, TimeUnit.SECONDS);
                            if (dataStr == null) {
                                Thread.sleep(1000);
                                continue;
                            }

                            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, null, dataStr);

                            if (syncKafka) {
                                producer.send(record, new Callback() {
                                    @Override
                                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                                        if (exception != null) {
                                            dataQueue.offer(dataStr);
                                        }

                                    }
                                });
                            }
                            System.out.println("--------" + dataStr);
                        } catch (InterruptedException e) {

                        }

                    }

                    countDownLatch.countDown();
                }
            });
        }


        DateTime nextPeriod = dt.withField(DateTimeFieldType.secondOfMinute(), 0).withField(DateTimeFieldType.millisOfSecond(), 0).withFieldAdded(
                DurationFieldType.minutes(), 1);

        String baseDir = "/web/logs";

        try {
            FileInputStream fis = new FileInputStream("/web/test.fifo");
            FileOutputStream fos = null;

            FileChannel channel = fis.getChannel();

            System.out.printf(channel.toString());

            ByteBuffer byteBuffer = ByteBuffer.allocate(BUF_SIZE);
            Charset charset = Charset.forName("UTF-8");
            CharsetDecoder decoder = charset.newDecoder();
            CharBuffer charBuffer = null;
            int len = 0;
            try {
                while ((len = channel.read(byteBuffer)) != -1) {
                    System.out.println("len=" + len);

                    dt = DateTime.now(DateTimeZone.getDefault());
                    if (dt.isAfter(nextPeriod) && fos != null) {
                        nextPeriod = nextPeriod.withFieldAdded(
                                DurationFieldType.minutes(), 1);

                        fos.close();
                        fos = null;
                    }

                    if (fos == null) {
                        sbFilePath.setLength(0);
                        sbFilePath.append(baseDir);
                        String t = dt.toString("/YYYYMMdd/HH/mm");
                        sbFilePath.append(t);
                        sbFilePath.append(".log");
                        String mypath = sbFilePath.toString();
                        File f = new File(mypath);
                        if (!f.getParentFile().exists()) {
                            f.getParentFile().mkdirs();
                        }
                        System.out.println("opening " + mypath);

                        fos = new FileOutputStream(mypath);
                    }

                    byteBuffer.flip();
                    ByteBuffer target = deepCopy(byteBuffer, null);
//                        dataQueue.offer();
                    charBuffer = decoder.decode(target);

                    FileChannel out_channel = fos.getChannel();
                    out_channel.write(byteBuffer);
                    dataQueue.offer(charBuffer.toString());
//                        System.out.println(charBuffer.toString());

                    target = null;
                    charBuffer = null;
                    byteBuffer.clear();
                }

                this.running = false;

                countDownLatch.countDown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private Map<String, LinkedList<String[]>> ipRequestDataMap = new HashMap<String, LinkedList<String[]>>();


    public void hadoopTest(String[] args) {
//        try {
//            int count = 0;
//            System.out.println("--------hadoopTest");
//            GenericOptionsParser parser = new GenericOptionsParser(conf, args);
//            FileSystem fs = FileSystem.get(conf);
//
//            String path = "/exchange/apkdownload/";
//            path += conf.get("month");
//            path += "/";
//            path += conf.get("date");
//            path += "/";
//            path += conf.get("hour");
//
//            Path p = new Path(path);
//            RemoteIterator<LocatedFileStatus> rit = fs.listFiles(p, false);
//            DateTimeFormatter format = org.joda.time.format.DateTimeFormat.forPattern("dd/MMM/yyyy:HH:mm:ss");
//
//            String line;
//            while (rit.hasNext()) {
//                LocatedFileStatus status = rit.next();
//
//                FSDataInputStream in;
//                BufferedReader bufreadr;
//                in = fs.open(status.getPath());
//                bufreadr = new BufferedReader(new InputStreamReader(in));
//                while ((line = bufreadr.readLine()) != null) {
//                    count++;
//                    if (count % 10000 == 0) {
//                        System.out.println("Total Read:" + count);
//                    }
//                    if (line.indexOf("format=apk") != -1) {
//                        String[] items = line.split(" ");
//                        String ip = items[0];
//                        String date = items[3].substring(1);
//
//                        if( ipRequestDataMap.containsKey(ip))
//                        {
//                            LinkedList<String[]> oldData = ipRequestDataMap.get(ip);
//                            oldData.clear();
//                        }
//                        else {
//                            LinkedList<String[]> oldData = new LinkedList<String[]>();
//                            ipRequestDataMap.put(ip,oldData);
//                        }
//                        //ip2DataMap.put(ip)
//                        try {
//                            DateTime dt = DateTime.parse(date, format);
//                        } catch (IllegalArgumentException e) {
//                            System.out.println(line);
//
//                            e.printStackTrace();
//                            System.exit(1);
//                        }
//                    }
//                }
//
////                System.out.println(status.getPath());
//            }
//
//            System.out.println("Total Read:" + count);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public static class A{
        public String a;
        public String b;

        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append(a);
            sb.append(b);

            return sb.toString();
        }
    }

    public static  class B extends A{
        public String c;

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toString());
            sb.append(c);

            return sb.toString();
        }
    }

    public static void main(String[] args) {

//        System.out.println(MyTestEnum.EN1.toString());
//        B b = new B();
//        b.a="a";
//        b.b="b";
//        b.c="c";
//        System.out.println(b.toString());
//
//        DownLoadStat ds = new DownLoadStat();
//        ds.init();
//        ds.processFilesFromHadoop(args);
//        ds.finish();
//        ds.saveData();
    }

    public static void requestPatternTest() {
        PatternMatcher matcher;
        PatternCompiler compiler;
        Pattern pattern;
        PatternMatcherInput input;
        MatchResult result;
        compiler = new Perl5Compiler();
        matcher = new Perl5Matcher();

        String formatdata = "182.127.137.84 - - [28/Jul/2015:15:30:08 +0800] \"GET /api/2/apps/net.appplus.addon?format=apk&udid=3f2616ce27b3c24&os=Android&os_version=4.4.4&device=BOWAY_U7&oem=BOWAY&app_version=160&aid=8815 HTTP/1.1\" 302 497 \"-\" \"SharePlus/Android\" - \"0.142\"";
        String formatdata2 = "182.127.137.85 - - [28/Jul/2015:15:30:08 +0800] \"GET /api/2/apps/net.appplus.addon?format=apk&udid=3f2616ce27b3c24&os=Android&os_version=4.4.4&device=BOWAY_U7&oem=BOWAY&app_version=160&aid=8815 HTTP/1.1\" 302 497 \"-\" \"SharePlus/Android\" - \"0.142\"";
        String requestPattern = "(.*) \\- \\- \\[(.*) \\+0800] \"GET /api/2/apps/(.*)\\?format=apk&(.*) HTTP.*";

        try {

            pattern = compiler.compile(requestPattern);
        } catch (MalformedPatternException e) {
            System.out.println("Bad pattern.");
            System.out.println(e.getMessage());
            return;
        }
        input = new PatternMatcherInput(formatdata);
        input.setInput(formatdata);

        DateTime now = new DateTime();
        for (int KK = 0; KK < 1; KK++) {
            if (KK % 10000 == 0)
                System.out.println("---------------" + KK);

            input.setInput(formatdata);
            input.setCurrentOffset(input.getBeginOffset());

            while (matcher.contains(input, pattern)) {

                result = matcher.getMatch();
                int n = result.groups();
                for (int j = 1; j < n; j++) {
                    String str = result.group(j);
                    System.out.println(str);
                }
            }

        }


        String downloadData = "119.185.185.248 - - [28/Jul/2015:15:30:07 +0800] \"GET /apps/com.aipai.android/android/version/aipai.apk HTTP/1.1\" 200 13417335 \"-\" \"Dalvik/1.6.0 (Linux; U; Android 4.2.1; 2013022 MIUI/JHACNBL30.0)\" - \"35.387\"";
        String downloadPattern = "(.*) \\- \\- \\[(.*) \\+0800] \"GET /apps/(.*)/android/version/(.*) HTTP.* ([0-9]{1,}) ([0-9]{1,}).* \\- \"(.*)\"$";


        DateTime now2 = new DateTime();
        Period p = new Period(now, now2, PeriodType.seconds());
        System.out.println(p.toString());
    }

    public static void downloadPatternTest() {
        PatternMatcher matcher;
        PatternCompiler compiler;
        Pattern pattern;
        PatternMatcherInput input;
        MatchResult result;
        compiler = new Perl5Compiler();
        matcher = new Perl5Matcher();

        String downloadData = "119.185.185.248 - - [28/Jul/2015:15:30:07 +0800] \"GET /apps/com.aipai.android/android/version/aipai.apk HTTP/1.1\" 200 13417335 \"-\" \"Dalvik/1.6.0 (Linux; U; Android 4.2.1; 2013022 MIUI/JHACNBL30.0)\" - \"35.387\"";
        String downloadPattern = "(.*) \\- \\- \\[(.*) \\+0800] \"GET /apps/(.*)/android/version/(.*) HTTP.* ([0-9]{1,}) ([0-9]{1,}).* \\- \"(.*)\"$";

        try {

            pattern = compiler.compile(downloadPattern);
        } catch (MalformedPatternException e) {
            System.out.println("Bad pattern.");
            System.out.println(e.getMessage());
            return;
        }

        input = new PatternMatcherInput(downloadData);
        input.setInput(downloadData);

        DateTime now = new DateTime();
        for (int KK = 0; KK < 1; KK++) {
            if (KK % 10000 == 0)
                System.out.println("---------------" + KK);

            input.setInput(downloadData);

            while (matcher.contains(input, pattern)) {

                result = matcher.getMatch();
                int n = result.groups();
                for (int j = 1; j < n; j++) {
                    String str = result.group(j);
                    System.out.println(str);
                }
            }
            input.setCurrentOffset(input.getBeginOffset());
//            input.setInput(downloadData);
//            input.setCurrentOffset(input.getBeginOffset());
//            while (matcher.contains(input, pattern)) {
//                result = matcher.getMatch();
//                int n = result.groups();
//                for (int j = 0; j < n; j++) {
//                    String str = result.group(j);
//                }
//            }
        }

        DateTime now2 = new DateTime();
        Period p = new Period(now, now2, PeriodType.seconds());
        System.out.println(p.toString());
    }

    private static void pipelineTest() {
        FileIoTest t = new FileIoTest();

        t.testData();

        t.waitForTermination();
    }

    private static void UriTest() {
//        URI u = URI.create("hdfs://apnamenode:28100/user/home/zws.txt");
//        Path p = new Path(u);
//        System.out.println(p.toUri().getScheme());
//        System.out.println(p.toUri().getAuthority());
//        System.out.println(p.toUri().getPath());
//        System.out.println(p.toString());
    }

    private static void hadoopFileTest(String[] args) {
        FileIoTest ta = new FileIoTest();
        ta.hadoopTest(args);
    }

    private static void jodaDateTest() {
        try {
            DateTimeFormatter format = org.joda.time.format.DateTimeFormat.forPattern("dd/MMM/yyyy:HH:mm:ss");
            DateTime dt = DateTime.parse("30/Jul/2015:06:40:08", format);
//            System.out.println(format.toString());
//            System.out.println(dt);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
