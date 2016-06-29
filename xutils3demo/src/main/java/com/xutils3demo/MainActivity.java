package com.xutils3demo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final String BASE_URL = "http://pic13.nipic.com/20110415/1369025_121513630398_2.jpg";

    //外部sdcard
    private static final String BASE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator;
    private Button buttonDownloadFile, bt_OpenPhoto;
    private TextView tv1, tv2, tv3;
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonDownloadFile = (Button) findViewById(R.id.bt_downloadFile);
        bt_OpenPhoto = (Button) findViewById(R.id.bt_OpenPhoto);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);

        // TODO: 2016/6/29 下载 
        buttonDownloadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = BASE_URL;
                String path = BASE_PATH + "/DCIM/Camera" + "/mm.jpeg";
                if (fileIsExists()) {
                    Toast.makeText(getApplication(), "文件已存在", Toast.LENGTH_SHORT).show();
                } else {
                    downloadFile(url, path);
                }
            }
        });
        // TODO: 2016/6/29 打开相册  
        bt_OpenPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                //根据版本号不同使用不同的Action
                if (Build.VERSION.SDK_INT < 19) {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                }
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
            }
        });

    }

    // TODO: 2016/6/29  判断文件是否存在
    public boolean fileIsExists() {
        try {
            File f = new File(BASE_PATH + "/DCIM/Camera" + "/mm.jpeg");
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {

            return false;
        }
        return true;
    }

    private void downloadFile(final String url, String path) {
        progressDialog = new ProgressDialog(this);
        RequestParams requestParams = new RequestParams(url);
        requestParams.setSaveFilePath(path);
        x.http().get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage("亲，努力下载中。。。");
                progressDialog.show();
                progressDialog.setMax((int) total);
                progressDialog.setProgress((int) current);
//                文件大小
                tv1.setText(total + "");
//                下载进度
                tv2.setText(current + "");
            }

            @Override
            public void onSuccess(File result) {
                Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                Toast.makeText(MainActivity.this, "下载失败，请检查网络和SD卡", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (null != data) {
                Uri uri = data.getData();
                //根据需要，也可以加上Option这个参数
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            }
        }
    }
}
