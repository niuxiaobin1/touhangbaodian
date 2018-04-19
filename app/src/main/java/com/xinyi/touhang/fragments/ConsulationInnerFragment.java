package com.xinyi.touhang.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xinyi.touhang.R;
import com.xinyi.touhang.adapter.ConsulationInnerAdapter;
import com.xinyi.touhang.base.BaseFragment;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DivItemDecoration;
import com.xinyi.touhang.utils.DividerDecoration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 咨询内部fragment
 * Use the {@link ConsulationInnerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsulationInnerFragment extends BaseFragment {

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.recylerView)
    RecyclerView recylerView;


    private List<View> views;//top views

    private MyPageraAdapter adapter;//banner adapter
    private ConsulationInnerAdapter innerAdapter;//news adapter

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ConsulationInnerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsulationInnerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsulationInnerFragment newInstance(String param1, String param2) {
        ConsulationInnerFragment fragment = new ConsulationInnerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_consulation_inner, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void initViews() {
        views = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            views.add(makeView());
        }
        adapter = new MyPageraAdapter();
        viewPager.setAdapter(adapter);

        recylerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recylerView.addItemDecoration(new DividerDecoration(getActivity(),R.color.colorItem,DensityUtil.dip2px(getActivity(),1)));
        innerAdapter = new ConsulationInnerAdapter(getActivity());
        recylerView.setAdapter(innerAdapter);
    }

    @Override
    public void initDatas() {

    }


    private View makeView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.page_item, null);
        //resize imageview
        int contentWidth = DensityUtil.getScreenWidth(getActivity())
                - DensityUtil.dip2px(getActivity(), 7) * 4;
        ImageView imageView = view.findViewById(R.id.imageView);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        params.width = contentWidth / 2;
        params.height = (int) (params.width * 0.6);
        imageView.setLayoutParams(params);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class MyPageraAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (position >= views.size()) {
                return;
            }
            container.removeView(views.get(position));
        }

        @Override
        public float getPageWidth(int position) {
            return 0.5f;
        }
    }

}
