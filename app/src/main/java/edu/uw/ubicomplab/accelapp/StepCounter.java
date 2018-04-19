package edu.uw.ubicomplab.accelapp;

import android.net.sip.SipSession;
import android.util.EventLog;

import com.jjoe64.graphview.series.DataPoint;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.EventListener;
import java.util.concurrent.Callable;


public class StepCounter {
    private final int NZEROS = 2;
    private final int NPOLES = 2;
    private final double GAIN = 1.804169259;
    private double[] xv = new double[NZEROS+1];
    private double[] yv = new double[NPOLES+1];

    private int windowSize = 500;
    private int numPts = 0;
    private DescriptiveStatistics accelWindow = new DescriptiveStatistics(windowSize);
    public int stepCount = 0;

    private boolean upFound = false;

    private double threshHi = 80.0;
    private double threshLo = -60.0;

    private StepEventListener stepListener;

    public StepCounter(StepEventListener listener) {
        stepListener = listener;
    }

    public DataPoint[] addDataPoint(float ax, float ay, float az) {
        DataPoint[] pts = new DataPoint[2];

        numPts += 1;

        // Calculate magnitude and add to window
        double amag = Math.sqrt(az*az + ax*ax + ay*ay);
        pts[0] = new DataPoint(numPts, amag);
        amag = lowPass(amag);
        pts[1] = new DataPoint(numPts, amag);
        accelWindow.addValue(amag);

        processData(amag);

        return pts;
    }

    private double lowPass(double in)
    {
        xv[0] = xv[1]; xv[1] = xv[2];
        xv[2] = in / GAIN;
        yv[0] = yv[1]; yv[1] = yv[2];
        yv[2] =   (xv[0] + xv[2]) + 2 * xv[1]
                + ( -0.8008026467 * yv[0]) + (  1.7786317778 * yv[1]);
        return yv[2] - 1000;
    }


    private void processData(double val) {
        if (!upFound) {
            if (val > threshHi) {
                upFound = true;
            }
        }
        else {
            if (val < threshLo) {
                stepCount += 1;
                upFound = false;
                stepListener.onStep(stepCount);
            }
        }
    }

    public double getMaxY() {
        return accelWindow.getMax();
    }

    public double getMinY() {
        return accelWindow.getMin();
    }

    public void reset() {
        stepCount = 0;
        accelWindow.clear();
        stepListener.onStep(0);
    }

    public interface StepEventListener {
        void onStep(int steps);
    }
}