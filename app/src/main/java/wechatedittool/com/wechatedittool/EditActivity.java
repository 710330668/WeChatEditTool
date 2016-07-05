package wechatedittool.com.wechatedittool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.richeditor.RichEditor;
import wechatedittool.com.wechatedittool.ui.DialogHelp;
import wechatedittool.com.wechatedittool.utils.FileUtil;
import wechatedittool.com.wechatedittool.utils.ImageUtils;
import wechatedittool.com.wechatedittool.utils.StringUtils;

public class EditActivity extends Activity {

    @Bind(R.id.edit_tv_back)
    TextView editTvBack;
    @Bind(R.id.edit_tv_title)
    TextView editTvTitle;
    @Bind(R.id.edit_tv_save)
    TextView editTvSave;
    String mContentString = "";
    String mContentAddress = "";
    private RichEditor mEditor;
    private String appID;
    private String appSecret;
    private UMWXHandler wxCircleHandler;
    public static final int ACTION_TYPE_ALBUM = 0;
    public static final int ACTION_TYPE_PHOTO = 1;
    private String theLarge, theThumbnail;
    private File imgFile;
    private ImageView mIvImage;
    private UMSocialService mController;
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1 && msg.obj != null) {
                // 显示图片
//                mIvImage.setImageBitmap((Bitmap) msg.obj);
//                Bitmap bitmap = (Bitmap) msg.obj;
                mEditor.insertImage(String.valueOf(msg.obj), "");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        mContentString = getIntent().getStringExtra("content");
        mContentAddress =getIntent().getStringExtra("address");
        initShare();

        mEditor = (RichEditor) findViewById(R.id.editor);
        mEditor.setHtml(mContentString);
//        mEditor.setEditorHeight(200);
//        mEditor.setEditorFontSize(22);
//        mEditor.setEditorFontColor(Color.RED);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
//        mEditor.setPadding(10, 10, 10, 10);
        //    mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
//        mEditor.setPlaceholder("Insert text here...");

        editTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBackDialog();
            }
        });

        editTvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShareDialog();
            }
        });


        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSubscript();
            }
        });

        findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSuperscript();
            }
        });

        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(6);
            }
        });

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mEditor.insertImage("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1467295573&di=98ef9420c5f31791e50aa4e4587bbc2e&src=http://p5.gexing.com/GSF/shaitu/20160528/1127/574910399e421.jpg",
//                        "dachshund");
                setImage();
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
            }
        });
        findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertTodo();
            }
        });
    }

    private void initShare() {
        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        // 设置分享内容
        mController.setShareContent("文章分享");
//         设置分享图片, 参数2为图片的url地址
        mController.setShareMedia(new UMImage(this,
                "http://img3.duitang.com/uploads/item/201507/08/20150708041219_AdYcW.jpeg"));
        mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
                SHARE_MEDIA.DOUBAN, SHARE_MEDIA.TENCENT);
        appID = "wx41527d40e5c8f348";
        appSecret = "e41452c6316100f1a7b991b23f6af5ff";
        wxCircleHandler = new UMWXHandler(EditActivity.this,
                appID, appSecret);
        wxCircleHandler.setToCircle(true);
        //链接
        wxCircleHandler.setTargetUrl(mContentAddress);
        wxCircleHandler.addToSocialSDK();

    }

    private void showShareDialog() {
        new AlertDialog.Builder(EditActivity.this)
        /* 弹出窗口的最上头文字 */
                .setTitle("提示")
        /* 设置弹出窗口的图式 */
//                .setIcon(R.drawable.hot)
        /* 设置弹出窗口的信息 */
                .setMessage("要将编辑后的文章分享到朋友圈？")
                .setPositiveButton("是的",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface,
                                    int i) {
                                //分享到微信圈
                                mController.postShare(EditActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE,
                                        mShareListener);
                            }
                        })
        /* 设置弹出窗口的返回事件 */
                .setNegativeButton("不是",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface,
                                    int i) {
                            }
                        }).show();
    }

    private void showBackDialog() {
        new AlertDialog.Builder(EditActivity.this)
        /* 弹出窗口的最上头文字 */
                .setTitle("提示")
        /* 设置弹出窗口的图式 */
//                .setIcon(R.drawable.hot)
        /* 设置弹出窗口的信息 */
                .setMessage("确定要放弃编辑返回上一页？")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface,
                                    int i) {
                                finish();/* 关闭窗口 */
                            }
                        })
        /* 设置弹出窗口的返回事件 */
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface,
                                    int i) {
                            }
                        }).show();
    }

    private void setImage() {
        DialogHelp.getSelectDialog(EditActivity.this, getResources().getStringArray(R.array.choose_picture), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                goToSelectPicture(i);
            }
        }).show();
    }


    private void goToSelectPicture(int position) {
        switch (position) {
            case ACTION_TYPE_ALBUM:
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "选择图片"),
                            ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
                } else {
                    intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "选择图片"),
                            ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
                }
                break;
            case ACTION_TYPE_PHOTO:
                // 判断是否挂载了SD卡
                String savePath = "";
                String storageState = Environment.getExternalStorageState();
                if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                    savePath = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/oschina/Camera/";
                    File savedir = new File(savePath);
                    if (!savedir.exists()) {
                        savedir.mkdirs();
                    }
                }

                // 没有挂载SD卡，无法保存文件
                if (StringUtils.isEmpty(savePath)) {
                    Toast.makeText(getApplicationContext(), "无法保存照片，请检查SD卡是否挂载", Toast.LENGTH_SHORT).show();
                    return;
                }

                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                        .format(new Date());
                String fileName = "osc_" + timeStamp + ".jpg";// 照片命名
                File out = new File(savePath, fileName);
                Uri uri = Uri.fromFile(out);

                theLarge = savePath + fileName;// 该照片的绝对路径

                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent,
                        ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
                break;
            default:
                break;
        }
    }

    SnsPostListener mShareListener = new SnsPostListener() {

        @Override
        public void onStart() {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int stCode,
                               SocializeEntity entity) {
            if (stCode == 200) {
//        Toast.makeText(InvitationPriceActivity.this, "分享成功", Toast.LENGTH_SHORT)
//            .show();
            } else {
//        Toast.makeText(InvitationPriceActivity.this,
//            "分享失败 : error code : " + stCode, Toast.LENGTH_SHORT)
//            .show();
            }
        }
    };



    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent imageReturnIntent) {
        if (resultCode != Activity.RESULT_OK)
            return;
        if(requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD||requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA) {

            new Thread() {
                private String selectedImagePath;

                @Override
                public void run() {
                    Bitmap bitmap = null;
                    if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD) {
                        if (imageReturnIntent == null)
                            return;
                        Uri selectedImageUri = imageReturnIntent.getData();
                        if (selectedImageUri != null) {
                            selectedImagePath = ImageUtils.getImagePath(
                                    selectedImageUri, EditActivity.this);
                        }

                        if (selectedImagePath != null) {
                            theLarge = selectedImagePath;
                        } else {
                            bitmap = ImageUtils.loadPicasaImageFromGalley(
                                    selectedImageUri, EditActivity.this);
                        }

                        if (isMethodsCompat(android.os.Build.VERSION_CODES.ECLAIR_MR1)) {
                            String imaName = FileUtil.getFileName(theLarge);
                            if (imaName != null)
                                bitmap = ImageUtils.loadImgThumbnail(EditActivity.this,
                                        imaName,
                                        MediaStore.Images.Thumbnails.MICRO_KIND);
                        }
                        if (bitmap == null && !StringUtils.isEmpty(theLarge))
                            bitmap = ImageUtils
                                    .loadImgThumbnail(theLarge, 100, 100);
                    } else if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA) {
                        // 拍摄图片
                        if (bitmap == null && !StringUtils.isEmpty(theLarge)) {
                            bitmap = ImageUtils
                                    .loadImgThumbnail(theLarge, 100, 100);
                        }
                    }

                    if (bitmap != null) {// 存放照片的文件夹
                        String savePath = Environment.getExternalStorageDirectory()
                                .getAbsolutePath() + "/OSChina/Camera/";
                        File savedir = new File(savePath);
                        if (!savedir.exists()) {
                            savedir.mkdirs();
                        }

                        String largeFileName = FileUtil.getFileName(theLarge);
                        String largeFilePath = savePath + largeFileName;
                        // 判断是否已存在缩略图
                        if (largeFileName.startsWith("thumb_")
                                && new File(largeFilePath).exists()) {
                            theThumbnail = largeFilePath;
                            imgFile = new File(theThumbnail);
                        } else {
                            // 生成上传的800宽度图片
                            String thumbFileName = "thumb_" + largeFileName;
                            theThumbnail = savePath + thumbFileName;
                            if (new File(theThumbnail).exists()) {
                                imgFile = new File(theThumbnail);
                            } else {
                                try {
                                    // 压缩上传的图片
                                    ImageUtils.createImageThumbnail(EditActivity.this,
                                            theLarge, theThumbnail, 800, 80);
                                    imgFile = new File(theThumbnail);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        // 保存动弹临时图片
                        // ((AppContext) getApplication()).setProperty(
                        // tempTweetImageKey, theThumbnail);

                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = theThumbnail;
                        handler.sendMessage(msg);
                    }
                }
            }.start();
        }
        else{

            UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
                    resultCode);
            if (ssoHandler != null) {
                ssoHandler.authorizeCallBack(requestCode, resultCode, imageReturnIntent);
            }

            super.onActivityResult(requestCode, resultCode, imageReturnIntent);
        }
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     *
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showBackDialog();
        }
        return super.onKeyDown(keyCode, event);
    }
}
