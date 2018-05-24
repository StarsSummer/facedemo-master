package com.zeroingin.x.facedemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.arcsoft.facerecognition.AFR_FSDKFace;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by 111 on 2018/4/5.
 */

public class FunctionActivity extends Activity implements View.OnClickListener{
    private final String TAG = this.getClass().toString();

    private static final int REQUEST_CODE_IMAGE_CAMERA = 1;
    private static final int REQUEST_CODE_IMAGE_OP = 2;
    private static final int REQUEST_CODE_OP = 3;

    private Uri mPath;
    private AFR_FSDKFace mAFR_FSDKFace;
    private MySqliteHelper helper;
    private String longstr="";
    private int l,r;
    private Socket socket;
    private final int port =9999;

    private String role;
    private String name;

    private Button btn_kids;
    private Button btn_anno;
    private Button btn_veri;
    private Button btn_self;
    private Button btn_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        Intent roleintent = getIntent();
        role = roleintent.getStringExtra("role");
        name = roleintent.getStringExtra("name");





        Toast.makeText(this,role,Toast.LENGTH_LONG).show();
        changefunc(role);
        View v = this.findViewById(R.id.Button_kids);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.Button_anno);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.Button_veri);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.Button_self);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.Button_chat);
        v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Button_kids:
                Intent pickintent = new Intent(FunctionActivity.this,PickupActivity.class);
                pickintent.putExtra("name",name);
                pickintent.putExtra("role",role);
                startActivity(pickintent);
                break;
            case R.id.Button_chat:
                Intent chatintent = new Intent(FunctionActivity.this,ChatActivity.class);
                chatintent.putExtra("name",name);
                startActivity(chatintent);
                break;
            case  R.id.Button_veri:
                if(role.equals("家长")) {
                    new AlertDialog.Builder(this)
                            .setTitle("请选择注册方式")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setItems(new String[]{"打开图片", "拍摄照片"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 1:
                                            //添加异常情况处理
                                            if (!AppUtils.isHaveCame(MediaStore.ACTION_IMAGE_CAPTURE)) {
                                                Toast.makeText(FunctionActivity.this, "该手机没有安装相机", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            //原代码使用"android.media.action.IMAGE_CAPTURE"
                                            Intent getImageByCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                            ContentValues values = new ContentValues(1);    //准备一个长度为1的键值对组
                                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");    //指定内容类型
                                            //向存储在SD卡上的图像文件文件ContentProvider的URI插入一个 并返回Uri
                                            mPath = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                                            //指定拍照存储路径 如果指定了目标uri，data就没有数据，如果没有指定uri，则data就返回有数据
                                            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, mPath);

                                            startActivityForResult(getImageByCamera, REQUEST_CODE_IMAGE_CAMERA);
                                            break;
                                        case 0:
                                            //隐式意图 选择最匹配的
                                            Intent getImageByAlbum = new Intent(Intent.ACTION_GET_CONTENT);
                                            //只返回可通过 openFileDescriptor() 以文件流形式表示的“可打开”文件
                                            getImageByAlbum.addCategory(Intent.CATEGORY_OPENABLE);
                                            //查看类型 String IMAGE_UNSPECIFIED = "image/*";
                                            getImageByAlbum.setType("image/jpeg");
                                            startActivityForResult(getImageByAlbum, REQUEST_CODE_IMAGE_OP);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            })
                            .show();
                }
                else if(role.equals("教师")){
                    Intent it = new Intent(FunctionActivity.this, DetecterActivity.class);
                    startActivityForResult(it, REQUEST_CODE_OP);
                }
                break;
            case  R.id.Button_anno:
                Intent annintent = new Intent(FunctionActivity.this,AnnoActivity.class);
                annintent.putExtra("role",role);
                annintent.putExtra("name",name);
                startActivity(annintent);
                break;
            case R.id.Button_self:
                Intent selintent = new Intent(FunctionActivity.this,SelfActivity.class);
                selintent.putExtra("role",role);
                selintent.putExtra("name",name);
                startActivity(selintent);
                break;
            default:
                break;
        }
    }

    protected void changefunc(String role){
        this.role = role;
        btn_kids = (Button) findViewById(R.id.Button_kids);
        btn_anno = (Button) findViewById(R.id.Button_anno);
        btn_veri = (Button) findViewById(R.id.Button_veri);
        if(role.equals("教师")){
            btn_kids.setText("查看接送情况");
            btn_anno.setText("发布公告");
            btn_veri.setText("刷脸验证身份");
        }
        if(role.equals("家长")){
            btn_kids.setText("确认接送情况");
            btn_anno.setText("查看公告");
            btn_veri.setText("增加临时接送人");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMAGE_OP && resultCode == RESULT_OK) {
            mPath = data.getData();
            String file = getPath(mPath);
            Bitmap bmp = Application.decodeImage(file);
            if (bmp == null || bmp.getWidth() <= 0 || bmp.getHeight() <= 0) {
                Log.e(TAG, "error");
            } else {
                Log.i(TAG, "bmp [" + bmp.getWidth() + "," + bmp.getHeight());
            }
            startRegister(bmp, file);
        } else if (requestCode == REQUEST_CODE_OP) {
            Log.i(TAG, "RESULT =" + resultCode);
            if (data == null) {
                return;
            }
            Bundle bundle = data.getExtras();   //将捆绑的参数取出
            String path = bundle.getString("imagePath");
            Log.i(TAG, "path=" + path);
        } else if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
            //当头型不正时便无法获取到人脸特征
            String file = getPath(mPath);
            Bitmap bmp = Application.decodeImage(file);
            startRegister(bmp, file);
        }
    }

    private String getPath(Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(this, uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    return null;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    return null;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(this, contentUri, selection, selectionArgs);
            }
        }
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualImageCursor = managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualImageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualImageCursor.moveToFirst();
        String img_path = actualImageCursor.getString(actual_image_column_index);
        String end = img_path.substring(img_path.length() - 4);
        if (0 != end.compareToIgnoreCase(".jpg") && 0 != end.compareToIgnoreCase(".png")) {
            return null;
        }
        return img_path;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    private void startRegister(Bitmap mBitmap, String file) {
        Intent it = new Intent(FunctionActivity.this, RegisterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("imagePath", file);
        it.putExtras(bundle);   //使用Bundle更利于多重传递 快速取出参数且可以传递对象 http://blog.csdn.net/garretly/article/details/6207950
        startActivityForResult(it, REQUEST_CODE_OP);
    }


}
