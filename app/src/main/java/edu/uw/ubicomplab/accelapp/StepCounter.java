package edu.uw.ubicomplab.accelapp;

import android.net.sip.SipSession;
import android.util.EventLog;
import android.widget.EditText;

import com.jjoe64.graphview.series.DataPoint;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.EventListener;
import java.util.concurrent.Callable;


public class StepCounter {
    private final int NZEROS = 2;
    private final int NPOLES = 2;
    private final double GAIN = 1.465674116;
    private double[] xv = new double[NZEROS+1];
    private double[] yv = new double[NPOLES+1];


    private int windowSize = 500;
    private int numPts = 0;
    private DescriptiveStatistics accelWindow = new DescriptiveStatistics(windowSize);
    public int stepCount = 0;

    private boolean upFound = false;

    private double threshHi = 80;
    private double threshLo = -20;

    private int SKIP = 20;
    private int skipedSamples = SKIP;

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
        xv[0] = xv[1];
        xv[1] = in / GAIN;
        yv[0] = yv[1];
        yv[1] =   (xv[0] + xv[1])
                + (  0.9690674172 * yv[0]);
        return yv[1] - 400;
    }

    public void updateThreshSkip(double hi, double lo, int skip) {
        threshHi = hi;
        threshLo = lo;
        SKIP = skip;
    }

    private void processData(double val) {
        if (skipedSamples >= SKIP) {
            if (!upFound) {
                if (val > threshHi) {
                    upFound = true;
                }
            } else {
                if (val < threshLo) {
                    stepCount += 1;
                    upFound = false;
                    stepListener.onStep(stepCount);
                    skipedSamples = 0;
                }
            }
        }
        else
        {
            skipedSamples++;
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