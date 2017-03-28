package com.dmall.scheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import static com.dmall.scheduler.JobSchedulerService.MESSENGER_INTENT_KEY;

public class MainActivity extends AppCompatActivity {

    private JobScheduler mJobScheduler;

    private int mJobId = 0;

    public static final int MESSAGE_CODE = 1000;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            if (msg.what == MESSAGE_CODE) {
                Toast.makeText(getApplicationContext(), "JobService task running", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, JobSchedulerService.class);
        Messenger messenger = new Messenger(mHandler);
        intent.putExtra(MESSENGER_INTENT_KEY, messenger);
        startService(intent);
    }

    public void createJob(View v) {
        mJobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        //创建定时任务
        JobInfo.Builder builder = new JobInfo.Builder(mJobId++,
                new ComponentName(this, JobSchedulerService.class));
        // 这个方法告诉系统这个任务每个三秒运行一次
        //builder.setPeriodic(3000);
        // 这个函数能让你设置任务的延迟执行时间(单位是毫秒)
        builder.setMinimumLatency(3000);
        // 这个方法设置任务最晚的延迟时间,到了时间其他条件还没有满足,任务也会被启动
        builder.setOverrideDeadline(3000);
        // 这个方法告诉系统只有在满足指定的网络条件时这个任务才会被执行
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        // 这个方法告诉系统当你的设备重启之后任务是否还要继续执行
        builder.setPersisted(true);
        // 这个方法告诉系统只有当设备在充电时这个任务才会被执行
        builder.setRequiresCharging(false);
        // 这个方法告诉系统只有当用户没有在使用该设备且有一段时间没有使用时才会启动该任务。
        builder.setRequiresDeviceIdle(false);
        // 设置退避、重试策略
        builder.setBackoffCriteria(3000, JobInfo.BACKOFF_POLICY_LINEAR);

        if (mJobScheduler.schedule(builder.build()) == JobScheduler.RESULT_FAILURE) {
            mJobScheduler.cancel(mJobId);
            //mJobScheduler.cancelAll();
        }
    }

    public void finishJob(View v) {
        List<JobInfo> allPendingJobs = mJobScheduler.getAllPendingJobs();
        if (allPendingJobs.size() > 0) {
            // Finish the last one
            int jobId = allPendingJobs.get(0).getId();
            mJobScheduler.cancel(jobId);
            Toast.makeText(MainActivity.this, "cancel jodId == " + jobId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "no job can cancel", Toast.LENGTH_SHORT).show();
        }
    }
}
