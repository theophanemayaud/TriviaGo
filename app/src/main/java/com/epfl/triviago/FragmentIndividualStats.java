package com.epfl.triviago;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentIndividualStats extends Fragment {

    public static FragmentIndividualStats getInstance() {
        FragmentIndividualStats fragmentIndividualStats = new FragmentIndividualStats();
        return  fragmentIndividualStats;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individual_stats, container, false);
        return  view;
    }

}
