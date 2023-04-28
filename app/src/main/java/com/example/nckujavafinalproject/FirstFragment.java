package com.example.nckujavafinalproject;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.nckujavafinalproject.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    AnimationDrawable lottery; //轉盤
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
        //

        view.findViewById(R.id.buttonFirst).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentCount = 100;
                /*
                隨機選取餐廳的區域
                 */
                try {
                    Thread.sleep(500); //點級按鈕後，延遲0.5秒跳轉
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                lottery.stop(); //停止轉盤畫面
                FirstFragmentDirections.ActionFirstFragmentToSecondFragment action = FirstFragmentDirections.actionFirstFragmentToSecondFragment(currentCount);
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