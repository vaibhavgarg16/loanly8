package com.loan8.loan8.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loan8.loan8.R;
import com.loan8.loan8.adapters.SliderAdapterExample;
import com.shuhart.stepview.StepView;
import com.smarteist.autoimageslider.SliderView;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.IndicatorType;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import com.warkiz.widget.TickMarkType;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import hearsilent.discreteslider.Dash;
import hearsilent.discreteslider.DiscreteSlider;
import hearsilent.discreteslider.Dot;
import hearsilent.discreteslider.libs.Utils;

public class HomeFragment extends Fragment implements View.OnClickListener {

    /*SliderView sliderId;*/
    StepView stepViewId;
    Button btnApplyId;
    DiscreteSlider mSlider;
    DiscreteSlider sliderPeriod;
    TextView txtAmount, txtTime, txtDateId, txtMonthlyPayment;

    String currentTime;

    String calLoanAmount, calLoanTIme, calMonthPay;

    IndicatorSeekBar seekBarAmount, seekBarPeriod;

    EditText etInterest, etLoanTime, etLoanAmount;

    public HomeFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    private void init(View view) {
        txtAmount = view.findViewById(R.id.txtAmount);
        txtTime = view.findViewById(R.id.txtTime);

        btnApplyId = view.findViewById(R.id.btnApplyId);
        btnApplyId.setOnClickListener(this);
        /*sliderId = view.findViewById(R.id.sliderId);
        sliderId.setSliderAdapter(new SliderAdapterExample(getActivity()));*/

        etLoanAmount = view.findViewById(R.id.etLoanAmount);
        etLoanTime = view.findViewById(R.id.etLoanTime);
        etInterest = view.findViewById(R.id.etInterest);

        etLoanAmount.addTextChangedListener(new CustomTextWatcher(etLoanAmount));
        etLoanTime.addTextChangedListener(new CustomTextWatcher(etLoanTime));
        etInterest.addTextChangedListener(new CustomTextWatcher(etInterest));

        txtMonthlyPayment = view.findViewById(R.id.txtMonthlyPayment);

        currentTime = DateFormat.getDateInstance().format(new Date());
        txtDateId = view.findViewById(R.id.txtDateId);
        txtDateId.setText(currentTime);

        //Below slider for loan period
        sliderPeriod = view.findViewById(R.id.sliderPeriod);
        sliderPeriod.setMode(DiscreteSlider.MODE_NORMAL);
        sliderPeriod.setClickable(true);
        sliderPeriod.setTrackWidth(Utils.convertDpToPixel(1, getContext()));
        sliderPeriod.setTrackColor(R.color.blue);
        sliderPeriod.setInactiveTrackColor(0x3DD81B60);
        sliderPeriod.setThumbRadius(Utils.convertDpToPixel(4, getContext()));
        sliderPeriod.setThumbColor(R.color.darkblue);
        sliderPeriod.setThumbPressedColor(R.color.darkblue);

        sliderPeriod.setTickMarkColor(R.color.darkgreen);
        sliderPeriod.setTickMarkInactiveColor(R.color.darkgreen);
        sliderPeriod.setTickMarkPatterns(Arrays.asList(new Dot(), new Dash(Utils.convertDpToPixel(8, getContext()))));
// TickMark step must be a factor of (count - 1)
        sliderPeriod.setTickMarkStep(1);

        sliderPeriod.setValueLabelTextColor(Color.WHITE);
        sliderPeriod.setValueLabelTextSize(Utils.convertSpToPixel(10, getContext()));
        sliderPeriod.setValueLabelGravity(DiscreteSlider.TOP);
        sliderPeriod.setValueLabelFormatter(new DiscreteSlider.ValueLabelFormatter() {

            @Override
            public String getLabel(int input) {
                return Integer.toString(input);
            }
        });
        sliderPeriod.setValueLabelMode(1); // 0: none, 1: showOnPressHold, 2: showOnProgressChange, 3: showOnPressHold & showOnProgressChange
        /*mSlider.setValueLabelDuration(1500);*/ // Use for mode `showOnProgressChange`


        sliderPeriod.setCount(82);
        sliderPeriod.setProgressOffset(3);
        sliderPeriod.setProgress(1); // The same as `setMinProgress`.
        sliderPeriod.setMinProgress(1);

        sliderPeriod.getProgress(); // The same as `getMinProgress`.
        sliderPeriod.getMinProgress();
        sliderPeriod.getMaxProgress();
        sliderPeriod.setValueChangedImmediately(false); // Default is false
        sliderPeriod.setOnValueChangedListener(new DiscreteSlider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int progress, boolean fromUser) {
                super.onValueChanged(progress, fromUser);
                Log.d("DiscreteSlider", "Progress: " + progress + ", fromUser: " + fromUser);
                txtTime.setText(Integer.toString(progress));
                /*loanAmount = String.valueOf(progress);
                SharePrefrancClass.getInstance(HomeActivity.this).savePref("loanamount", String.valueOf(progress));*/
            }

            @Override
            public void onValueChanged(int minProgress, int maxProgress, boolean fromUser) {
                super.onValueChanged(minProgress, maxProgress, fromUser);
                Log.i("DiscreteSlider",
                        "MinProgress: " + minProgress + ", MaxProgress: " + maxProgress +
                                ", fromUser: " + fromUser);
            }
        });

        //New seekbar slider library for loan amount
        seekBarAmount = view.findViewById(R.id.seekBarAmount);
        seekBarAmount.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                txtAmount.setText(String.valueOf(seekParams.progress));
                /*calLoanAmount = String.valueOf(seekParams.progress);*/
//                calMonthPay = String.valueOf(seekParams.progress / Integer.parseInt(calLoanTIme));
                txtMonthlyPayment.setText(String.valueOf(seekParams.progress));
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });
        seekBarAmount = IndicatorSeekBar
                .with(getContext())
                .max(200000)
                .min(10000)
                .progress(53)
                .tickCount(7)
                .showTickMarksType(TickMarkType.OVAL)
                .tickMarksColor(getResources().getColor(R.color.blue, null))
                .tickMarksSize(13)//dp
                .showTickTexts(true)
                .tickTextsColor(getResources().getColor(R.color.blue))
                .tickTextsSize(13)//sp
                .tickTextsTypeFace(Typeface.MONOSPACE)
                .showIndicatorType(IndicatorType.ROUNDED_RECTANGLE)
                .indicatorColor(getResources().getColor(R.color.blue))
                .indicatorTextColor(Color.parseColor("#ffffff"))
                .indicatorTextSize(13)//sp
                .thumbColor(getResources().getColor(R.color.colorAccent, null))
                .thumbSize(14)
                .trackProgressColor(getResources().getColor(R.color.colorAccent, null))
                .trackProgressSize(4)
                .trackBackgroundColor(getResources().getColor(R.color.blue))
                .trackBackgroundSize(2)
                .onlyThumbDraggable(false)
                .build();

        //New seekbar slider library for loan period
        seekBarPeriod = view.findViewById(R.id.seekBarPeriod);
        seekBarPeriod.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                txtTime.setText(String.valueOf(seekParams.progress));

                /*calLoanTIme = String.valueOf(seekParams.progress);

                calMonthPay = String.valueOf(Integer.parseInt(calLoanAmount) / seekParams.progress);
                txtMonthlyPayment.setText(calMonthPay);*/
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });
        seekBarPeriod = IndicatorSeekBar
                .with(getContext())
                .max(84)
                .min(3)
                .progress(53)
                .tickCount(7)
                .showTickMarksType(TickMarkType.OVAL)
                .tickMarksColor(getResources().getColor(R.color.blue, null))
                .tickMarksSize(13)//dp
                .showTickTexts(true)
                .tickTextsColor(getResources().getColor(R.color.blue))
                .tickTextsSize(13)//sp
                .tickTextsTypeFace(Typeface.MONOSPACE)
                .showIndicatorType(IndicatorType.ROUNDED_RECTANGLE)
                .indicatorColor(getResources().getColor(R.color.blue))
                .indicatorTextColor(Color.parseColor("#ffffff"))
                .indicatorTextSize(13)//sp
                .thumbColor(getResources().getColor(R.color.colorAccent, null))
                .thumbSize(14)
                .trackProgressColor(getResources().getColor(R.color.colorAccent, null))
                .trackProgressSize(4)
                .trackBackgroundColor(getResources().getColor(R.color.blue))
                .trackBackgroundSize(2)
                .onlyThumbDraggable(false)
                .build();


        //Below slider for loan amount
        mSlider = view.findViewById(R.id.mSlider);
        mSlider.setMode(DiscreteSlider.MODE_NORMAL);
        mSlider.setClickable(true);
        mSlider.setTrackWidth(Utils.convertDpToPixel(1, getContext()));
        mSlider.setTrackColor(R.color.blue);
        mSlider.setInactiveTrackColor(0x3DD81B60);
        mSlider.setThumbRadius(Utils.convertDpToPixel(4, getContext()));
        mSlider.setThumbColor(R.color.darkblue);
        mSlider.setThumbPressedColor(R.color.darkblue);

        mSlider.setTickMarkColor(R.color.darkgreen);
        mSlider.setTickMarkInactiveColor(R.color.darkgreen);
        mSlider.setTickMarkPatterns(Arrays.asList(new Dot(), new Dash(Utils.convertDpToPixel(8, getContext()))));
// TickMark step must be a factor of (count - 1)
        mSlider.setTickMarkStep(1);

        mSlider.setValueLabelTextColor(Color.WHITE);
        mSlider.setValueLabelTextSize(Utils.convertSpToPixel(10, getContext()));
        mSlider.setValueLabelGravity(DiscreteSlider.TOP);
        mSlider.setValueLabelFormatter(new DiscreteSlider.ValueLabelFormatter() {

            @Override
            public String getLabel(int input) {
                return Integer.toString(input);
            }
        });
        mSlider.setValueLabelMode(1); // 0: none, 1: showOnPressHold, 2: showOnProgressChange, 3: showOnPressHold & showOnProgressChange
        /*mSlider.setValueLabelDuration(1500);*/ // Use for mode `showOnProgressChange`


        mSlider.setCount(200000);
        mSlider.setProgressOffset(100);
        mSlider.setProgress(1); // The same as `setMinProgress`.
        mSlider.setMinProgress(1);

        mSlider.getProgress(); // The same as `getMinProgress`.
        mSlider.getMinProgress();
        mSlider.getMaxProgress();
        mSlider.setValueChangedImmediately(false); // Default is false
        mSlider.setOnValueChangedListener(new DiscreteSlider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int progress, boolean fromUser) {
                super.onValueChanged(progress, fromUser);
                Log.d("DiscreteSlider", "Progress: " + progress + ", fromUser: " + fromUser);
                txtAmount.setText(Integer.toString(progress));
                /*loanAmount = String.valueOf(progress);
                SharePrefrancClass.getInstance(HomeActivity.this).savePref("loanamount", String.valueOf(progress));*/
            }

            @Override
            public void onValueChanged(int minProgress, int maxProgress, boolean fromUser) {
                super.onValueChanged(minProgress, maxProgress, fromUser);
                Log.i("DiscreteSlider",
                        "MinProgress: " + minProgress + ", MaxProgress: " + maxProgress +
                                ", fromUser: " + fromUser);
            }
        });

//        stepViewId = view.findViewById(R.id.stepViewId);

        /*stepViewId.getState()
                .selectedTextColor(ContextCompat.getColor(getActivity(), R.color.black))
                .animationType(StepView.ANIMATION_CIRCLE)
                .selectedCircleColor(ContextCompat.getColor(getActivity(), R.color.blue))
                .selectedCircleRadius(getResources().getDimensionPixelSize(R.dimen.dp14))
                .steps(new ArrayList<String>() {{
                    add("300");
                    add("1000");
                    add("5000");
                }})
                // You should specify only steps number or steps array of strings.
                // In case you specify both steps array is chosen.
                .stepsNumber(3)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .textSize(getResources().getDimensionPixelSize(R.dimen.sp14))
                .stepNumberTextSize(getResources().getDimensionPixelSize(R.dimen.sp16))
//                .typeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_italic))
                // other state methods are equal to the corresponding xml attributes
                .commit();*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnApplyId:
//                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_completeProfile);
                goToAttract(view);
                break;
        }
    }

    public void goToAttract(View v) {
        Intent intent = new Intent(getActivity(), CompleteProfileActivity.class);
        startActivity(intent);
    }

    class CustomTextWatcher implements TextWatcher {

        View view;

        public CustomTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = s.toString();

            switch (view.getId()) {
                case R.id.etLoanAmount:
                    //loan amount
                    txtAmount.setText(etLoanAmount.getText().toString());
                    try {
                        int num1 = Integer.parseInt(etLoanAmount.getText().toString());
                        int num2 = Integer.parseInt(etLoanTime.getText().toString());
                        int num3 = Integer.parseInt(etInterest.getText().toString());
                        String to = String.valueOf(num1 * num3 / 100);
                        txtMonthlyPayment.setText(to);
                    } catch (NumberFormatException numberFormatException) {

                    }
                    break;

                case R.id.etLoanTime:
                    //loan time
                    txtTime.setText(etLoanTime.getText().toString());
                    try {
                        int t1 = Integer.parseInt(etLoanAmount.getText().toString());
                        int t2 = Integer.parseInt(etLoanTime.getText().toString());
                        int t3 = Integer.parseInt(etInterest.getText().toString());
                        String to = String.valueOf(t1 * t3 / 100);
                        txtMonthlyPayment.setText(to);
                    } catch (NumberFormatException numberFormatException) {

                    }
                    break;

                case R.id.etInterest:
                    //loan interest
                    try {
                        int i1 = Integer.parseInt(etLoanAmount.getText().toString());
                        int i2 = Integer.parseInt(etLoanTime.getText().toString());
                        int i3 = Integer.parseInt(etInterest.getText().toString());
                        String to = String.valueOf(i1 * i3 / 100);
                        txtMonthlyPayment.setText(to);
                    } catch (NumberFormatException numberFormatException) {

                    }
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}