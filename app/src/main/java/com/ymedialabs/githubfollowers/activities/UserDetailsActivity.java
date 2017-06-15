package com.ymedialabs.githubfollowers.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ymedialabs.githubfollowers.R;
import com.ymedialabs.githubfollowers.models.User;
import com.ymedialabs.githubfollowers.network.IGithubApi;
import com.ymedialabs.githubfollowers.network.RestClient;
import com.ymedialabs.githubfollowers.uiutils.ImageLoader;
import com.ymedialabs.githubfollowers.uiutils.UiConstants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailsActivity extends AppCompatActivity {

    private static final String TAG = UserDetailsActivity.class.getSimpleName();
    private Toolbar toolbar;
    private ProgressBar progressView;
    private TextView tvLoginName;
    private TextView tvFullName;
    private TextView tvFollowersVal;
    private TextView tvFollowingVal;
    private TextView tvRepositories;
    private TextView tvEmail;
    private TextView tvLocation;
    private ImageView ivProfileImage;
    private RelativeLayout rlUserFollowers;
    private ImageLoader imgLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        progressView = (ProgressBar) findViewById(R.id.progressBar);

        Bundle bundle = getIntent().getExtras();
        String loginName = bundle.getString(UiConstants.LOGIN_NAME,"");
        if(!loginName.isEmpty()){
            setViews();
            fetchUserDetails(loginName);
            imgLoader = new ImageLoader(this);

        }else{
            this.finish();
        }
    }

    /**
     * initialize views
     */
    private void setViews() {

        tvLoginName = (TextView) findViewById(R.id.tvLoginName);
        tvFullName = (TextView) findViewById(R.id.tvFullName);
        tvFollowersVal = (TextView) findViewById(R.id.tvFollowersVal);
        tvFollowingVal = (TextView) findViewById(R.id.tvFollowingVal);
        tvRepositories = (TextView) findViewById(R.id.tvReposVal);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        rlUserFollowers = (RelativeLayout) findViewById(R.id.rlUserFollowers);
        ivProfileImage = (ImageView) findViewById(R.id.ivAvtar);
    }

    private void fetchUserDetails(String loginName) {

        showProgressBar();
        RestClient client = new RestClient();
        IGithubApi apiInterface = client.createService();
        Call<User> call = apiInterface.getUser(loginName);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                hideProgressBar();
                User user = response.body();
                setData(user);

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                hideProgressBar();
                Log.d(TAG, "response is " + t.getMessage());
                Toast.makeText(UserDetailsActivity.this,getString(R.string.net_comm_failure),Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * set data to views
     */
    private void setData(final User user) {

        if(user!=null){

            tvLoginName.setText(user.getLoginName());
            tvFullName.setText(user.getFullName());
            tvFollowersVal.setText(user.getFollowers());
            tvFollowingVal.setText(user.getFollowing());
            tvRepositories.setText(user.getRepositories());
            /**Picasso.with(this).load(user.getAvatarUrl()).placeholder(R.drawable.ic_face_black_24dp).error(R.drawable.ic_face_black_24dp).into(ivProfileImage);
            final BitmapManager bitmapManager = BitmapManager.getInstance();

            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {

                Bitmap bitmap=null;
                @Override
                public void run() {
                   bitmap = bitmapManager.downloadBitmap(user.getAvatarUrl(), 100, 100);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if(bitmap!=null)
                             ivProfileImage.setImageBitmap(bitmap);
                        }
                    });
                }
            };

            Thread threadRunnable = new Thread(runnable);
            threadRunnable.start();**/

            imgLoader.displayImage(user.getAvatarUrl(), ivProfileImage);

            String email = user.getEmail();
            if(email!=null && !email.isEmpty()) {

                tvEmail.setVisibility(View.VISIBLE);
                tvEmail.setText(email);
            }else {
                tvEmail.setVisibility(View.GONE);
            }

            String location = user.getLocation();
            if(location!=null && !location.isEmpty()) {

                tvEmail.setVisibility(View.VISIBLE);
                tvLocation.setText(location);
            }else{
                tvLocation.setVisibility(View.GONE);
            }
        }
    }

    public void showProgressBar() {
        // Show progress item
        if( progressView!=null) {
            progressView.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        // Hide progress item

        if( progressView!=null) {
            progressView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }
}
