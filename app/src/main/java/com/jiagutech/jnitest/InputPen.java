package com.jiagutech.jnitest;

import java.util.LinkedList;

import android.os.Handler;


public class InputPen {

    /**********************************************************************************/
    private static InputPen instance = null;

    private boolean mStarted = false;

    private final static String TAG = "InputPen";

    //主线程Handler
    private Handler mHandler = null;
    //PenEvent列表
    private LinkedList<PenEvent> mEventList = new LinkedList<PenEvent>();
    //锁
    private Object mListLock = new Object();

    /*******************************************************************/
    //以下定义请参考input_event.h文件
    private final static int EV_SYN = 0x0;
    private final static int EV_KEY = 0x01;

    private final static int EV_ABS = 0x03;


    private final static int  ABS_X = 0x00;
    private final static int  ABS_Y = 0x01;
    private final static int  ABS_PRESSURE = 0x18;
    private final static int  BTN_TOUCH = 0x14a;
    private final static int  BTN_TOOL_PEN = 0x140;
    private final static int  BTN_STYLUS = 0x14b;
    /*******************************************************************/
    //原始的x最大分辨率
    private static final float MAX_X = 15360.0f;
    //屏幕x最大分辨率
    private static final float MAX_X_STANDARD = 1280.0f;

    //原始的y最大分辨率
    private static final float MAX_Y = 9600.0f;
    //屏幕y最大分辨率
    private static final float MAX_Y_STANDARD = 800.0f;

    //原始的最大压力数据
    private static final float MAX_PRESSURE = 1023.0f;
    //Android标准最大压力数据
    private static final float MAX_PRESSURE_STANDARD= 1.0f;

    private int _x=-1,_y=-1,_pressure=-1;
    private int _bintouch = 0, _lastBinTouch = 0;
    //x轴转换系数
    private float xScale = MAX_X_STANDARD / MAX_X;
    //y轴转换系数
    private float yScale = MAX_Y_STANDARD / MAX_Y;
    //压力值转换系统
    private float pScale = MAX_PRESSURE_STANDARD / MAX_PRESSURE;

    //y轴便宜
    private float yOffset = 73.0f;

    /**
     * 加载libinput_pen.so，并初始化回调函数
     */
    static {
        try {
            System.loadLibrary("input_pen");
            class_init_native();


        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }

    }

    private InputPen() {
    }

    /**
     * 单例模式
     * @return
     */
    public static synchronized InputPen getInstance() {
        if (instance == null) {
            instance = new InputPen();
        }
        return instance;
    }

    /**
     * 通知主线程获取PenEvent进行处理
     *
     */
    private void onPenTouch() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(0);
        }

    }

    /**
     * 设置主线程handler
     * @param handler
     */
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    /**
     * 添加PenEvent到list
     * @param event
     */
    private void addPenEvent(PenEvent event) {
        synchronized (mListLock) {
            mEventList.add(event);
        }
    }

    /**
     * 从list获取最旧的PenEvent
     * @return
     */
    public PenEvent getPenEvent() {
        PenEvent event = null;
        synchronized (mListLock) {
            if (mEventList.size() > 0) {
                event = mEventList.removeFirst();
            }
        }

        return event;
    }


    /*******************************************************************/
    //public method

    /**
     * 坐标转换，并生成PenEvent数据
     * @param event
     */
    protected void transform(PenEvent event) {
        float x = MAX_Y_STANDARD - ((float)_y) * yScale;
        float y = (float)_x * xScale - yOffset;
        float p = (float)_pressure * pScale;
        event.setX(x);
        event.setY(y);
        event.setPressure(p);
    }

    /**
     * 处理input_event数据
     */
    protected void processEvent() {

        if (_bintouch != _lastBinTouch ) {
            _lastBinTouch = _bintouch;
            //Log.d(TAG, String.format("x=%d,y=%d,pressure=%d,bintouch=%d", _x,_y,_pressure,_bintouch));
            if (_bintouch == 1) { //down事件
                PenEvent event = new PenEvent();
                event.setAction(PenEvent.ACTION_DOWN);
                transform(event);
                addPenEvent(event);
                onPenTouch();

            } else { //up事件
                PenEvent event = new PenEvent();
                event.setAction(PenEvent.ACTION_UP);
                transform(event);
                addPenEvent(event);
                onPenTouch();
            }
        } else if (_bintouch == 1) { //move事件
            PenEvent event = new PenEvent();
            event.setAction(PenEvent.ACTION_MOVE);
            transform(event);
            addPenEvent(event);
            onPenTouch();

        }




    }

    /**
     * 获取input_event数据，由jni层调用此函数
     * @param type
     * @param code
     * @param value
     */
    protected void getEvent(int type, int code, int value) {
        switch (type) {

            case EV_SYN:
                processEvent();
                break;

            case EV_KEY:
                if (code == BTN_TOUCH) {
                    _bintouch = value;
                }
                break;
            case EV_ABS:
                if (code == ABS_X) {
                    _x = value;
                } else if (code == ABS_Y) {
                    _y = value;
                } else if (code == ABS_PRESSURE) {
                    _pressure = value;
                }
                break;
            default:
                break;
        }
    }

    /**
     * 启动线程
     */
    protected void start() {
        if (!mStarted) {
            if (native_input_pen_init()) {
                mStarted = true;
            }

        }


    }

    /**
     * 停止线程
     */
    protected void stop() {
        if (mStarted) {
            native_input_pen_exit();
            mStarted = false;
        }

    }

    public void dispose() {
        stop();
    }

    @Override
    protected void finalize() throws Throwable {

        stop();
        // TODO Auto-generated method stub
        super.finalize();
    }


    /*******************************************************************/
    //native method
    protected  static native void class_init_native();

    protected  native boolean native_input_pen_init();

    protected  native void native_input_pen_exit();
}