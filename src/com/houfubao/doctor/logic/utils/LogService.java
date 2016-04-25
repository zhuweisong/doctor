package com.houfubao.doctor.logic.utils;

import java.io.BufferedReader;  
import java.io.File;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.text.ParseException;  
import java.text.SimpleDateFormat;  
import java.util.ArrayList;  
import java.util.Arrays;
import java.util.Comparator;  
import java.util.Date;  
import java.util.List;  
  

import android.app.AlarmManager;  
import android.app.PendingIntent;  
import android.app.Service;  
import android.content.BroadcastReceiver;  
import android.content.Context;  
import android.content.Intent;  
import android.content.IntentFilter;  
import android.os.Environment;  
import android.os.IBinder;  




/** 
 * 日志服务，日志默认会存储在SDcar里,如果没有SDcard会存储在内存中的安装目录下面。 
 * 1.本服务默认在SDcard中每天生成一个日志文件, 
 * 2.本service的运行日志： /storage/emulated/legacy/MyApp/log 
 * /sdcard/tencent/gamestation/store/download
 * @author Administrator 
 *  
 */ 


public class LogService extends Service {
    private static final String TAG = "LogService";  
    private static final int LOG_FILE_MAX_SIZE = 10 * 1024 * 1024;           //内存中日志文件最大值，10M  
    private static final int LOG_FILE_MONITOR_INTERVAL = 10 * 60 * 1000;     //内存中的日志文件大小监控时间间隔，10分钟  
      
    private String LOG_PATH_SDCARD_DIR;     //日志文件在sdcard中的路径  
    private String CURR_INSTALL_LOG_NAME;   //如果当前的日志写在内存中，记录当前的日志文件名称  

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss");//日志名称格式  
      
    private Process process;  

    private LogTaskReceiver logTaskReceiver;   

      
    /* 是否正在监测日志文件大小；*/  
    private boolean logSizeMoniting = false;          
      
    private static String MONITOR_LOG_SIZE_ACTION = "MONITOR_LOG_SIZE";     //日志文件监测action  
    private static String SWITCH_LOG_FILE_ACTION = "SWITCH_LOG_FILE_ACTION";    //切换日志文件action  
      
    @Override  
    public IBinder onBind(Intent intent) {  
        return null;  
    }  
  
    // This is the new method that instead of the old onStart method on the pre-2.0 platform.  
    @Override //开始服务，执行更新widget组件的操作  
    public int onStartCommand(Intent intent, int flags, int startId) {  
          


        return Service.START_REDELIVER_INTENT;  
    }  
      
    @Override  
    public void onCreate() {  
        super.onCreate();  
        
        QLog.i(TAG, "===================onCreate===========================");  
        
        init();  
        register();  
        new LogCollectorThread().start();  
    }  
      
    private void init(){  

        LOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator  
                            +   "MyApp" + File.separator + "Log";  
        createLogDir();  
        QLog.i(TAG, "LogService onCreate:" + LOG_PATH_SDCARD_DIR);  
    }  
      
    private void register(){  
        IntentFilter logTaskFilter = new IntentFilter();  
        logTaskFilter.addAction(MONITOR_LOG_SIZE_ACTION);  
        logTaskFilter.addAction(SWITCH_LOG_FILE_ACTION);  
        logTaskReceiver = new LogTaskReceiver();  
        registerReceiver(logTaskReceiver,logTaskFilter);  
    }  
      
    /** 
     * 日志收集 
     * 1.清除日志缓存  
     * 2.杀死应用程序已开启的Logcat进程防止多个进程写入一个日志文件 
     * 3.开启日志收集进程  
     * 4.处理日志文件 
     *   移动 OR 删除 
     */  
    class LogCollectorThread extends Thread {  
          
        public LogCollectorThread(){  
            super("LogCollectorThread");  
            QLog.d(TAG, "LogCollectorThread is create");  
        }  
          
        @Override  
        public void run() {  
            try {  
                List<String> orgProcessList = getAllProcess();  
                List<ProcessInfo> processInfoList = getProcessInfoList(orgProcessList);  
                killLogcatProc(processInfoList);  
                  
                createLogCollector();  
                  
                Thread.sleep(1000);//休眠，创建文件，然后处理文件，不然该文件还没创建，会影响文件删除  
                  
                handleLog();  

            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }  
      
    /** 
     * 关闭由本程序开启的logcat进程： 
     * 根据用户名称杀死进程(如果是本程序进程开启的Logcat收集进程那么两者的USER一致) 
     * 如果不关闭会有多个进程读取logcat日志缓存信息写入日志文件 
     *  
     * @param allProcList 
     * @return 
     */  
    private void killLogcatProc(List<ProcessInfo> allProcList) {  
        if(process != null){  
            process.destroy();  
        }  
        String packName = this.getPackageName();  
        String myUser = getAppUser(packName, allProcList);  

        for (ProcessInfo processInfo : allProcList) {  
            if (processInfo.name.toLowerCase().equals("logcat")  
                    && processInfo.user.equals(myUser)) {  
                android.os.Process.killProcess(Integer  
                        .parseInt(processInfo.pid));  
            }  
        }  
    }  
  
    /** 
     * 获取本程序的用户名称 
     *  
     * @param packName 
     * @param allProcList 
     * @return 
     */  
    private String getAppUser(String packName, List<ProcessInfo> allProcList) {  
        for (ProcessInfo processInfo : allProcList) {  
            if (processInfo.name.equals(packName)) {  
                return processInfo.user;  
            }  
        }  
        return null;  
    }  
  
    /** 
     * 根据ps命令得到的内容获取PID，User，name等信息 
     *  
     * @param orgProcessList 
     * @return 
     */  
    private List<ProcessInfo> getProcessInfoList(List<String> orgProcessList) {  
        List<ProcessInfo> procInfoList = new ArrayList<ProcessInfo>();  
        for (int i = 1; i < orgProcessList.size(); i++) {  
            String processInfo = orgProcessList.get(i);  
            String[] proStr = processInfo.split(" ");  
            // USER PID PPID VSIZE RSS WCHAN PC NAME  
            // root 1 0 416 300 c00d4b28 0000cd5c S /init  
            List<String> orgInfo = new ArrayList<String>();  
            for (String str : proStr) {  
                if (!"".equals(str)) {  
                    orgInfo.add(str);  
                }  
            }  
            if (orgInfo.size() == 9 ) {  
                    ProcessInfo pInfo = new ProcessInfo();  
                    pInfo.user = orgInfo.get(0);  
                    pInfo.pid = orgInfo.get(1);  
                    pInfo.ppid = orgInfo.get(2);  
                    pInfo.name = orgInfo.get(8);  
                    procInfoList.add(pInfo);  
            } 
        }  
        return procInfoList;  
    }  
  
    /** 
     * 运行PS命令得到进程信息 
     *  
     * @return 
     *          USER PID PPID VSIZE RSS WCHAN PC NAME 
     *          root 1 0 416 300 c00d4b28 0000cd5c S /init 
     */  
    private List<String> getAllProcess() {  
        List<String> orgProcList = new ArrayList<String>();  
        Process proc = null;  
        try {  
            proc = Runtime.getRuntime().exec("ps");  
            StreamConsumer errorConsumer = new StreamConsumer(proc  
                    .getErrorStream());  
  
            StreamConsumer outputConsumer = new StreamConsumer(proc  
                    .getInputStream(), orgProcList);  
  
            errorConsumer.start();  
            outputConsumer.start();  
            if (proc.waitFor() != 0) {  
                QLog.e(TAG, "getAllProcess proc.waitFor() != 0");  
            }  
        } catch (Exception e) {  
            QLog.e(TAG, "getAllProcess failed", e);  
        } finally {  
            try {  
                proc.destroy();  
            } catch (Exception e) {  
                QLog.e(TAG, "getAllProcess failed", e);  
            }  
        }  
        return orgProcList;  
    }  
      
    /** 
     * 开始收集日志信息 
     */  
    public void createLogCollector() {  
       
        List<String> commandList = new ArrayList<String>();  
        commandList.add("logcat");  
        commandList.add("-f");  
//      commandList.add(LOG_PATH_INSTALL_DIR + File.separator + logFileName);  
        commandList.add(getLogPath());  
        commandList.add("-v");  
        commandList.add("time");  
//        commandList.add("*:I");  

        
        
        //commandList.add("*:E");// 过滤所有的错误信息  
 
        // 过滤指定TAG的信息  
        commandList.add(QLog.TAG );  
        commandList.add("*:S");  
        try {  
            process = Runtime.getRuntime().exec(  
                    commandList.toArray(new String[commandList.size()]));  

            // process.waitFor();  
        } catch (Exception e) {  
            QLog.e(TAG, "CollectorThread == >" + e.getMessage(), e);  
        }  
    }  
      
    /** 
     * 根据当前的存储位置得到日志的绝对存储路径 
     * @return 
     */    
    public String getLogPath(){  
        createLogDir();  
        String logFileName = sdf.format(new Date()) + ".Log";// 日志文件名称  
        
        CURR_INSTALL_LOG_NAME = LOG_PATH_SDCARD_DIR + File.separator + logFileName;  
        QLog.d(TAG, "QLog stored in SDcard, the path is:"+LOG_PATH_SDCARD_DIR + File.separator + logFileName);  
        return CURR_INSTALL_LOG_NAME;  

    }  
      
    /** 
     * 处理日志文件 
     * 1.如果日志文件存储位置切换到内存中，删除除了正在写的日志文件 
     *   并且部署日志大小监控任务，控制日志大小不超过规定值 
     * 2.如果日志文件存储位置切换到SDCard中，删除7天之前的日志，移 
     *     动所有存储在内存中的日志到SDCard中，并将之前部署的日志大小 
     *   监控取消 
     */  
    public void handleLog(){  
            deployLogSizeMonitorTask();  
            deleteMemoryExpiredLog();            
    }  
      
    /** 
     * 部署日志大小监控任务 
     */  
    private void deployLogSizeMonitorTask() {  

		if(logSizeMoniting){    //如果当前正在监控着，则不需要继续部署  
            return;  
        }  
        logSizeMoniting = true;  
        Intent intent = new Intent(MONITOR_LOG_SIZE_ACTION);  
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);  
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);  
        am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), LOG_FILE_MONITOR_INTERVAL, sender);  
        QLog.d(TAG, "deployLogSizeMonitorTask() succ !");  
    }  
      

    /** 
     * 检查日志文件大小是否超过了规定大小 
     * 如果超过了重新开启一个日志收集进程 
     */  
    private void checkLogSize(){  
        if(CURR_INSTALL_LOG_NAME != null && !"".equals(CURR_INSTALL_LOG_NAME)){  
            String path = CURR_INSTALL_LOG_NAME;  
            File file = new File(path);  
            if(!file.exists()){  
                return;  
            }  
            QLog.d(TAG, "checkLog() ==> The size of the QLog is too big?");  
            if(file.length() >= LOG_FILE_MAX_SIZE){  
                QLog.d(TAG, "The QLog's size is too big!");  
                new LogCollectorThread().start();  
            }  
        }  
    }  
      
    /** 
     * 创建日志目录 
     */  
    private void createLogDir() {  

        if (Environment.getExternalStorageState().equals(  
                Environment.MEDIA_MOUNTED)) {  
        	File file = new File(LOG_PATH_SDCARD_DIR);  
            if (!file.isDirectory()) {  
            	boolean mkOk = file.mkdirs();  
                if (!mkOk) {  
                    return;  
                }  
            }  
        }  
    }  
      

      
    /** 
     * 删除内存中的过期日志，删除规则： 
     * 除了当前的日志和离当前时间最近的日志保存其他的都删除 
     */  
    private void deleteMemoryExpiredLog(){  
        File file = new File(LOG_PATH_SDCARD_DIR);  
        if (file.isDirectory()) {  
            File[] allFiles = file.listFiles();  
            Arrays.sort(allFiles, new FileComparator());  
            for (int i=0;i<allFiles.length-5;i++) {  //"-5"保存最近的两个日志文件  
                File _file =  allFiles[i];  
                _file.delete();  
                QLog.d(TAG, "delete expired QLog success,the QLog path is:"+_file.getAbsolutePath());  
            }
        }
    }
  
      
    /** 
     * 去除文件的扩展类型（.QLog） 
     * @param fileName 
     * @return 
     */  
    private String getFileNameWithoutExtension(String fileName){  
        return fileName.substring(0, fileName.indexOf("."));  
    }  
  
    class ProcessInfo {  
        public String user;  
        public String pid;  
        public String ppid;  
        public String name;  
  
        @Override  
        public String toString() {  
            String str = "user=" + user + " pid=" + pid + " ppid=" + ppid  
                    + " name=" + name;  
            return str;  
        }  
    }  
    class StreamConsumer extends Thread {  
        InputStream is;  
        List<String> list;  
  
        StreamConsumer(InputStream is) {  
            this.is = is;  
        }  
  
        StreamConsumer(InputStream is, List<String> list) {  
            this.is = is;  
            this.list = list;  
        }  
  
        public void run() {  
            try {  
                InputStreamReader isr = new InputStreamReader(is);  
                BufferedReader br = new BufferedReader(isr);  
                String line = null;  
                while ((line = br.readLine()) != null) {  
                    if (list != null) {  
                        list.add(line); 
                    }  
                }  
            } catch (IOException ioe) {  
                ioe.printStackTrace();  
            }  
        }  
    }  
      
      
    /** 
     * 日志任务接收 
     * 切换日志，监控日志大小 
     * @author Administrator 
     * 
     */  
    class LogTaskReceiver extends BroadcastReceiver{  
        public void onReceive(Context context, Intent intent) {  
            String action = intent.getAction();  
            if(SWITCH_LOG_FILE_ACTION.equals(action)){  
                new LogCollectorThread().start();  
            }else if(MONITOR_LOG_SIZE_ACTION.equals(action)){  
                checkLogSize();  
            }  
        }  
    }  
      
    class FileComparator implements Comparator<File>{  
        public int compare(File file1, File file2) {  

            String createInfo1 = getFileNameWithoutExtension(file1.getName());  
            String createInfo2 = getFileNameWithoutExtension(file2.getName());  
              
            try {  
                Date create1 = sdf.parse(createInfo1);  
                Date create2 = sdf.parse(createInfo2);  
                if(create1.before(create2)){  
                    return -1;  
                }else{  
                    return 1;  
                }   
            } catch (ParseException e) {  
                return 0;  
            }  
        }  
    }  
      
    @Override  
    public void onDestroy() {  
        //对于通过startForeground启动的service，需要通过stopForeground来取消前台运行状态  
        stopForeground(true);  
//      super.onDestroy();  

        if (process != null) {  
            process.destroy();  
        }  

        unregisterReceiver(logTaskReceiver);  
    }  
}
