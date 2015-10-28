package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;

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
     * An object to draw a board around the graph
     */
    private Boarder mBoarder;
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

        zoomDisplayX.setTheListener(new ZoomDisplay.ZoomChangedListener() {
            @Override
            public void zoomChanged() {
//                for (GridLines gridLines : mGridLinesMajor) {
//                    if (gridLines.getAxisOrientation() == GridLines.AxisOrientation.xAxis) {
//                        gridLines.updateZoomLevel();
//                    }
//                }
//                for (GridLines gridLines : mGridLinesMinor) {
//                    if (gridLines.getAxisOrientation() == GridLines.AxisOrientation.xAxis) {
//                        gridLines.updateZoomLevel();
//                    }
//                }
            }
        });
        zoomDisplayY.setTheListener(new ZoomDisplay.ZoomChangedListener() {
            @Override
            public void zoomChanged() {
//                for (GridLines gridLines : mGridLinesMajor) {
//                    if (gridLines.getAxisOrientation() == GridLines.AxisOrientation.yAxis) {
//                        gridLines.updateZoomLevel();
//                    }
//                }
//                for (GridLines gridLines : mGridLinesMinor) {
//                    if (gridLines.getAxisOrientation() == GridLines.AxisOrientation.yAxis) {
//                        gridLines.updateZoomLevel();
//                    }
//                }
            }
        });

        mBackground = new Background(new DrawableArea(0, 0, 0, 0));
        mBoarder = new Boarder(new DrawableArea(0, 0, 0, 0));
//        mXAxis = new LinXAxis(new DrawableArea(0, 0, 0, 0));
//        mXAxis.setGridStrokeWidth(sGridLineStrokeWidth);
//
//        mYAxis = new LinYAxis(new DrawableArea(0, 0, 0, 0));
//        mYAxis.setGridStrokeWidth(sGridLineStrokeWidth);

        mGridLinesMajor.add(new LogYGridLines(new DrawableArea(0, 0, 0, 0), mZoomDisplayY));
        mGridLinesMajor.add(new LogXGridLines(new DrawableArea(0, 0, 0, 0), mZoomDisplayX));

        mGridLinesMinor.add(new LogXGridLines(new DrawableArea(0, 0, 0, 0), mZoomDisplayX));

        for (GridLines gridLines : mGridLinesMinor) {
            gridLines.setGridStrokeWidth(2);
            gridLines.setColor(Color.DKGRAY);
        }
    }

    public void surfaceChanged(int width, int height) {
        mBackground.surfaceChange(new DrawableArea(0, 0, width, height));

        mBoarder.surfaceChange(new DrawableArea(0, 0, width, height));

        for (GridLines gridLines : mGridLinesMajor) {
            // We have to take into account the size of the boarders
            gridLines.surfaceChange(new DrawableArea(mBoarder.getStrokeWidth(),
                    mBoarder.getStrokeWidth(), width - (2 * mBoarder.getStrokeWidth()),
                    height - (2 * mBoarder.getStrokeWidth())));

            if (gridLines.getAxisOrientation() == GridLines.AxisOrientation.xAxis) {
                for (GridLines gridLinesMinor : mGridLinesMinor) {
                    if (gridLinesMinor.getAxisOrientation() == GridLines.AxisOrientation.xAxis) {
                        int minorGridLineWidth = (int) gridLines.intersect(0);
                        gridLinesMinor.surfaceChange(new DrawableArea(0, 0,
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
        mBoarder.doDraw(canvas);
//        mXAxis.doDraw(canvas);
//        mYAxis.doDraw(canvas);
//
        for (GridLines gridLines : mGridLinesMajor) {
            gridLines.doDraw(canvas);
        }
        for (GridLines gridLines : mGridLinesMinor) {
            gridLines.doDraw(canvas);
        }
    }
}
