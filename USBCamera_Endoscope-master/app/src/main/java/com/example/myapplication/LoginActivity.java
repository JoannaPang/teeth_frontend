package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginActivity extends Activity{
    public static String userName = "";
    private String TestLog = "TestLog";
    //记住密码相关操作  qsr 
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String iName;
    private String iPsw;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TestLog, "Login Activity");
        initData();
        initView();

    }

    //初始化UI相关操作 qsr
    private void initView() {
        EditText inputName = (EditText) findViewById(R.id.editText_login_username);
        EditText inputPsw = (EditText) findViewById(R.id.editText_login_password);

        //判断是否已经有过记住账户和密码,有保存过就自动填充
        String name = sp.getString("username","");
        String psd = sp.getString("password","");
        if(!name.isEmpty()){
            inputName.setText(name);
            inputPsw.setText(psd);
            Toast.makeText(this,"保存的账户名密码已填充",Toast.LENGTH_SHORT).show();
        }

        Log.d(TestLog, "EditText Ok");

        // 确认登录 验证填写信息
        Button loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TestLog, "LoginActivity - check Input");
                iName = inputName.getText().toString();
                iPsw = inputPsw.getText().toString();

                Log.d(TestLog, "Login  name:" + iName + " psw:" + iPsw);
                new Thread(new LoginThread(iName, iPsw)).start();
            }
        });

        // 切换至注册
        TextView change2regis = (TextView) findViewById(R.id.view_to_register);
        change2regis.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TestLog, "LoginActivity Change to RegisterActivity");
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                //finish(); //取消掉该句话之后，可以实现从registeractivity按返回键返回至LoginActivity
            }
        });

        // 设置服务器地址及端口号
        TextView login_set = (TextView) findViewById(R.id.login_to_set);
        login_set.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TestLog, "dialog button listen");
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                LayoutInflater factory = LayoutInflater.from(LoginActivity.this);
                final View changeServerView = factory.inflate(R.layout.change_server, null);

                final EditText inputServer = (EditText) changeServerView.findViewById(R.id.text_server);
                final EditText inputPort = (EditText) changeServerView.findViewById(R.id.text_port);

                Log.d(TestLog, "init var");

                inputServer.setHint(MainActivity.LocalHost);
                inputPort.setHint("" + MainActivity.port);

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
                            MainActivity.LocalHost = iServer;
//							outServer.setText("当前服务器：" + iServer);
                        }
                        int inputPort;
                        try {
                            if((inputPort = inputCheckPort(iPort)) != -1) {
                                Log.d(TestLog, "change Port");
                                MainActivity.port = inputPort;
//								outPort.setText(out);
                            }
                        }catch(Exception e) {
                            Log.d(TestLog, e.getMessage());
                        }
                        Log.d(TestLog, "After the change :" + MainActivity.LocalHost + "/" + MainActivity.port);
                    }
                    private boolean inputCheckServer(String iServer) {
                        // 参考资料https://blog.csdn.net/chaiqunxing51/article/details/50975961/
                        if(iServer == null || iServer.length() == 0) { // 基础检验
                            return false;
                        }
                        String[] parts = iServer.split("\\.");
                        if(parts.length != 4) { // 四段ip设置
                            return true;
                        }
                        for(int i = 0; i < 4; i++) {
                            try {
                                int n = Integer.parseInt(parts[i]);
                                if(n< 0 || n > 255) return true; // ip数检验
                            }catch(NumberFormatException e) {
                                return true; // 非法字符检验
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

        inputName.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override

            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {

                    for (int i = 0; i < s.length(); i++) {

                        char c = s.charAt(i);

                        if (c >= 0x4e00 && c <= 0X9fff) { // 根据字节码判断

                            // 如果是中文，则清除输入的字符，否则保留

                            s.delete(i,i+1);

                        }

                    }

                }

            }

        });
    }

    //初始化数据相关操作 qsr
    private void initData() {
        sp = getSharedPreferences("setting", MODE_PRIVATE);
    }

    // 通过Handler实现报错提示
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 0) {
                saveAccountAndPsw();//保存用户名密码 -- qsr
                Toast.makeText(getApplicationContext(), "@@登录成功@@", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                String congraText = "登陆成功";
                builder.setTitle("恭喜！") ;
                builder.setMessage(congraText);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("出错误啦！") ;
                String errorText = "";
                if(msg.what == 1) errorText = "用户名不存在";
                else if(msg.what == 2) errorText = "密码有误";
                else if(msg.what == 404) errorText = "与服务器" + MainActivity.LocalHost + ":" + MainActivity.port + "连接失败";
                builder.setMessage(errorText);
                builder.setPositiveButton("确定",null );
                builder.show();
            }
        }
    };

    //保存账户密码操作  - qsr
    private void saveAccountAndPsw() {
        editor = sp.edit();
        editor.putString("username",iName);
        editor.putString("password",iPsw);
        editor.commit();
    }

    // 通过Socket实现的 客户端 - 服务器 进行的登录交互
    public class LoginThread implements Runnable {
        private String name;
        private String psw;

        public LoginThread(String name, String psw) {
            Log.d(TestLog, "Login Thread - run");
            this.name = name;
            this.psw = psw;
        }

        @Override
        public void run() {
            Log.d(TestLog, "Login Thread - run");
            android.os.Message message = Message.obtain();
            message.obj = null;
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new FormBody.Builder()
                    .add("name", name)
                    .add("psw", psw)
                    .add("submit", "Login")
                    .build();
            Request request = new Request.Builder()
                    .url(MainActivity.LocalHost + ":" + MainActivity.port + "/login")
                    .post(requestBody)
                    .build();
            try {
                Call call = client.newCall(request);
                Response response = call.execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code");
                ResponseBody body = response.body();
                String responseData = body.string();
                if (responseData.equals("wrong password")) {
                    Log.d(TestLog, "Login Thread - Illegal Name!");
                    // 保存信息
                    message.what = 1;
                    handler.sendMessage(message);
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");
                    Log.d(TestLog, "status is:" + status);
                    String token = jsonObject.getString("token");
                    Log.d(TestLog, "token:" + token);
                    userName = name;
                    message.what = 0;
                    handler.sendMessage(message);
                    Log.d(TestLog, "Login Thread - Finish Success");
                }catch (JSONException e){
                    e.printStackTrace();
                }

            } catch (IOException e) {
                message.what = 404;
                handler.sendMessage(message);
                Log.d(TestLog, "catch error:" + e.getMessage() + request.url());
            }
        }
    }
}
