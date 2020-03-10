package com.example.dyscours;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragmentParticipate.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fragmentParticipate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmentParticipate extends DyscoursFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "debugTagParticipate";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;

    public fragmentParticipate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmentParticipate.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmentParticipate newInstance(String param1, String param2) {
        fragmentParticipate fragment = new fragmentParticipate();
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
        Log.d(TAG, "participateOnCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_participate, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.debateList);
        final ArrayList<Debate> arrayList = ((MainActivity )getActivity()).getParticipateDebates();

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        RecyclerView.Adapter adapter = new TopicRecyclerAdapter(arrayList, this, (MainActivity) getActivity());
        recyclerView.setAdapter(adapter);

        ImageButton addButton = (ImageButton) view.findViewById(R.id.addDebateButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder();
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        return view;
    }

    public void updateView(){
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void dialogBuilder(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final DyscoursFragment finalThis = this;
        final View finalView = finalThis.getActivity().getLayoutInflater().inflate(R.layout.dialog_add_debate, null);
        builder.setTitle("Creating a debate").setView(finalView).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText opinionEditText = finalView.findViewById(R.id.opinionEditText);
                String debateName = opinionEditText.getText().toString();
                if (debateName == null || debateName.isEmpty()){
                    return;
                }
                Debate debate = new Debate(opinionEditText.getText().toString(), 600);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ChatActivity.DEBATE_VALUE, debate);
                bundle.putBoolean(ChatActivity.IS_PARTICIPATE, true);
                bundle.putBoolean(ChatActivity.IS_USER_1, true);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }).setNegativeButton("Exit", null);
        builder.create().show();
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
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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

    public static String getArgParam1() {
        return ARG_PARAM1;
    }

    public static String getArgParam2() {
        return ARG_PARAM2;
    }

    public static String getTAG() {
        return TAG;
    }

    public String getmParam1() {
        return mParam1;
    }

    public void setmParam1(String mParam1) {
        this.mParam1 = mParam1;
    }

    public String getmParam2() {
        return mParam2;
    }

    public void setmParam2(String mParam2) {
        this.mParam2 = mParam2;
    }

    public OnFragmentInteractionListener getmListener() {
        return mListener;
    }

    public void setmListener(OnFragmentInteractionListener mListener) {
        this.mListener = mListener;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

}
