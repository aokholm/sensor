package com.vaavud.sensor.internal.processor.magnetic;

public enum Window {
    RETANGULAR_WINDOW{
        @Override
        public Double window(int bin, int length) {
            return 1D;
        }
        
        @Override
        public Double scalingFactor() {
            return 1d;
        }
    }, 
    WELCH_WINDOW {
        @Override
        public Double window(int bin, int length) {
            // since periodic we add one to length, but still only use the first N samples
            length = length +1;
            
            double w = 1 - Math.pow((bin - (double) (length - 1) / 2)
                            / ((double) (length + 1) / 2), 2);
            return w;
        }
        @Override
        public Double scalingFactor() {
            // Signal Processing for Intelligent Sensor Systems p. 109 // might not be totally accurate
            return 1.55d;
        }
        
    }, 
    BLACK_MAN {
        @Override
        public Double window(int bin, int length) {
            // since periodic we add one to length, but still only use the first N samples
            length = length +1;
            
            double alpha = 0.16;
            
            double a0 = (1-alpha)/2;
            double a1 = 1/2d;
            double a2 = alpha/2;
            
            double w = a0 - a1*Math.cos(2*Math.PI*bin / (length -1)) 
                    + a2*Math.cos(4*Math.PI*bin/(length-1));
            
            return w;
        }
        
        @Override
        public Double scalingFactor() {
            // http://www.ni.com/white-paper/4278/en/ 
            return 2.38d;
        }
    },
    HANN {
        @Override
        public Double window(int bin, int length) {
            // since periodic we add one to length, but still only use the first N samples
            length = length +1;
            
            double w = 0.5 * (1- Math.cos((2*Math.PI*bin)/(length-1)));
            return w;
        }
        
        @Override
        public Double scalingFactor() {
            // http://www.ni.com/white-paper/4278/en/ 
            return 2.0d;
        }
    };
    
    public abstract Double window(int bin, int length);
    public abstract Double scalingFactor();
}
