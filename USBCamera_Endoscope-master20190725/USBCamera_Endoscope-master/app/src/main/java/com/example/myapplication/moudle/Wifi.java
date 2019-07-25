package com.example.myapplication.moudle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Wifi {
    public native byte[] getResolution();
    public native void iCameraCloseFile();
    public native int iCameraCloseSocket();
    public native void iCameraCloseVoice();
    public native byte[] iCameraGetCmdData();
    public native byte[] iCameraGetFrame();
    public native byte[] iCameraGetOneFrame(int i);
    public native byte[] iCameraGetOneSecond(double d);
    public native int iCameraGetTotalFrame();
    public native double iCameraGetTotalTime();
    public native byte[] iCameraGetVoice(double d);
    public native int iCameraInitSocket();
    public native void iCameraInsertCmdData(byte[] bArr, int i, int i2, byte b);
    public native void iCameraOpenFile(String str);
    public native void iCameraOpenVoice();
    public native void iCameraRecInsertData(byte[] bArr, int i);
    public native void iCameraRecSetParams(int i, int i2, int i3);
    public native void iCameraRecStart(String str);
    public native void iCameraRecStop();
    public native int iCameraServerStart();
    public native void iCameraServerStop();
    public native void iCameraWriteData(byte[] bArr);
    public native int sendChangeName(String str);
    public native int sendChangePassword(String str);
    public native int sendChangeResolution(int i, int i2, int i3);
    public native int sendClearPassword();
    public native int sendReboot();
    static {
        System.loadLibrary("openal");
        System.loadLibrary("icamera");
    }

    private String BSSID = "192.168.10.123";
    private InetAddress Inetaddress = null;
    private Thread startCapture = null;
    public DatagramSocket socket = null;
    public boolean isStop = false;
    public boolean has_connect = false;
    public SurfaceView surfaceView = null;

    public Wifi(SurfaceView surfaceView) {
        this.isStop = false;
        this.surfaceView = surfaceView;
        createSocket(54098);
        startCapture();
    }

    public void createSocket(int port) {
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            this.Inetaddress = InetAddress.getByName(this.BSSID);
        } catch (UnknownHostException e2) {
            e2.printStackTrace();
        }
    }

    private void startCapture() {
        if (this.startCapture == null || !this.startCapture.isAlive()) {
            this.startCapture = new Thread() {
                public void run() {
                    Bitmap bitmap;
                    Wifi.this.isStop = false;
                    int ret = Wifi.this.iCameraServerStart();
                    while (ret <= 0) {
                        ret = Wifi.this.iCameraServerStart();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    while (!Wifi.this.isStop) {
                        byte[] data = Wifi.this.iCameraGetFrame();
                        if (data == null || data.length <= 0) {
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                        } else {
                            Wifi.this.has_connect = true;
                            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            Wifi.this.surfaceView.SetBitmap(bitmap);
                        }
                    }
                }
            };
            this.startCapture.start();
        }
    }
}
