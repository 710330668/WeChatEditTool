package wechatedittool.com.wechatedittool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity {

    private static final String URL1 = "http://mp.weixin.qq.com/s?__biz=MzI1MTA1MzM2Nw==&mid=2649796799&idx=1&sn=42061b7d021b8d8fba00202286cd9372&scene=4#wechat_redirect";

    @Bind(R.id.main_btn_reset)
    Button mainBtnReset;
    @Bind(R.id.main_btn_open)
    Button mainBtnOpen;
    @Bind(R.id.main_tv_link)
    EditText mainTvLink;
    @Bind(R.id.webview)
    WebView mWebView;
    private ProgressDialog mProgressDialog;
    boolean isGo = true;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.getData() != null && msg.getData().getString("MSG") != null && isGo) {
                mProgressDialog.dismiss();
                startActivity(new Intent(MainActivity.this, EditActivity.class).putExtra("content", msg.getData().getString("MSG")).putExtra("address",mainTvLink.getText().toString()));
                isGo = false;
                Log.e("************", msg.getData().getString("MSG"));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);
        setOnClick();
        initWebView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isGo = true;
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

        if(TextUtils.isEmpty(mainTvLink.getText().toString())){
            Toast.makeText(MainActivity.this,"链接不能为空",Toast.LENGTH_LONG).show();
        }else{
            mProgressDialog.show();
            mWebView.loadUrl(mainTvLink.getText().toString());
        }

    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            Message msg = new Message();
            Bundle mBundle = new Bundle();
            mBundle.putString("MSG", html);
            msg.setData(mBundle);
            mHandler.sendMessage(msg);

        }
    }

    private void initWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.requestFocus();
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("##########", "shouldOverrideUrlLoading");
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("#########", "Finish");
                view.loadUrl("javascript:window.local_obj.showSource('<head>'+"
                        + "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });
    }

}
