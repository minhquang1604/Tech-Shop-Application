package com.example.tech_shop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.PersonalInfoRequest;
import com.example.tech_shop.models.UserProfileResponse;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformationActivity extends AppCompatActivity {
    Button btnSave;
    EditText edtFullname, edtPhone, edtBirthday;
    RadioGroup radioGender;
    ImageView imgAvatar;

    boolean isEditing = false; // trạng thái chỉnh sửa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_information);

        btnSave = findViewById(R.id.btnSave);
        edtFullname = findViewById(R.id.edtFullname);
        edtPhone = findViewById(R.id.edtPhone);
        edtBirthday = findViewById(R.id.edtBirthday);
        radioGender = findViewById(R.id.radioGender);
        imgAvatar = findViewById(R.id.imgAvatar);

        loadProfile(); // load dữ liệu lần đầu

        btnSave.setOnClickListener(v -> {
            if(!isEditing) {
                // Chuyển sang trạng thái edit
                isEditing = true;
                btnSave.setText("Save");
                setEditing(true);
            } else {
                // Gửi PUT để cập nhật
                updateProfile();
            }
        });
    }

    private void setEditing(boolean enable) {
        edtFullname.setEnabled(enable);
        edtPhone.setEnabled(enable);
        edtBirthday.setEnabled(enable);
        radioGender.setEnabled(enable);
        // imgAvatar có thể click để chọn avatar nếu enable
    }

    private void loadProfile() {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.getProfile().enqueue(new Callback<PersonalInfoRequest>() {
            @Override
            public void onResponse(Call<PersonalInfoRequest> call, Response<PersonalInfoRequest> response) {
                if(response.isSuccessful() && response.body() != null) {
                    PersonalInfoRequest profile = response.body();
                    edtFullname.setText(profile.getName());
                    edtPhone.setText(profile.getPhoneNumber());
                    edtBirthday.setText(profile.getBirthday().substring(0,10)); // yyyy-MM-dd
                    if("Man".equalsIgnoreCase(profile.getGender()) || "Male".equalsIgnoreCase(profile.getGender())) {
                        radioGender.check(R.id.radioMale);
                    } else if("Woman".equalsIgnoreCase(profile.getGender()) || "Female".equalsIgnoreCase(profile.getGender())) {
                        radioGender.check(R.id.radioFemale);
                    }

                    // Load avatar nếu có
                    // Glide.with(InformationActivity.this).load(profile.getAvatar()).circleCrop().into(imgAvatar);

                    setEditing(false); // ban đầu không cho sửa
                    btnSave.setText("Edit");
                }
            }

            @Override
            public void onFailure(Call<PersonalInfoRequest> call, Throwable t) {
                Toast.makeText(InformationActivity.this, "Failed to load profile: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String name = edtFullname.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String birthday = edtBirthday.getText().toString().trim();
        String gender = radioGender.getCheckedRadioButtonId() == R.id.radioMale ? "Male" : "Female";
        String avatar = ""; // Nếu có chọn avatar thì convert sang Base64

        PersonalInfoRequest request = new PersonalInfoRequest(name, phone, birthday, gender, avatar);

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.addPersonalInfo(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(InformationActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                    isEditing = false;
                    btnSave.setText("Edit");
                    setEditing(false);
                } else {
                    Toast.makeText(InformationActivity.this, "Update failed! Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(InformationActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
