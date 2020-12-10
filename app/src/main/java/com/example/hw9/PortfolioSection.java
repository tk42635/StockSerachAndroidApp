package com.example.hw9;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

class PortfolioSection extends Section {

    private List<Company> companyList;
    //private final ClickListener clickListener;
    private Context ctx;
    //private final ClickListener clickListener;
    private FragmentManager fm;
    private java.text.DecimalFormat df;

    public PortfolioSection(Context ctx, List<Company> companyList, FragmentManager fm) {
        // call constructor with layout resources for this Section header and items
        super(SectionParameters.builder()
                .itemResourceId(R.layout.company_section)
                .build());
        this.companyList = companyList;
        this.ctx = ctx;
        this.fm = fm;
        df = new java.text.DecimalFormat("0.00");
    }

    public List<Company> getData() {
        return companyList;
    }
    public void removeItem(int position) {
        companyList.remove(position);
    }

    public void restoreItem(Company item, int position) {
        companyList.add(position, item);
    }

    @Override
    public int getContentItemsTotal() {
        return companyList.size(); // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new PortfolioViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final PortfolioViewHolder itemHolder = (PortfolioViewHolder) holder;

        final Company companyItem = companyList.get(position);
        if(companyItem.change >= 0)
        {
            itemHolder.imgItem.setImageResource(R.drawable.ic_twotone_trending_up_24);
            itemHolder.companyLast.setTextColor(Color.GREEN);
        }
        else
        {
            itemHolder.imgItem.setImageResource(R.drawable.ic_baseline_trending_down_24);
            itemHolder.companyLast.setTextColor(Color.RED);
        }
        itemHolder.companyLast.setText(df.format(companyItem.last));
        itemHolder.companyChange.setText(df.format(companyItem.change));
        itemHolder.companyTicker.setText(companyItem.title.toUpperCase());
        if(companyItem.share > 0)
            itemHolder.companyName.setText(companyItem.share + " shares");
        else
            itemHolder.companyName.setText(companyItem.name);

        itemHolder.btn.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, SearchableActivity.class);
            intent.putExtra("ticker",companyItem.title);
            ctx.startActivity(intent);
        });
    }



}

