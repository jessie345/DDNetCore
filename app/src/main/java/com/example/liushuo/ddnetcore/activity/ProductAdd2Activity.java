package com.example.liushuo.ddnetcore.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.liushuo.ddnetcore.R;
import com.example.liushuo.ddnetcore.base.BaseActivity;
import com.example.liushuo.ddnetcore.databinding.ActivityProductAddBinding;
import com.example.liushuo.ddnetcore.entity.ProductBean;
import com.luojilab.netsupport.netcore.builder.Constants;
import com.luojilab.netsupport.netcore.builder.DataRequestBuilder;
import com.luojilab.netsupport.netcore.datasource.retrofit.ResponseHeader;
import com.luojilab.netsupport.netcore.domain.eventbus.EventResponse;
import com.luojilab.netsupport.netcore.domain.request.Request;

public class ProductAdd2Activity extends BaseActivity {
    private static final String REQUEST_PRODUCT_ADD = "request_product_add";

    ActivityProductAddBinding mBinding;

    public static void intentTo(Context contex) {
        Intent intent = new Intent(contex, ProductAdd2Activity.class);
        contex.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_add);

        mBinding.llInput.setVisibility(View.VISIBLE);
        mBinding.llDetail.setVisibility(View.GONE);

        mBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mBinding.etName.getText().toString();
                String brief = mBinding.etBrief.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(brief)) {
                    Toast.makeText(ProductAdd2Activity.this, "缺少必要信息", Toast.LENGTH_SHORT);
                    return;
                }

                addProduct(name, brief);
            }
        });
    }

    private void addProduct(String name, String brief) {
        Request request = DataRequestBuilder.asObjectRequest("/products/add")
                .requestId(REQUEST_PRODUCT_ADD)
                .parameter("name", name)
                .parameter("brief", brief)
                .contentType(Constants.CONTENT_TYPE_FORM)
                .dataClass(ProductBean.class)
                .httpMethod(Constants.HTTP_METHOD_POST)
                .requestDefaultStrategy(Constants.STRATEGY_ONLY_NET)
                .build();

        enqueueRequest(request);
    }

    @Override
    public void handleNetRequestError(@NonNull Request request, @NonNull ResponseHeader rb) {
        Toast.makeText(ProductAdd2Activity.this, rb.getMessage(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void handleReceivedResponse(@NonNull EventResponse event) {
        Request request = event.mRequest;
        switch (request.getRequestId()) {
            case REQUEST_PRODUCT_ADD:
                handleAddProductResponse(request);
                break;
        }
    }

    private void handleAddProductResponse(Request request) {
        ProductBean productBean = (ProductBean) request.getResult();
        if (productBean != null) {
            mBinding.llDetail.setVisibility(View.VISIBLE);
            mBinding.llInput.setVisibility(View.GONE);

            mBinding.tvName.setText(productBean.getName());
            mBinding.tvBrief.setText(productBean.getBrief());
        }
    }
}
