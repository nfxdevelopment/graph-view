package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collection;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 *
 * The background manager holders many drawable objects which are considered background objects
 * It makes batch calls to DoDraw functions of all it's members and individual sizing options
 * are possible by overriding surfaceChanged
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
     * Handles the drawing of all grid lines
     */
    private Collection<GridLines> mGridLinesMajor = new ArrayList<>();
    private Collection<GridLines> mGridLinesMinor = new ArrayList<>();
    /**
     * Handles the drawing of all text on axis
     */
    private Collection<AxisText> mXAxisText = new ArrayList<>();
    private Collection<AxisText> mYAxisText = new ArrayList<>();
    private boolean mShowAxisText = true;
    /**
     * These object will be passed into drawable objects that need to be resized based on zoom level
     */
    private ZoomDisplay mZoomDisplayX;
    private ZoomDisplay mZoomDisplayY;

    /**
     * Constructor for Background Manager, all drawable objects are created here
     * @param context application context
     * @param zoomDisplayX reference to graphManagers zoomDisplay for x
     * @param zoomDisplayY reference to graphManagers zoomDisplay for y
     */
    public BackgroundManager(Context context, ZoomDisplay zoomDisplayX, ZoomDisplay zoomDisplayY) {

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

        mBackground = new Background();
        mBoarder = new Boarder();


        GridLines xGridLines = new LinXGridLines(mZoomDisplayX);
        GridLines yGridLines = new LinYGridLines(mZoomDisplayY);
        mGridLinesMajor.add(xGridLines);
        mGridLinesMajor.add(yGridLines);

        mGridLinesMinor.add(new LogXGridLines(mZoomDisplayX));

        mXAxisText.add(new XAxisText(context, xGridLines));
        mYAxisText.add(new YAxisText(context, yGridLines));

        for (GridLines gridLines : mGridLinesMinor) {
            gridLines.setGridStrokeWidth(2);
            gridLines.setColor(Color.DKGRAY);
        }
    }

    /**
     * Call when the surface view changes it's dimensions
     *
     * @param width  passed throught from surfaceview
     * @param height passed throught from surfaceview
     */
    public void surfaceChanged(int width, int height) {
        mBackground.getDrawableArea().setDrawableArea(0, 0, width, height);

        int boarderXOffset = 0;
        int boarderYOffset = 0;
        int boarderWidth = 0;
        int boarderHeight = 0;

        if (mShowAxisText) {
            for (AxisText axisText : mXAxisText) {
                axisText.getDrawableArea().setDrawableArea(0, height - (int) axisText.getTextSize(),
                        width, (int) axisText.getTextSize());

                boarderYOffset = 0;
                boarderHeight = height - (int) axisText.getTextSize();
            }
            for (AxisText axisText : mYAxisText) {
                axisText.getDrawableArea().setDrawableArea(0, 0,
                        (int) axisText.getMaximumTextWidth(), height);

                boarderXOffset = (int) axisText.getMaximumTextWidth();
                boarderWidth = width - (int) axisText.getMaximumTextWidth();
            }
        } else {
            boarderXOffset = 0;
            boarderYOffset = 0;
            boarderWidth = width;
            boarderHeight = height;
        }

        mBoarder.getDrawableArea().setDrawableArea(boarderXOffset, boarderYOffset, boarderWidth,
                boarderHeight);

        int gridLinesXOffset = boarderXOffset + mBoarder.getStrokeWidth();
        int gridLinesYOffset = boarderYOffset + mBoarder.getStrokeWidth();
        int gridLinesWidth = boarderWidth - (mBoarder.getStrokeWidth() * 2);
        int gridLinesHeight = boarderHeight - (mBoarder.getStrokeWidth() * 2);

        for (GridLines gridLines : mGridLinesMajor) {
            // We have to take into account the size of the boarders
            gridLines.getDrawableArea().setDrawableArea(gridLinesXOffset, gridLinesYOffset,
                    gridLinesWidth, gridLinesHeight);

            if (gridLines.getAxisOrientation() == GridLines.AxisOrientation.xAxis) {
                for (GridLines gridLinesMinor : mGridLinesMinor) {
                    if (gridLinesMinor.getAxisOrientation() == GridLines.AxisOrientation.xAxis) {
                        int minorGridLineWidth = (int) gridLines.intersect(0);
                        gridLinesMinor.getDrawableArea().setDrawableArea(gridLinesXOffset,
                                gridLinesYOffset, minorGridLineWidth, gridLinesHeight);
                    }
                }
            }
        }
    }

    /**
     * Call with the canvas to draw on
     * @param canvas canvas to draw the objects onto
     */
    public void doDraw(Canvas canvas) {
        mBackground.doDraw(canvas);
        mBoarder.doDraw(canvas);

        for (GridLines gridLines : mGridLinesMajor) {
            gridLines.doDraw(canvas);
        }
        for (GridLines gridLines : mGridLinesMinor) {
            gridLines.doDraw(canvas);
        }

        for (AxisText axisText : mXAxisText) {
            axisText.doDraw(canvas);
        }
        for (AxisText axisText : mYAxisText) {
            axisText.doDraw(canvas);
        }
    }
}
