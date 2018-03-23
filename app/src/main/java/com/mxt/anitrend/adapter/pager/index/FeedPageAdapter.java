package com.mxt.anitrend.adapter.pager.index;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.mxt.anitrend.R;
import com.mxt.anitrend.base.custom.pager.BaseStatePageAdapter;
import com.mxt.anitrend.util.GraphUtil;
import com.mxt.anitrend.util.KeyUtils;
import com.mxt.anitrend.view.fragment.index.FeedFragment;

/**
 * Created by max on 2017/11/07.
 */

public class FeedPageAdapter extends BaseStatePageAdapter {

    public FeedPageAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager, context);
        setPagerTitles(R.array.feed_titles);
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return FeedFragment.newInstance(getParams(), GraphUtil.getDefaultQuery(true)
                        .setVariable(KeyUtils.arg_type, KeyUtils.MEDIA_LIST));
            case 1:
                return FeedFragment.newInstance(getParams(), GraphUtil.getDefaultQuery(true)
                        .setVariable(KeyUtils.arg_type, KeyUtils.TEXT));
            case 2:
                return FeedFragment.newInstance(getParams(), GraphUtil.getDefaultQuery(true)
                        .setVariable(KeyUtils.arg_isFollowing, false)
                        .setVariable(KeyUtils.arg_isMixed, true));
        }
        return null;
    }
}