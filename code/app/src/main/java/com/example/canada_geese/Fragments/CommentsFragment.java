package com.example.canada_geese.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canada_geese.Adapters.CommentAdapter;
import com.example.canada_geese.Managers.DatabaseManager;
import com.example.canada_geese.Models.CommentModel;
import com.example.canada_geese.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A BottomSheetDialogFragment that displays and manages comments for a given mood event.
 */
public class CommentsFragment extends BottomSheetDialogFragment {

    private String moodEventId;
    private String moodOwnerId;
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private List<CommentModel> commentList = new ArrayList<>();
    private TextView tvNoComments;

    public CommentsFragment() {}

    /**
     * Creates a new instance of CommentsFragment with the given mood event and owner.
     *
     * @param moodEventId The ID of the mood event.
     * @param moodOwnerId The ID of the owner of the mood event.
     * @return A new instance of CommentsFragment.
     */
    public static CommentsFragment newInstance(String moodEventId, String moodOwnerId) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putString("moodEventId", moodEventId);
        args.putString("moodOwnerId", moodOwnerId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates a new dialog for the fragment.
     *
     * @param savedInstanceState The saved instance state.
     * @return A new BottomSheetDialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return dialog;
    }

    /**
     * Creates the view for the fragment.
     *
     * @param inflater The LayoutInflater used to inflate the view.
     * @param container The parent view that this fragment's UI should be attached to.
     * @param savedInstanceState The saved instance state.
     * @return The created view.
     */
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comments_bottom_sheet, container, false);
    }

    /**
     * Called when the fragment is started. Sets the height of the bottom sheet dialog.
     */
    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                int desiredHeight = (int) (screenHeight * 0.8);
                bottomSheet.getLayoutParams().height = desiredHeight;
                bottomSheet.setLayoutParams(bottomSheet.getLayoutParams());
                BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setPeekHeight(desiredHeight);
            }
        }
    }

    /**
     * Called when the view is created. Initializes the UI components and sets up the comment posting functionality.
     *
     * @param view The created view.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            moodEventId = getArguments().getString("moodEventId");
            moodOwnerId = getArguments().getString("moodOwnerId");
        }

        final EditText etComment = view.findViewById(R.id.et_comment);
        final Button btnPost = view.findViewById(R.id.btn_post_comment);
        rvComments = view.findViewById(R.id.rv_comments);
        tvNoComments = view.findViewById(R.id.tv_no_comments);

        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        commentAdapter = new CommentAdapter(commentList, getContext(), moodEventId);
        rvComments.setAdapter(commentAdapter);

        if (etComment != null) {
            etComment.requestFocus();
            new Handler().postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(etComment, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 100);
        }

        btnPost.setOnClickListener(v -> postComment(etComment));
        loadComments();
    }

    /**
     * Posts a new comment to Firestore and updates the UI.
     */
    private void postComment(EditText etComment) {
        String commentText = etComment.getText().toString().trim();
        if (commentText.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = firebaseUser.getUid();
        FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        String username = task.getResult().getString("username");
                        if (username == null || username.isEmpty()) {
                            username = firebaseUser.getEmail();
                        }
                        CommentModel comment = new CommentModel(commentText, username, new Date());
                        comment.setUserId(uid);

                        DatabaseManager.getInstance().addComment(moodOwnerId, moodEventId, comment, postTask -> {
                            if (postTask.isSuccessful()) {
                                Toast.makeText(getContext(), "Comment posted", Toast.LENGTH_SHORT).show();
                                etComment.setText("");
                            } else {
                                String errorMsg = postTask.getException() != null ? postTask.getException().getMessage() : "Unknown error";
                                Toast.makeText(getContext(), "Failed to post comment: " + errorMsg, Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Failed to retrieve username", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Loads the list of comments from Firestore and updates the adapter.
     */
    private void loadComments() {
        if (moodOwnerId == null || moodEventId == null) return;

        FirebaseFirestore.getInstance().collection("users")
                .document(moodOwnerId)
                .collection("moodEvents")
                .document(moodEventId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) return;

                    commentList.clear();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        CommentModel comment = doc.toObject(CommentModel.class);
                        if (comment != null) {
                            comment.setDocumentId(doc.getId());
                            commentList.add(comment);
                        }
                    }

                    if (commentList.isEmpty()) {
                        tvNoComments.setVisibility(View.VISIBLE);
                        rvComments.setVisibility(View.GONE);
                    } else {
                        tvNoComments.setVisibility(View.GONE);
                        rvComments.setVisibility(View.VISIBLE);
                    }

                    commentAdapter.updateComments(commentList);
                });
    }
}
