package wechatedittool.com.wechatedittool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity {

    private static final String URL1 = "http://mp.weixin.qq.com/s?__biz=MzI1MTA1MzM2Nw==&mid=2649796799&idx=1&sn=42061b7d021b8d8fba00202286cd9372&scene=4#wechat_redirect";
    private static final String URL2 = "http://www.cnblogs.com/hibraincol/";

    @Bind(R.id.main_btn_reset)
    Button mainBtnReset;
    @Bind(R.id.main_btn_open)
    Button mainBtnOpen;
    @Bind(R.id.main_tv_link)
    EditText mainTvLink;
    @Bind(R.id.webview)
    WebView webview;
    private ProgressDialog mProgressDialog;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.getData() != null && msg.getData().getString("MSG") != null) {
                mProgressDialog.dismiss();
//                Toast.makeText(MainActivity.this, msg.getData().getString("MSG"), Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, EditActivity.class).putExtra("content", msg.getData().getString("MSG")));
                Log.e("************", msg.getData().getString("MSG"));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);
        setOnClick();

        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        webview.setWebViewClient(new MyWebViewClient());
//        webview.loadUrl("http://www.cnblogs.com/hibraincol/");
    }

    private void setOnClick() {
        mainBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainTvLink.setText("");
            }
        });

        mainBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLink();
            }
        });
    }

    private void openLink() {
        mProgressDialog.show();
        MyThread thread1 = new MyThread();
        thread1.start();
    }

    final class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d("WebView", "onPageStarted");
            super.onPageStarted(view, url, favicon);
        }

        public void onPageFinished(WebView view, String url) {
            Log.d("WebView", "onPageFinished ");
            view.loadUrl("javascript:window.local_obj.showSource('<head>'+" +
                    "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            super.onPageFinished(view, url);
        }
    }

    final class InJavaScriptLocalObj {
        public void showSource(String html) {
            Log.e("###############", html);
        }
    }

    class MyThread extends Thread {
        @Override
        public void run() {
            String str = posturl(URL2);
            Message msg = new Message();
            Bundle mBundle = new Bundle();
            mBundle.putString("MSG", str);
            msg.setData(mBundle);
            mHandler.sendMessage(msg);

//            try {
//                URL url = new URL("http://mp.weixin.qq.com/s?__biz=MzI1MTA1MzM2Nw==&mid=2649796799&idx=1&sn=42061b7d021b8d8fba00202286cd9372&scene=4#wechat_redirect");
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setDoInput(true);
//                conn.setConnectTimeout(10000);
//                conn.setRequestMethod("GET");
//                conn.setRequestProperty("accept", "*/*");
//                String location = conn.getRequestProperty("location");
//                int resCode = conn.getResponseCode();
//                conn.connect();
//                InputStream stream = conn.getInputStream();
//                byte[] data = new byte[102400];
//                int length = stream.read(data);
//                String str = new String(data, 0, length);
//                conn.disconnect();
//                Message msg = new Message();
//                Bundle mBundle = new Bundle();
//                mBundle.putString("MSG", str);
//                msg.setData(mBundle);
//                mHandler.sendMessage(msg);
//                stream.close();
//            } catch (Exception ee)
//
//            {
//                System.out.print("ee:" + ee.getMessage());
//            }
        }
    }


    /**
     * 获取指定地址的网页数据  * 返回数据流
     */
    public InputStream streampost(String remote_addr) {
        URL infoUrl = null;
        InputStream inStream = null;
        try {
            infoUrl = new URL(remote_addr);
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode ==
                    HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return inStream;
    }


    /**
     * 获取参数指定的网页代码，将其返回给调用者，由调用者对其解析  * 返回String
     */
    public String posturl(String url) {
        InputStream is = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (Exception e) {
            return "Fail to establish http connection!" + e.toString();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            return "Fail to convert net stream!";
        }
        return result;
    }

}
