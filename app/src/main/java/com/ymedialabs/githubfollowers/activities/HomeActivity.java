package com.ymedialabs.githubfollowers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ymedialabs.githubfollowers.R;
import com.ymedialabs.githubfollowers.adapters.UserAdapter;
import com.ymedialabs.githubfollowers.models.User;
import com.ymedialabs.githubfollowers.network.IGithubApi;
import com.ymedialabs.githubfollowers.network.RestClient;
import com.ymedialabs.githubfollowers.uiutils.GridSpacingItemDecoration;
import com.ymedialabs.githubfollowers.uiutils.UiConstants;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, UserAdapter.CustomOnClickListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private TextView tvEmptyList;
    private UserAdapter adapter;
    private View progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        setViews();
    }

    private void setViews() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvEmptyList = (TextView) findViewById(R.id.tvEmptyList);
        progressView = (ProgressBar) findViewById(R.id.progressBar);
        fetchFollowers("JakeWharton");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * If photo list not loaded yet, set an empty view to GridView
     */
    private void setEmptyView() {

        tvEmptyList.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        searchView.clearFocus();
        fetchFollowers(query);
        return true;
    }

    /**
     * Fetch follower list from server
     */
    private void fetchFollowers(String query) {

        showProgressBar();
        RestClient client = new RestClient();
        IGithubApi apiInterface = client.createService();
        Call<ArrayList<User>> call = apiInterface.getUserFollowers(query);
        call.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {

                hideProgressBar();
                ArrayList<User> followerList = response.body();

                if(followerList == null && followerList.size()<=0)
                    setEmptyView();
                else
                    loadViews(followerList);
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {

                hideProgressBar();
                Toast.makeText(HomeActivity.this,getString(R.string.net_comm_failure),Toast.LENGTH_SHORT).show();
                Log.d(TAG, "response is " + t.getMessage());
            }
        });
    }

    private void loadViews(ArrayList<User> followerList) {

        tvEmptyList.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new UserAdapter(this, followerList);
        adapter.setClickListener(this);

        recyclerView.addItemDecoration(new GridSpacingItemDecoration(this, 2, true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

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
    public boolean onQueryTextChange(String newText) {

        //fetchFollowers(newText);
        return true;
    }

    /**
     * custom click event
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(View view, int position,String loginName) {

        CircleImageView ivAvtar = (CircleImageView) view.findViewById(R.id.ivAvtar);
        TextView tvLoginName = (TextView) findViewById(R.id.tvName);

        Intent intent = new Intent(HomeActivity.this,UserDetailsActivity.class);
        intent.putExtra(UiConstants.LOGIN_NAME,loginName);
        // shared elements for transition
        Pair<View, String> pair1 = Pair.create((View)ivAvtar, getString(R.string.transitionProfile));
        Pair<View, String> pair2 = Pair.create((View)tvLoginName, getString(R.string.transitionLoginName));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,pair1,pair2);
        startActivity(intent,options.toBundle());
    }
}
