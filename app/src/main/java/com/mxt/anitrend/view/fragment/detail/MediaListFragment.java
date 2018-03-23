package com.mxt.anitrend.view.fragment.detail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.annimon.stream.IntPair;
import com.annimon.stream.Optional;
import com.mxt.anitrend.R;
import com.mxt.anitrend.adapter.recycler.index.SeriesListAdapter;
import com.mxt.anitrend.base.custom.consumer.BaseConsumer;
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList;
import com.mxt.anitrend.model.entity.anilist.MediaList;
import com.mxt.anitrend.model.entity.base.MediaBase;
import com.mxt.anitrend.model.entity.container.body.PageContainer;
import com.mxt.anitrend.model.entity.container.request.QueryContainer;
import com.mxt.anitrend.presenter.activity.ProfilePresenter;
import com.mxt.anitrend.presenter.fragment.SeriesPresenter;
import com.mxt.anitrend.util.CompatUtil;
import com.mxt.anitrend.util.KeyUtils;
import com.mxt.anitrend.util.NotifyUtil;
import com.mxt.anitrend.util.SeriesActionUtil;
import com.mxt.anitrend.view.activity.detail.MediaActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by max on 2017/12/18.
 * series list fragment
 */

public class MediaListFragment extends FragmentBaseList<MediaList, PageContainer<MediaList>, SeriesPresenter> implements BaseConsumer.onRequestModelChange<MediaList> {

    private String userName;
    private QueryContainer queryContainer;

    public static MediaListFragment newInstance(Bundle params, QueryContainer queryContainer) {
        Bundle args = new Bundle(params);
        args.putParcelable(KeyUtils.arg_graph_params, queryContainer);
        MediaListFragment fragment = new MediaListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Override and set presenter, mColumnSize, and fetch argument/s
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            userName = getArguments().getString(KeyUtils.arg_user_name);
            queryContainer = getArguments().getParcelable(KeyUtils.arg_graph_params);
        }
        mColumnSize = R.integer.grid_list_x2; isFilterable = true; isPager = true;
        setPresenter(new ProfilePresenter(getContext()));
        setViewModel(true);
    }

    /**
     * Is automatically called in the @onStart Method if overridden in list implementation
     */
    @Override
    protected void updateUI() {
        if(mAdapter == null) {
            mAdapter = new SeriesListAdapter(model, getContext());
            ((SeriesListAdapter)mAdapter).setCurrentUser(userName);
        }
        if(model != null && model.size() > 0)
            if(getPresenter().isCurrentUser(userName))
                getPresenter().getDatabase().saveMediaLists(model);
        injectAdapter();
    }

    /**
     * All new or updated network requests should be handled in this method
     */
    @Override
    public void makeRequest() {
        queryContainer.setVariable(KeyUtils.arg_user_name, userName)
                .setVariable(KeyUtils.arg_page, getPresenter().getCurrentPage())
                .setVariable(KeyUtils.arg_sort, null);
        getViewModel().getParams().putParcelable(KeyUtils.arg_graph_params, queryContainer);
        getViewModel().requestData(KeyUtils.MEDIA_LIST_BROWSE_REQ, getContext());
    }

    /**
     * Initialize the contents of the Fragment host's standard options menu.  You
     * should place your menu items in to <var>menu</var>.  For this method
     * to be called, you must have first called {@link #setHasOptionsMenu}.  See
     * {@link Activity#onCreateOptionsMenu(Menu) Activity.onCreateOptionsMenu}
     * for more information.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater menu inflater
     * @see #setHasOptionsMenu
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_genre).setVisible(false);
        menu.findItem(R.id.action_type).setVisible(false);
        menu.findItem(R.id.action_year).setVisible(false);
        menu.findItem(R.id.action_status).setVisible(false);
    }

    /**
     * When the target view from {@link View.OnClickListener}
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been clicked
     * @param data   the model that at the click index
     */
    @Override
    public void onItemClick(View target, MediaList data) {
        switch (target.getId()) {
            case R.id.series_image:
                MediaBase mediaBase = data.getMedia();
                Intent intent = new Intent(getActivity(), MediaActivity.class);
                intent.putExtra(KeyUtils.arg_id, data.getMediaId());
                intent.putExtra(KeyUtils.arg_media_type, mediaBase.getType());
                CompatUtil.startRevealAnim(getActivity(), target, intent);
                break;
        }
    }

    /**
     * When the target view from {@link View.OnLongClickListener}
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been long clicked
     * @param data   the model that at the long click index
     */
    @Override
    public void onItemLongClick(View target, MediaList data) {
        switch (target.getId()) {
            case R.id.series_image:
                if(getPresenter().getApplicationPref().isAuthenticated()) {
                    seriesActionUtil = new SeriesActionUtil.Builder()
                            .setModel(data).build(getActivity());
                    seriesActionUtil.startSeriesAction();
                } else
                    NotifyUtil.makeText(getContext(), R.string.info_login_req, R.drawable.ic_group_add_grey_600_18dp, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        onRefresh();
    }


    @SuppressLint("SwitchIntDef")
    @Override @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onModelChanged(BaseConsumer<MediaList> consumer) {
        int pairIndex;
        if(getPresenter().isCurrentUser(userName)) {
            Optional<IntPair<MediaList>> pairOptional = CompatUtil.findIndexOf(model, consumer.getChangeModel());
            if (pairOptional.isPresent())
                switch (consumer.getRequestMode()) {
                    case KeyUtils.MUT_SAVE_MEDIA_LIST:
                        pairIndex = pairOptional.get().getFirst();
                        model.set(pairIndex, consumer.getChangeModel());
                        mAdapter.onItemChanged(consumer.getChangeModel(),pairIndex);
                        break;
                    case KeyUtils.MUT_DELETE_MEDIA_LIST:
                        pairIndex = pairOptional.get().getFirst();
                        model.remove(pairIndex);
                        mAdapter.onItemRemoved(pairIndex);
                        break;
                }
        }
    }

    @Override
    public void onChanged(@Nullable PageContainer<MediaList> content) {
        if(content != null) {
            if(content.hasPageInfo())
                pageInfo = content.getPageInfo();
            if(!content.isEmpty())
                onPostProcessed(content.getPageData());
        }
    }
}