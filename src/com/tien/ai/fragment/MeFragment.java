/**
 * 
 */
package com.tien.ai.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tien.ai.AIApplication;
import com.tien.ai.Constant;
import com.tien.ai.R;
import com.tien.ai.SuggestActivity;
import com.tien.ai.crop.CropImage;
import com.tien.ai.crop.CropUtil;
import com.tien.ai.demain.UserInfo;
import com.tien.ai.listener.NetworkErrorResponeListener;
import com.tien.ai.net.FormFile;
import com.tien.ai.net.HttpResult;
import com.tien.ai.net.ProtocolClient;
import com.tien.ai.utils.FileUtil;
import com.tien.ai.utils.ImageUtil;
import com.tien.ai.utils.NetworkUtil;
import com.tien.ai.utils.PreferenceUtils;
import com.tien.ai.utils.ToastUtil;
import com.tien.ai.utils.XLog;
import com.tien.ai.view.PullScrollView;
import com.tien.volley.Response;
import com.tien.volley.VolleyError;
import com.tien.volley.imagecache.ImageCacheManager;
import com.tien.volley.toolbox.ImageRequest;
import com.tien.volley.toolbox.JsonObjectRequest;
import com.tien.volley.toolbox.NetworkImageView;

/**
 * TODO
 * 
 * @author wangtianfei01
 * 
 */
public class MeFragment extends Fragment implements OnClickListener {
    
    private PullScrollView containerSV;
    private ImageView headIV;
    private NetworkImageView avatarTV;
    private TextView nicknameTV;
    private TextView moodTV;
    private TextView aicountTV;
    private EditText nicknameET;
    private EditText moodET;
    private Button modifyBtn;
    private Button suggestBtn;
    private UserInfo user;
    private boolean editStatus = false;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.me_view, null);
        
        // containerSV = (ScrollView)view.findViewById(R.id.container_sv);
        
        avatarTV = (NetworkImageView) view.findViewById(R.id.avator_iv);
        nicknameTV = (TextView) view.findViewById(R.id.nickname_tv);
        moodTV = (TextView) view.findViewById(R.id.mood_tv);
        aicountTV = (TextView) view.findViewById(R.id.ai_count);
        nicknameET = (EditText) view.findViewById(R.id.nickname_et);
        moodET = (EditText) view.findViewById(R.id.mood_et);
        modifyBtn = (Button) view.findViewById(R.id.modify_btn);
        suggestBtn = (Button) view.findViewById(R.id.suggest_btn);
        return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        modifyBtn.setEnabled(false);
        avatarTV.setOnClickListener(this);
        modifyBtn.setOnClickListener(this);
        suggestBtn.setOnClickListener(this);
        loadData(PreferenceUtils.loadUid());
        
    }
    
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.avator_iv) {
            showPicSelectDialog();
        } else if (v.getId() == R.id.suggest_btn) {
            Intent intent = new Intent(getActivity(), SuggestActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.modify_btn) {
            if (editStatus) {
                suggestBtn.setVisibility(View.GONE);
                String newNickname = nicknameET.getEditableText().toString().trim();
                String newMood = moodET.getEditableText().toString().trim();
                if (user.getNickname().equals(newNickname) && user.getMood().equals(newMood)) {
                    nicknameTV.setVisibility(View.VISIBLE);
                    moodTV.setVisibility(View.VISIBLE);
                    nicknameET.setVisibility(View.GONE);
                    moodET.setVisibility(View.GONE);
                    modifyBtn.setText("编辑");
                    editStatus = false;
                    return;
                }
                modify(newNickname, newMood);
            } else {
                editStatus = true;
                modifyBtn.setText("保存");
                nicknameTV.setVisibility(View.GONE);
                moodTV.setVisibility(View.GONE);
                nicknameET.setVisibility(View.VISIBLE);
                moodET.setVisibility(View.VISIBLE);
                suggestBtn.setVisibility(View.VISIBLE);
            }
        }
        
    }
    
    public static final int TAKE_PRICTURE = 1000;
    public static final int LOC_PRICTURE = 1001;
    public static final int PHOTO_RESULT = 1002;
    
    private void showPicSelectDialog() {
        Builder builder = new Builder(new ContextThemeWrapper(getActivity(),
            R.style.AlertDialogCustom));
        builder.setItems(R.array.pic_item, new DialogInterface.OnClickListener() {
            
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intentLocPic = new Intent(Intent.ACTION_GET_CONTENT);
                        intentLocPic.setType("image/*");
                        intentLocPic.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intentLocPic, LOC_PRICTURE);
                        break;
                    case 1:
                        // 调用相机
                        String status = Environment.getExternalStorageState();
                        if (status.equals(Environment.MEDIA_MOUNTED)) {
                            Intent intentTakePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            picPath = Environment.getExternalStorageDirectory() + "/ai" + "/";
                            FileUtil.createDir(picPath);
                            picPath += System.currentTimeMillis() + ".jpg";
                            // 照片保存路径
                            File myCaptureFile = new File(picPath);
                            Uri uri = Uri.fromFile(myCaptureFile);
                            intentTakePic.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            intentTakePic.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                            startActivityForResult(intentTakePic, TAKE_PRICTURE);
                        } else {
                            ToastUtil.shortToast("没有存储卡!");
                        }
                        break;
                    case 2:
                        break;
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    
    String picPath = "", prePicPath = "", cameraPic = "";
    Bitmap picBitmap = null;
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inTempStorage = new byte[1024 * 1024 * 5]; // 5MB的临时存储空间
        opt.inSampleSize = 2;
        opt.inJustDecodeBounds = false;
        
        if (requestCode == LOC_PRICTURE) {
            ContentResolver cr = getActivity().getContentResolver();
            // 相册
            if (data != null) {
                try {
                    Uri uri = data.getData();
                    if (uri != null) {
                        XLog.i("From Gallary result uri is:" + uri.toString());
                        if (uri.toString().contains("file://")) {
                            picPath = uri.getPath();
                            XLog.i("From Gallary file result path is:" + picPath);
                            if (ImageUtil.isImage(picPath)) {
                                // 缩放图片
                                picBitmap = BitmapFactory.decodeFile(picPath, opt);
                            } else {
                                ToastUtil.shortToast("图片路径不存在！");
                                return;
                            }
                        } else if (uri.toString().contains("content://")) {
                            picBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null,
                                opt);
                            // 获取实际路径
                            String[] proj = { MediaStore.Images.Media.DATA };
                            Cursor actualimagecursor = getActivity().managedQuery(uri, proj, null,
                                null, null);
                            int actual_image_column_index = actualimagecursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            actualimagecursor.moveToFirst();
                            // // 这里如果是调用媒体库返回的图片数据，则调用剪切程序进行剪切
                            picPath = actualimagecursor.getString(actual_image_column_index);
                            XLog.i("From Gallary content result path is:" + picPath);
                        }
                    }
                    
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    ToastUtil.shortToast("图片路径不存在！");
                }
            }
            cropImage();
            return;
        } else if (requestCode == TAKE_PRICTURE) {
            // 相机
            if (requestCode == TAKE_PRICTURE || requestCode == -1) {
                // 第二照相取消时，没有图片，使用第一次的图片
                if ("".equals(cameraPic)) {
                    File file = new File(prePicPath);
                    if (file.exists()) {
                        picPath = prePicPath;
                    }
                } else {
                    picPath = cameraPic;
                }
                // 缩放图片
                Options options = ImageUtil.getOptions(picPath);
                picBitmap = BitmapFactory.decodeFile(picPath);
            }
            cropImage();
            return;
        }
        // 调用图片剪切程序返回数据
        if (requestCode == PHOTO_RESULT) {
            XLog.i("is from crop image result");
            if (data != null) {
                picPath = data.getStringExtra("cropImagePath");
                XLog.i("is from crop image result cropImagePath:" + picPath);
                // 剪切后的图片 存放 sdcard/35ewave/photo
                picBitmap = BitmapFactory.decodeFile(picPath, opt);
                picBitmap = ImageUtil.toRoundCorner(picBitmap, 5);
                setPic();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void cropImage() {
        // 把得到的图片绑定在控件上显示
        if (picBitmap != null) { // 调用剪切程序
            // 将选择的图片等比例压缩后缓存到存储卡根目录，并返回图片文件
            File f = CropUtil.makeTempFile(picBitmap, "TEMPIMG");
            // 调用CropImage类对图片进行剪切
            Intent intent = new Intent(getActivity(), CropImage.class);
            Bundle extras = new Bundle();
            extras.putString("circleCrop", "true");
            extras.putInt("aspectX", 200);
            extras.putInt("aspectY", 200);
            intent.setDataAndType(Uri.fromFile(f), "image/jpeg");
            intent.putExtras(extras);
            startActivityForResult(intent, PHOTO_RESULT);
        }
    }
    
    private void updateUI() {
        modifyBtn.setEnabled(true);
        
        this.nicknameTV.setText(user.getNickname());
        this.nicknameET.setText(user.getNickname());
        if (!"".equals(user.getMood())) {
            this.moodTV.setText(user.getMood());
            this.moodET.setText(user.getMood());
        }
        this.aicountTV.setText(user.getAi_count() + "");
        
        String avatar = user.getAvatar();
        if (avatar == null || "".equals(avatar))
            return;
        
        avatarTV.setErrorImageResId(R.drawable.avatar_default);
        avatarTV.setImageResource(R.drawable.avatar_default);
        avatarTV.setDefaultImageResId(R.drawable.avatar_default);
        avatarTV.setImageUrl(avatar, ImageCacheManager.getInstance().getImageLoader());
        
//        ImageRequest imageRequest = new ImageRequest(avatar, new Response.Listener<Bitmap>() {
//            @Override
//            public void onResponse(Bitmap response) {
//                avatarTV.setImageBitmap(response);
//            }
//        }, 0, 0, Config.RGB_565, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                avatarTV.setImageResource(R.drawable.hugh);
//            }
//        });
        
//        AIApplication.getInstance().getRequestQueue().add(imageRequest);
    }
    
    private void modifyUI(String nickname, String mood) {
        user.setNickname(nickname);
        user.setMood(mood);
        
        editStatus = false;
        modifyBtn.setText("编辑");
        nicknameTV.setVisibility(View.VISIBLE);
        moodTV.setVisibility(View.VISIBLE);
        nicknameET.setVisibility(View.GONE);
        moodET.setVisibility(View.GONE);
        
        this.nicknameTV.setText(user.getNickname());
        this.moodTV.setText(user.getMood());
    }
    
    private void loadData(String uid) {
        
        String url = Constant.URL_USER_INFO;
        Map<String, String> params = NetworkUtil.initRequestParams();
        params.put("uid", uid);
        JsonObjectRequest request = new JsonObjectRequest(url, params,
            new Response.Listener<JSONObject>() {
                
                @Override
                public void onResponse(JSONObject response) {
                    int ret = response.optInt("ret");
                    if (ret == 0) {
                        JSONObject data = response.optJSONObject("data");
                        if (data != null) {
                            Gson gson = new Gson();
                            user = gson.fromJson(data.toString(), UserInfo.class);
                            updateUI();
                        }
                        
                    } else {
                        JSONObject data = response.optJSONObject("data");
                        if (data != null) {
                            String msg = data.optString("msg");
                            ToastUtil.shortToast(msg);
                        } else {
                        }
                    }
                }
                
            }, new NetworkErrorResponeListener(getActivity()) {
                
                @Override
                public void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                }
            });
        // 界面启动时优先使用缓存;
        request.setTag(this);
        AIApplication.getInstance().getRequestQueue().add(request);
    }
    
    private void modify(final String nickname, final String mood) {
        
        String url = Constant.URL_MODIFY_USER_INFO;
        Map<String, String> params = NetworkUtil.initRequestParams();
        params.put("nickname", nickname);
        params.put("mood", mood);
        JsonObjectRequest request = new JsonObjectRequest(url, params,
            new Response.Listener<JSONObject>() {
                
                @Override
                public void onResponse(JSONObject response) {
                    int ret = response.optInt("ret");
                    if (ret == 0) {
                        JSONObject data = response.optJSONObject("data");
                        if (data != null) {
                            modifyUI(nickname, mood);
                            ToastUtil.shortToast("保存成功！");
                        }
                        
                    } else {
                        JSONObject data = response.optJSONObject("data");
                        if (data != null) {
                            String msg = data.optString("msg");
                            ToastUtil.shortToast(msg);
                        } else {
                        }
                    }
                }
                
            }, new NetworkErrorResponeListener(getActivity()) {
                
                @Override
                public void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                }
            });
        // 界面启动时优先使用缓存;
        request.setTag(this);
        AIApplication.getInstance().getRequestQueue().add(request);
    }
    
    private void setPic() {
        // 把得到的图片绑定在控件上显示
        if (picBitmap != null) {
            new setAvatarAsyncTask().execute();
        }
    }
    
    // private void setImage(String url, final ImageView imageView) {
    // String imageName = url.substring(url.lastIndexOf('/') + 1);
    // imageView.setImageResource(R.drawable.default_avatar_large);
    // mAsyncImageLoader.loadImageImmediately(url, imageName, imageView);
    // }
    
    ProgressDialog mProgressDialog;
    
    /**
     * 
     * @Description:设置头像线程
     * @author:Tienfook Chang
     * @see:
     * @since:
     * @copyright © 35.com
     * @Date:2013-3-27
     */
    private class setAvatarAsyncTask extends AsyncTask<Void, Void, HttpResult> {
        
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", "正在上传...", true, false);
        }
        
        @Override
        protected HttpResult doInBackground(final Void... params) {
            HttpResult mHttpResult = null;
            try {
                if (!"".equals(picPath)) {
                    FormFile imageFile = FormFile.buildFormFile(picPath, 300, 300, "avatar");
                    FormFile[] formfiles = new FormFile[] { imageFile };
                    mHttpResult = setAvatar(formfiles, getActivity());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mHttpResult;
        }
        
        @Override
        protected void onPostExecute(HttpResult result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            if (result != null) {
                if (result.getStatus()) {
                    avatarTV.setImageBitmap(picBitmap);
                    ToastUtil.shortToast("上传成功");
                }
            } else {
                ToastUtil.shortToast("上传失败");
            }
        }
    }
    
    public HttpResult setAvatar(FormFile[] mFormFiles, Context mContext) {
        HttpResult mHttpResult = null;
        Map<String, String> params = NetworkUtil.initRequestParams();
        try {
            mHttpResult = ProtocolClient.postWithFile(mContext, params, mFormFiles);
            String json = mHttpResult.getJson();
            XLog.i("wanges:" + json);
            if (!"".equals(json)) {
                
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            mHttpResult = new HttpResult();
            mHttpResult.setStatus(false);
        }
        // 根据网络在UI以吐司方式提示
        return mHttpResult;
    }
    
}
