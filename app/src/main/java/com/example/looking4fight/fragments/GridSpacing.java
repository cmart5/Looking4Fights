package com.example.looking4fight.fragments;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
public class GridSpacing extends RecyclerView.ItemDecoration {
    private final int Spacing;

    public GridSpacing(int spacing) {
        this.Spacing = spacing;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = Spacing;
        outRect.bottom = Spacing;
        outRect.left = Spacing;
        outRect.right = Spacing;
    }
}
