package com.example.hw9;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ScrollingActivity extends AppCompatActivity {

    private static final String[] COUNTRIES = new String[] {
            "Belgium", "France", "Italy", "Germany", "Spain"
    };
    private static final String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private RequestQueue queue;
    private String url ="http://csci571-hw8-dehuo-2.us-east-1.elasticbeanstalk.com/query/";
    private String url2 ="http://csci571-hw8-dehuo-2.us-east-1.elasticbeanstalk.com/item/";
    private String ticker = "";
    private String keyword = "";
    private Contact[] resContact;
    //String dataArr[] = {"test1", "test2"};
    private ArrayList<String> dataArrList;
    private ArrayAdapter<String> newsAdapter;
    private TextView mContent;
    private Gson gson;

    private SharedPreferences sharedPreferencesTotal;

    private List<Company> portfolioList, watchlistList;
    private String[] tmpPortfolioList, tmpWatchlistList;
    private Map<String, Company> allItems;
    private Map<String, String> tickerToName;
    private Map<String, Integer> tickerToShare;
    private Set<String> allItemsTicker;

    private java.text.DecimalFormat df;

    private Handler handler = new Handler();
    private Runnable runnable;
    private int delay = 15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.ThemeHW9NoActionBar);
        super.onCreate(savedInstanceState);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        queue = Volley.newRequestQueue(this);
        gson = new Gson();
        dataArrList = new ArrayList<>();
        df = new java.text.DecimalFormat("0.00");
        //System.out.println("111111111111111111111111111111111111111111111111");
        update2();

//        tmpPortfolioList = new ArrayList<>();
//        tmpWatchlistList = new ArrayList<>();


//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, COUNTRIES);
//        AutoCompleteTextView textView = (AutoCompleteTextView)
//                findViewById(R.id.autocomplete);
//        textView.setAdapter(adapter);
//        // Get ActionBar
//        ActionBar actionBar = getSupportActionBar();
//        // Set below attributes to add logo in ActionBar.
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayUseLogoEnabled(true);
//        actionBar.setLogo(R.drawable.icon_tech_32);

//        actionBar.setTitle("dev2qa.com - Search Example");
//        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
//        toolBarLayout.setTitle(getTitle());

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void update() {
        String reqeustString = url2;
        for(String e : allItemsTicker)
            if(!e.equals(""))
                reqeustString += e + ",";
            if(reqeustString.equals(url2))
            {
                setContentView(R.layout.activity_scrolling);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                return;
            }
            //params = new JSONArray(reqeustString);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, reqeustString, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray res = response.getJSONArray("data");
//                            resString = response.toString();
                            if(!response.toString().equals("{}")) {
                                for(int i = 0; i < res.length(); i++)
                                {
                                    JSONObject o = res.getJSONObject(i);
                                    String tmpTicker = o.getString("ticker").toLowerCase();
                                    double last = o.getDouble("last");
                                    double change = last - o.getDouble("prevClose");
                                    allItems.put(tmpTicker, new Company(tmpTicker, tickerToName.get(tmpTicker), last, change, tickerToShare.get(tmpTicker)));
                                }
                                createView();
                            }
                            else {

                            }
                        } catch (JSONException e) {
                            System.out.println("??????????" + e.toString());
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
            queue.add(jsonObjectRequest);

    }

    private void createView() {
        portfolioList = new ArrayList<>();
        watchlistList = new ArrayList<>();
        double net = 0;
        for(String e : tmpPortfolioList) {
            if(!e.equals("")) {
                Company eTmp = allItems.get(e);
                portfolioList.add(eTmp);
                net += eTmp.last * eTmp.share;
            }
        }
        for(String e : tmpWatchlistList)
            if(!e.equals(""))
                watchlistList.add(allItems.get(e));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentManager fm = getSupportFragmentManager();
        PortfolioRecyclerViewFragment fragment = new PortfolioRecyclerViewFragment(portfolioList, fm, 1);
        PortfolioRecyclerViewFragment fragment2 = new PortfolioRecyclerViewFragment(watchlistList, fm, 2);

        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView netWorth = (TextView) findViewById(R.id.net_worth_2);
        netWorth.setText(df.format(net));

        TextView link = (TextView) findViewById(R.id.linkBottom);
        link.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setData(Uri.parse("http://www.tiingo.com/"));
            intent.setAction(Intent.ACTION_VIEW);
            startActivity(intent);
        });

        Calendar calendar = new GregorianCalendar();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int date = calendar.get(Calendar.DAY_OF_MONTH);

        TextView main_date = (TextView) findViewById(R.id.main_date);
        main_date.setText(months[month] + " " + date + ", " + year);

        transaction.replace(R.id.portfolio_content_fragment, fragment);
        transaction.replace(R.id.watchlist_content_fragment, fragment2);
        transaction.commit();
    }
    @Override
    protected void onResume() {
        super.onResume();
//        handler.postDelayed(runnable = new Runnable() {
//            public void run() {
//                handler.postDelayed(runnable, delay);
//                update2();
//            }
//        }, delay);
        update2();



//        View decorView = getWindow().getDecorView();
//        // Hide the status bar.
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
//        // Remember that you should never show the action bar if the
//        // status bar is hidden, so hide that too if necessary.
//        ActionBar actionBar = getActionBar();
//        actionBar.hide();
    }
    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();//stop handler when activity not visible super.onPause();
    }

    private void update2() {
        System.out.println("Fetching Data...");
        setContentView(R.layout.spin);
//        View decorView = getWindow().getDecorView();
//        // Hide the status bar.
//        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//        decorView.setSystemUiVisibility(uiOptions);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sharedPreferencesTotal= getSharedPreferences("total",Context.MODE_PRIVATE);
        SharedPreferences.Editor editorTotal = sharedPreferencesTotal.edit();
        if(sharedPreferencesTotal.getString("total", null) == null) {
            editorTotal.putString("total", "20000");
            editorTotal.commit();
        }
        if(sharedPreferencesTotal.getString("portfolio", null) == null) {
            editorTotal.putString("portfolio", "^");
            editorTotal.commit();
        }
        else
        {
            String tmp = sharedPreferencesTotal.getString("portfolio", null);
            tmpPortfolioList = tmp.substring(1).split(";");
        }
        if(sharedPreferencesTotal.getString("watchlist", null) == null) {
            editorTotal.putString("watchlist", "^");
            editorTotal.commit();
        }
        else
        {
            String tmp = sharedPreferencesTotal.getString("watchlist", null);
            tmpWatchlistList = tmp.substring(1).split(";");
        }
//        System.out.println(tmpWatchlistList.length);
        allItemsTicker = new HashSet<String>(Arrays.asList(tmpPortfolioList));
        allItemsTicker.addAll(Arrays.asList(tmpWatchlistList));
        tickerToName = new HashMap<>();
        tickerToShare = new HashMap<>();
        allItems = new HashMap<>();
        for(String e : allItemsTicker) {
            tickerToName.put(e, sharedPreferencesTotal.getString(e + ":name", "null"));
            tickerToShare.put(e, sharedPreferencesTotal.getInt(e, 0));
        }
        editorTotal.commit();
//        dataArrList.add("test1");
//
        update();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_scrolling, menu);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_scrolling, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) ScrollingActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(ScrollingActivity.this.getComponentName()));
        }
        // Get SearchView autocomplete object.
//        AutoCompleteTextView textView = (AutoCompleteTextView)
//                findViewById(R.id.autocomplete);
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setBackgroundColor(Color.WHITE);
        searchAutoComplete.setTextColor(Color.BLACK);
//        searchAutoComplete.setDropDownBackgroundResource(android.R.color.holo_blue_light);
//
        // Create a new ArrayAdapter and add data to search auto complete object.
//        String dataArr[] = {"Apple" , "Amazon" , "Amd", "Microsoft", "Microwave", "MicroNews", "Intel", "Intelligence"};
        newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, dataArrList);
        searchAutoComplete.setAdapter(newsAdapter);

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);
                ticker = resContact[itemIndex].ticker.toLowerCase();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.contains(" - "))
                {
                    int index = query.indexOf(" - ");
                    ticker = query.substring(0, index).toLowerCase();
                }
                else
                    ticker = query;
                Intent intent = new Intent(ScrollingActivity.this, SearchableActivity.class);
                intent.putExtra("ticker",ticker);
                startActivity(intent);
                //ScrollingActivity.this.finish();

                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() >= 3) {
                    keyword = newText.trim();
                    String newQuery = url + keyword;
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, newQuery, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // Display the first 500 characters of the response string.
//                                resContact = (Contact)
//                                        gson.fromJson(response.toString(), Contact.class);
                                    String resString = "";
                                    try {
                                        resString = response.get("data").toString();
                                        if(!resString.equals("[]") && response.get("request").toString().equals(keyword)) {
                                            resContact = (Contact[]) gson.fromJson(resString, Contact[].class);

                                            newsAdapter.clear();
//                                            dataArrList.clear();
                                            for(Contact c : resContact)
                                            {
                                                newsAdapter.add(c.ticker + " - " + c.name);
                                            }
                                            newsAdapter.notifyDataSetChanged();

//                                            newsAdapter.add("000");
                                            //dataArr = dataArrList.toArray(new String[dataArrList.size()]);
                                            //newsAdapter.notifyDataSetChanged();
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    newsAdapter.notifyDataSetChanged();
//                                                }
//                                            });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //mContent.setText("That didn't work!" + error.toString());
                        }
                    });
                    queue.add(jsonObjectRequest);
                }
                else {
                    newsAdapter.clear();
                }
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getTitle().equals("Search")) {
//            Toast.makeText(getApplicationContext(), "Search = "+onSearchRequested(), Toast.LENGTH_LONG).show();
//            return onSearchRequested();
//        } else {
//            return false;
//        }
//    }
}

class Contact {
    public String name;
    public String ticker;
//    static class Contact2 {
//        public String name;
//        public String ticker;
//    }
}