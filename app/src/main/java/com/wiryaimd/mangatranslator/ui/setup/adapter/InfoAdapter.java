package com.wiryaimd.mangatranslator.ui.setup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.model.InfoModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InfoAdapter extends PagerAdapter {

    private Context context;
    private List<InfoModel> infoList;

    public InfoAdapter(Context context, List<InfoModel> infoList) {
        this.context = context;
        this.infoList = infoList;
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @NotNull
    @Override
    public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_info, container, false);
        TextView title = view.findViewById(R.id.info_item_title);
        TextView desc = view.findViewById(R.id.info_item_desc);

        title.setText(infoList.get(position).getTitle());
        desc.setText(infoList.get(position).getDesc());

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull @NotNull ViewGroup container, int position, @NonNull @NotNull Object object) {
        container.removeView((View)object);
    }
}
