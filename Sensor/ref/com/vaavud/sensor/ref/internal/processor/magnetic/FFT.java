package com.vaavud.sensor.ref.internal.processor.magnetic;

import java.util.ArrayList;
import java.util.List;

import com.vaavud.sensor.ref.internal.processor.magnetic.model.MagneticPoint;
import com.vaavud.sensor.ref.internal.processor.magnetic.model.MeasurementPoint;

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

    private double[] windowValues;
    
    public static enum Interpolation {
        NO_INTERPOLATION, QUADRATIC_INTERPOLATION, LOGARITHMIC_INTERPOLATION
    }

    public FFT(Integer dataLength, Integer FFTLength, Window windowType,
            Interpolation interpolationType) {

        this.dataLength = dataLength;
        this.FFTLength = FFTLength;
        this.windowType = windowType;
        this.interpolationType = interpolationType;
            
        generatePrecalculatedWindowValues(windowType);

        myFFTAlgorithm = new FFTAlgorithm(FFTLength);
    }

    // legacy / reference method
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


    private void generatePrecalculatedWindowValues(Window windowType) {
        windowValues = new double[dataLength];
        
        for (int bin = 0; bin < dataLength; bin++) {
            windowValues[bin] = windowType.window(bin, dataLength)*windowType.scalingFactor();
        }
        
    }


    @Override
    public String toString() {
        return "FFT [dataLength=" + dataLength + ", FFTLength=" + FFTLength + ", interpolationType="
                + interpolationType + ", windowType=" + windowType + "]";
    }
    
    

}
