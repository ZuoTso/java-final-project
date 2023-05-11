package com.example.nckujavafinalproject;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.nckujavafinalproject.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private AnimationDrawable lottery; //轉盤

    private RestaurantViewModel mRestaurantViewModel;

    private List<Restaurant> mAllRestaurants = new ArrayList<Restaurant>(); //餐廳array

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //轉盤動畫
        ImageView image = (ImageView) getView().findViewById(R.id.image);
        image.setBackgroundResource(R.drawable.animation);
        lottery = (AnimationDrawable) image.getBackground();
        lottery.stop();
        lottery.start(); //開始轉動

        //拿取資料庫
        mRestaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
        mRestaurantViewModel.getAllRestaurants().observe(requireActivity(),restaurants -> {
            mAllRestaurants = restaurants;
        });


        view.findViewById(R.id.buttonFirst).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pick;
                String RestaurantName;
                String picklabels;




                if (mAllRestaurants.size()==0){
                    RestaurantName="無符合標準的餐廳";
                    picklabels="無符合標準的標籤";
                }
                else {
                    pick = (int) (Math.random() * (mAllRestaurants.size())); //隨機選取餐廳index
                    picklabels = mAllRestaurants.get(pick).getLabels(); //取得中選餐廳的標籤
                    RestaurantName = mAllRestaurants.get(pick).getName(); //取得中選餐廳的名字
                }

                try {
                    Thread.sleep(500); //點級按鈕後，延遲0.5秒跳轉
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                lottery.stop(); //停止轉盤畫面
                FirstFragmentDirections.ActionFirstFragmentToSecondFragment action = FirstFragmentDirections.actionFirstFragmentToSecondFragment(RestaurantName,picklabels);
                NavHostFragment.findNavController(FirstFragment.this).navigate(action);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
