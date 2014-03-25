package com.travelapp;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboHandler.Response;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;

public class SocialActivity extends Activity implements IWeiboHandler.Response {
	private ImageView mBackImageView;
	private TextView mTitleTextView;
	private static ImageView mCameraImageView;
	private ImageView mLeft;
	private ImageView mRight;
	private String imageFilePath;
	private static Bitmap mCamera;
	private Button mShareWBButton;
	private Button mShareWXButton;
	private SsoHandler mSsoHandler;
	private Oauth2AccessToken mAccessToken;
	private IWeiboShareAPI mWeiboShareAPI;
	private int CAMERA_RESULT = 0;
	private boolean isCamera = false;
	private boolean hasWB = false;
	private boolean hasWX = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social);
		initView();
		initCamera();
		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
		// 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
		// 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
		// 失败返回 false，不调用上述回调
		if (savedInstanceState != null) {
			mWeiboShareAPI.handleWeiboResponse(getIntent(), (Response) this);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// 如果拍照成功
		if (resultCode == RESULT_OK) {
			// 取得屏幕的显示大小
			Display currentDisplay = getWindowManager().getDefaultDisplay();
			int dw = currentDisplay.getWidth();
			int dh = currentDisplay.getHeight();

			// 对拍出的照片进行缩放
			BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
			bmpFactoryOptions.inJustDecodeBounds = true;
			mCamera = BitmapFactory
					.decodeFile(imageFilePath, bmpFactoryOptions);
			int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight
					/ (float) dh);
			int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
					/ (float) dw);

			if (heightRatio > 1 && widthRatio > 1) {

				if (heightRatio > widthRatio) {

					bmpFactoryOptions.inSampleSize = heightRatio;
				} else {
					bmpFactoryOptions.inSampleSize = widthRatio;
				}

			}

			bmpFactoryOptions.inJustDecodeBounds = false;
			mCamera = BitmapFactory
					.decodeFile(imageFilePath, bmpFactoryOptions);

			mCameraImageView.setImageBitmap(mCamera);
			if (mSsoHandler != null) {
				mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
			isCamera = false;
		} else if ((resultCode == RESULT_CANCELED) && isCamera) {
			Intent intent = new Intent(SocialActivity.this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			SocialActivity.this.startActivity(intent);
			SocialActivity.this.finish();
			SocialActivity.this.overridePendingTransition(
					R.anim.anim_in_left2right, R.anim.anim_out_left2right);
		} else if (hasWB) {
			Toast.makeText(this, R.string.weibosdk_demo_toast_share_success,
					Toast.LENGTH_LONG).show();
			mShareWBButton.setText("已" + getString(R.string.share_wb));
			mShareWBButton.setEnabled(false);
			if (hasWX) {
				Intent intent = new Intent(SocialActivity.this,
						HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				SocialActivity.this.startActivity(intent);
				SocialActivity.this.finish();
				SocialActivity.this.overridePendingTransition(
						R.anim.anim_in_left2right, R.anim.anim_out_left2right);
			}
		}

	}

	/**
	 * 接收微客户端博请求的数据。 当微博客户端唤起当前应用并进行分享时，该方法被调用。
	 * 
	 * @param baseRequest
	 *            微博请求数据对象
	 * @see {@link IWeiboShareAPI#handleWeiboRequest}
	 */
	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			Toast.makeText(this, R.string.weibosdk_demo_toast_share_success,
					Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			Toast.makeText(this, R.string.weibosdk_demo_toast_share_canceled,
					Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			Toast.makeText(
					this,
					getString(R.string.weibosdk_demo_toast_share_failed)
							+ "Error Message: " + baseResp.errMsg,
					Toast.LENGTH_LONG).show();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			TravelApplication.buildExitDialog(SocialActivity.this);
		}
		return false;
	}

	private void initCamera() {
		// TODO Auto-generated method stub
		isCamera = true;
		imageFilePath = SocialActivity.this.getExternalFilesDir(null)
				+ "/mypicture.jpg";
		File imageFile = new File(imageFilePath);
		Uri imageFileUri = Uri.fromFile(imageFile);

		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);

		startActivityForResult(i, CAMERA_RESULT);
	}

	private void initView() {
		// TODO Auto-generated method stub
		mBackImageView = (ImageView) findViewById(R.id.imgListBack);
		mTitleTextView = (TextView) findViewById(R.id.txtListTitle);
		mLeft = (ImageView) findViewById(R.id.imgLeft);
		mRight = (ImageView) findViewById(R.id.imgRight);
		mCameraImageView = (ImageView) findViewById(R.id.imgCamera);
		mTitleTextView.setText("分享");
		mShareWBButton = (Button) findViewById(R.id.btnShareWB);
		mShareWXButton = (Button) findViewById(R.id.btnShareWX);
		mBackImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mCamera != null) {
					mCamera.recycle();
				}
				Intent intent = new Intent(SocialActivity.this,
						HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				SocialActivity.this.startActivity(intent);
				SocialActivity.this.finish();
				SocialActivity.this.overridePendingTransition(
						R.anim.anim_in_left2right, R.anim.anim_out_left2right);
			}
		});
		mRight.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCameraImageView.setDrawingCacheEnabled(true);
				Bitmap mBitmap = mCameraImageView.getDrawingCache();
				if (mBitmap != null) {
					mCameraImageView.setImageBitmap(rotateImage(mBitmap, 0));
				}
				mBitmap.recycle();
				mCameraImageView.setDrawingCacheEnabled(false);
			}
		});
		mLeft.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCameraImageView.setDrawingCacheEnabled(true);
				Bitmap mBitmap = mCameraImageView.getDrawingCache();
				if (mBitmap != null) {
					mCameraImageView.setImageBitmap(rotateImage(mBitmap, 1));
				}
				mBitmap.recycle();
				mCameraImageView.setDrawingCacheEnabled(false);
			}
		});
		mShareWBButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendWBMeg();
			}
		});
		mShareWXButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendWXMeg();
			}
		});
		// 创建微博分享接口实例
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this,
				getApplicationContext().getString(R.string.wb_app_id));
	}

	public static Bitmap rotateImage(Bitmap bgimage, int direction) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 旋转图片
		switch (direction) {
		case 0:
			matrix.postRotate(90);
			break;
		case 1:
			matrix.postRotate(-90);
		default:
			break;
		}
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		mCamera = Bitmap.createBitmap(mCamera, 0, 0, (int) mCamera.getWidth(),
				(int) mCamera.getHeight(), matrix, true);
		return bitmap;
	}

	private void sendWXMeg() {
		hasWX = true;
		// 初始化一个WXImageObject对象
		mCameraImageView.setDrawingCacheEnabled(true);

		WXImageObject imgObj = new WXImageObject(mCamera);

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;
		msg.title = "Trvael_app";
		msg.thumbData = getBitmapBytes(mCamera, false); // 设置缩略图

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;

		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		// 调用api接口发送数据到微信
		boolean isSend = TravelApplication.mIwxapi.sendReq(req);
		if (isSend) {
			Log.e("WX", "OK");
			mShareWXButton.setText("已" + getString(R.string.share_wx));
			mShareWXButton.setEnabled(false);
			if (hasWB) {
				Intent intent = new Intent(SocialActivity.this,
						HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				SocialActivity.this.startActivity(intent);
				SocialActivity.this.finish();
				SocialActivity.this.overridePendingTransition(
						R.anim.anim_in_left2right, R.anim.anim_out_left2right);
			}
		} else {
			Log.e("WX", "Error");
		}
		mCameraImageView.setDrawingCacheEnabled(false);
	}

	private static byte[] getBitmapBytes(Bitmap bitmap, boolean paramBoolean) {
		Bitmap localBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);
		Canvas localCanvas = new Canvas(localBitmap);
		int i;
		int j;
		if (bitmap.getHeight() > bitmap.getWidth()) {
			i = bitmap.getWidth();
			j = bitmap.getWidth();
		} else {
			i = bitmap.getHeight();
			j = bitmap.getHeight();
		}
		mCameraImageView.setDrawingCacheEnabled(true);
		while (true) {
			localCanvas.drawBitmap(bitmap, new Rect(0, 0, i, j), new Rect(0, 0,
					80, 80), null);
			if (paramBoolean)
				bitmap.recycle();
			ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
					localByteArrayOutputStream);
			localBitmap.recycle();
			byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
			try {
				localByteArrayOutputStream.close();
				return arrayOfByte;
			} catch (Exception e) {
				Log.e("SendWX", e.toString());
			}
			i = bitmap.getHeight();
			j = bitmap.getHeight();
		}
	}

	private void sendWBMeg() {
		hasWB = true;
		getTokenBySSO();
		// 检查微博客户端环境是否正常，如果未安装微博，弹出对话框询问用户下载微博客户端
		if (mWeiboShareAPI.checkEnvironment(true)) {
			mWeiboShareAPI.registerApp();
		}
		// 1. 初始化微博的分享消息
		WeiboMultiMessage mWeiboMultiMessage = new WeiboMultiMessage();

		mWeiboMultiMessage.textObject = getTextObj();
		mWeiboMultiMessage.imageObject = getImageObj();
		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = mWeiboMultiMessage;

		// 3. 发送请求消息到微博，唤起微博分享界面
		mWeiboShareAPI.sendRequest(request);

	}

	private void getTokenBySSO() {
		mSsoHandler = new SsoHandler(SocialActivity.this,
				TravelApplication.mWeiboAuth);
		mSsoHandler.authorize(new AuthListener());
	}

	/**
	 * 创建文本消息对象。
	 * 
	 * @return 文本消息对象。
	 */
	private TextObject getTextObj() {
		TextObject textObject = new TextObject();
		textObject.text = SocialActivity.this.getString(R.string.share_text);
		return textObject;
	}

	/**
	 * 创建图片消息对象。
	 * 
	 * @return 图片消息对象。
	 */
	private ImageObject getImageObj() {
		ImageObject imageObject = new ImageObject();
		imageObject.setImageObject(mCamera);
		return imageObject;
	}

	/**
	 * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
	 * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
	 * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
	 * SharedPreferences 中。
	 */
	class AuthListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken.isSessionValid()) {
				// 显示 Token
				// updateTokenView(false);

				// 保存 Token 到 SharedPreferences
				AccessTokenKeeper.writeAccessToken(SocialActivity.this,
						mAccessToken);
				Toast.makeText(SocialActivity.this,
						R.string.weibosdk_demo_toast_auth_success,
						Toast.LENGTH_SHORT).show();
			} else {
				// 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
				String code = values.getString("code");
				String message = getString(R.string.weibosdk_demo_toast_auth_failed);
				if (!TextUtils.isEmpty(code)) {
					message = message + "\nObtained the code: " + code;
				}
				Toast.makeText(SocialActivity.this, message, Toast.LENGTH_LONG)
						.show();
			}
		}

		@Override
		public void onCancel() {
			Toast.makeText(SocialActivity.this,
					R.string.weibosdk_demo_toast_auth_canceled,
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(SocialActivity.this,
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}
}
