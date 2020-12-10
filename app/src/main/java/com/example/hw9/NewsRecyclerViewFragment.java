package com.example.hw9;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.FixedPreloadSizeProvider;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class NewsRecyclerViewFragment extends Fragment{

    private SectionedRecyclerViewAdapter sectionedAdapter;
    private List<News> newsList;
    private List<String> myUrls;
    private final int imageWidthPixels = 50;
    private final int imageHeightPixels = 50;
    private FragmentManager fm;
    private News x;

    NewsRecyclerViewFragment(List<News> newsList, List<String> myUrls, FragmentManager fm, News x) {
        this.newsList = newsList;
        this.myUrls = myUrls;
        this.fm = fm;
        this.x = x;
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

        final View view = inflater.inflate(R.layout.news_fragment, container, false);
        sectionedAdapter = new SectionedRecyclerViewAdapter();

        sectionedAdapter.addSection(new NewsSection(getContext(),
                new ArrayList<News>(Arrays.asList(x)),
                fm,
                R.layout.section_ex2_item2
        ));
        sectionedAdapter.addSection(new NewsSection(getContext(),
                newsList,
                fm,
                R.layout.section_ex2_item
        ));



        final RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
//
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionedAdapter);

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

