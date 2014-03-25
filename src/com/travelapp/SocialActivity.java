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
		// �� Activity �����³�ʼ��ʱ���� Activity ���ں�̨ʱ�����ܻ������ڴ治�㱻ɱ���ˣ���
		// ��Ҫ���� {@link IWeiboShareAPI#handleWeiboResponse} ������΢���ͻ��˷��ص����ݡ�
		// ִ�гɹ������� true�������� {@link IWeiboHandler.Response#onResponse}��
		// ʧ�ܷ��� false�������������ص�
		if (savedInstanceState != null) {
			mWeiboShareAPI.handleWeiboResponse(getIntent(), (Response) this);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// ������ճɹ�
		if (resultCode == RESULT_OK) {
			// ȡ����Ļ����ʾ��С
			Display currentDisplay = getWindowManager().getDefaultDisplay();
			int dw = currentDisplay.getWidth();
			int dh = currentDisplay.getHeight();

			// ���ĳ�����Ƭ��������
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
			mShareWBButton.setText("��" + getString(R.string.share_wb));
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
	 * ����΢�ͻ��˲���������ݡ� ��΢���ͻ��˻���ǰӦ�ò����з���ʱ���÷��������á�
	 * 
	 * @param baseRequest
	 *            ΢���������ݶ���
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
		mTitleTextView.setText("����");
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
		// ����΢������ӿ�ʵ��
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this,
				getApplicationContext().getString(R.string.wb_app_id));
	}

	public static Bitmap rotateImage(Bitmap bgimage, int direction) {
		// ��ȡ���ͼƬ�Ŀ�͸�
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// ��������ͼƬ�õ�matrix����
		Matrix matrix = new Matrix();
		// ��תͼƬ
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
		// ��ʼ��һ��WXImageObject����
		mCameraImageView.setDrawingCacheEnabled(true);

		WXImageObject imgObj = new WXImageObject(mCamera);

		// ��WXTextObject�����ʼ��һ��WXMediaMessage����
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;
		msg.title = "Trvael_app";
		msg.thumbData = getBitmapBytes(mCamera, false); // ��������ͼ

		// ����һ��Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;

		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		// ����api�ӿڷ������ݵ�΢��
		boolean isSend = TravelApplication.mIwxapi.sendReq(req);
		if (isSend) {
			Log.e("WX", "OK");
			mShareWXButton.setText("��" + getString(R.string.share_wx));
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
		// ���΢���ͻ��˻����Ƿ����������δ��װ΢���������Ի���ѯ���û�����΢���ͻ���
		if (mWeiboShareAPI.checkEnvironment(true)) {
			mWeiboShareAPI.registerApp();
		}
		// 1. ��ʼ��΢���ķ�����Ϣ
		WeiboMultiMessage mWeiboMultiMessage = new WeiboMultiMessage();

		mWeiboMultiMessage.textObject = getTextObj();
		mWeiboMultiMessage.imageObject = getImageObj();
		// 2. ��ʼ���ӵ�������΢������Ϣ����
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// ��transactionΨһ��ʶһ������
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = mWeiboMultiMessage;

		// 3. ����������Ϣ��΢��������΢���������
		mWeiboShareAPI.sendRequest(request);

	}

	private void getTokenBySSO() {
		mSsoHandler = new SsoHandler(SocialActivity.this,
				TravelApplication.mWeiboAuth);
		mSsoHandler.authorize(new AuthListener());
	}

	/**
	 * �����ı���Ϣ����
	 * 
	 * @return �ı���Ϣ����
	 */
	private TextObject getTextObj() {
		TextObject textObject = new TextObject();
		textObject.text = SocialActivity.this.getString(R.string.share_text);
		return textObject;
	}

	/**
	 * ����ͼƬ��Ϣ����
	 * 
	 * @return ͼƬ��Ϣ����
	 */
	private ImageObject getImageObj() {
		ImageObject imageObject = new ImageObject();
		imageObject.setImageObject(mCamera);
		return imageObject;
	}

	/**
	 * ΢����֤��Ȩ�ص��ࡣ 1. SSO ��Ȩʱ����Ҫ�� {@link #onActivityResult} �е���
	 * {@link SsoHandler#authorizeCallBack} �� �ûص��Żᱻִ�С� 2. �� SSO
	 * ��Ȩʱ������Ȩ�����󣬸ûص��ͻᱻִ�С� ����Ȩ�ɹ����뱣��� access_token��expires_in��uid ����Ϣ��
	 * SharedPreferences �С�
	 */
	class AuthListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			// �� Bundle �н��� Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken.isSessionValid()) {
				// ��ʾ Token
				// updateTokenView(false);

				// ���� Token �� SharedPreferences
				AccessTokenKeeper.writeAccessToken(SocialActivity.this,
						mAccessToken);
				Toast.makeText(SocialActivity.this,
						R.string.weibosdk_demo_toast_auth_success,
						Toast.LENGTH_SHORT).show();
			} else {
				// ����ע���Ӧ�ó���ǩ������ȷʱ���ͻ��յ� Code����ȷ��ǩ����ȷ
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
