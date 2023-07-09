package com.loan8.loan8.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.loan8.loan8.AboutUsActivity;
import com.loan8.loan8.R;
import com.loan8.loan8.ReferFriendActivity;
import com.loan8.loan8.models.UserProfileShow;
import com.loan8.loan8.utils.ConStant;
import com.loan8.loan8.utils.RetrofitClient;
import com.preference.PowerPreference;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    CircleImageView imgCircleId, circleImageTwoId;
    LinearLayout linearLayoutOneId, linearLayoutTwoId, linearLayoutFourId, linearLayoutThreeId;

    String languageToLoad;

    TextView txtMoNumberId;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        languageToLoad = PowerPreference.getDefaultFile().getString("lang", "en");

        Log.d("0210log", "onClick:173 " + languageToLoad);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getContext().getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        sharedPreferences = getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String mobNumber = sharedPreferences.getString("mobile_number", "");

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        init(view);

        //Setting logged in mobile number in the textview
        txtMoNumberId.setText(mobNumber);
        return view;
    }

    private void init(View view) {
        txtMoNumberId = view.findViewById(R.id.txtMoNumberId);
        imgCircleId = view.findViewById(R.id.imgCircleId);
        circleImageTwoId = view.findViewById(R.id.circleImageTwoId);
        linearLayoutOneId = view.findViewById(R.id.linearLayoutOneId);
        linearLayoutOneId.setOnClickListener(this);
        linearLayoutTwoId = view.findViewById(R.id.linearLayoutTwoId);
        linearLayoutTwoId.setOnClickListener(this);
        linearLayoutFourId = view.findViewById(R.id.linearLayoutFourId);
        linearLayoutFourId.setOnClickListener(this);
        linearLayoutThreeId = view.findViewById(R.id.linearLayoutThreeId);
        linearLayoutThreeId.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.linearLayoutOneId:
//                apiUserProfileShow();
                startActivity(new Intent(getActivity(), CompleteProfileActivity.class));
                break;
            case R.id.linearLayoutTwoId:
                startActivity(new Intent(getActivity(), ReferFriendActivity.class));
                break;
            case R.id.linearLayoutFourId:
                View customLayoutLanguagePopUp = LayoutInflater.from(getActivity()).inflate(R.layout.language_select_popup_layout, null);
                PopupWindow languagePopUp = new PopupWindow(customLayoutLanguagePopUp, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                languagePopUp.showAtLocation(customLayoutLanguagePopUp, Gravity.CENTER, 0, 0);

                LinearLayout l1 = customLayoutLanguagePopUp.findViewById(R.id.l1);
                final TextView tvEnglish = customLayoutLanguagePopUp.findViewById(R.id.tvEnglish);
                final TextView tvMalaysia = customLayoutLanguagePopUp.findViewById(R.id.tvMalaysia);
                final TextView tvChina = customLayoutLanguagePopUp.findViewById(R.id.tvChina);
                final TextView tvOther = customLayoutLanguagePopUp.findViewById(R.id.tvOther);

                l1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvEnglish.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_tick_12, 0);
                        tvMalaysia.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvChina.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvOther.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        languageToLoad = "en";
                        ConStant.setLanguageToLoad(languageToLoad);
                        PowerPreference.getDefaultFile().setString("lang", languageToLoad);

                        // Reload current fragment
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.profileId);

                        languagePopUp.dismiss();
                    }
                });
                LinearLayout l2 = customLayoutLanguagePopUp.findViewById(R.id.l2);
                l2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvEnglish.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvMalaysia.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_tick_12, 0);
                        tvChina.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvOther.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        languageToLoad = "km";
                        ConStant.setLanguageToLoad(languageToLoad);
                        PowerPreference.getDefaultFile().setString("lang", languageToLoad);

                        // Reload current fragment
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.profileId);
                        languagePopUp.dismiss();
                    }
                });
                LinearLayout l3 = customLayoutLanguagePopUp.findViewById(R.id.l3);
                l3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvEnglish.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvMalaysia.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvChina.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_tick_12, 0);
                        tvOther.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        languageToLoad = "my";
                        ConStant.setLanguageToLoad(languageToLoad);
                        PowerPreference.getDefaultFile().setString("lang", languageToLoad);

                        // Reload current fragment
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.profileId);
                        languagePopUp.dismiss();
                    }
                });
                LinearLayout l4 = customLayoutLanguagePopUp.findViewById(R.id.l4);
                l4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvEnglish.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvMalaysia.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvChina.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvOther.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_tick_12, 0);
                    }
                });
                break;
            case R.id.linearLayoutThreeId:
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
                break;
        }
    }

    // call api for display profile user data.
    private void apiUserProfileShow(){
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        String userId = "37";
//        int userId = 37;

        Call<UserProfileShow> call = RetrofitClient.getInstance().getMyApi().apiUserProShow(userId);
        call.enqueue(new Callback<UserProfileShow>() {
            @Override
            public void onResponse(Call<UserProfileShow> call, Response<UserProfileShow> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()){
                    UserProfileShow userProfileShow = response.body();
                    if (userProfileShow.status){
                        Toast.makeText(getContext(), userProfileShow.message+" " +userProfileShow.userName+" " + userProfileShow.emergencyName  ,Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), userProfileShow.message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileShow> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}