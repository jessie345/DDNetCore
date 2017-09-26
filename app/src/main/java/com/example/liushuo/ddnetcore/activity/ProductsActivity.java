package com.example.liushuo.ddnetcore.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.liushuo.ddnetcore.R;
import com.example.liushuo.ddnetcore.adapter.ProductsAdapter;
import com.example.liushuo.ddnetcore.base.BaseActivity;
import com.example.liushuo.ddnetcore.databinding.ActivityMainBinding;
import com.example.liushuo.ddnetcore.entity.ProductBean;
import com.luojilab.netsupport.netcore.builder.ArrayRequestBuilder;
import com.luojilab.netsupport.netcore.builder.Constants;
import com.luojilab.netsupport.netcore.builder.DataRequestBuilder;
import com.luojilab.netsupport.netcore.datasource.retrofit.ResponseHeader;
import com.luojilab.netsupport.netcore.domain.eventbus.EventResponse;
import com.luojilab.netsupport.netcore.domain.request.Request;
import com.luojilab.netsupport.netcore.network.NetworkControlListener;

public class ProductsActivity extends BaseActivity {
    private static final String REQUEST_FETCH_PRODUCTS = "request_fetch_products";
    private static final String REQUEST_REFRESH_PRODUCTS = "request_refresh_products";

    private ActivityMainBinding mBinding;
    private ProductsAdapter mProductsAdapter;

    public static void intentTo(Context context) {
        Intent intent = new Intent(context, ProductsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mProductsAdapter = new ProductsAdapter(this);
        mBinding.lvProducts.setAdapter(mProductsAdapter);
        mBinding.lvProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ProductBean productBean = (ProductBean) adapterView.getItemAtPosition(i);
                if (productBean != null) {
                    ProductDetailActivity.intentTo(ProductsActivity.this, productBean.get_id());
                }
            }
        });

        fetchProducts();

    }

    private void fetchProducts() {
        Request request = DataRequestBuilder.asArrayRequest("/products")
                .requestId(REQUEST_FETCH_PRODUCTS)
                .requestDefaultStrategy(Constants.STRATEGY_LEVEL3_CACHE)
                .requestExpireStrategy(Constants.STRATEGY_REFRESH_CACHE)
                .memoryCache()
                .dbCache()
                .dataClass(ProductBean.class)
                .httpMethod(Constants.HTTP_METHOD_GET)
                .build();

        enqueueRequest(request);

    }

    private void refreshProducts() {
        Request request = DataRequestBuilder.asArrayRequest("/products")
                .requestId(REQUEST_REFRESH_PRODUCTS)
                .requestDefaultStrategy(Constants.STRATEGY_ONLY_NET)
                .memoryCache()
                .dbCache()
                .dataClass(ProductBean.class)
                .httpMethod(Constants.HTTP_METHOD_GET)
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
        Request request = event.mRequest;

        String requestId = request.getRequestId();
        switch (requestId) {
            case REQUEST_FETCH_PRODUCTS:
                handleProductsResponse(request);
                break;
            case REQUEST_REFRESH_PRODUCTS:
                handleProductsResponse(request);
                break;
        }
    }

    private void handleProductsResponse(Request request) {
        ProductBean[] productBeans = (ProductBean[]) request.getResult();
        if (productBeans != null) {
            mProductsAdapter.addAll(productBeans);
        }
    }
}
