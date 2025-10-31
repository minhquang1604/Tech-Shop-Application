package com.example.tech_shop.api;

import com.example.tech_shop.models.Product;
import com.example.tech_shop.models.ProductDetail;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/Product/Fetch")
    Call<List<Product>> getProducts(@Query("number") int number);
    // Lấy chi tiết sản phẩm theo ID
    @GET("api/Product/Details/{id}")
    Call<ProductDetail> getProductDetails(@Path("id") String id);

    @GET("api/Product/Search/{keyword}")
    Call<List<Product>> getProductsSearch(@Path("keyword") String keyword);
}
