package com.example.liushuo.ddnetcore.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.liushuo.ddnetcore.R;
import com.example.liushuo.ddnetcore.databinding.ActivityCategoryBinding;

public class CategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCategoryBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_category);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();

                if (id == R.id.btn_case_get) {
                    ProductsActivity.intentTo(CategoryActivity.this);
                } else if (id == R.id.btn_case_post_json) {
                    ProductAddActivity.intentTo(CategoryActivity.this);
                } else if (id == R.id.btn_case_post_form) {
                    ProductAdd2Activity.intentTo(CategoryActivity.this);
                }
            }
        };

        binding.btnCaseGet.setOnClickListener(clickListener);
        binding.btnCasePostJson.setOnClickListener(clickListener);
        binding.btnCasePostForm.setOnClickListener(clickListener);
    }
}
