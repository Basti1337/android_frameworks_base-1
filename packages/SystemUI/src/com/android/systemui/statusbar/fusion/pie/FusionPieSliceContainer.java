/*
 * Copyright (C) 2014 The Fusion Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.android.systemui.statusbar.fusion.pie;

import android.graphics.Canvas;
import android.util.Slog;

import com.android.systemui.statusbar.fusion.pie.FusionPieLayout.FusionPieDrawable;
import com.android.systemui.statusbar.policy.FusionPieController.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic container for {@link FusionPieItems}.
 */
public class FusionPieSliceContainer extends FusionPieLayout.FusionPieSlice {

    protected FusionPieLayout mFusionPieLayout;
    private List<FusionPieItem> mItems = new ArrayList<FusionPieItem>();

    public FusionPieSliceContainer(FusionPieLayout parent, int initialFlags) {
        mFusionPieLayout = parent;

        flags = initialFlags | FusionPieLayout.FusionPieDrawable.VISIBLE;
    }

    @Override
    public void prepare(Position position, float scale, boolean mirrorRightFusionPie) {
        if (hasItems()) {
            int totalWidth = 0;
            boolean topRight = false;
            for (FusionPieItem item : mItems) {
                if ((item.flags & FusionPieDrawable.VISIBLE) != 0) {
                    totalWidth += item.width;
                }
            }

            // if there is no item to be lay out stop here
            if (totalWidth == 0) {
                return;
            }

            float gapMinder = ((totalWidth * GAP * 2.0f) / (mOuter + mInner));
            float deltaSweep = mSweep / totalWidth;
            if (mirrorRightFusionPie) {
                // check if it is top or right trigger to mirror later the items correct
                topRight = (position == Position.TOP) || (position == Position.RIGHT);
            } else {
                topRight = (position == Position.TOP);
            }
            int width = topRight ? totalWidth : 0;

            int viewMask = FusionPieDrawable.VISIBLE | position.FLAG;

            for (FusionPieItem item : mItems) {
                if ((item.flags & viewMask) == viewMask) {
                    if (topRight) width -= item.width;

                    item.setGeometry(mStart + deltaSweep * width,
                            item.width * deltaSweep, mInner, mOuter);
                    item.setGap(deltaSweep * gapMinder);

                    if (FusionPieLayout.DEBUG) {
                        Slog.d(FusionPieLayout.TAG, "Layout " + item.tag + " : ("
                                + (mStart + deltaSweep * width) + ","
                                + (item.width * deltaSweep) + ")");
                    }

                    if (!topRight) width += item.width;
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas, Position gravity) {
    }

    @Override
    public FusionPieItem interact(float alpha, int radius) {
        return null;
    }

    public boolean hasItems() {
        return !mItems.isEmpty();
    }

    public void addItem(FusionPieItem item) {
        mItems.add(item);
    }

    public List<FusionPieItem> getItems() {
        return mItems;
    }

    public void clear() {
        mItems.clear();
    }
}
