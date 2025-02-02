package com.kosmo.nbbangapp.sign;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kosmo.nbbangapp.MainActivity;
import com.kosmo.nbbangapp.R;
import com.kosmo.nbbangapp.info.MyUrl;
import com.kosmo.nbbangapp.models.MemberItem;
import com.kosmo.nbbangapp.models.RespItem;
import com.kosmo.nbbangapp.retrofit.LoginService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "CHECKER";
    private Intent getIntent;
    private EditText edt_email,edt_password,edt_name,edt_nickname,edt_birth,edt_phone;
    private RadioGroup radioGroup;
    private CheckBox cbox_media,cbox_life,cbox_lecture;
    private int check_cnt=0;
    private boolean[] input_toggle = new boolean[7];
    private Button btn_regist;
    private Dialog dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getIntent = getIntent();
        String type = getIntent.getStringExtra("type");
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        edt_name = findViewById(R.id.edt_name);
        edt_nickname = findViewById(R.id.edt_nickname);
        edt_birth = findViewById(R.id.edt_birth);
        edt_phone = findViewById(R.id.edt_phone);
        radioGroup = findViewById(R.id.radioGroup);
        cbox_lecture = findViewById(R.id.cbox_lecture);
        cbox_life = findViewById(R.id.cbox_life);
        cbox_media = findViewById(R.id.cbox_media);
        btn_regist = findViewById(R.id.btn_regist);

        edt_email.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);


        if(!type.equals("general")){
            //연동로그인 회원가입시 미리 기입할 것

            edt_email.setText(getIntent.getStringExtra("email"));
            edt_email.setEnabled(false);
            input_toggle[0] = true;
            edt_password.setVisibility(View.INVISIBLE);
            input_toggle[1] = true;
            edt_name.setText(getIntent.getStringExtra("name"));
            input_toggle[2] = true;
        }



        edt_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()){
                    input_toggle[0] = false;
                }else{
                    input_toggle[0] = true;
                }
            }
        });


        edt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>5){
                    input_toggle[1] = true;
                }else{
                    input_toggle[1] = false;
                }
            }


        });



        edt_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>=2){
                    input_toggle[2] = true;
                }else{
                    input_toggle[2] = false;
                }
            }
        });


        edt_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>=2){
                    input_toggle[3] = true;
                }else{
                    input_toggle[3] = false;
                }
            }
        });

        edt_birth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                //940319
                if(!Pattern.compile("^\\d{4}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|[3][01])").matcher(s.toString()).matches()){
                    input_toggle[4] = false;
                }else{
                    input_toggle[4] = true;
                }
            }
        });

        edt_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()!=11){
                    input_toggle[5]=false;
                }else{
                    input_toggle[5] = true;
                }
            }

        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                input_toggle[6] = true;

            }
        });






        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //유효성 검증 후
                Log.d(TAG, "번호"+edt_phone.getText().length());
                String result = validate();

                if(!result.isEmpty()){
                    dialog = new AlertDialog.Builder(SignUpActivity.this)
                            .setCancelable(true)
                            .setTitle("미기입 오류입니다.")
                            .setMessage(result)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    dialog.show();
                    return;
                }

                //레트로핏으로 등록

                dialog = new AlertDialog.Builder(SignUpActivity.this)
                        .setCancelable(false)
                        .setView(R.layout.progress_layout)
                        .create();

                dialog.show();



                Retrofit retrofit =new Retrofit.Builder()
                        .baseUrl(MyUrl.NBBANGBaseURL)
                        .addConverterFactory(JacksonConverterFactory.create())
                        .build();

                LoginService loginService = retrofit.create(LoginService.class);


                Call<RespItem> call = loginService.registMember(setMemberItem());

                call.enqueue(new Callback<RespItem>() {
                    @Override
                    public void onResponse(Call<RespItem> call, Response<RespItem> response) {
                        if(response.isSuccessful()){

                            RespItem resp = response.body();
                            Toast.makeText(getApplicationContext(),"결과 : " + resp.getResp_code(),Toast.LENGTH_SHORT).show();
                            if(resp.getResp_code().equals("success")){
                                dialog.dismiss();
                                goToMain(getApplicationContext());
                            }else{
                                Toast.makeText(getApplicationContext(),"가입에 실패하였습니다. ",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<RespItem> call, Throwable t) {
                        Log.d(TAG, "onFailure: "+t.getMessage());
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                }).start();

            }
        });


    }

    private void goToMain(Context context){
        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
        intent.putExtra("email",setMemberItem().getEmail());
        startActivity(intent);
        finish();
    }


    private MemberItem setMemberItem(){
        MemberItem res = new MemberItem();
        String pw = !getIntent.getStringExtra("type").equals("general") ? null :edt_password.getText().toString() ;
        String gender = radioGroup.getCheckedRadioButtonId() == R.id.rbtn_man ? "man" : "min";
        ArrayList<String> arr = new ArrayList<>();
        if(cbox_life.isChecked()){
            arr.add(cbox_life.getText().toString());
        }
        if(cbox_media.isChecked()){
            arr.add(cbox_media.getText().toString());
        }
        if(cbox_lecture.isChecked()){
            arr.add(cbox_lecture.getText().toString());
        }


        res.setEmail(edt_email.getText().toString());
        res.setPassword(pw);
        res.setName(edt_name.getText().toString());
        res.setNickname(edt_nickname.getText().toString());
        res.setBirthdate(edt_birth.getText().toString());
        res.setGender(gender);
        res.setTel(edt_phone.getText().toString());
        res.setPreference(Arrays.toString(arr.toArray(new String[arr.size()])));
        res.setUsertype("NULL");

        return res;
    }

    private String validate() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < input_toggle.length ; i++){
            if(!input_toggle[i]){
                sb.append(convertStr(i)+" ");
            }
        }

        if(cbox_life.isChecked() ||cbox_lecture.isChecked() ||cbox_media.isChecked()){
        }else{

            sb.append("관심사항"+" ");
        }
        return sb.toString();
    }

    private String convertStr(int i) {
        String result =null;
        switch (i){
            case 0:
                result="이메일";
                break;
            case 1:
                result="비밀번호";
                break;
            case 2:
                result="이름";
                break;
            case 3:
                result="닉네임";
                break;
            case 4:
                result="생년월일";
                break;
            case 5:
                result="핸드폰 번호";
                break;
            case 6:
                result="성별";
                break;
        }
        return result;
    }


}
