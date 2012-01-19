package com.thuzi.FBSimpleSample;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FBSimpleSampleActivity extends Activity {
	/** Called when the activity is first created. */
	Facebook facebook = new Facebook("YOUR_APP_ID");
	ImageButton login;
	ImageButton logout;
	ImageButton showlike;
	ImageButton hidelike;
	WebView wv;
	AsyncFacebookRunner Runner = new AsyncFacebookRunner(facebook);
	String FILENAME = "AndroidSSO_data";
	private SharedPreferences mPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// buttons
		login = (ImageButton) findViewById(R.id.loginButton);
		logout = (ImageButton) findViewById(R.id.logoutButton);
		showlike = (ImageButton) findViewById(R.id.showLike);
		hidelike = (ImageButton) findViewById(R.id.hideLike);

		//hide show/hide for now
		hidelike.setVisibility(ImageButton.INVISIBLE);
		showlike.setVisibility(ImageButton.INVISIBLE);
		
		//webview
		wv = (WebView) findViewById(R.id.wv);

		// button listeners
		addListenerForLogin();
		addListenerForLogout();
		addListenerForShowLike();
		addListenerForHideLike();

		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);
		if (access_token != null) {
			facebook.setAccessToken(access_token);
			login.setVisibility(ImageButton.INVISIBLE);
			logout.setVisibility(ImageButton.VISIBLE);
			showlike.setVisibility(ImageButton.VISIBLE);
		}
		else{
			login.setVisibility(ImageButton.VISIBLE);
			logout.setVisibility(ImageButton.INVISIBLE);
			showlike.setVisibility(ImageButton.INVISIBLE);
		}
		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

	}

	public void addListenerForLogin() {
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				fbLogin();
			}

		});
	}

	public void addListenerForLogout() {
		logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				fbLogout();
			}

		});
	}

	public void addListenerForShowLike() {
		showlike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showLike();
			}

		});
	}

	public void addListenerForHideLike() {
		hidelike = (ImageButton) findViewById(R.id.hideLike);
		hidelike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hideLike();
			}

		});
	}

	public void fbLogin() {
		facebook.authorize(this,
				new String[] { "user_likes", "user_about_me" },
				new DialogListener() {
					@Override
					public void onComplete(Bundle values) {
						SharedPreferences.Editor editor = mPrefs.edit();
						editor.putString("access_token",
								facebook.getAccessToken());
						editor.putLong("access_expires",
								facebook.getAccessExpires());
						editor.commit();
						login.setVisibility(ImageButton.INVISIBLE);
						logout.setVisibility(ImageButton.VISIBLE);
						showlike.setVisibility(ImageButton.VISIBLE);
						hidelike.setVisibility(ImageButton.INVISIBLE);
					}

					@Override
					public void onFacebookError(FacebookError error) {
						Log.d("Error", "Some kind of facebook error");
					}

					@Override
					public void onError(DialogError e) {
						Log.d("Error", "Some kind of facebook error");
					}

					@Override
					public void onCancel() {
					}
				});

	}

	public void fbLogout() {
		try {
			Boolean loggedOut = new Boolean(facebook.logout(this));
			if (loggedOut) {
				login.setVisibility(ImageButton.VISIBLE);
				logout.setVisibility(ImageButton.INVISIBLE);
				showlike.setVisibility(ImageButton.INVISIBLE);
				hidelike.setVisibility(ImageButton.INVISIBLE);
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void showLike() {
		showlike.setVisibility(ImageButton.INVISIBLE);
		hidelike.setVisibility(ImageButton.VISIBLE);
		wv.setVisibility(WebView.VISIBLE);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.loadUrl("file:///android_asset/like.html");
	}

	public void hideLike() {
		showlike.setVisibility(ImageButton.VISIBLE);
		hidelike.setVisibility(ImageButton.INVISIBLE);
		wv.setVisibility(WebView.INVISIBLE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		facebook.authorizeCallback(requestCode, resultCode, data);
	}

}