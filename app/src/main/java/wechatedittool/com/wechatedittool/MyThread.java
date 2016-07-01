package wechatedittool.com.wechatedittool;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/6/30.
 */

public class MyThread extends Thread {
    @Override
    public void run() {
        InputStream inputStream = Util.getInstance().streampost("");
        String str = "";
        try {
            str = Util.getInstance().inputStream2String(inputStream);
        } catch (IOException e) {
            Log.e("ERROR", e.toString());
        }
//            String str = Util.getInstance().posturl(URL2);
        Message msg = new Message();
        Bundle mBundle = new Bundle();
        mBundle.putString("MSG", str);
        msg.setData(mBundle);
//            mHandler.sendMessage(msg);
    }
}


/*
class MyThread extends Thread {
    @Override
    public void run() {
        InputStream inputStream = Util.getInstance().streampost(URL1);
        String str = "";
        try {
            str = Util.getInstance().inputStream2String(inputStream);
        } catch (IOException e) {
            Log.e("ERROR", e.toString());
        }
//            String str = Util.getInstance().posturl(URL2);
        Message msg = new Message();
        Bundle mBundle = new Bundle();
        mBundle.putString("MSG", str);
        msg.setData(mBundle);
        mHandler.sendMessage(msg);
    }
}*/
