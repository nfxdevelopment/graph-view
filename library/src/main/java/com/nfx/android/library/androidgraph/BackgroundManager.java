package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Collection;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 */
public class BackgroundManager {
    private static final String TAG = "BackgroundManager";
    /**
     * An object which draws onto the canvas
     **/
    private Background mBackground;
    /**
     * Handles the drawing of each axis text
     **/
    private XAxis mXAxis;
    private YAxis mYAxis;
    /**
     * Handles the drawing of all grid lines
     */
    private Collection<GridLines> mGridLinesMajor = new ArrayList<>();
    private Collection<GridLines> mGridLinesMinor = new ArrayList<>();
    /**
     * These object will be passed into drawable objects that need to be resized based on zoom level
     */
    private ZoomDisplay mZoomDisplayX;
    private ZoomDisplay mZoomDisplayY;

    public BackgroundManager(ZoomDisplay zoomDisplayX, ZoomDisplay zoomDisplayY) {

        mZoomDisplayX = zoomDisplayX;
        mZoomDisplayY = zoomDisplayY;

        mBackground = new Background(new DrawableArea(0, 0, 0, 0));
//        mXAxis = new LinXAxis(new DrawableArea(0, 0, 0, 0));
//        mXAxis.setGridStrokeWidth(sGridLineStrokeWidth);
//
//        mYAxis = new LinYAxis(new DrawableArea(0, 0, 0, 0));
//        mYAxis.setGridStrokeWidth(sGridLineStrokeWidth);

        mGridLinesMajor.add(new LinYGridLines(new DrawableArea(0, 0, 0, 0), mZoomDisplayY));
        mGridLinesMajor.add(new LinXGridLines(new DrawableArea(0, 0, 0, 0), mZoomDisplayX));
        mGridLinesMinor.add(new LogXGridLines(new DrawableArea(0, 0, 0, 0), mZoomDisplayX));
    }

    public void surfaceChanged(int width, int height) {
        mBackground.surfaceChange(new DrawableArea(0, 0, width, height));
        for (GridLines gridLines : mGridLinesMajor) {
            gridLines.surfaceChange(new DrawableArea(0, 0, width, height));

            if (gridLines.getAxisOrientation() == GridLines.AxisOrientation.xAxis) {
                XGridLines xGridLinesMajor = (XGridLines) gridLines;
                for (GridLines gridLinesMinor : mGridLinesMinor) {
                    if (gridLinesMinor.getAxisOrientation() == GridLines.AxisOrientation.xAxis) {
                        int xOffset = (int) xGridLinesMajor.xIntersect(0);
                        int minorGridLineWidth = (int) xGridLinesMajor.xIntersect(1) - xOffset;
                        gridLinesMinor.surfaceChange(new DrawableArea(xOffset, 0,
                                minorGridLineWidth, height));
                    }
                }
            }
        }


//        mXAxis.surfaceChange(new DrawableArea(0, height - getPaddingBottom() -
//                (int) sGridLineStrokeWidth, width, (int) sGridLineStrokeWidth));
//
//        mYAxis.surfaceChange(new DrawableArea(0, 0, (int) sGridLineStrokeWidth, height));
    }

    public void doDraw(Canvas canvas) {
        mBackground.doDraw(canvas);
//        mXAxis.doDraw(canvas);
//        mYAxis.doDraw(canvas);

        for (GridLines gridLines : mGridLinesMajor) {
            gridLines.doDraw(canvas);
        }
        for (GridLines gridLines : mGridLinesMinor) {
            gridLines.doDraw(canvas);
        }
    }
}
