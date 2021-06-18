package com.example.administrator.tes30;

import android.icu.util.TimeZone;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.zhiyun360.wsn.droid.WSNRTConnect;
import com.zhiyun360.wsn.droid.WSNRTConnectListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button btn_Recv,btn_Send;
    TextView textView;

    private WSNRTConnect wsnrtConnect;
    private WSNRTConnectListener wsnrtConnectListener;

    private String  myZclouID = "714321638564";
    private String myZclouKey = "dnIBdwdydQUADXJ1RAREBwYADnY";
    private String  myServerAddr = "api.zhiyun360.com:28081";

    private Boolean isConnect = false;
    private Boolean lightState = false;

    private List<String> itemList;//数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("WSN demo");
        btn_Recv = (Button) findViewById(R.id.btn_Recv);
        btn_Send = (Button) findViewById(R.id.btn_Send);
        textView = (TextView) findViewById(R.id.text_view);

        WebView webview = (WebView) findViewById(R.id.wv);
        webview.loadUrl("http://www.baidu.com/");
        //webview.loadUrl("file:///android_asset/example.html");

        wsnrtConnect = new WSNRTConnect();
        wsnrtConnect.setIdKey(myZclouID,myZclouKey);
        wsnrtConnect.setServerAddr(myServerAddr);

        webview.getSettings().setJavaScriptEnabled(true); //webview设置支持Javascript

        //设置自适应屏幕，两者合用
        webview.getSettings().setUseWideViewPort(true);//将图片调整到适合webview的大小
        webview.getSettings().setLoadWithOverviewMode(true);//缩放至屏幕的大小

//用于控制开启 DOM Storage（存储）
        webview.getSettings().setDomStorageEnabled(true);

        webview.canGoBack();
//返回上一层级
        webview.goBack();
//判断是否可以前进，可以返回true
        webview.canGoForward();
//进入上一层级
        webview.goForward();
//刷新
        webview.reload();

        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                return true;
            }

            //还可以重写其他的方法
        });


        wsnrtConnect.setRTConnectListener(new WSNRTConnectListener() {
            @Override
            public void onConnectLost(Throwable throwable) {
                textView.setText("连接失败");
                isConnect = false;
            }

            @Override
            public void onConnect() {
                textView.setText("连接成功");
                isConnect = true;
            }

            @Override
            public void onMessageArrive(final String mac, final byte[] data) {
//            textView.setText(mac + "  " + new String(data));
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(DemoActivity.this,android.R.layout.simple_dropdown_item_1line,data);
//                ListView listView = (ListView) findViewById(R.id.list_view);
//                listView.setAdapter(adapter);
//                Intent intent = new Intent(DemoActivity.this,HistoryActivity.class);
//                intent.putExtra("extra_data",data);
//                startActivity(intent)
                String str1 = new String(data);
                Date time = new Date(System.currentTimeMillis());
                Log.d("mac",mac);
                Log.d("data",str1);

                ListView listView1 = (ListView) findViewById(R.id.list_view);

                List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("mac",mac);
                map.put("data",str1);
                map.put("time",time);
                list.add(map);

                final SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,list,R.layout.item,new String[]{"mac","data","time"},new int[]{R.id.mac,R.id.data,R.id.time});

                listView1.setAdapter(adapter);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                            try{
                                Thread.sleep(1000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    }
                });

            }
        });

        btn_Recv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnect) {
                    wsnrtConnect.connect();
                }else {
                    wsnrtConnect.disconnect();
                }
            }
        });
        btn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mac = "00:12:4B:00:1B:D5:00:0E";
                if (lightState == false) {
                    String data = "{OD1=63,D1=?}";
                    wsnrtConnect.sendMessage(mac, data.getBytes());
                    lightState = true;
                    textView.setText("灯亮");
                }else {
                    String data = "{CD1=63,D1=?}";
                    wsnrtConnect.sendMessage(mac, data.getBytes());
                    lightState = false;
                    textView.setText("灯灭");
                }
            }
        });

    }
}
