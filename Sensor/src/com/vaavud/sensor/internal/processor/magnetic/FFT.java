package com.vaavud.sensor.internal.processor.magnetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaavud.sensor.Sensor;
import com.vaavud.sensor.Sensor.Type;
import com.vaavud.sensor.SensorEvent;
import com.vaavud.sensor.SensorEvent3D;
import com.vaavud.sensor.SensorEventFreq;
import com.vaavud.sensor.internal.processor.magnetic.model.MagneticPoint;
import com.vaavud.sensor.internal.processor.magnetic.model.MeasurementPoint;

public class FFT {

    public class FreqAmp {

        public double frequency;
        public double amplitude;

        FreqAmp(double frequency, double amplitude) {
            this.frequency = frequency;
            this.amplitude = amplitude;
        }
    }

    private Integer dataLength;
    private Integer FFTLength;
    private Interpolation interpolationType;
    private FFTAlgorithm myFFTAlgorithm;

    private double[] windowValues;
    private double[] filter;
    private boolean filtering;
    private Sensor sensor;

    public static enum Window {
        RETANGULAR_WINDOW, WELCH_WINDOW
    }

    public static enum Interpolation {
        NO_INTERPOLATION, QUADRATIC_INTERPOLATION, LOGARITHMIC_INTERPOLATION
    }

    public static enum Filter {
        NO_FILTER, ANGULAR_FILTER
    }

    // axis
    public static enum Axis {
        X, Y, Z
    }

    public FFT(Integer dataLength, Integer FFTLength, Window windowType,
            Interpolation interpolationType, Filter filterType,
            Integer movingAverage) {

        this.sensor = new Sensor(Type.FREQUENCY, "Freq_1");

        this.dataLength = dataLength;
        this.FFTLength = FFTLength;
        this.interpolationType = interpolationType;

        generatePrecalculatedWindowValues(windowType);
        generatePrecalculatedFilterValues(filterType, movingAverage);

        myFFTAlgorithm = new FFTAlgorithm(FFTLength);
    }

    public SensorEvent getSensorEvent(List<SensorEvent3D> events, Double SF) {

        List<Double> xAxis = new ArrayList<Double>(dataLength);
        List<Double> yAxis = new ArrayList<Double>(dataLength);
        List<Double> zAxis = new ArrayList<Double>(dataLength);

        for (int i = 0; i < events.size(); i++) {
            xAxis.add(events.get(i).getX());
            yAxis.add(events.get(i).getY());
            zAxis.add(events.get(i).getZ());
        }

        List<Double> fftResultx = fftResult(xAxis);
        List<Double> fftResulty = fftResult(yAxis);
        List<Double> fftResultz = fftResult(zAxis);

        if (fftResultx == null) {
            return null;
        }

        if (fftResulty == null) {
            return null;
        }

        if (fftResultz == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        List<Double> averageFftResult = averageFftResult(Arrays
                .<List<Double>> asList(fftResultx, fftResulty, fftResultz));

        if (filtering) {
            applyFilter(averageFftResult);
        }

        FreqAmp freqAmp = speedAndAmpFromFFTResult(averageFftResult, SF);

        if (freqAmp == null) {
            return null;
        }

        SensorEvent event = new SensorEventFreq(sensor,
                events.get(events.size() - 1).getTimeUs(), freqAmp.frequency, freqAmp.amplitude, SF);
        return event;
    }

    public List<Double> fftResult(List<Double> oneAxisData) {

        oneAxisData = applyZeroMean(oneAxisData);
        oneAxisData = windowData(oneAxisData);

        int i = 0; // check if all values is not zero
        while (oneAxisData.get(i).doubleValue() == 0f) {
            i++;
            if (i == oneAxisData.size())
                return null;
        }

        return myFFTAlgorithm.doFFT(oneAxisData);
    }

    public MeasurementPoint getFreqAndAmp3DFFT(
            List<MagneticPoint> magneticPoints, Double sampleFrequency) {

        double frequencyMean;
        double frequencyRMSD;
        double amplitudeMean;

        List<Double> xAxis = new ArrayList<Double>(dataLength);
        List<Double> yAxis = new ArrayList<Double>(dataLength);
        List<Double> zAxis = new ArrayList<Double>(dataLength);

        for (int i = 0; i < magneticPoints.size(); i++) {
            xAxis.add(magneticPoints.get(i).getX());
            yAxis.add(magneticPoints.get(i).getY());
            zAxis.add(magneticPoints.get(i).getZ());
        }

        FreqAmp myFAx = getFreqAndAmpOneAxisFFT(xAxis, sampleFrequency);
        if (myFAx == null) {
            return null;
        }

        FreqAmp myFAy = getFreqAndAmpOneAxisFFT(yAxis, sampleFrequency);
        if (myFAy == null) {
            return null;
        }

        FreqAmp myFAz = getFreqAndAmpOneAxisFFT(zAxis, sampleFrequency);
        if (myFAz == null) {
            return null;
        }

        // calculate frequency RMS

        frequencyMean = (myFAx.frequency + myFAy.frequency + myFAz.frequency) / 3;
        frequencyRMSD = Math.sqrt(0.3333333333D
                * Math.pow(myFAx.frequency - frequencyMean, 2)
                + Math.pow(myFAy.frequency - frequencyMean, 2)
                + Math.pow(myFAz.frequency - frequencyMean, 2));
        amplitudeMean = (myFAx.amplitude + myFAy.amplitude + myFAz.amplitude) / 3;

        // System.out.println(String.format("wind: %f, %f, %f amp:  %f, %f, %f, fRMSD: %f",
        // myFAx.frequency, myFAy.frequency, myFAz.frequency, myFAx.amplitude,
        // myFAy.amplitude, myFAz.amplitude, frequencyRMSD));

        if (frequencyRMSD < 0.2 && frequencyMean > 1 && amplitudeMean > 0.3) {
            MeasurementPoint meanCoreMeasurementPoint = new MeasurementPoint(
                    frequencyMean, amplitudeMean);
            return meanCoreMeasurementPoint;
        }

        return null;
    }

    public FreqAmp getFreqAndAmpOneAxisFFT(List<Double> oneAxisData,
            Double sampleFrequency) {

        List<Double> fftresult;

        oneAxisData = applyZeroMean(oneAxisData);
        oneAxisData = windowData(oneAxisData);

        int i = 0;
        while (oneAxisData.get(i).doubleValue() == 0f) {
            i++;
            if (i == oneAxisData.size())
                return null;
        }

        fftresult = myFFTAlgorithm.doFFT(oneAxisData);

        FreqAmp mySpeedAndAmp = speedAndAmpFromFFTResult(fftresult,
                sampleFrequency.doubleValue());

        return mySpeedAndAmp;
    }

    private FreqAmp speedAndAmpFromFFTResult(List<Double> fftResult,
            Double sampleFrequency) {

        int maxBin = 1;
        double maxPeak = 0;
        double peakAmplitude;
        double peakFrequency;

        // find the highest peak (bin)
        for (int i = 1; i < (FFTLength / 2); i++) {

            if (fftResult.get(i) > maxPeak) {
                maxBin = i;
                maxPeak = fftResult.get(i).doubleValue();
            }
        }

        switch (interpolationType) {
        case NO_INTERPOLATION:
            peakFrequency = (maxBin) * sampleFrequency / FFTLength;
            peakAmplitude = maxPeak;
            break;

        case QUADRATIC_INTERPOLATION:
            double alph = fftResult.get(maxBin - 1);
            double beta = fftResult.get(maxBin);
            double gamma = fftResult.get(maxBin + 1);

            double p = 0.5d * (alph - gamma) / (alph - 2 * beta + gamma);

            peakFrequency = (double) ((maxBin + p) * sampleFrequency / FFTLength);
            peakAmplitude = (double) (beta - 1 / 4 * (alph - gamma) * p);
            break;

        case LOGARITHMIC_INTERPOLATION:
            // not implemented yet
            interpolationType = Interpolation.NO_INTERPOLATION;
            return speedAndAmpFromFFTResult(fftResult, sampleFrequency);

        default:
            // Log.e(MainActivity.TAG, "Wrong Interpolation type!");
            interpolationType = Interpolation.NO_INTERPOLATION;
            return speedAndAmpFromFFTResult(fftResult, sampleFrequency);

        }
        return new FreqAmp(peakFrequency, peakAmplitude);
    }

    public Integer getDataLength() {
        return dataLength;
    }

    private static List<Double> applyZeroMean(List<Double> data) {
        double sum = 0d;
        double mean;

        for (int i = 0; i < data.size(); i++) {
            sum = sum + data.get(i);
        }

        mean = sum / data.size();

        for (int i = 0; i < data.size(); i++) {
            data.set(i, (double) (data.get(i) - mean));
        }

        return data;
    }

    private List<Double> windowData(List<Double> data) {
        for (int i = 0; i < data.size(); i++) {
            data.set(i, (double) windowValues[i] * data.get(i));
        }
        return data;
    }

    private List<Double> averageFftResult(List<List<Double>> fftresults) {

        // double[] result = new double[fftresults.get(0).size()];
        List<Double> result = new ArrayList<Double>(fftresults.get(0).size());
        int j = 0;
        for (List<Double> fftResult : fftresults) {
            for (int i = 0; i < fftResult.size(); i++) {
                if (j == 0) {
                    result.add(fftResult.get(i));
                } else {
                    result.set(i, result.get(i) + fftResult.get(i));
                }
            }
        }

        for (Double item : result) {
            item = item / fftresults.size();
        }

        return result;
    }

    private void applyFilter(List<Double> list) {
        for (int i = 0; i < filter.length; i++) {
            list.set(i, list.get(i) * filter[i]);
        }
    }

    private void generatePrecalculatedWindowValues(Window windowType) {
        windowValues = new double[dataLength];

        if (windowType == Window.RETANGULAR_WINDOW) {
            for (int i = 0; i < windowValues.length; i++) {
                windowValues[i] = 1;
            }
        } else if (windowType == Window.WELCH_WINDOW) {
            for (int i = 0; i < windowValues.length; i++) {
                windowValues[i] = 1 - Math.pow(
                        (i - (double) (dataLength - 1) / 2)
                                / ((double) (dataLength + 1) / 2), 2);
            }
        } else {

            // Log.e(MainActivity.TAG, "Unsuported WindowType");
            generatePrecalculatedWindowValues(Window.RETANGULAR_WINDOW);
        }
    }

    private void generatePrecalculatedFilterValues(Filter filterType,
            Integer movA) {

        filter = new double[FFTLength / 2];

        if (filterType == Filter.NO_FILTER) {
            for (int i = 0; i < FFTLength / 2; i++) {
                filter[i] = 1;
            }
        }

        if (filterType == Filter.ANGULAR_FILTER) {
            filter[0] = 1;

            for (int i = 1; i < (FFTLength / 2); i++) {
                double a = ((double) i) / FFTLength * 2 * Math.PI;
                filter[i] = 1 / a * (Math.sin(a / 2) - Math.sin(-a / 2));
            }
        }

        if (movA != null) {
            for (int i = 0; i < FFTLength / 2; i++) {
                double f = ((double) i) / FFTLength;
                filter[i] = filter[i]
                        * Math.abs(Math.sin(Math.PI * f * movA)
                                / (movA * Math.sin(Math.PI * f)));
            }
        }

        // Inverse filter
        for (int i = 0; i < FFTLength / 2; i++) {
            double x = 1 / (filter[i]);

            filter[i] = 15 * Math.tanh(x / 15);
        }

        if (filterType == Filter.NO_FILTER && movA == null) {
            filtering = false;
        } else {
            filtering = true;
        }

    }

}
