package com.example.hw9;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.utils.EmptyViewHolder;


public class SearchableActivity extends AppCompatActivity {

    private String ticker, name;
    private String url ="http://csci571-hw8-dehuo-2.us-east-1.elasticbeanstalk.com/search/details/";
    private Gson gson;
    private RequestQueue queue;
    private JSONObject price, summary;
    private JSONArray news, history;

    private LinearLayout spinner;
    private WebView myWebView;
    private GridView simpleGrid;

    private java.text.DecimalFormat df;
    private double change = 0;
    private String[] gridData = {"Current Price: ","Low: ","Bid Price: ", "Open Price: ","Mid: ","High: ", "Volume: "};
    private GridAdapter gridAdapter;

    private SectionedRecyclerViewAdapter newsSectionedAdapter;

    private int share, amount;
    private double curPrice, total;
    private SharedPreferences sharedPreferencesTotal;
    private SharedPreferences.Editor editorTotal;

    private boolean favorite = false;
    private Handler handler = new Handler();
    private Runnable runnable;
    private int delay = 15000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.ThemeHW9NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spin);

        Intent intent = getIntent();
        ticker = intent.getStringExtra("ticker");

        queue = Volley.newRequestQueue(this);
        gson = new Gson();
        df = new java.text.DecimalFormat("0.00");
//        invalidateOptionsMenu();
        requestData();
        sharedPreferencesTotal= getSharedPreferences("total",Context.MODE_PRIVATE);
        editorTotal = sharedPreferencesTotal.edit();
        total = Double.valueOf(sharedPreferencesTotal.getString("total", null));
        // Get the intent, verify the action and get the query
//        Intent intent = getIntent();
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            //doMySearch(query);
//        }
    }
    @Override
    protected void onResume() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.spin);
//        handler.postDelayed(runnable = new Runnable() {
//            public void run() {
//                handler.postDelayed(runnable, delay);
//                System.out.println("Fetching Data...");
//                requestData();
//            }
//        }, delay);
        super.onResume();
//      requestData();
    }
    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();//stop handler when activity not visible super.onPause();
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_scrolling, menu);
//
//        // Get the SearchView and set the searchable configuration
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.search_button).getActionView();
//        // Assumes current activity is the searchable activity
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
//
//        return true;
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_searchable, menu);

        MenuItem item = (MenuItem) menu.findItem(R.id.action_favorite);

        if(sharedPreferencesTotal.getString("watchlist", null).contains(";"+ticker+";") || sharedPreferencesTotal.getString("watchlist", null).contains("^"+ticker+";"))
        {
            item.setIcon(R.drawable.ic_star__1_);
            favorite = true;
        }
        else
        {
            item.setIcon(R.drawable.ic_star);
            favorite = false;
        }
//        item.getActionView().setOnClickListener(v ->{
//            if(!favorite) {
//                editorTotal.putString("watchlist", sharedPreferencesTotal.getString("watchlist", null) + ticker + ";");
//                item.setIcon(R.drawable.ic_star__1_);
//                favorite = true;
//            }
//            else
//            {
//                String tmp = sharedPreferencesTotal.getString("watchlist", null);
//                editorTotal.putString("watchlist", tmp.substring(0, tmp.indexOf(ticker+";"))+tmp.substring(tmp.indexOf(ticker+";")+ticker.length()+1));
//                item.setIcon(R.drawable.ic_star);
//                favorite = false;
//            }
//                editorTotal.commit();

//        });
//
        return super.onCreateOptionsMenu(menu);
    }
//    @Override
//    public void invalidateOptionsMenu() {
//        super.invalidateOptionsMenu();
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_favorite:
                if(!favorite) {
                    editorTotal.putString("watchlist", sharedPreferencesTotal.getString("watchlist", null) + ticker + ";");
                    editorTotal.putString(ticker + ":name", name);
                    item.setIcon(R.drawable.ic_star__1_);
                    favorite = true;
                    Toast.makeText(getApplicationContext(), "\"" + ticker.toUpperCase() + "\" was added to favorites", Toast.LENGTH_LONG).show();
                }
                else
                {
                    String tmp = sharedPreferencesTotal.getString("watchlist", null);
                    editorTotal.putString("watchlist", tmp.substring(0, tmp.indexOf(ticker+";"))+tmp.substring(tmp.indexOf(ticker+";")+ticker.length()+1));
                    editorTotal.remove(ticker + ":name");
                    item.setIcon(R.drawable.ic_star);
                    favorite = false;
                    Toast.makeText(getApplicationContext(), "\"" + ticker.toUpperCase() + "\" was removed from favorites", Toast.LENGTH_LONG).show();
                }
                    editorTotal.commit();
                return true;
            default:
                onBackPressed();
                return super.onOptionsItemSelected(item);
        }

    }

    private void requestData() {

        String newQuery = url + ticker;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, newQuery, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
//                                resContact = (Contact)
//                                        gson.fromJson(response.toString(), Contact.class);
//                        String resString = "";
                        try {
//                            resString = response.toString();
                            if(!response.toString().equals("{}")) {
                                price = response.getJSONObject("price");
                                summary = response.getJSONObject("summary");
                                news = response.getJSONObject("news").getJSONArray("articles");
                                history = response.getJSONArray("history");
//                                spinner.setVisibility(View.INVISIBLE);
                                update();
                            }
                            else {
                                Intent intent = new Intent(SearchableActivity.this, ScrollingActivity.class);
                                startActivity(intent);
                                Toast.makeText(SearchableActivity.this, "Please enter a valid symbol!", Toast.LENGTH_LONG).show();
                                SearchableActivity.this.finish();
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
    private void update() {
        List<News> newsList = new ArrayList<>();
        List<News> newsList2 = new ArrayList<>();
        List<String> urlList = new ArrayList<>();
        List<String> urlList2 = new ArrayList<>();
//            TextView xt1 = (TextView) findViewById(R.id.tvHeaderx);
//            TextView xt2 = (TextView) findViewById(R.id.tvDatex);
//            TextView xt3 = (TextView) findViewById(R.id.sourcex);
//            try {
////                xt1.setText(news.getJSONObject(0).getString("title").toUpperCase());
//                news_item_url = news.getJSONObject(i).getString("url");
////                xt1.setText(news.getJSONObject(0).getString("urlToImage"));
//                long days = (Calendar.getInstance().getTimeInMillis() -  new Date(news.getJSONObject(0).getString("publishedAt")).getTime()) / 3600 / 24 / 1000;
////                xt2.setText(days + " days ago");
//                xt3.setText(news.getJSONObject(0).getJSONObject("source").getString("name"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//            }
        for(int i = 1; i < 20; i++)
        {
            String news_item_title = "", news_item_url = "", news_item_urlToImage = "", news_item_date = "", news_item_source = "";
            try {
                news_item_title = news.getJSONObject(i).getString("title");
                news_item_url = news.getJSONObject(i).getString("url");
                news_item_urlToImage = news.getJSONObject(i).getString("urlToImage");
                news_item_date = news.getJSONObject(i).getString("publishedAt");
                news_item_source = news.getJSONObject(i).getJSONObject("source").getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            }
            newsList.add(new News(news_item_title, news_item_source, news_item_url, new Date(news_item_date), news_item_urlToImage));
            urlList.add(news_item_url);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentManager fm = getSupportFragmentManager();
        setContentView(R.layout.activity_searchable);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        TextView t1 = (TextView) findViewById(R.id.res_textView1);
        TextView t2 = (TextView) findViewById(R.id.res_textView2);
        TextView t3 = (TextView) findViewById(R.id.res_textView3);
        TextView t4 = (TextView) findViewById(R.id.res_textView4);
        TextView about = (TextView) findViewById(R.id.about_text);
        TextView portfolio1 = (TextView) findViewById(R.id.res_portfolio_1);
        TextView portfolio2 = (TextView) findViewById(R.id.res_portfolio_2);


        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl("file:///android_asset/chart.html?ticker=" + ticker);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        String[] gridDataTmp = gridData.clone();
        simpleGrid = (GridView) findViewById(R.id.simpleGridView);
        try {
            name = summary.getString("name");
            curPrice = price.getDouble("last");
            change = (price.getDouble("last")-price.getDouble("prevClose"));
            if(change >= 0)
            {
                t4.setTextColor(Color.GREEN);
                t4.setText("+$" + df.format(change));
            }
            else
            {
                t4.setTextColor(Color.RED);
                t4.setText("-$" + df.format(-change));
            }
            t1.setText(summary.getString("ticker"));
            t3.setText(summary.getString("name"));
            t2.setText("$" + df.format(price.getDouble("last")));

            about.setText(summary.getString("description"));
            makeTextViewResizable(about, 2, "Show More...", true);

            gridDataTmp[0] += df.format(price.getDouble("last"));
            gridDataTmp[1] += df.format(price.getDouble("low"));
            String bidPrice = price.getString("bidPrice");
            if(!bidPrice.equals("null"))
                gridDataTmp[2] += df.format(price.getDouble("bidPrice"));
            else
                gridDataTmp[2] += "-";
            gridDataTmp[3] += df.format(price.getDouble("open"));
            String mid = price.getString("mid");
            if(!mid.equals("null"))
                gridDataTmp[4] += df.format(price.getDouble("mid"));
            else
                gridDataTmp[4] += "-";
            gridDataTmp[5] += df.format(price.getDouble("high"));
            String volume = price.getString("volume");
            if(!volume.equals("null"))
                gridDataTmp[6] += price.getInt("volume");
            else
                gridDataTmp[6] += "-";

            gridAdapter = new GridAdapter(this, R.layout.activity_gridview, gridDataTmp);
            simpleGrid.setAdapter(gridAdapter);
            gridAdapter.notifyDataSetChanged();



            try {
            NewsRecyclerViewFragment fragment = new NewsRecyclerViewFragment(newsList, urlList, fm, new News( news.getJSONObject(0).getString("title"),
                    news.getJSONObject(0).getJSONObject("source").getString("name"), news.getJSONObject(0).getString("url"),
                    new Date(news.getJSONObject(0).getString("publishedAt")), news.getJSONObject(0).getString("urlToImage")));
                transaction.replace(R.id.sample_content_fragment, fragment);
                transaction.commit();
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            }


            if(sharedPreferencesTotal.getInt(ticker, -1) == -1)
                share = 0;
            else
                share = sharedPreferencesTotal.getInt(ticker, -1);

            portfolio1.setText("You have " + share + " shares of " + ticker.toUpperCase() + ".");
            portfolio2.setText("Start Trading!");
            Button tradeBtn = (Button) findViewById(R.id.TradeBtn);
            tradeBtn.setOnClickListener(v -> {
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.porfolio_dialog_1);
                dialog.getWindow().setLayout(1350, 1450);
                try {
                    TextView portfolioTitle1 = (TextView) dialog.findViewById(R.id.portfolio_dialog_title1);
                    portfolioTitle1.setText("Trade " + summary.getString("name") + " Shares");
                    TextView portfolioTitle2 = (TextView) dialog.findViewById(R.id.portfolio_dialog_title2);
                    portfolioTitle2.setText("$" + df.format(total) + " available to buy " + ticker.toUpperCase());

                } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                    }
                EditText editText = (EditText) dialog.findViewById(R.id.portfolio_editText);
                TextView portfolioTitle3 = (TextView) dialog.findViewById(R.id.portfolio_dialog_title4);
                portfolioTitle3.setText("0 x $" + df.format(curPrice) + "/share = $" + df.format(0));
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                        TextView portfolioTitle3 = (TextView) dialog.findViewById(R.id.portfolio_dialog_title4);
                        if(s != null && !s.toString().equals("")) {
                            amount = Integer.valueOf(s.toString());
                            portfolioTitle3.setText(amount + " x $" + df.format(curPrice) + "/share = $" + df.format(amount * curPrice));
                        }
                        else {
                            portfolioTitle3.setText("0 x $" + df.format(curPrice) + "/share = $" + df.format(0));
                        }
                    }
                });
                // if button is clicked, close the custom dialog
                Button buyBtn = (Button) dialog.findViewById(R.id.portfolio_buyBtn);
                buyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(amount <= 0) {
                            Toast.makeText(SearchableActivity.this, "Cannot buy less than 0 shares!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(amount*curPrice > total) {
                            Toast.makeText(SearchableActivity.this, "Not enough money to buy!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        total -= amount*curPrice;
                        share += amount;
                        editorTotal.putString("total", String.valueOf(df.format(total)));
                        editorTotal.putInt(ticker, share);
                        editorTotal.putString(ticker+":name", name);
                        if(share == amount) {
                            String tmp = sharedPreferencesTotal.getString("portfolio", null);
                            editorTotal.putString("portfolio", tmp + ticker + ";");
                        }
                        editorTotal.commit();
                        portfolio1.setText("You have " + share + " shares of " + ticker.toUpperCase() + ".");
                        dialog.dismiss();
                        final Dialog dialog2 = new Dialog(SearchableActivity.this);
                        dialog2.setContentView(R.layout.porfolio_dialog_2);
                        dialog2.getWindow().setLayout(1350, 1150);
                        TextView mainTitle = (TextView) dialog2.findViewById(R.id.main_title);
                        mainTitle.setText("You have successfully bought " + amount + " shares of " + ticker.toUpperCase());
                        Button done = (Button) dialog2.findViewById(R.id.donetBtn);
                        done.setOnClickListener(v1 -> {
                            dialog2.dismiss();
                        });
                        dialog2.show();
                        amount = 0;
                    }
                });
                Button sellBtn = (Button) dialog.findViewById(R.id.portfolio_selBtn);
                // if button is clicked, close the custom dialog
                sellBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(amount <= 0) {
                            Toast.makeText(SearchableActivity.this, "Cannot sell less than 0 shares!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(amount > share) {
                            Toast.makeText(SearchableActivity.this, "‘Not enough shares to sell!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        total += amount*curPrice;
                        share -= amount;
                        editorTotal.putString("total", String.valueOf(df.format(total)));
                        if(share == 0) {
                            editorTotal.remove(ticker);
                            String tmp = sharedPreferencesTotal.getString("portfolio", null);
                            editorTotal.putString("portfolio", tmp.substring(0, tmp.indexOf(ticker)) + tmp.substring(tmp.indexOf(ticker) + ticker.length() + 1));
                        }
                        else editorTotal.putInt(ticker, share);
                        editorTotal.commit();
                        portfolio1.setText("You have " + share + " shares of " + ticker.toUpperCase() + ".");
                        dialog.dismiss();
                        final Dialog dialog2 = new Dialog(SearchableActivity.this);
                        dialog2.setContentView(R.layout.porfolio_dialog_2);
                        dialog2.getWindow().setLayout(1350, 1150);
                        TextView mainTitle = (TextView) dialog2.findViewById(R.id.main_title);
                        mainTitle.setText("You have successfully sold " + amount + " shares of " + ticker.toUpperCase());
                        Button done = (Button) dialog2.findViewById(R.id.donetBtn);
                        done.setOnClickListener(v1 -> {
                            dialog2.dismiss();
                        });
                        dialog2.show();
                        amount = 0;
                    }
                });

                dialog.show();
            });

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        }

    }


    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                String text;
                int lineEndIndex;
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    lineEndIndex = tv.getLayout().getLineEnd(0);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length()) + "... " + expandText;
                } else {
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                }
                tv.setText(text);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setText(
                        addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                viewMore), TextView.BufferType.SPANNABLE);
            }
        });
    }
    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                                          final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new MySpannable(false){
                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "Show Less", false);
                    } else {
                        makeTextViewResizable(tv, 3, "Show More...", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;
    }


}


class GridAdapter extends ArrayAdapter {
    Context context;
    String data[];
    LayoutInflater inflater;
    public GridAdapter(Context applicationContext, int textViewResourceId, String[] data) {
        super(applicationContext, textViewResourceId, data);
        this.context = applicationContext;
        this.data = data;
        // = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return data.length;
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.activity_gridview, null); // inflate the layout
//        ImageView icon = (ImageView) view.findViewById(R.id.icon); // get the reference of ImageView
//        icon.setImageResource(logos[i]); // set logo images
        TextView text = (TextView) view.findViewById(R.id.grid_text);
        text.setText(data[i]);
        return view;
    }
}

class MySpannable extends ClickableSpan {

    private boolean isUnderline = false;

    /**
     * Constructor
     */
    public MySpannable(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(isUnderline);
        ds.setColor(Color.GRAY);
        ds.setFakeBoldText(true);

    }

    @Override
    public void onClick(View widget) {

    }
}

class NewsSection extends Section {

    private List<News> news;
    //private final ClickListener clickListener;
    private Context ctx;
    //private final ClickListener clickListener;
    private FragmentManager fm;

    public NewsSection(Context ctx, List<News> news, FragmentManager fm, int flag) {
        // call constructor with layout resources for this Section header and items
        super(SectionParameters.builder()
                .itemResourceId(flag)
                .build());

        this.news = news;
        this.ctx = ctx;
        this.fm = fm;
    }

    @Override
    public int getContentItemsTotal() {
        return news.size(); // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final NewsViewHolder itemHolder = (NewsViewHolder) holder;

        final News newsItem = news.get(position);

        itemHolder.tvHeader.setText(newsItem.title);
        long days = (Calendar.getInstance().getTimeInMillis() -  newsItem.date.getTime()) / 3600 / 24 / 1000;
        itemHolder.tvDate.setText(days + " days ago");
        itemHolder.source.setText(newsItem.source);
        if(position != 0)
            Glide.with(itemHolder.rootView).load(newsItem.image).override(400, 400).centerCrop().into(itemHolder.imgItem);
        else
            Glide.with(itemHolder.rootView).load(newsItem.image).override(2220, 1050).centerCrop().into(itemHolder.imgItem);
        itemHolder.rootView.setOnLongClickListener(v -> {
            //NewsDialog newsDialog = new NewsDialog();

                        // custom dialog
                        final Dialog dialog = new Dialog(ctx);
                        dialog.setContentView(R.layout.news_dialog);
                        ImageView image = (ImageView) dialog.findViewById(R.id.dialogNewsImage);
                        Glide.with(itemHolder.rootView).load(newsItem.image).override(1600, 1000).centerCrop().into(image);
                        //newsDialog.show(fm, "newsDialog");
                        TextView dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
                        dialog_title.setText(newsItem.title);
                        ImageButton dialogButton = (ImageButton) dialog.findViewById(R.id.dialogButtonChrome);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setData(Uri.parse(newsItem.url));
                                intent.setAction(Intent.ACTION_VIEW);
                                ctx.startActivity(intent);
                            }
                        });
                        ImageButton dialogButton2 = (ImageButton) dialog.findViewById(R.id.dialogButtonTwitter);
                        // if button is clicked, close the custom dialog
                        dialogButton2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + newsItem.title + "&url=" + newsItem.url));
                                intent.setAction(Intent.ACTION_VIEW);
                                ctx.startActivity(intent);
                            }
                        });

                        dialog.show();
                        return true;
                });
        itemHolder.rootView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setData(Uri.parse(newsItem.url));//Url 就是你要打开的网址
            intent.setAction(Intent.ACTION_VIEW);
            ctx.startActivity(intent); //启动浏览器
        });


    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        // return an empty instance of ViewHolder for the headers of this section
        return new HeaderViewHolder(view);
    }
    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.tvTitle.setText("");
    }
    @Override
    public RecyclerView.ViewHolder getFooterViewHolder(final View view) {
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindFooterViewHolder(final RecyclerView.ViewHolder holder) {
        final FooterViewHolder footerHolder = (FooterViewHolder) holder;

        //footerHolder.rootView.setOnClickListener(v -> clickListener.onFooterRootViewClicked(this, footerHolder.getAdapterPosition()));
    }

    interface ClickListener {

        void onItemRootViewClicked(@NonNull final NewsSection section, final int itemAdapterPosition);

        void onFooterRootViewClicked(@NonNull final NewsSection section, final int itemAdapterPosition);
    }
}

class NewsViewHolder extends RecyclerView.ViewHolder {
    View rootView;
    ImageView imgItem;
    TextView tvHeader;
    TextView tvDate;
    TextView source;


    NewsViewHolder(View view) {
        super(view);

        rootView = view;
        imgItem = view.findViewById(R.id.imgItem);
        tvHeader = view.findViewById(R.id.tvHeader);
        source = view.findViewById(R.id.source);
        tvDate = view.findViewById(R.id.tvDate);
    }
}

class HeaderViewHolder extends RecyclerView.ViewHolder {

    TextView tvTitle;

    HeaderViewHolder(View view) {
        super(view);

        tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setVisibility(View.INVISIBLE);
    }
}

class FooterViewHolder extends RecyclerView.ViewHolder {

    View rootView;

    FooterViewHolder(View view) {
        super(view);
        view.setVisibility(View.INVISIBLE);
        rootView = view;
    }
}

