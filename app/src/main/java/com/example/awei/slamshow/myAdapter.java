package com.example.awei.slamshow;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.numberprogressbar.NumberProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class myAdapter extends BaseAdapter {
    private Context context;
    private List<house> list;
    private ProgressDialog mProgressDiglog;
    public static class ViewHolder{
        TextView name;
        TextView price;
        ImageView image;
        TextView location;
        Button detail_btn;
        NumberProgressBar numberProgressBar;
    }
    public class mHandle extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what ==  1){
                update(msg.arg1);
            }
        }
    }
    private final Handler mhandle;
    public myAdapter(Context context, List<house> list){
        this.context = context;
        this.list = list;
        mhandle = new mHandle();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
            holder = new ViewHolder();
            holder.price=  view.findViewById(R.id.price);
            holder.name= view.findViewById(R.id.name);
            holder.location = view.findViewById(R.id.location);
            holder.detail_btn = view.findViewById(R.id.detail);
            holder.image = view.findViewById(R.id.house_image);
            holder.numberProgressBar = view.findViewById(R.id.progress);
            view.setTag(holder);
        }else
        {
            holder = (ViewHolder) view.getTag();//取出ViewHolder对象
        }

        holder.location.setText(list.get(i).getLocation());
        holder.name.setText(list.get(i).getName());
        holder.price.setText(list.get(i).getPrice());
        holder.detail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Intent intent = new Intent(context,GlviewActivity.class);
                if(existFile()){
                    System.out.println(">>?");
                    intent.putExtra("plyUrl",list.get(i).getPlyUrl());
                    context.startActivity(intent);
                }else{
                    mProgressDiglog = new ProgressDialog(context);
                    mProgressDiglog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDiglog.setCancelable(false);
                    mProgressDiglog.setCanceledOnTouchOutside(false);
                    mProgressDiglog.setTitle("正在下载模型文件...");
                    mProgressDiglog.show();
                    download(list.get(i).getPlyUrl(), new DonwloadResponseListener() {
                        @Override
                        public void OnDowning(long bytesRead, long contentLength, boolean done) {

                        }

                        @Override
                        public void onfailure() {

                        }

                        @Override
                        public void onSuccess(File file) {
                            // 下载成功
                            intent.putExtra("plyUrl",list.get(i).getPlyUrl());
                            context.startActivity(intent);
                        }
                    });
                }


            }
        });
        Glide.with(context).load(list.get(i).getImage()).into(holder.image);

        return view;
    }
    private boolean existFile(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/test/a.ply");
        System.out.println(file.exists());
        if(file.exists())
            return true;
        return false;
    }
    private void download(String url,final DonwloadResponseListener donwloadResponseListener){
            httpUtils.getHttpHelper().doGet(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int len;
                    byte[] buf = new byte[4096];
                    InputStream inputStream = response.body().byteStream();
                    long contentLength = response.body().contentLength();
                    int count = 0;
                    File file = null;
                    // 使用自定义文件名
                    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/test");
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file+"/a.ply");
                    while((len = inputStream.read(buf))!= -1){
                        System.out.println(len);
                        count += len;
                        Message message = Message.obtain();
                        message.what = 1;
                        message.arg1 = 100*count/(int)contentLength;
                        mhandle.sendMessage(message);
                        fileOutputStream.write(buf, 0, len);
                    }
                    donwloadResponseListener.onSuccess(file);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    inputStream.close();
                }
            });
    }
    public void update(int progress){
        mProgressDiglog.setProgress(progress);
    }
    public interface DonwloadResponseListener {
        /**
         * @param bytesRead     已下载字节数
         * @param contentLength 总字节数
         * @param done          是否下载完成
         * @deprecated 计算方式是 (100 * bytesRead) / contentLength
         * 日志为 45%...
         */
        void OnDowning(long bytesRead, long contentLength, boolean done);

        /**
         * 下载失败的回调方法
         */
        void onfailure();

        /**
         * 这是一个下载成功之后,返回文件路径的方法
         *
         * @param file
         */
        void onSuccess(File file);
    }
}
