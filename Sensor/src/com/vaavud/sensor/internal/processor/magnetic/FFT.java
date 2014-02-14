package com.vaavud.sensor.internal.processor.magnetic;

import java.util.ArrayList;
import java.util.List;

import com.vaavud.sensor.Sensor;
import com.vaavud.sensor.SensorEvent;
import com.vaavud.sensor.SensorEvent3D;
import com.vaavud.sensor.SensorEventFreq;
import com.vaavud.sensor.SensorListener;

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
    private Window windowType;
    private Filter filterType;
    private Integer movingAverage;
    private Double highPass;

    private double[] windowValues;
    private double[] filter;
    private boolean filtering;
    private Sensor sensor;
    private SensorListener listener;
    
    public static enum Interpolation {
        NO_INTERPOLATION, QUADRATIC_INTERPOLATION, LOGARITHMIC_INTERPOLATION
    }

    public static enum Filter {
        NO_FILTER, ANGULAR_FILTER
    }

    public FFT(Integer dataLength, Integer FFTLength, Window windowType,
            Interpolation interpolationType, Filter filterType,
            Integer movingAverage, Sensor sensor, SensorListener listener, Double highPass) {

        this.dataLength = dataLength;
        this.FFTLength = FFTLength;
        this.windowType = windowType;
        this.interpolationType = interpolationType;
        this.filterType = filterType;
        this.movingAverage = movingAverage;
        this.sensor = sensor;
        this.listener = listener;
        this.highPass = highPass;
        
        if (sensor != null) { // for support of MagneticProcessorRef
            sensor.setDescriptor(this);
        }
            
        generatePrecalculatedWindowValues(windowType);
        generatePrecalculatedFilterValues(filterType, movingAverage);

        myFFTAlgorithm = new FFTAlgorithm(FFTLength);
    }

    public void newSensorEvent(List<SensorEvent3D> events, Double SF) {

        List<Double> xAxis = new ArrayList<Double>(dataLength);
        List<Double> yAxis = new ArrayList<Double>(dataLength);
        List<Double> zAxis = new ArrayList<Double>(dataLength);

        for (int i = 0; i < events.size(); i++) {
            xAxis.add(events.get(i).getX());
            yAxis.add(events.get(i).getY());
            zAxis.add(events.get(i).getZ());
        }

        List<Double> fftResultx = fftResult(xAxis); 
        List<Double> fftResulty = fftResult(yAxis);// TODO returns 129 and not 128, could be a mistake
        List<Double> fftResultz = fftResult(zAxis); 

        if (fftResultx == null) {
            return;
        }

        if (fftResulty == null) {
            return;
        } 

        if (fftResultz == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        List<Double> averageFftResult = averageLists(fftResultx, fftResulty, fftResultz); 
        
        if (filtering) {
            applyFilter(averageFftResult);
        }

        FreqAmp freqAmp = speedAndAmpFromFFTResult(averageFftResult, SF);

        if (freqAmp == null) {
            return;
        }
        
        if (freqAmp.frequency < highPass) {
            return;
        }
        
        SensorEvent event = new SensorEventFreq(sensor,
                events.get(events.size() - 1).getTimeUs(), freqAmp.frequency, freqAmp.amplitude, SF);
        listener.newEvent(event);
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
            peakAmplitude = (double) (beta - 1 / 4.0 * (alph - gamma) * p);
            break;

        case LOGARITHMIC_INTERPOLATION:
            // not implemented yet
            interpolationType = Interpolation.NO_INTERPOLATION;
            return speedAndAmpFromFFTResult(fftResult, sampleFrequency);

        default:
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

    private List<Double> averageLists(List<Double> ... fftresults) {

        double[] result = new double[fftresults[0].size()];
        
        for (List<Double> fftResult : fftresults) {
            for (int i = 0; i < fftResult.size(); i++) {
                result[i] = result[i] + fftResult.get(i);
            }
        }
        
        List<Double> resultList = new ArrayList<Double>(fftresults[0].size());
        
        for (double item : result) {
            resultList.add(item / fftresults.length);
        }
        
        return resultList;
    }

    private void applyFilter(List<Double> list) {
        for (int i = 0; i < filter.length; i++) {
            list.set(i, list.get(i) * filter[i]);
        }
    }

    private void generatePrecalculatedWindowValues(Window windowType) {
        windowValues = new double[dataLength];
        
        for (int bin = 0; bin < dataLength; bin++) {
            windowValues[bin] = windowType.window(bin, dataLength)*windowType.scalingFactor();
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

    @Override
    public String toString() {
        return "FFT [dataLength=" + dataLength + ", FFTLength=" + FFTLength + ", interpolationType="
                + interpolationType + ", windowType=" + windowType + ", filterType=" + filterType + ", movingAverage="
                + movingAverage + "]";
    }
    
    

}
