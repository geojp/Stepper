package edu.uw.ubicomplab.accelapp;

import android.content.Context;
import android.graphics.Color;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import android.provider.ContactsContract;
import android.view.View;

public class SignalGrapher {

    private LineGraphSeries<DataPoint> timeAccelX = new LineGraphSeries<>();
    private GraphView graph;
    private static final int GRAPH_X_BOUNDS = 500; // Adjust to show more points on graph
    private static final int GRAPH_Y_BOUNDS = 1000;
    private int graphColor[] = {Color.argb(255,244,170,50),
            Color.argb(255, 60, 175, 240),
            Color.argb(225, 50, 220, 100)};

    public SignalGrapher(View v) {
        graph = (GraphView)v;
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graph.setBackgroundColor(Color.TRANSPARENT);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(GRAPH_X_BOUNDS);
        graph.getViewport().setMinY(-GRAPH_Y_BOUNDS);
        graph.getViewport().setMaxY(GRAPH_Y_BOUNDS);
        timeAccelX.setColor(graphColor[0]);
        timeAccelX.setThickness(10);
        graph.addSeries(timeAccelX);
    }

    public void addSignalPoint(DataPoint pt) {
        timeAccelX.appendData(pt, true, GRAPH_X_BOUNDS);
        // Advance the graph
        graph.getViewport().setMinX(pt.getX() - GRAPH_X_BOUNDS);
        graph.getViewport().setMaxX(pt.getX());
    }
}
