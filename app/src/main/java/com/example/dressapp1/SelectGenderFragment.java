package com.example.dressapp1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class SelectGenderFragment extends Fragment implements View.OnClickListener {
    View view;
    ImageButton manBtn, womanBtn, addPost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_select_gender, container, false);

        manBtn = view.findViewById(R.id.man_btn);
        womanBtn = view.findViewById(R.id.woman_btn);
        addPost = view.findViewById(R.id.add_new_post_btn);

        manBtn.setOnClickListener(this);
        womanBtn.setOnClickListener(this);
        addPost.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.man_btn:
                Navigation.findNavController(view).navigate(SelectGenderFragmentDirections.actionSelectGenderFragmentToProductGridFragment2());
                break;
            case R.id.woman_btn:
                Navigation.findNavController(view).navigate(SelectGenderFragmentDirections.actionSelectGenderFragmentToProductGridFragment2());
                break;
            case R.id.add_new_post_btn:
                Navigation.findNavController(view).navigate(SelectGenderFragmentDirections.actionSelectGenderFragmentToNewPostFragment());
                break;
            default:
                break;
        }
    }
}