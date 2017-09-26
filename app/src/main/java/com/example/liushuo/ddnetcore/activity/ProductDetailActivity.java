package com.example.liushuo.ddnetcore.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.liushuo.ddnetcore.R;
import com.example.liushuo.ddnetcore.base.BaseActivity;
import com.example.liushuo.ddnetcore.databinding.ActivityProductDetailBinding;
import com.example.liushuo.ddnetcore.entity.ProductBean;
import com.luojilab.netsupport.netcore.builder.Constants;
import com.luojilab.netsupport.netcore.builder.DataRequestBuilder;
import com.luojilab.netsupport.netcore.datasource.retrofit.ResponseHeader;
import com.luojilab.netsupport.netcore.domain.eventbus.EventResponse;
import com.luojilab.netsupport.netcore.domain.request.Request;

/**
 * Created by liushuo on 2017/9/24.
 */

public class ProductDetailActivity extends BaseActivity {
    public static final String EXTRA_PRODUCT = "extra_product";

    private static final String REQUEST_FETCH_PRODUCT = "request_fetch_product";

    private ActivityProductDetailBinding mBinding;

    public static void intentTo(Context context, String product) {
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra(EXTRA_PRODUCT, product);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail);

        String product = getIntent().getStringExtra(EXTRA_PRODUCT);
        if (TextUtils.isEmpty(product)) finish();


        fetchProductDetail(product);
    }

    private void fetchProductDetail(String product) {
        Request request = DataRequestBuilder.asObjectRequest("/products/product")
                .requestDefaultStrategy(Constants.STRATEGY_LEVEL3_CACHE)
                .httpMethod(Constants.HTTP_METHOD_GET)
                .requestExpireStrategy(Constants.STRATEGY_REFRESH_CACHE)
                .dataClass(ProductBean.class)
                .cacheId(String.format("product_%s", product))
                .dbCache()
                .memoryCache()
                .parameter("product", product)
                .requestId(REQUEST_FETCH_PRODUCT)
                .build();

        enqueueRequest(request);
    }

    @Override
    public void handlePreNetRequest(@NonNull Request request) {
        // manually show loading
    }

    @Override
    public void handleNetRequestError(@NonNull Request request, @NonNull ResponseHeader rb) {
        Toast.makeText(this, rb.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handleReceivedResponse(@NonNull EventResponse event) {
        String requestId = event.mRequest.getRequestId();

        switch (requestId) {
            case REQUEST_FETCH_PRODUCT:
                handleProductResponse(event.mRequest);
                break;
        }
    }

    private void handleProductResponse(Request request) {
        ProductBean productBean = (ProductBean) request.getResult();
        if (productBean == null) return;

        mBinding.tvName.setText(productBean.getName());
        mBinding.tvDes.setText(productBean.getBrief());
    }
}
