package com.dmall.scheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.dmall.scheduler.MainActivity.MESSAGE_CODE;

/**
 * Created by yelong on 2017/3/27.
 * mail:354734713@qq.com
 */
public class JobSchedulerService extends JobService {

    private static final String TAG = JobSchedulerService.class.getSimpleName();

    public static final String MESSENGER_INTENT_KEY
            = BuildConfig.APPLICATION_ID + ".MESSENGER_INTENT_KEY";

    //Handler信使
    private Messenger mMessenger;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMessenger = intent.getParcelableExtra(MESSENGER_INTENT_KEY);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service onDestroy");
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.i(TAG, "on stop job: " + params.getJobId());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendMessage(MESSAGE_CODE, params.getJobId());
                //任务执行之后手动完成
                jobFinished(params, false);
            }
        },3000);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "on stop job: " + params.getJobId());
        return false;
    }

    private void sendMessage(int messageID, @Nullable Object params) {
        if (mMessenger == null) {
            Log.i(TAG, "There's no callback to send a message to.");
            return;
        }
        Message message = Message.obtain();
        message.what = messageID;
        message.obj = params;
        try {
            mMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(TAG, "Error passing service object back to activity.");
        }
    }
}
