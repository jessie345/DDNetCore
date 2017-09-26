package com.example.liushuo.ddnetcore.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.liushuo.ddnetcore.R;
import com.example.liushuo.ddnetcore.base.BaseArrayAdapter;
import com.example.liushuo.ddnetcore.databinding.LayoutProductsItemBinding;
import com.example.liushuo.ddnetcore.entity.ProductBean;

/**
 * Created by liushuo on 2017/9/22.
 */

public class ProductsAdapter extends BaseArrayAdapter<ProductBean> {

    public ProductsAdapter(@NonNull Context context) {
        super(context, R.layout.layout_products_item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutProductsItemBinding binding;
        if (convertView == null) {
            binding = LayoutProductsItemBinding.inflate(mInflater);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (LayoutProductsItemBinding) convertView.getTag();
        }

        ProductBean productBean = getItem(position);

        binding.tvName.setText(productBean.getName());

        return convertView;
    }
}
