package com.example.tech_shop.api;

import com.example.tech_shop.models.AddToCartRequest;
import com.example.tech_shop.models.AddToWishlistRequest;
import com.example.tech_shop.models.CartCountResponse;
import com.example.tech_shop.models.CartItem;
import com.example.tech_shop.models.ConfirmPurchaseRequest;
import com.example.tech_shop.models.Order;
import com.example.tech_shop.models.PrepareRequest;
import com.example.tech_shop.models.PrepareResponse;
import com.example.tech_shop.models.Product;
import com.example.tech_shop.models.ProductDetail;
import com.example.tech_shop.models.ProductWishlist;
import com.example.tech_shop.models.Review;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @GET("api/Cart")
    Call<List<CartItem>> getCart();

    @POST("api/Cart/add")
    Call<Map<String, Object>> addToCart(@Body AddToCartRequest request);

    @DELETE("api/Cart/remove/{productId}")
    Call<Void> removeFromCart(@Path("productId") String productId);

    @GET("api/Cart/count")
    Call<CartCountResponse> getCartCount();

    @GET("api/Review/product/{productId}")
    Call<List<Review>> getReviewsByProductId(@Path("productId") String productId);

    @GET("api/Product/Fetch/{category}/{number}")
    Call<List<Product>> getProductsByCategory(
            @Path("category") String category,
            @Path("number") int number
    );

    @GET("/api/Wishlist")
    Call<List<ProductWishlist>> getProductsWishlist();

    @POST("api/Wishlist/add")
    Call<String> addToWishlist(@Body AddToWishlistRequest request);


    @POST("/api/Order/prepare")
    Call<PrepareResponse> prepareOrder(@Body PrepareRequest request);

    // Lấy tất cả đơn hàng của user
    @GET("/api/Order/my")
    Call<List<Order>> getMyOrders();

    @POST("/api/Purchase/confirm/{orderId}")
    Call<Void> confirmPurchase(@Path("orderId") String orderId, @Body ConfirmPurchaseRequest body);

    @GET("/api/Order/{id}")
    Call<Order> getOrderById(@Path("id") String id);

    @DELETE("/api/Order/{orderId}")
    Call<Void> cancelOrder(@Path("orderId") String orderId);

    @DELETE("/api/Wishlist/remove/{productId}")
    Call<Void> removeProductFromWishlist(@Path("productId") String productId);

    @POST("/api/Authenticate/Email/Opt/Sent/ForgotPassword")
    Call<Void> sendEmailVerify(@Body RequestBody email);

    @POST("/api/Authenticate/Email/Opt/Verify/PassWord")
    Call<Void> verifyOtpPassword(@Body Map<String, String> body);

    @PUT("/api/Authenticate/ResetPassword")
    Call<Void> resetPassword(@Body Map<String, String> body);

}
