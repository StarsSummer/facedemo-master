package com.zeroingin.x.facedemo;

import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.guo.android_extend.java.ExtInputStream;
import com.guo.android_extend.java.ExtOutputStream;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gqj3375 on 2017/7/11.
 */

public class FaceDB {
    private final String TAG = this.getClass().toString();

    public static String appid = "45HVLTYb6kr9tPgYJoPcWRVqcXdaBmrj5kA9ma2n5gFT";
    public static String ft_key = "BGGrzC7chuWshdSZYpHes9bwaVdCLW4jjgYNXFVJeRu";
    public static String fd_key = "BGGrzC7chuWshdSZYpHes9j6ykr3ryo8Xw7uFqzvThy";
    public static String fr_key = "BGGrzC7chuWshdSZYpHesADkaoT1TjJaAbNCuKGKtne";


    String mDBPath;
    List<FaceRegist> mRegister;
    AFR_FSDKEngine mFREngine;
    AFR_FSDKVersion mFRVersion;
    boolean mUpgrade;

    class FaceRegist {
        String mName;
        String mStatus;
        String mId;
        List<AFR_FSDKFace> mFaceList;

        public FaceRegist(String mId,String name,String status) {
            mId = mId;
            mName = name;
            mStatus = status;
            mFaceList = new ArrayList<>();
        }
    }

    public FaceDB(String path) {
        mDBPath = path;
        mRegister = new ArrayList<>();
        mFRVersion = new AFR_FSDKVersion();
        mUpgrade = false;
        mFREngine = new AFR_FSDKEngine();
        AFR_FSDKError error = mFREngine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
        if (error.getCode() != AFR_FSDKError.MOK) {
            Log.e(TAG, "AFR_FSDK_InitialEngine fail! error code :" + error.getCode());
        } else {
            mFREngine.AFR_FSDK_GetVersion(mFRVersion);
            Log.d(TAG, "AFR_FSDK_GetVersion=" + mFRVersion.toString());
        }
    }

    public void destroy() {
        if (mFREngine != null) {
            mFREngine.AFR_FSDK_UninitialEngine();
        }
    }

    private boolean saveInfo() {
        try {
            FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt");
            ExtOutputStream bos = new ExtOutputStream(fs);
            bos.writeString(mFRVersion.toString() + "," + mFRVersion.getFeatureLevel());
            bos.close();
            fs.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean loadInfo() {
        try {
            FileInputStream fs = new FileInputStream(mDBPath + "/face.txt");
            ExtInputStream bos = new ExtInputStream(fs);
            //load version
            String version_saved = bos.readString();
            if (version_saved.equals(mFRVersion.toString() + "," + mFRVersion.getFeatureLevel())) {
                mUpgrade = true;
            }
            //load all regist name.
            if (version_saved != null) {
                for (String name = bos.readString(); name != null; name = bos.readString()) {
                    if (new File(mDBPath + "/" + name + ".data").exists()) {
                        mRegister.add(new FaceRegist(new String(name.split("|")[0]),new String(name.split("|")[1]),new String(name.split("|")[2])));
                    }
                }
            }
            bos.close();
            fs.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean loadFaces() {
        if (loadInfo()) {
            try {
                for (FaceRegist face : mRegister) {
                    Log.d(TAG, "load name:" + face.mName + "'s face feature data.");
                    FileInputStream fs = new FileInputStream(mDBPath + "/" + face.mName + ".data");
                    ExtInputStream bos = new ExtInputStream(fs);
                    AFR_FSDKFace afr = null;
                    do {
                        if (afr != null) {
                            if (mUpgrade) {
                                //upgrade data.
                            }
                            face.mFaceList.add(afr);
                        }
                        afr = new AFR_FSDKFace();
                    } while (bos.readBytes(afr.getFeatureData()));
                    bos.close();
                    fs.close();
                    Log.d(TAG, "load name: size = " + face.mFaceList.size());
                }
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // TODO: 2017/10/4 待完善注册时先对比人脸库中是否含有同一个人 而非使用名字将特征放在一起
    public void addFace(String id,String name,String status, AFR_FSDKFace face) {
        try {
            //check if already registered.


            boolean add = true;
            boolean isSame = false;
            for (FaceRegist frface : mRegister) {
                //判断数据库中是否存在该人名字 有则将脸谱加入list中
                if (frface.mName.equals(name)) {
                    for (AFR_FSDKFace afr_fsdkFace : frface.mFaceList) {
                        if (new String(face.getFeatureData()).equals(new String(afr_fsdkFace.getFeatureData()))) {  //byte[]转换为string后对比
                            isSame = true;  //传入的与list中已存在的任意一个特征相同即为重复
                        }
                    }
                    if (!isSame)  //不重复则添加进list
                        frface.mFaceList.add(face);
                    add = false;
                    break;
                }
            }

            if (add) { // not registered.
                FaceRegist frface = new FaceRegist(id,name,status);

                frface.mFaceList.add(face);
                mRegister.add(frface);


            }

            if (!isSame) {  //重复特征则不保存信息
                if (saveInfo()) {
                    //update all names
                    FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt", true);
                    ExtOutputStream bos = new ExtOutputStream(fs);
                    for (FaceRegist frface : mRegister) {
                        bos.writeString(frface.mName + "|" + frface.mStatus);
                    }
                    bos.close();
                    fs.close();

                    //save new feature
                    fs = new FileOutputStream(mDBPath + "/" + name + ".data", true);
                    bos = new ExtOutputStream(fs);
                    bos.writeBytes(face.getFeatureData());
                    bos.close();
                    fs.close();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean delete(String name) {
        try {
            //check if already registered.
            boolean find = false;
            for (FaceRegist frface : mRegister) {
                if (frface.mName.equals(name)) {
                    File delfile = new File(mDBPath + "/" + name + ".data");
                    if (delfile.exists()) {
                        delfile.delete();
                    }
                    mRegister.remove(frface);
                    find = true;
                    break;
                }
            }

            if (find) {
                if (saveInfo()) {
                    //update all names
                    FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt", true);
                    ExtOutputStream bos = new ExtOutputStream(fs);
                    for (FaceRegist frface : mRegister) {
                        bos.writeString(frface.mName);
                    }
                    bos.close();
                    fs.close();
                }
            }
            return find;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean upgrade() {
        return false;
    }

}
