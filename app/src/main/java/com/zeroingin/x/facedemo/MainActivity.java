package com.zeroingin.x.facedemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener {
    private final String TAG = this.getClass().toString();

    private static final int REQUEST_CODE_IMAGE_CAMERA = 1;
    private static final int REQUEST_CODE_IMAGE_OP = 2;
    private static final int REQUEST_CODE_OP = 3;

    private Uri mPath;


    private Socket socket;
    private final int port =9999;

    private AFR_FSDKFace mAFR_FSDKFace;
    private MySqliteHelper helper;
    private String longstr="";
    private int l,r;
    private Button invi;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        View v = this.findViewById(R.id.button1);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.button2);
        v.setOnClickListener(this);
        //invi = (Button) this.findViewById(R.id.button1);
        //invi.setVisibility(View.INVISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("", port);
                    Log.i("info","Server connected!");
                    InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[31457280];
                    int len;
                    while ((len = inputStream.read(buffer))!=-1){
                        String response = new String(buffer,0,len);
                        r=response.length();
                        longstr = longstr+response;
                        l=longstr.length();
                        response="";
                    }
                    parseJSONWithJSONObject(longstr);
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
//     */
//    @Override
//    protected void onDestroy() {
//        // TODO Auto-generated method stub
//        super.onDestroy();
//    }

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

    @Override
    public void onClick(View paramView) {
        // TODO Auto-generated method stub
        switch (paramView.getId()) {
            case R.id.button2:
                //获取Application里非静态变量
                //if (((Application) getApplicationContext()).mFaceDB.mRegister.isEmpty()) {
                if(Nofacesyet()){
                    Toast.makeText(this, "没有注册人脸，请先注册！", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("请选择相机")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setItems(new String[]{"后置相机", "前置相机"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startDetector(which);
                                }
                            }).show();
                }
                break;
            case R.id.button1:
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
                                            Toast.makeText(MainActivity.this, "该手机没有安装相机", Toast.LENGTH_SHORT).show();
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
                break;
            default:
                break;
        }
    }

    /**
     * @param uri
     * @return
     */
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

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
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

    /**
     * @param mBitmap
     */
    private void startRegister(Bitmap mBitmap, String file) {
        Intent it = new Intent(MainActivity.this, RegisterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("imagePath", file);
        it.putExtras(bundle);   //使用Bundle更利于多重传递 快速取出参数且可以传递对象 http://blog.csdn.net/garretly/article/details/6207950
        startActivityForResult(it, REQUEST_CODE_OP);
    }

    private void startDetector(int camera) {
        Intent it = new Intent(MainActivity.this, DetecterActivity.class);
        it.putExtra("Camera", camera);
        startActivityForResult(it, REQUEST_CODE_OP);
    }

    private boolean Nofacesyet(){
        helper = new MySqliteHelper(getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql1 = "select * from users";
        Cursor cursor = db.rawQuery(sql1, null);
        return false;
    }

    private void parseJSONWithJSONObject(String jsonData){
        try{
           // helper = new MySqliteHelper(getApplicationContext());
            //SQLiteDatabase db = helper.getWritableDatabase();
           // String sql ="insert into users(name,role) values ('\"+name+\"','\"+role+\"')";
//            Gson gson = new Gson();
//            List<UserFace> faces= gson.fromJson(jsonData,new TypeToken<List<UserFace>>(){}.getType());
//            for(UserFace userFace: faces){
//                String id =userFace.getId();
//                String name = userFace.getName();
//                String role = userFace.getRole();
//                byte[] mfeature = userFace.getFeature();
//           //     db.execSQL(sql);
//                mAFR_FSDKFace.setFeatureData(mfeature);
//                ((Application) MainActivity.this.getApplicationContext()).mFaceDB.addFace(name,role,mAFR_FSDKFace);
//            }
                JSONArray jsonArray=new JSONArray(jsonData);
                for(int j=0;j<jsonArray.length();j++){
                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    String id =jsonObject.getString("ID");
                    String name = jsonObject.getString("name");
                    String role = jsonObject.getString("role");
                    String face = jsonObject.getString("feature");
                    byte[] mfeature = Base64.decode(face,Base64.DEFAULT);
                    AFR_FSDKFace newface = new AFR_FSDKFace(mfeature);
                    ((Application) MainActivity.this.getApplicationContext()).mFaceDB.addFace(id,name,role,newface);
                }

                Log.i("info","Sql completed!");

        }catch (Exception e){
            e.printStackTrace();
        }
    }


}

