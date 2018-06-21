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

public class MessageFragment extends BaseSmackPushFragment {

    public static MessageFragment newInstance(Bundle bundle){
        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.title_msg));
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
