package com.jiagutech.jnitest;

public class PenEvent {


    public PenEvent() {

    }

    public final static int ACTION_DOWN = 0x0;
    public final static int ACTION_UP = 0x1;
    public final static int ACTION_MOVE = 0x2;

    //表示事件类型，down/up/move
    private int action;
    //x轴坐标
    private float x;
    //y轴坐标
    private float y;
    //压力数据
    private float pressure;

    public void setAction(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }


    public void setX(float x) {
        this.x = x;
    }

    public float getX() {
        return x;
    }


    public void setY(float y) {
        this.y = y;
    }

    public float getY() {
        return y;
    }

    public void setPressure(float p) {
        this.pressure = p;
    }

    public float getPressure() {
        return pressure;
    }



}

