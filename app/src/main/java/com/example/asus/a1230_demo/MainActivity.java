package com.example.asus.a1230_demo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;

import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;



import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {


    Button btn1;
    Button btn2;
    ImageView tv;
    private HttpUtils httputils;
    Uri fileUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1= (Button) findViewById(R.id.btn1);
        btn2= (Button) findViewById(R.id.btn2);
        tv= (ImageView) findViewById(R.id.content);
        httputils = new HttpUtils();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);



            }
        });



        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileUri = getOutputMediaFileUri(0); //得到存储地址的Uri
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //此action表示进行拍照
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  //指定图片的输出地址
                startActivityForResult(i, 0);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path=null;
        if (requestCode == 0) {
            path = fileUri.getPath(); //取得拍照存储的地址
        } else { //解析得到所选相册图片的地址
            Uri selectedImage = data.getData();
            path = selectedImage.getPath();
            System.out.println(path);
        }
        File f=new File(path);
        Bitmap map=BitmapFactory.decodeFile(f.getPath());
        tv.setImageBitmap(map);
        String uploadHost="http://172.25.136.1:8080/upload/UploadServlet";
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("photo", new File(path));

        httputils.send(HttpMethod.POST,uploadHost, params, new RequestCallBack<String>() {

                            @Override
                            public void onStart() {
                                super.onStart();
                            }

                            @Override
                            public void onLoading(long total, long current,
                                                  boolean isUploading) {
                                super.onLoading(total, current, isUploading);

                            }

                            @Override
                            public void onFailure(HttpException arg0,
                                                  String arg1) {

                            }

                            @Override
                            public void onSuccess(ResponseInfo<String> arg0) {
                                Toast.makeText(
                                        MainActivity.this,"success", Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    /** 为保存图片或视频创建一个文件地址 */
    private static Uri getOutputMediaFileUri(int type){

        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        // 首先检测外部SDCard是否存在并且可读可写
        if (!Environment.getExternalStorageState().equals("mounted")) {
            return null;
        }
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath());
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // 如果存储路径不存在，则创建
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()) {
            if (! mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 0) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }



}
