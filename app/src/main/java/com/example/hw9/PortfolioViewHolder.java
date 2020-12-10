package com.example.hw9;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class PortfolioViewHolder extends RecyclerView.ViewHolder {
    View rootView;
    ImageView imgItem;
    TextView companyTicker;
    TextView companyLast;
    TextView companyName;
    TextView companyChange;
    ImageButton btn;


    PortfolioViewHolder(View view) {
        super(view);

        rootView = view;
        imgItem = view.findViewById(R.id.company_change_image);
        companyTicker = view.findViewById(R.id.company_ticker);
        companyLast = view.findViewById(R.id.company_price);
        companyName = view.findViewById(R.id.company_nameOrShare);
        companyChange = view.findViewById(R.id.company_change);
        btn = (ImageButton) view.findViewById(R.id.company_search);
    }
}
