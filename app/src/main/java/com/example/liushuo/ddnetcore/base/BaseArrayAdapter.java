package com.example.liushuo.ddnetcore.base;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.Collection;

public abstract class BaseArrayAdapter<T> extends ArrayAdapter<T> {
    protected int mItemLayout;
    protected Context mContext;
    protected LayoutInflater mInflater;

    public BaseArrayAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        this.mItemLayout = resource;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public void addAll(T... items) {
        if (items == null) {
            super.clear();
            return;
        }

        this.setNotifyOnChange(false);
        super.clear();
        this.setNotifyOnChange(true);
        super.addAll(items);
    }

    @NonNull
    public abstract View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent);
}
