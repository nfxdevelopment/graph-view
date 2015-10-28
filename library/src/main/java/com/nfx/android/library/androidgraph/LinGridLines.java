package com.nfx.android.library.androidgraph;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 */
public abstract class LinGridLines extends GridLines {
    public LinGridLines(DrawableArea drawableArea, ZoomDisplay zoomDisplay, AxisOrientation
            axisOrientation) {
        super(drawableArea, zoomDisplay, axisOrientation);
    }

    /**
     * Gives the value of where a grid line will interest x on the screen
     *
     * @param gridLine        grid line to find, base 0
     * @param dimensionLength Either width or height
     * @return the x Intersect or -1 if the grid line is out of range
     */
    public float intersect(int gridLine, int dimensionLength) {
        if (gridLine >= mNumberOfGridLines || gridLine < 0) {
            return -1f;
        }
        // +1 as there would be mNumberOfGridLines intersecting the graph which splits
        // mNumberOfGridLines + 1 areas
        float spacing = ((float) dimensionLength) / (float) (mNumberOfGridLines + 1);
        // The first line lies at on the area boundary of the first block hence +1
        return spacing * (float) (gridLine + 1);
    }
}
