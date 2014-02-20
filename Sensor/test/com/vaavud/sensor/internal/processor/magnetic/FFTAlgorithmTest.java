/**
 * 
 */
package com.vaavud.sensor.internal.processor.magnetic;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author aokholmRetina
 *
 */
public class FFTAlgorithmTest {
    
    private static final double DELTA = 1e-15;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

//    @Test
//    public void test() {
//        fail("Not yet implemented");
//    }
    
    @Test
    public void testDoFFT_full() {

        int FFTLength = 128;
        
        FFTAlgorithm fftAlgorithm = new FFTAlgorithm(FFTLength);
        
        List<Double> realData = new ArrayList<Double>();
        
        int bin = 7;
        
        for (int i = 0; i < FFTLength; i++) {
            realData.add( Math.sin(bin * Math.PI * i*2 / (double) FFTLength) ); 
        }
        
        System.out.println( fftAlgorithm.doFFT(realData));
        
        // check if multiply(10,5) returns 50
        for (int i = 0; i < FFTLength/2; i++) {
            if (i == bin) {
                assertEquals("bin ".concat(String.valueOf(i)), new Double(1), fftAlgorithm.doFFT(realData).get(i), DELTA);
            }
            else {
                assertEquals("bin ".concat(String.valueOf(i)), new Double(0), fftAlgorithm.doFFT(realData).get(i), DELTA);
            }
            
        }
    }
    
    @Test
    public void testDoFFT_70() {

        int FFTLength = 128;
        int dataLength = 70;
        
        FFTAlgorithm fftAlgorithm = new FFTAlgorithm(FFTLength);
        
        List<Double> realData = new ArrayList<Double>();
        
        int bin = 7;
        
        for (int i = 0; i < dataLength; i++) {
            realData.add( Math.sin(bin * Math.PI * i*2 / (double) FFTLength) ); 
        }
        
        // check if multiply(10,5) returns 50
        for (int i = 0; i < FFTLength/2; i++) {
            if (i == bin) {
                assertEquals("bin ".concat(String.valueOf(i)), new Double(70/128d), fftAlgorithm.doFFT(realData).get(i), 0.1);
            }
            else {
                assertEquals("bin ".concat(String.valueOf(i)), new Double(0), fftAlgorithm.doFFT(realData).get(i), 0.1);
            }
            
        }
        
        
      } 
    
}
