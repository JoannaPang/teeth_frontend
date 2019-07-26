package com.example.myapplication;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.core.content.FileProvider;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@SuppressLint("NewApi")
public class MainActivity extends Activity{
    public static String LocalHost = "http://115.236.52.123";
    //public static String LocalHost = "http://219.224.167.188";//个人电脑后台IP
    private static String TestLog = "TestLog";
    private static String YAYA_PATH = "DCIM/SOAY";
    public static String BACK_PATH = "yaya/DCIM/BACK";

    public static String BACK_DATA_PATH = "yaya/DCIM/BACK/data";
    private static String BACK_TMP_PATH = "yaya/DCIM/BACK/data/thumb";
    private static String BACK_DIAGNO_PATH = "yaya/DCIM/BACK/data/diagno";
    private static String MY_PATH = "DCIM/DoctorT";//pang_add_importance

    public static int port = 9080;
    //public static int port = 5000;//个人电脑后台端口号



    private boolean isLogin = false;

    private static File photo;
    private static final int LOGININTENT = 200;
    private static final int REGISINTENT = 300;
    private static final int WATCHINTENT = 400;
    private static final int SENDPICINTENT = 500;
    private static final int CUTPICINTENT = 600;

    private static final int NOTLOGIN = 201;

    private ImageView ivImage;
    private Uri teethResultPath;

    private long exitTime = 0;

    private void permissionGen() {
        PermissionGen.with(MainActivity.this)
                .addRequestCode(200)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(launcherIntent);
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            //moveTaskToBack(true);
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(),
                    "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        }
        else{
                finish();
                System.exit(0);
            }
    }


    @PermissionSuccess(requestCode = 200)
    public void doSomething(){
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
    }
    @PermissionFail(requestCode = 200)
    public void doFailSomething(){
        Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dc);
        Log.d(TestLog, "Main Activity");
        permissionGen();

        isLogin = false;
        ivImage = (ImageView) findViewById(R.id.ivImage);
        ivImage.setVisibility(View.INVISIBLE);


        autoSavedImage();

        // 设置服务器地址及端口号（功能暂时不需要）
        Log.d(TestLog,"lalallalal0");
        /*
        Button btn_SetPort = (Button)findViewById(R.id.tabbutton_set);
        btn_SetPort.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TestLog, "dialog button listen");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                final View changeServerView = factory.inflate(R.layout.change_server, null);

                final EditText inputServer = (EditText) changeServerView.findViewById(R.id.text_server);
                final EditText inputPort = (EditText) changeServerView.findViewById(R.id.text_port);

                Log.d(TestLog, "init var");

                inputServer.setHint(LocalHost);
                inputPort.setHint("" + port);

                Log.d(TestLog, "init hint over");

                builder.setTitle("修改服务器信息");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setView(changeServerView);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String iServer = inputServer.getText().toString();
                        String iPort = inputPort.getText().toString();

                        Log.d(TestLog, "the change :" + iServer + "/" + iPort);

                        // 合法性审查
                        if(inputCheckServer(iServer)) {
                            Log.d(TestLog, "change Server");
                            LocalHost = iServer;
//							outServer.setText("当前服务器：" + iServer);
                        }
                        int inputPort;
                        try {
                            if((inputPort = inputCheckPort(iPort)) != -1) {
                                Log.d(TestLog, "change Port");
                                port = inputPort;
//								outPort.setText(out);
                            }
                        }catch(Exception e) {
                            Log.d(TestLog, e.getMessage());
                        }
                        Log.d(TestLog, "After the change :" + LocalHost + "/" + port);
                    }
                    private boolean inputCheckServer(String iServer) {
                        // 参考资料https://blog.csdn.net/chaiqunxing51/article/details/50975961/
                        if(iServer == null || iServer.length() == 0) { // 基础检验
                            return false;
                        }
                        String[] parts = iServer.split("\\.");
                        if(parts.length != 4) { // 四段ip设置
                            return false;
                        }
                        for(int i = 0; i < 4; i++) {
                            try {
                                int n = Integer.parseInt(parts[i]);
                                if(n< 0 || n > 255) return false; // ip数检验
                            }catch(NumberFormatException e) {
                                return false; // 非法字符检验
                            }
                        }
                        return true;
                    }
                    private int inputCheckPort(String iPort) {
                        try {
                            int port = Integer.parseInt(iPort);
                            if(1024 < port && port < 65535) {
                                return port;
                            }
                        }catch(NumberFormatException e) {
                        }
                        return -1;
                    }
                    private int inputCheckColor(String iColor) {
                        try {
                            String regex="^#[A-Fa-f0-9]{6}$";
                            if(iColor.matches(regex)) {
                                return 0;
                            }
                        }catch(Exception e) {
                        }
                        return -1;
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });
        */

        // 查看相册功能
        /*
        Button watchButton = (Button) findViewById(R.id.tabbutton_see);
        watchButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.d(TestLog, "View the Album");

                File file = preCreateDir(YAYA_PATH);

                scanDir(MainActivity.this, file.getAbsolutePath());

                File backDir = preCreateDir(BACK_PATH);
                scanDir(MainActivity.this, backDir.getAbsolutePath());

                Log.d(TestLog, "Send Broadcast");

                String intentact = "";
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {//4.4版本前
                    intentact = Intent.ACTION_PICK;
                } else {//4.4版本后
                intentact = Intent.ACTION_GET_CONTENT;
                }
                Intent intent = new Intent(intentact);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                Log.d(TestLog, "Look the Album");
                startActivityForResult(intent, WATCHINTENT);
            }
        });
        */

        //[P]主界面拍照按钮
        //Button cameraButton = (Button) findViewById(R.id.tabbutton_take);
        ImageView cameraButton = (ImageView) findViewById(R.id.tabbutton_take);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TestLog, "Took photo");

                Log.d(TestLog, "Check If App is installed");
                if(!CallYaYa.checkYaYaExist(MainActivity.this)){
                    // 未安装YaYa APP - 提示及跳转下载页面
                    //showYayaNoticeDialog(); //为了在虚拟机上进行测试//pang_add_20190620

                    Log.d(TestLog, "YaYa App is not installed");
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogStyle_normal);
//                    builder.setTitle("出错误啦");
                    builder.setMessage("未下载辅助APP！");
                    builder.setIcon(android.R.drawable.ic_dialog_info);
                    builder.setNegativeButton("点击下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            // 跳转到下载页面
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse("https://android.myapp.com/myapp/detail.htm?apkName=com.wifidevice.coantec.activity#");
                            intent.setData(content_url);
                            startActivity(intent);
                        }
                    });
                    builder.setPositiveButton("稍后再说", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    return;
                }else{
                    Log.d(TestLog, "YaYa App has been installed successfully!");
                    showYayaNoticeDialog(); //pang_add_20190620
                    return;
                }

            }
        });

        //[E]主界面牙菌斑检测功能
        //Button sendPic = (Button) findViewById(R.id.tabbutton_update);
        ImageView sendPic = (ImageView) findViewById(R.id.tabbutton_update);
        sendPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showNoticeDialog(); //在上传之前增加提示信息//pang_fix_20190620
            }
        });

        //[H]主界面查看历史记录功能
        ImageView checkHistory = (ImageView) findViewById(R.id.img_tab_history);
        checkHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, CheckHistoryActivity.class);
                startActivity(intent);
            }
        });
        // 查看图片完毕//触摸图片，把ImageViewer设置为不可见（等待更好的设计）
        /*ImageView imageRecover = (ImageView)findViewById(R.id.ivImage);
        imageRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivImage.setVisibility(View.INVISIBLE);
            }
        });*/
    }

    //onCreate函数结束

    //自动保存五张图片到系统内存
    public void autoSavedImage(){

        File myfile1 = preCreateDir(MY_PATH);

        //NO.1
        Resources res1 = this.getResources();
        BitmapDrawable d1 = (BitmapDrawable) res1.getDrawable(R.drawable.my_ic_launcher1);
        Bitmap img1 = d1.getBitmap();

        String fn1 = "/image_test1.png";
        //String path = this.getFilesDir() + File.separator + fn;
        String fn_path1 = myfile1.getAbsolutePath()+fn1;
        System.out.println(fn_path1);


        //NO.2
        Resources res2 = this.getResources();
        BitmapDrawable d2 = (BitmapDrawable) res2.getDrawable(R.drawable.my_ic_launcher2);
        Bitmap img2 = d2.getBitmap();

        String fn2 = "/image_test2.png";
        //String path = this.getFilesDir() + File.separator + fn;
        String fn_path2 = myfile1.getAbsolutePath()+fn2;
        System.out.println(fn_path2);

        //NO.3
        Resources res3 = this.getResources();
        BitmapDrawable d3 = (BitmapDrawable) res3.getDrawable(R.drawable.my_ic_launcher3);
        Bitmap img3 = d3.getBitmap();

        String fn3 = "/image_test3.png";
        //String path = this.getFilesDir() + File.separator + fn;
        String fn_path3 = myfile1.getAbsolutePath()+fn3;
        System.out.println(fn_path3);

        //NO.4
        Resources res4 = this.getResources();
        BitmapDrawable d4 = (BitmapDrawable) res4.getDrawable(R.drawable.my_ic_launcher4);
        Bitmap img4 = d4.getBitmap();

        String fn4 = "/image_test4.png";
        //String path = this.getFilesDir() + File.separator + fn;
        String fn_path4 = myfile1.getAbsolutePath()+fn4;
        System.out.println(fn_path4);

        //NO.5
        Resources res5 = this.getResources();
        BitmapDrawable d5 = (BitmapDrawable) res5.getDrawable(R.drawable.my_ic_launcher5);
        Bitmap img5 = d5.getBitmap();

        String fn5 = "/image_test5.png";
        //String path = this.getFilesDir() + File.separator + fn;
        String fn_path5 = myfile1.getAbsolutePath()+fn5;
        System.out.println(fn_path5);


        try{
            //NO.1
            OutputStream os1 = new FileOutputStream(fn_path1);
            img1.compress(Bitmap.CompressFormat.PNG, 100, os1);
            os1.close();

            //NO.2
            OutputStream os2 = new FileOutputStream(fn_path2);
            img2.compress(Bitmap.CompressFormat.PNG, 100, os2);
            os2.close();


            //NO.3
            OutputStream os3 = new FileOutputStream(fn_path3);
            img3.compress(Bitmap.CompressFormat.PNG, 100, os3);
            os3.close();


            //NO.4
            OutputStream os4 = new FileOutputStream(fn_path4);
            img4.compress(Bitmap.CompressFormat.PNG, 100, os4);
            os4.close();

            //NO.5
            OutputStream os5 = new FileOutputStream(fn_path5);
            img5.compress(Bitmap.CompressFormat.PNG, 100, os5);
            os5.close();

        }catch(Exception e){
            Log.e("TAG", "", e);
        }

        //important_flush
        scanDir(MainActivity.this, myfile1.getAbsolutePath()); //pang_add
        return ;
    }

    //在系统内存中创建路径
    private File preCreateDir(String path){
        if (Build.VERSION.SDK_INT >= 26 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }//针对不同的SDK申请权限

        File dir = new File(Environment.getExternalStorageDirectory(), path);
        Log.d(TestLog, "dir.exists is:" + dir);
        if(dir.exists() && dir.isFile()) {
            dir.delete();
            Log.d(TestLog, "dir.delete:" + dir);
        }
        if(!dir.exists()) {
            Log.d(TestLog, "dir.exists");
            if(dir.mkdirs()) {
                Log.d(TestLog, "pre Create Dir:" + dir.getAbsolutePath());
            }
        }
        else {
            Log.d(TestLog, "dir.exists");
        }
        return dir;
    }

    //[P_dialog]点击拍照按钮触发，弹出拍照前提示信息
    //pang_add_20190620
    private void showYayaNoticeDialog() {
        Dialog callYayaInfoDialog = new Dialog(this, R.style.MyDialogStyle_normal);
        callYayaInfoDialog.setCanceledOnTouchOutside(true); // 点击加载框以外的区域
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.call_yaya_info, null);
        Button cancel = v.findViewById(R.id.dialog_cancel);
        Button submit = v.findViewById(R.id.dialog_submit);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callYayaInfoDialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callYayaInfoDialog.dismiss();
                //确定后跳转APP进行拍照
                Intent intent = new Intent();
                //包名 包名+类名（全路径）
                ComponentName comp = new ComponentName("com.wifidevice.coantec.activity","com.methnm.coantec.activity.MainActivity");
                intent.setComponent(comp);
                intent.setAction("android.intent.action.MAIN");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("data", "123");
                startActivity(intent);
            }
        });
        callYayaInfoDialog.setContentView(v);
        callYayaInfoDialog.show();
    }

    //[E_dialog]点击上传按钮触发，弹出拍照前提示信息
    //pang_add_20190620
    private void showNoticeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyDialogStyle_normal);
//        builder.setTitle("上传提示");
        builder.setMessage("请选择内窥镜牙齿图片进行上传");
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //确定后进行浏览相册
                goForPicture();
            }
        });
        builder.show();
    }

    //[E]上传时浏览相册功能
    // pang_add_20190620
    private void goForPicture() {
        File dir = preCreateDir(YAYA_PATH);
        // 先刷新后选择
        scanDir(MainActivity.this, dir.getAbsolutePath());

        String intentact = "";
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {//4.4版本前
            intentact = Intent.ACTION_PICK;
        } else {//4.4版本后
            intentact = Intent.ACTION_GET_CONTENT;
        }
        Intent intent = new Intent(intentact);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, SENDPICINTENT);
    }


    //生命周期中的函数，回调函数
    @Override
    protected void onActivityResult(int requestCode, int result, Intent data) {
        super.onActivityResult(requestCode,result,data);
        Log.d(TestLog, "requeseCode = " + requestCode);
        switch(requestCode){
            //查看相册功能（暂时不需要）
            case WATCHINTENT:
                Log.d(TestLog, "show select pic:");
                // 显示图片
                if(data == null){
                    Log.d(TestLog, "Select Pic Cancel");
                    return;
                }

                Uri uri = data.getData();
                Log.d(TestLog, "显示相册中的大图");
                ivImage.setImageURI(uri);
                ivImage.setVisibility(View.VISIBLE);
                break;
            //[E]牙菌斑检测剪裁功能
            case SENDPICINTENT:
                Log.d(TestLog, "SEND PIC INTENT 1");
                // 查看已有相册图片 -> 建立连接发送图片 -> 接收图片
                String sendPath = null;
                if(data != null){
                    Toast.makeText(getApplicationContext(), "请选择感兴趣区域", Toast.LENGTH_SHORT).show();

                    Uri dataUri = data.getData();
                    if(dataUri.toString().indexOf("%2F") != -1)//增加判断，确认uri的格式 20190722
                    {
                        dataUri = UriDeal.getFileUri(MainActivity.this, dataUri);
                    }
                    Log.d(TestLog, "img uri " + dataUri);
                    sendPath = UriDeal.getFilePathByUri(dataUri, MainActivity.this);
                    Log.d(TestLog, "img path " + sendPath);
                    if(sendPath == null){
                        Log.d(TestLog, "illegal img path : null");
                        return;
                    }
                    cropPic(sendPath);
                }
                else{
                    Toast.makeText(getApplicationContext(), "返回主界面成功", Toast.LENGTH_SHORT).show();
                    //增加该句话之后可以实现正常返回
                    Log.d(TestLog,"*****************未上传正常返回主界面********************");
                }
                break;
            //[E] 牙菌斑检测图像上传功能
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                Log.d(TestLog, "SEND PIC INTENT 2");
                if(!(CropImage.getActivityResult(data)== null)) {
                    CropImage.ActivityResult cropResult = CropImage.getActivityResult(data);

                    Uri resultUri = cropResult.getUri();
                    if (resultUri == null) //增加判断，防止部分机型闪退  20190722
                    {
                        return;
                    }
                    if(data != null){
                        Log.d(TestLog, "data not null");
                        ivImage.setImageURI(resultUri);
                        Log.d(TestLog, "resultUri : " + resultUri);
                        ivImage.setVisibility(View.VISIBLE);
                    }
                    String cropResultPath = UriDeal.getFilePathByUri(resultUri, com.example.myapplication.MainActivity.this);
                    scanFile(com.example.myapplication.MainActivity.this, cropResultPath);
                    Log.d(TestLog, "UPDATE CUT PIC");
                    Log.d(TestLog, cropResultPath);
                    // 发送图片
                    Thread sendPhoto = new Thread(new com.example.myapplication.MainActivity.SocketSendGetThread(cropResultPath));
                    sendPhoto.start();
                }
                else{
                    Toast.makeText(getApplicationContext(), "请重新上传选择图片", Toast.LENGTH_SHORT).show();
                }
                break;
            //获得返回的分析结果
            case 123:
                ivImage.setImageURI(teethResultPath);
                Log.d(TestLog, "resultUri : " + teethResultPath);
                ivImage.setVisibility(View.VISIBLE);
                break;
        }
    }


    //?????
    public static void setPhoto(File file){
        photo = file;
    }

    public final int CROP_PHOTO = 10;
    public final int ACTION_TAKE_PHOTO = 20;


     //获取本应用在系统的存储目录
    public static String getAppFile(Context context, String uniqueName) {
        String cachePath;
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
                && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getParent();
        } else {
            cachePath = context.getCacheDir().getParent();
        }
        return cachePath + File.separator + uniqueName;
    }

    /**
     * 跳转到系统裁剪图片页面
     * @param imagePath 需要裁剪的图片路径
     */
    //[E]牙菌斑检测剪裁函数
    private void cropPic(String imagePath) {
        Log.d(TestLog, "imagePath: " + imagePath);
        File file = new File(imagePath);

        Uri contentUri = null;
        contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".uriprovider", file);

        Log.d(TestLog, "SEND PIC INTENT 1");
        CropImage.activity(contentUri)
                .setActivityTitle("牙齿剪裁")
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setCropMenuCropButtonTitle("Done")
                .setRequestedSize(700,700)
                .setCropMenuCropButtonTitle("确定")
                .start(this);
    }



    //Alert窗口定义
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 0) {
                Log.d(TestLog, "分析结束");
                Bitmap bitmap = (Bitmap) msg.obj;
                ivImage.setImageBitmap(bitmap);
                ivImage.setVisibility(View.VISIBLE);

                //pang_add_20190619
                ImageView imageRecover_pang = (ImageView)findViewById(R.id.ivImage);
                imageRecover_pang.setOnClickListener(new View.OnClickListener() {
                    boolean isflag = true; //设置只可以点击一次
                    @Override
                    public void onClick(View view) {
                        if (isflag) {
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, CheckDetailsActivity.class);
                            startActivity(intent);
                            isflag = false;
                        } else {
                            Log.d(TestLog, "already checked the details");
                            imageRecover_pang.setVisibility(View.INVISIBLE); //点击查看一次之后即设为不可见
                        }
                    }
                });
            }
            else if(msg.what <= 5){
               /* AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyDialogStyle_normal);
                String congraText = "";
                if(msg.what == 1) congraText = "注册成功";
                else if(msg.what == 2) {
                    congraText = "登录成功";
                    isLogin = true;
                }
                else if(msg.what == 3) {
                    congraText = "上传成功, 正在分析";
                    isLogin = true;
                }
                else if(msg.what == 5) {
                    congraText = "分析结束，点击结果图片可查看详细结果"; //pang_fix_20190619
                    isLogin = true;
                }
                builder.setTitle("恭喜！") ;
                builder.setMessage(congraText);
                builder.setPositiveButton("确定",null );
                builder.show();*/
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogStyle_normal);
//                builder.setTitle("出错误啦！") ;
                String errorText = "";
                if(msg.what == 1) errorText = "用户名不存在";
                else if(msg.what == 2) errorText = "用户名已被注册";
                else if(msg.what == 3) errorText = "未识别到本机摄像头";
                else if(msg.what == 404) errorText = "与服务器" + LocalHost + ":" + port + "连接失败；请重新上传照片";
                builder.setMessage(errorText);
                builder.setPositiveButton("确定",null );
                builder.show();
            }
        }
    };

    //?????
    public class LoggingInterceptor implements Interceptor{
        @Override
        public Response intercept(Chain chain) throws IOException{
            Request request = chain.request();
            long t1 = System.nanoTime();
            Log.d(TestLog, String.format("send %s on %s%n%s", request.url(),
                    chain.connection(), request.headers()));
            Response response = chain.proceed(request);
            long t2 = System.nanoTime();
            ResponseBody responseBody = response.peekBody(1024 * 1024);
            Log.d(TestLog, String.format("rec：[%s] %nreturn json:%s  %.1fms%n%s",
                    response.request().url(),
                    responseBody.string(),
                    (t2-t1) /1e6d,
                    response.headers()
            ));
            return response;
        }
    }

    public class HttpLogger implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            Log.d("HttpLogInfo", message);//okHttp的详细日志会打印出来
        }
    }

    // 通过Socket进行图片收发
    public class SocketSendGetThread implements Runnable{
        private File file;
        private String filePath;
        private String resultPath;
        private String Username = LoginActivity.userName;
        public SocketSendGetThread(String filePath) {
            this.filePath = filePath;
        }
        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            String date = String.valueOf(calendar.get(Calendar.YEAR)) + "."
                    + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "."
                    + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            file = new File(filePath);
            resultPath = filePath + "_result.jpg";
            String fileName = file.getName();
            Log.d(TestLog, "OkhttpSendImg");
            Log.d(TestLog, "filename : " + fileName);
            android.os.Message message = Message.obtain();
            message.obj = null;
            OkHttpClient client = new OkHttpClient.Builder()
                    .callTimeout(25, TimeUnit.SECONDS)
                    .connectTimeout(25, TimeUnit.SECONDS)
                    .readTimeout(25, TimeUnit.SECONDS)
                    .writeTimeout(25, TimeUnit.SECONDS)
                    .addNetworkInterceptor(logInterceptor)
                    .build();
            String fileType = getMimeType(fileName);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("username", Username)
//                    .addFormDataPart("submit", "Upload")
                    .addFormDataPart("file", fileName,
                            RequestBody.create(MediaType.parse(fileType), file))
                    .addFormDataPart("Username", Username)
                    .addFormDataPart("Date", date)
                    .addFormDataPart("Type", fileType)
                    .addFormDataPart("submit", "Upload")
                    .build();

            Request request = new Request.Builder()
                    .url(LocalHost + ":" + port + "/upload")
                    .post(requestBody)
                    .build();
            try{
                Call call = client.newCall(request);
                message.what = 3;
                handler.sendMessage(message);
                Response response = call.execute();
                if(!response.isSuccessful()) throw new IOException("Unexpected code");

//                Log.d(TestLog, "response " + response.body().string());
                ResponseBody body = response.body();
                long contentLength = body.contentLength();
                Log.d(TestLog, "contentLength" + String.valueOf(contentLength));
                if(contentLength <= 25){
                    Log.d(TestLog, "Login Thread - Illegal type!");
                    // 保存信息
                    android.os.Message messageE = Message.obtain();
                    messageE.obj = null;
                    messageE.what = 1;
                    handler.sendMessage(messageE);
//                    response.close();
                    return;
                }else{
                    Log.d(TestLog, "save");
                    Bitmap bitmap = parseJsonWithJsonObject(body);
                    android.os.Message messageRW = Message.obtain();
                    messageRW.obj = null;
                    messageRW.what = 5;
                    handler.sendMessage(messageRW);
                    Log.d(TestLog, "message is ok");
                    android.os.Message messageR = Message.obtain();
                    messageR.obj = bitmap;
                    messageR.what = 0;
                    handler.sendMessage(messageR);
                    Log.d(TestLog, "message is ok");
                    return;
                }

            }catch (IOException e){
                android.os.Message messageError = Message.obtain();
                messageError.obj = null;
                messageError.what = 404;
                handler.sendMessage(messageError);
                Log.d(TestLog, "catch error:" + e.getMessage() + request.url());
            }
        }

        private String getMimeType(String filename) {
            FileNameMap filenameMap = URLConnection.getFileNameMap();
            String contentType = filenameMap.getContentTypeFor(filename);
            if (contentType == null) {
                contentType = "application/octet-stream"; //* exe,所有的可执行程序
            }
            return contentType;
        }
        private Bitmap parseJsonWithJsonObject(ResponseBody body) throws IOException{
            String responseData = body.string();
            try{
                // 定位输出路径
                File dir = preCreateDir(BACK_PATH);
                File dir_data = preCreateDir(BACK_DATA_PATH);
                File dir_thumb = preCreateDir(BACK_TMP_PATH);
                File dir_diagno = preCreateDir(BACK_DIAGNO_PATH);
                // 使用时间作为输出
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String timePath =  dateFormat.format(date);
                String filePath = dir.getAbsolutePath() + "/Receive_" + timePath + ".jpg";
                File outputFile = new File(filePath);
                Log.d(TestLog, "filepath is: " + filePath);
                Log.d(TestLog, "iscreate");
                if(outputFile.exists()){
                    outputFile.delete();
                }
                Log.d(TestLog, "iscreate");
                boolean iscreate = outputFile.createNewFile();
                Log.d(TestLog, "iscreate" + iscreate);
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

                Log.d(TestLog, "Receive is ok");
                //解析json文件
                JSONObject jsonObject = new JSONObject(responseData);
                String photoName = jsonObject.getString("name");
                Log.d(TestLog, "photo name is:" + photoName);
                String imageBase64String = jsonObject.getString("image_base64_string");
                String ratio = jsonObject.getString("ratio");
                //保存图片
                byte[] buffer = Base64.decode(imageBase64String, Base64.DEFAULT);
                bufferedOutputStream.write(buffer);
                bufferedOutputStream.flush();
                outputStream.close();
                //创建显示用bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                Log.d(TestLog, "bitmap is ok");
                // 获取缩略图
                String thumbPath = dir_thumb.getAbsolutePath() + "/Thumb_" + timePath + ".jpg";
                BufferedOutputStream buffStream = new BufferedOutputStream(new FileOutputStream(new File(thumbPath)));
                Bitmap bitmap_thumb = Bitmap.createScaledBitmap(bitmap, 100 , 100, true);
                bitmap_thumb.compress(Bitmap.CompressFormat.JPEG, 100, buffStream);
                buffStream.flush();
                buffStream.close();
                //存储评价信息
                String diagnoPath = dir_diagno.getAbsolutePath() + "/Diagno_" + timePath + ".txt";
                File diagnoFile = new File(diagnoPath);
                if(diagnoFile.exists() && diagnoFile.isDirectory()){
                    diagnoFile.delete();
                    diagnoFile.createNewFile();
                }
                else if(!diagnoFile.exists()){
                    diagnoFile.createNewFile();
                }
                FileWriter fwd = new FileWriter(diagnoFile);
                BufferedWriter bwd = new BufferedWriter(fwd);
                bwd.write( ratio + "%\n");
                bwd.close();
                fwd.close();
                // 存储时间戳信息
                String historyPath = dir_data.getAbsolutePath() + "/History.txt";
                File historyFile = new File(historyPath);
                if(historyFile.exists() && historyFile.isDirectory()){
                    historyFile.delete();
                    historyFile.createNewFile();
                }
                else if(!historyFile.exists()){
                    historyFile.createNewFile();
                }

                FileWriter fwt=new FileWriter(historyFile, true);
                BufferedWriter bwt=new BufferedWriter(fwt);
                bwt.write(timePath + "\n");
                bwt.close();
                fwt.close();

                return bitmap;
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = null;
            return bitmap;
        }

    }

    // 文件刷新
    public static void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

    // 文件夹刷新
    public static void scanDir(Context context, String dir) {
        File[] files = new File(dir).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });

        if(files == null) return;

        String[] paths = new String[files.length];
        for (int co = 0; co < files.length; co++) {
            paths[co] = files[co].getAbsolutePath();
            Log.d(TestLog, "Scan File :" + files[co].getAbsolutePath());
            scanFile(context, paths[co]);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}

