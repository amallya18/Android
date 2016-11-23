package com.codepath.project.android.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.codepath.project.android.R;
import com.codepath.project.android.adapter.SearchResultsAdapter;
import com.codepath.project.android.fragments.HomeFragment;
import com.codepath.project.android.fragments.MyProductsFragment;
import com.codepath.project.android.fragments.UserDetailFragment;
import com.codepath.project.android.model.Product;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnSuggestionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    public static String[] columns = new String[]{"_id", "productId", "name", "image"};

    SearchView searchView;

    SearchResultsAdapter mSearchViewAdapter;

    final String CHANNEL_NAME = "abcd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setNavigationDrawer();
        addFragment();
        if(ParseUser.getCurrentUser() != null) {
            Log.e("quest", "onCreate: yes user");
            ParseInstallation pi = ParseInstallation.getCurrentInstallation();
            pi.put("user", ParseUser.getCurrentUser());
            pi.saveInBackground();
        } else {
            Log.e("quest", "onCreate: no user");
        }
    }

    private void addFragment(){
        HomeFragment homeFragment = HomeFragment.newInstance(1);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homeFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if(!TextUtils.isEmpty(searchView.getQuery())) {
            searchView.setQuery("", false);
            searchView.clearFocus();
            return;
        } if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setSearchView();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setSearchView() {
        searchView = (SearchView) findViewById(R.id.searchView);
        //searchView.requestFocus();
        searchView.setOnSuggestionListener(this);
        searchView.setIconifiedByDefault(false);
        mSearchViewAdapter = new SearchResultsAdapter(this, R.layout.search_result_list_item, null, columns, null, -1000);
        searchView.setSuggestionsAdapter(mSearchViewAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 1) {
                    loadSearchData(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 1) {
                    loadSearchData(newText);
                }
                return true;
            }
        });
    }

    private void loadSearchData(String newText) {
        ParseQuery<Product> query = ParseQuery.getQuery(Product.class);
        query.whereMatches("name", newText, "i");
        query.setLimit(5);
        query.findInBackground((objects, e) -> {
            if (e == null) {
                MatrixCursor matrixCursor = convertToCursor(objects);
                mSearchViewAdapter.changeCursor(matrixCursor);
            }
        });
    }

    private MatrixCursor convertToCursor(List<Product> products) {
        MatrixCursor cursor = new MatrixCursor(columns);
        int i = 0;
        for (Product product : products) {
            String[] temp = new String[4];
            i = i + 1;
            temp[0] = Integer.toString(i);
            temp[1] = product.getObjectId();
            temp[2] = product.getName();
            temp[3] = product.getImageUrl();
            cursor.addRow(temp);
        }
        return cursor;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return goToProductDetailView(position);
    }

    @Override
    public boolean onSuggestionClick(int position) {
        return goToProductDetailView(position);
    }

    private boolean goToProductDetailView(int position) {
        Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
        String productId = cursor.getString(1);
        searchView.clearFocus();
        Intent intent = new Intent(this, ProductViewActivity.class);
        intent.putExtra("productId", productId);
        startActivity(intent);
        return true;
    }

    private void setNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView tvUserName = (TextView) header.findViewById(R.id.tvUserName);
        if (ParseUser.getCurrentUser() == null) {
            tvUserName.setText("Hello, Sandeep");
        } else {
            ParseUser.getCurrentUser().fetchIfNeededInBackground((object, e) -> {
                if(object.get("firstName") != null) {
                    tvUserName.setText("Hello, " + object.get("firstName"));
                } else {
                    tvUserName.setText("Hello, Sandeep");
                }
            });
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.nav_logout) {
            ParseUser.logOut();
            Intent logoutIntent = new Intent(this, SplashScreenActivity.class);
            startActivity(logoutIntent);
        }

        Fragment fragment = null;
        Class fragmentClass;

        switch (item.getItemId()) {
            case R.id.nav_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_my_products:
                fragmentClass = MyProductsFragment.class;
                break;
            case R.id.nav_about_me:
                fragmentClass = UserDetailFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
                break;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        drawer.closeDrawer(GravityCompat.START);

        item.setChecked(true);
        setTitle(item.getTitle());

        return true;
    }
}
