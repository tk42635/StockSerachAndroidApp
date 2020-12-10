package com.example.hw9;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.FixedPreloadSizeProvider;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class PortfolioRecyclerViewFragment extends Fragment{

    private SpecialAdapter sectionedAdapter;
    private List<Company> companyList;
    private FragmentManager fm;
    private int flag;

    PortfolioRecyclerViewFragment(List<Company> companyList, FragmentManager fm, int flag) {
        this.companyList = companyList;
        this.fm = fm;
        this.flag = flag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
//        ListPreloader.PreloadSizeProvider sizeProvider =
//                new FixedPreloadSizeProvider(imageWidthPixels, imageHeightPixels);
//        MyPreloadModelProvider modelProvider = new MyPreloadModelProvider();
//        RecyclerViewPreloader<Drawable> preloader =
//                new RecyclerViewPreloader<>(
//                        Glide.with(this), modelProvider, sizeProvider, 10 /*maxPreload*/);

        final View view;
        if(flag == 1) view = inflater.inflate(R.layout.portfolio_fragment, container, false);
        else view = inflater.inflate(R.layout.watchlist_fragment, container, false);
        sectionedAdapter = new SpecialAdapter();


        sectionedAdapter.addSection(new PortfolioSection(getContext(),
                companyList,
                fm
        ));

        final RecyclerView recyclerView;
        if(flag == 1) recyclerView = view.findViewById(R.id.recyclerview_p);
        else recyclerView = view.findViewById(R.id.recyclerview_w);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
//
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionedAdapter);

        ItemTouchHelper.Callback callback =
                new ItemMoveCallback((ItemMoveCallback.ItemTouchHelperContract) sectionedAdapter, getContext()) {
                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                        final int position = viewHolder.getAdapterPosition();
                        PortfolioSection tmp = (PortfolioSection) sectionedAdapter.getSection(0);
                        final Company item = tmp.getData().get(position);
                        SharedPreferences sharedPreferencesTotal = getContext().getSharedPreferences("total", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorTotal = sharedPreferencesTotal.edit();
                        String watchlist = sharedPreferencesTotal.getString("watchlist", "null");
                        editorTotal.putString("watchlist", watchlist.substring(0, watchlist.indexOf(item.title+";"))+watchlist.substring(watchlist.indexOf(item.title+";")+item.title.length()+1));
                        editorTotal.commit();
                        sectionedAdapter.removeItem(position);
//
//
//                        Snackbar snackbar = Snackbar
//                                .make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
//                        snackbar.setAction("UNDO", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//                                mAdapter.restoreItem(item, position);
//                                recyclerView.scrollToPosition(position);
//                            }
//                        });
//
//                        snackbar.setActionTextColor(Color.YELLOW);
//                        snackbar.show();

                    }
                };

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        return view;
    }


//    @Override
//    public void onItemRootViewClicked(@NonNull final NewsSection section, final int itemAdapterPosition) {
//        final SectionItemInfoDialog dialog = SectionItemInfoDialog.getInstance(
//                SectionItemInfoFactory.create(itemAdapterPosition, sectionedAdapter),
//                SectionInfoFactory.create(section, sectionedAdapter.getAdapterForSection(section))
//        );
//        dialog.show(getParentFragmentManager(), DIALOG_TAG);
//    }
//
//    @Override
//    public void onFooterRootViewClicked(@NonNull final NewsSection section, final int itemAdapterPosition) {
//        final SectionItemInfoDialog dialog = SectionItemInfoDialog.getInstance(
//                SectionItemInfoFactory.create(itemAdapterPosition, sectionedAdapter),
//                SectionInfoFactory.create(section, sectionedAdapter.getAdapterForSection(section))
//        );
//        dialog.show(getParentFragmentManager(), DIALOG_TAG);
//    }
//    class MyPreloadModelProvider implements ListPreloader.PreloadModelProvider {
//        @Override
//        @NonNull
//        public List<String> getPreloadItems(int position) {
//            String url = myUrls.get(position);
//            if (TextUtils.isEmpty(url)) {
//                return Collections.emptyList();
//            }
//            return Collections.singletonList(url);
//        }
//
//
//        @Override
//        @Nullable
//        public RequestBuilder getPreloadRequestBuilder(@NonNull Object item) {
//            return
//                    Glide.with(getContext())
//                            .load(item.toString())
//                            .override(imageWidthPixels, imageHeightPixels);
//        }
//    }

}

class SpecialAdapter extends SectionedRecyclerViewAdapter implements ItemMoveCallback.ItemTouchHelperContract{
    SpecialAdapter() {
        super();
    }
    public void removeItem(int position) {
        PortfolioSection tmp = (PortfolioSection) this.getSection(0);
        tmp.removeItem(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        PortfolioSection tmp = (PortfolioSection) this.getSection(0);
        List<Company> data = tmp.getData();
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(data, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(data, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        System.out.println("Move from " + fromPosition + " to " + toPosition);
    }

    @Override
    public void onRowSelected(PortfolioViewHolder myViewHolder) {
        myViewHolder.rootView.setBackgroundColor(Color.GRAY);
        System.out.println("Select");
    }

    @Override
    public void onRowClear(PortfolioViewHolder myViewHolder) {
        myViewHolder.rootView.setBackgroundColor(Color.WHITE);

    }
}

