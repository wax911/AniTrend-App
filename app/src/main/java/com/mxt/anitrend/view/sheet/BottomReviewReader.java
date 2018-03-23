package com.mxt.anitrend.view.sheet;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.mxt.anitrend.R;
import com.mxt.anitrend.base.custom.sheet.BottomSheetBase;
import com.mxt.anitrend.base.custom.view.text.SeriesTitleView;
import com.mxt.anitrend.databinding.BottomSheetReviewBinding;
import com.mxt.anitrend.model.entity.anilist.Review;
import com.mxt.anitrend.util.CompatUtil;
import com.mxt.anitrend.util.KeyUtils;
import com.mxt.anitrend.util.NotifyUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by max on 2017/11/05.
 * Review reader bottom sheet
 */

public class BottomReviewReader extends BottomSheetBase {

    private Review model;
    private BottomSheetReviewBinding binding;

    protected @BindView(R.id.series_title)
    SeriesTitleView seriesTitleView;

    public static BottomReviewReader newInstance(Bundle bundle) {
        BottomReviewReader fragment = new BottomReviewReader();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Set up your custom bottom sheet and check for arguments if any
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
            model = getArguments().getParcelable(KeyUtils.arg_model);
    }

    /**
     * Setup your view un-binder here as well as inflating other views as needed
     * into your view stub
     *
     * @param savedInstanceState
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        binding = BottomSheetReviewBinding.inflate(CompatUtil.getLayoutInflater(getActivity()));
        dialog.setContentView(binding.getRoot());
        unbinder = ButterKnife.bind(this, dialog);
        createBottomSheetBehavior(binding.getRoot());
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.setModel(model);
        seriesTitleView.setTitle(model);
        NotifyUtil.makeText(getContext(), R.string.text_processing, Toast.LENGTH_SHORT).show();
    }

    public static class Builder extends BottomSheetBuilder {

        @Override
        public BottomSheetBase build() {
            return newInstance(bundle);
        }

        public BottomSheetBuilder setReview(Review review) {
            bundle.putParcelable(KeyUtils.arg_model, review);
            return this;
        }
    }
}