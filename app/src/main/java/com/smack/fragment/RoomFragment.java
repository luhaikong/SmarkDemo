package com.smack.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smack.BaseSmackPushFragment;
import com.smack.R;

/**
 *
 * @author MyPC
 * @date 2018/6/21
 */

public class RoomFragment extends BaseSmackPushFragment {

    public static RoomFragment newInstance(Bundle bundle){
        RoomFragment fragment = new RoomFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.title_chatRoom));
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
