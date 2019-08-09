package com.example.awei.slamshow;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private List<house> data = new ArrayList<>();
    public ListView listView;
    private MyGLview myGLview;
    public Button button;
    public RefreshLayout refresh;
    public final String url = "https://heyblack.top/slam/";
    public myAdapter myAdapter;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        //申请读写权限
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        myAdapter = new myAdapter(this,data);
        listView.setAdapter(myAdapter);

    }

    private void init(){
        listView = findViewById(R.id.house_list);
        listView.setDividerHeight(0);
        refresh = findViewById(R.id.reflash);
        refresh.setRefreshHeader(new ClassicsHeader(this));
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                httpUtils.getHttpHelper().doGet(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        refresh.finishRefresh(1000,false);//传入false表示刷新失败
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"网络请求失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String res = response.body().string();
                        getData(res);
                        System.out.println(res);
                        refresh.finishRefresh(1000,true);//传入false表示刷新失败
                    }
                });
                myAdapter.notifyDataSetChanged();
            }
        });
        //向后台请求拿到数据
        httpUtils.getHttpHelper().doGet(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"网络请求失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                getData(res);
            }
        });

    }

    private void getData(String response){
        data.clear();
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray = new JSONArray(response);
                for(int i =0; i<jsonArray.length();i++){
                    JSONObject jsonObject= jsonArray.getJSONObject(i);
                    String imagUrl = jsonObject.getString("img_rul");
                    String address = jsonObject.getString("address");
                    String price = jsonObject.getString("price") + "元/㎡";
                    String name = jsonObject.getString("name");
                    String plyUrl = jsonObject.getString("ply_url");
                    data.add(new house(address, price, name, imagUrl, plyUrl));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
