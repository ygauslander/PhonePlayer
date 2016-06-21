package com.example.administrator.phoneplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.phoneplayer.R;
import com.example.administrator.phoneplayer.activity.domain.MediaItem;
import com.example.administrator.phoneplayer.activity.utils.Utils;
import com.example.administrator.phoneplayer.activity.view.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SystemVideoPlayerActivity extends Activity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    /**
     * 未能实现：
     * 屏幕放大缩小
     * 声音的手势控制（只能改变一小段）
     */


    //GestrueDector实现点击屏幕监听
    //控制面板的自动显示，隐藏
    //屏幕大小控制***
    //设置不锁屏
    //音量控制：按钮静音，拖动音量条，手势控制声音，手机声音按键控制***
    //换图标，名字
    //支持播放服务器上的视频（服务器没装）***
    //添加进度及网速显示
    //网络视频设置缓冲
    //对播放器设置可以支持的视频类型
    //MedidaPlayer监听卡（拖动卡MediaPlayer.onInfolistener，
    // 本次播放进度和应该的播放进度做对比，判断是否卡顿）
    //perPosition与currentPosition进行对比
    //显示当前网速？在utils中实现
    //配置Vitamio
    //设置切换播放器按钮
    //设置网络视频列表
    //设置缓存数据（在离现时得到已保存数据）

    private WindowManager windowManager;

    private static final int DELAYED_CONTROLLER_HIDDEN_MESSAGE = 2;
    private static final int SCREEN_FULL = 1;
    private static final int SCREEN_DEFULT = 2;
    private final static int VIDEO_START = 1;
    private final static int SHOW_NET_SPEED = 3;
    private GestureDetector detector;
    private VideoView videoView;
    private CheckBox cb_loop;

    private Uri uri;
    private Utils utils;
    private LinearLayout llTop;//上方的控制器
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemtime;
    private Button btnVideoVoice;
    private SeekBar seekbarVoice;
    private Button btnVideoSwichPlayer;
    private LinearLayout llBottom;//下方的控制器
    private TextView tvCurrent;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnVideoExit;
    private Button btnVideoPre;
    private Button btnVideoPlayPause;
    private Button btnVideoNext;
    private Button btnVideoSwichScreen;
    private BatteryStatusReceiver bReceiver;
    private int whichVideo;//当前是哪一个视频
    private ArrayList<MediaItem> datas = new ArrayList<>();//接收到的视频列表
    private MediaItem currentMediaItem;//当前播放的视频对象
    private int currentVideoPosition; //当前视频播放进度
    private int currentVideoVolume;
    private int maxVideoVolume;
    private AudioManager am;
    private boolean isMute;//是否静音

    private boolean isNetVideo;//是否为网络
    private Uri netVideoUri;
    private boolean isSingleLocalVideo;


    private boolean isShowVideoController = false;
    private RelativeLayout fl_control_player;
    private int screenWidth;
    private int screenHeight;
    private TextView tv_netspeed;
    private TextView tv_buffer_speek;
    private boolean isScreenFull = false;
    private Button btn_switch_screen;
    private int videoWidth;
    private int videoHeight;
    private LinearLayout player_loading_bg;


    private float startY;//起始y轴坐标
    private float touchRang;//拖动范围
    private int mVol;//当前音量

    private boolean isBuffing;//视频是否卡了？true，卡了


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case SHOW_NET_SPEED:
                    String speed = utils.getNetSpeed(SystemVideoPlayerActivity.this);
                    tv_buffer_speek.setText(speed);
                    tv_netspeed.setText(speed);
                    removeMessages(SHOW_NET_SPEED);
                    sendEmptyMessageDelayed(SHOW_NET_SPEED, 1000);
                    break;

                case VIDEO_START:
                    if (isNetVideo) {
                        int bufferPercentage = videoView.getBufferPercentage();//0~100
                        int totalBuffer = bufferPercentage * seekbarVideo.getMax();
                        int secondaryProgress = totalBuffer / 100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    }
                    Log.e("TAG", "currentVideoPosition=" + currentVideoPosition);
                    //设置seekbarvideo的进度
                    seekbarVideo.setProgress(currentVideoPosition);
                    //设置当前进度显示文字
                    tvCurrent.setText(utils.stringForTime(seekbarVideo.getProgress()));
                    //设置当前系统时间
                    tvSystemtime.setText(getSystemTime());
                    //设置每秒钟刷新一次
                    handler.removeMessages(VIDEO_START);
                    handler.sendEmptyMessageDelayed(VIDEO_START, 1000);
                    break;

                case DELAYED_CONTROLLER_HIDDEN_MESSAGE:
                    setVideoControllerHide();
                    break;

            }

        }
    };


    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());

    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-06-17 16:07:40 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        player_loading_bg = (LinearLayout) findViewById(R.id.player_loading_bg);
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVideoVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVideoVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        fl_control_player = (RelativeLayout) findViewById(R.id.fl_control_player);
        cb_loop = (CheckBox) findViewById(R.id.cb_isloop);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemtime = (TextView) findViewById(R.id.tv_systemtime);
        btnVideoVoice = (Button) findViewById(R.id.btn_video_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnVideoSwichPlayer = (Button) findViewById(R.id.btn_video_swich_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrent = (TextView) findViewById(R.id.tv_current);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnVideoExit = (Button) findViewById(R.id.btn_video_exit);
        btnVideoPre = (Button) findViewById(R.id.btn_video_pre);
        btnVideoPlayPause = (Button) findViewById(R.id.btn_video_play_pause);
        btnVideoNext = (Button) findViewById(R.id.btn_video_next);
        btnVideoSwichScreen = (Button) findViewById(R.id.btn_video_swich_screen);
        videoView = (VideoView) findViewById(R.id.videoview);
        btn_switch_screen = (Button) findViewById(R.id.btn_video_swich_screen);
        tv_netspeed = (TextView) findViewById(R.id.tv_netspeed);
        tv_buffer_speek = (TextView) findViewById(R.id.tv_buffer_speek);


        btnVideoVoice.setOnClickListener(this);
        btnVideoSwichPlayer.setOnClickListener(this);
        btnVideoExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoPlayPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnVideoSwichScreen.setOnClickListener(this);
        seekbarVideo.setOnSeekBarChangeListener(this);
        //设置监听器
        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorListener(this);
        seekbarVoice.setOnSeekBarChangeListener(this);

        handler.sendEmptyMessage(SHOW_NET_SPEED);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        findViews();
        voiceControl();
        getAndSetData();
        initBufferListener();
        initReceiver();
        //设置系统提供的控制面板
        //     videoView.setMediaController(new MediaController(this));

    }


    private void initBufferListener() {
        //设置系统自带的监听卡，注意最低支持API：17
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        player_loading_bg.setVisibility(View.VISIBLE);
                        isBuffing = true;

                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        player_loading_bg.setVisibility(View.GONE);
                        isBuffing = false;

                        break;
                }
                return true;
            }
        });


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVideoVolume--;
            updateVolumeChange(currentVideoVolume);
            handler.removeMessages(DELAYED_CONTROLLER_HIDDEN_MESSAGE);
            handler.sendEmptyMessageDelayed(DELAYED_CONTROLLER_HIDDEN_MESSAGE, 3000);
            return true;//在这里return true是为了不显示系统自带的声音条

        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVideoVolume++;
            updateVolumeChange(currentVideoVolume);
            handler.removeMessages(DELAYED_CONTROLLER_HIDDEN_MESSAGE);
            handler.sendEmptyMessageDelayed(DELAYED_CONTROLLER_HIDDEN_MESSAGE, 3000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void voiceControl() {
        seekbarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateVolumeChange(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                removeDelayedHiddenMessage();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendDelayedHiddenMessage();

            }
        });
    }

    private void updateVolumeChange(int volume) {
        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekbarVoice.setProgress(0);

        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
            seekbarVoice.setProgress(volume);


        }
        currentVideoVolume = volume;

    }


    private void getAndSetData() {
        utils = new Utils();
        Intent intent = getIntent();
        if (intent != null) {
            //  Log.e("TAG", "uri is null" + ((intent.getData()==null)?"null":"not null"));
            if (intent.getData() == null) {
                whichVideo = intent.getIntExtra("position", 0);
                datas = (ArrayList<MediaItem>) intent.getSerializableExtra("data");
                currentMediaItem = datas.get(whichVideo);
                uri = Uri.parse(currentMediaItem.getData());
            } else {
                //当getdata()不为空时，说明视频是从外部打开的
                Uri data = intent.getData();
                isNetVideo = utils.isNetUrl(data.toString());
                // isNetVideo = utils.isNetUrl("http://localhost:8080/go.rmvb");
                Log.e("TAG", "isNetVideo=" + isNetVideo);
                if (isNetVideo) {
                    netVideoUri = data;
                    uri = null;
                    //不是外部打开的本地视频
                    isSingleLocalVideo = false;

                } else {
                    //是外部打开的本地视频
                    isSingleLocalVideo = true;
                    uri = data;
                    netVideoUri = null;
                }

            }
        }
        if (uri != null) {
            //本地列表视频和外部打开本地视频在这里设置资源
            Log.e("TAG", "uri != null");
            videoView.setVideoURI(uri);
        }
        if (netVideoUri != null) {
            //网络视频资源在这里设置资源
            Log.e("TAG", "netVideoUri != null");
            videoView.setVideoURI(netVideoUri);
        }
        /*
        if(isShowControlPlayer){
            hideControlPlayer();
          }else{
            showControlPlayer();
            sendDelayedHideControlPlayerMessage();
          }
          return super.onSingleTapConfirmed(e);
       }

         */

        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isShowVideoController) {
                    Log.e("TAG", "onSingleTapConfirmed");
                    setVideoControllerHide();
                    handler.removeMessages(DELAYED_CONTROLLER_HIDDEN_MESSAGE);

                } else {
                    setVideoControllerShow();
                    sendDelayedHiddenMessage();
                }

                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.e("TAG", "onDoubleTap");
                setVideoType();
                return super.onDoubleTap(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.e("TAG", "onLongPress");
                if (videoView.isPlaying()) {
                    videoPlayPause();
                } else {
                    videoPlayResume();
                }

                super.onLongPress(e);
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        //设置屏幕不锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }

    private void setVideoType() {
        if (isScreenFull) {
            //默认
            setVideoType(SCREEN_DEFULT);
        } else {
            //全屏
            setVideoType(SCREEN_FULL);
        }
    }

    public void setVideoType(int type) {
        switch (type) {
            case SCREEN_FULL:
                Log.e("TAG", "SCREEN_FULL");
                videoView.setVideoSize(screenWidth, screenHeight);
                isScreenFull = true;
                btn_switch_screen.setBackgroundResource(R.drawable.btn_switch_screen_default_selector);
                break;

            case SCREEN_DEFULT:
                Log.e("TAG", "SCREEN_DEFULT");
                //视频的宽
                int mVideoWidth = videoWidth;
                //视频的高
                int mVideoHeight = videoHeight;
                //屏幕的宽
                int width = screenWidth;
                //屏幕的宽
                int height = screenHeight;
                if (mVideoWidth > 0 && mVideoHeight > 0) {
                    if (mVideoWidth * height > width * mVideoHeight) {
                        //Log.i("@@@", "image too tall, correcting");
                        height = width * mVideoHeight / mVideoWidth;
                    } else if (mVideoWidth * height < width * mVideoHeight) {
                        //Log.i("@@@", "image too wide, correcting");
                        width = height * mVideoWidth / mVideoHeight;
                    } else {
                        //Log.i("@@@", "aspect ratio is correct: " +
                        //width+"/"+height+"="+
                        //mVideoWidth+"/"+mVideoHeight);
                    }
                }

                videoView.setVideoSize(width, height);
                btn_switch_screen.setBackgroundResource(R.drawable.btn_switch_screen_full_selector);
                isScreenFull = false;

                break;
        }

    }


    private void initReceiver() {
        //设置电量状态广播接收器
        bReceiver = new BatteryStatusReceiver();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(bReceiver, intentfilter);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {


        //   finish();
        if (isNetVideo) {
            Toast.makeText(SystemVideoPlayerActivity.this, "播放结束", Toast.LENGTH_SHORT).show();
            finish();

        } else {
            if (isSingleLocalVideo) {
                //外部打开的本地视频
                Toast.makeText(SystemVideoPlayerActivity.this, "播放结束", Toast.LENGTH_SHORT).show();
                finish();


            } else {
                //列表中的视频
                if (cb_loop != null && cb_loop.isChecked()) {

                    if (uri != null) {
                        videoView.setVideoURI(uri);
                    }

                } else {
                    nextVideo();
                }

            }
        }


    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(SystemVideoPlayerActivity.this, "播放出错了哦,extra=" + extra, Toast.LENGTH_LONG).show();
        //视频格式不支持，切换到万能播放器
        //播放过程中有网络异常
        //视频文件有损害
        // -2147483648

        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e("TAG", "onPrepared");
        if (isNetVideo) {
            //资源是网络视频时的准备工作
            //更新缓冲进度
            Log.e("TAG", "onPrepared->isNetVideo=" + isNetVideo);
            int bufferPercentage = videoView.getBufferPercentage();//0~100
            int totalBuffer = bufferPercentage * seekbarVideo.getMax();
            int secondaryProgress = totalBuffer / 100;
            seekbarVideo.setSecondaryProgress(secondaryProgress);
            seekbarVideo.setMax(videoView.getDuration());
            tvDuration.setText(utils.stringForTime(videoView.getDuration()));
            tvName.setText(netVideoUri.toString());

        } else {
            Log.e("TAG", "onPrepared");
            //不是网络视频时的准备工作

            if (isSingleLocalVideo) {
                //是外部打开的本地视频

                tvName.setText(uri.toString());//可以进行截取得到文件名，先不处理
                seekbarVideo.setMax(videoView.getDuration());
                tvDuration.setText(utils.stringForTime(videoView.getDuration()));


            } else {
                //在应用本身列表中打开的本地视频
                //设置视频名字
                tvName.setText(currentMediaItem.getName());
                //设置总时长
                seekbarVideo.setMax((int) currentMediaItem.getDuration());
                //设置当前进度
                tvDuration.setText(utils.stringForTime((int) currentMediaItem.getDuration()));


            }
            seekbarVideo.setSecondaryProgress(0);

        }
        Log.e("TAG", "onPrepared-> videoView.start()");
        //准备好了开始播放视频

        videoView.start();
        setPreAndNextBtn();


        //开始更新进度
        handler.sendEmptyMessage(VIDEO_START);
        handler.sendEmptyMessageDelayed(DELAYED_CONTROLLER_HIDDEN_MESSAGE, 3000);


    }

    //DELAYED_HIDDEN_MESSAGE
    protected void sendDelayedHiddenMessage() {
        handler.sendEmptyMessageDelayed(DELAYED_CONTROLLER_HIDDEN_MESSAGE, 3000);

    }

    protected void removeDelayedHiddenMessage() {
        handler.removeMessages(DELAYED_CONTROLLER_HIDDEN_MESSAGE);

    }


    private void setPreAndNextBtn() {

        //对前一个视频和后一个视频按钮进行设置

        if (isNetVideo) {
            Log.e("TAG", "setPreAndNextBtn->isNetVideo=" + isNetVideo);
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);


        } else {
            Log.e("TAG", "setPreAndNextBtn->isNetVideo=" + isNetVideo);
            if (isSingleLocalVideo) {
                Log.e("TAG", "setPreAndNextBtn->isSingleLocalVideo=" + isSingleLocalVideo);
                btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnVideoPre.setEnabled(false);
                btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnVideoNext.setEnabled(false);

            } else {
                //本应用列表中的视频
                Log.e("TAG", "setPreAndNextBtn->isSingleLocalVideo=" + isSingleLocalVideo);
                setButtonStatus();

            }
        }


    }

    private void setButtonStatus() {
        if (datas != null && datas.size() > 0) {

            if (datas.size() == 1) {
                btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnVideoPre.setEnabled(false);
                btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnVideoNext.setEnabled(false);

            } else if (datas.size() == 2) {


                if (whichVideo == 0) {

                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);

                } else if (whichVideo == 1) {

                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);

                }

            } else if (datas.size() > 2) {

                if (whichVideo == 0) {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);

                } else if (whichVideo == datas.size() - 1) {
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);

                } else {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);

                }

            }


        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnVideoVoice) {
            // Handle clicks for btnVideoVoice
            //是否静音
            isMute = !isMute;
            updateVolumeChange(currentVideoVolume);


        } else if (v == btnVideoSwichPlayer) {
            // Handle clicks for btnVideoSwichPlayer

        } else if (v == btnVideoExit) {
            // Handle clicks for btnVideoExit

            finish();

        } else if (v == btnVideoPre) {
            // Handle clicks for btnVideoPre
            //上一个视频

            previousVideo();

        } else if (v == btnVideoPlayPause) {
            // Handle clicks for btnVideoPlayPause
            if (videoView.isPlaying()) {
                videoPlayPause();
            } else {
                videoPlayResume();
            }

        } else if (v == btnVideoNext) {
            // Handle clicks for btnVideoNext
            //下一个视频

            nextVideo();


        } else if (v == btnVideoSwichScreen) {
            // Handle clicks for btnVideoSwichScreen
            setVideoType();
        }
        handler.removeMessages(DELAYED_CONTROLLER_HIDDEN_MESSAGE);
        handler.sendEmptyMessageDelayed(DELAYED_CONTROLLER_HIDDEN_MESSAGE, 3000);
    }


    private void videoPlayResume() {
        handler.sendEmptyMessage(VIDEO_START);
        videoView.start();
        btnVideoPlayPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
    }

    private void videoPlayPause() {
        handler.removeMessages(VIDEO_START);
        videoView.pause();

        btnVideoPlayPause.setBackgroundResource(R.drawable.btn_video_play_selector);
    }

    private void previousVideo() {
        if (datas != null && datas.size() > 0) {
            whichVideo--;
            if (whichVideo >= 0) {
                currentMediaItem = datas.get(whichVideo);
                tvName.setText(currentMediaItem.getName());
                videoView.setVideoURI(Uri.parse(currentMediaItem.getData()));
                setPreAndNextBtn();
                resetPlayButton();
            }
        }
    }

    private void nextVideo() {
        if (datas != null && datas.size() > 0) {
            whichVideo++;
            if (whichVideo < datas.size()) {
                currentMediaItem = datas.get(whichVideo);
                tvName.setText(currentMediaItem.getName());
                videoView.setVideoURI(Uri.parse(currentMediaItem.getData()));
                setPreAndNextBtn();
                resetPlayButton();
            } else {
                whichVideo = datas.size() - 1;

            }
        }

    }

    private void resetPlayButton() {
        btnVideoPlayPause.setBackgroundResource(R.drawable.btn_video_pause_selector);

    }

    //当进度变化时
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //如果是用户手动拖动的
        if (fromUser) {
            // seekbarVideo.setProgress(progress);
            // tvCurrent.setText(progress);
            videoView.seekTo(progress);
            //声音大小的更新


        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        removeDelayedHiddenMessage();


    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        sendDelayedHiddenMessage();

    }

    class BatteryStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            setBatteryStatus(level);

        }
    }

    protected void setVideoControllerShow() {
        isShowVideoController = true;
        if (fl_control_player != null) {
            fl_control_player.setVisibility(View.VISIBLE);
        }


    }

    protected void setVideoControllerHide() {
        isShowVideoController = false;
        fl_control_player.setVisibility(View.GONE);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //将点击事件传递给GestureDetctor

        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                touchRang = Math.min(screenWidth, screenHeight);
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                handler.removeMessages(DELAYED_CONTROLLER_HIDDEN_MESSAGE);


                break;
            case MotionEvent.ACTION_UP:
                //手指离开屏幕时开始计时,隐藏控制面板

                break;
            case MotionEvent.ACTION_MOVE:
                //2.来到结束的坐标
                float endY = event.getY();
                //3.计算偏移量
                float distanceY = startY - endY;

                //要改变的声音 = (滑动的距离 / 总距离)*最大音量
                float delta = (distanceY / touchRang) * maxVideoVolume;
                //最终声音 = 原来的声音 + 要改变的声音
                float volume = Math.min(Math.max(mVol + delta, 0), maxVideoVolume);
                if (delta != 0) {
                    updateVolumeChange((int) volume);
                }
//                startY = event.getY();
                handler.sendEmptyMessageDelayed(DELAYED_CONTROLLER_HIDDEN_MESSAGE, 3000);


                break;
        }

        return super.onTouchEvent(event);
    }

    //设置电池电量
    private void setBatteryStatus(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }

    }

    @Override
    protected void onDestroy() {
        //在销毁时因该将自定义代码写在上部
        if (bReceiver != null) {
            unregisterReceiver(bReceiver);
            bReceiver = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        super.onDestroy();
    }
}
