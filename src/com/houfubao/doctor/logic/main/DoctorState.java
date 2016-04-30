package com.houfubao.doctor.logic.main;

import java.io.File;

import com.houfubao.doctor.logic.db.DoctorDBProxy;
import com.houfubao.doctor.logic.online.QuestionManager;
import com.houfubao.doctor.logic.online.QuestionManagerImpl;
import com.houfubao.doctor.logic.online.RequestorLeanClound;
import com.houfubao.doctor.logic.utils.LogService;
import com.houfubao.doctor.logic.utils.QLog;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;

public class DoctorState {
	private static final String TAG = "DoctorState";
	private static Context sContext;
	private static DoctorState mInstance;
	//
	private DoctorDBProxy mdb;
	private QuestionManagerImpl mQuestionManager;
	private RequestorLeanClound mRequestor;
	//
	private HandlerThread mWorkThread;
	
	//网络连接统一注册，然后接口回调
	public static interface NetworkStateChanged {
		 public void onNetworkStateChanged(boolean isConnected);
	 }
	
	public static synchronized DoctorState getInstance() {

		if (mInstance == null) {
			mInstance = new DoctorState();
			mInstance.init(sContext);
			if (DoctorConst.DEBUG) {
				Intent i = new Intent(sContext, LogService.class);
				sContext.startService(i);
			}
		}

		return mInstance;
	}


	public static void setApplicationContext(Context context) {
		if (sContext != null) {
			QLog.i(TAG, "setApplicationContext error, twice");
		}
		sContext = context.getApplicationContext();
		QLog.e(TAG, "setApplicationContext error, twice" + context + "  " + sContext);
	}
	
	public QuestionManager getQuestionManager() {
		return mQuestionManager;
	}
	
	private DoctorState() {
		if (sContext==null) {
			throw new IllegalStateException("context is null");
		}
	}
	
	private void init(Context sContext2) {
		
		mWorkThread = new HandlerThread("AppStoreWorkThread");
        mWorkThread.start();
        
		//1、db 
		mdb = new DoctorDBProxy();

		//2. LeanCloud Manager
		mRequestor = new RequestorLeanClound();
		
		
		//3. monline
		mQuestionManager = new QuestionManagerImpl(mRequestor);
		
		//
		mRequestor.init(sContext2);
		mdb.init(mWorkThread.getLooper(), sContext2);
		mQuestionManager.init(sContext2);
		
		
		//10.
		loadImageLoaderConfig(sContext2);
	}
	
	public void onTerminate() {
		mQuestionManager.terminate();
	}
	
	
	/**
	 * 在线图片下载的配置
	 * 
	 * @param context
	 *            传递上下文
	 */
	private final static int MaxFreqMemorySizeLimit = 32 * 1024 * 1024;// 最大内存缓存50M
	private final static int MaxDiskCacheSize = 64 * 1024 * 1024; // 最大 硬盘缓存500M
	private final static int ConnectTimeout = 5 * 1000;// 默认连接超时30s
	private final static int ReadTimeout = 30 * 1000;// 默认连接读取超时120s
	private final static int MaxCacheFileCount = 500;// 最大缓存文件数
	private final static int ThreadPoolSize = 3;// 线程池个数
	private final static int ThreadPriority = Thread.NORM_PRIORITY - 2;// 线程优先级

	
	private void loadImageLoaderConfig(Context context) {
		// 使用内存资源
		DisplayImageOptions options = new DisplayImageOptions.Builder()
//				.showImageOnLoading(R.drawable.default_pic) // 加载图片时的图片
				.cacheInMemory(true) // 启用内存缓存
				.cacheOnDisk(true) // 启用外存缓存
				.build();

		// 设置缓存目录
		File cacheDir = StorageUtils.getOwnCacheDirectory(
				sContext.getApplicationContext(), ".imageloader/Cache");
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				// .memoryCacheExtraOptions(MaxImageWidthForMemoryCache,
				// MaxImageHeightForMemoryCache)
				.threadPoolSize(ThreadPoolSize)
				// 线程池内加载的数量
				.threadPriority(ThreadPriority)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(
						new UsingFreqLimitedMemoryCache(MaxFreqMemorySizeLimit))
				.diskCacheSize(MaxDiskCacheSize)
				// 将保存的时候的URI名称用MD5 加密
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				// 缓存的文件数量
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.diskCacheFileCount(MaxCacheFileCount)
				.diskCache(new UnlimitedDiscCache(cacheDir))
				// 自定义缓存路径
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.defaultDisplayImageOptions(options)
				.imageDownloader(
						new BaseImageDownloader(context, ConnectTimeout,
								ReadTimeout)).writeDebugLogs() // Remove for
																// release app
				.build();// 开始构建
		ImageLoader.getInstance().init(config);
	}

}
