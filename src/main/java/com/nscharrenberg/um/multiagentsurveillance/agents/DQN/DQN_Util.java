package com.nscharrenberg.um.multiagentsurveillance.agents.DQN;

import java.util.Arrays;

public class DQN_Util {

    /*
    Matrices are represented as follows: [c][x][y]
    c: channels
    x: x position
    y: y position
     */

    public static int matrixIndex(int x, int y){
        return x + (y * 8);
    }


    public static double[][][] intToDouble3D(int[][][] input){
        assert input[0].length == input[0][0].length : "Input layer not square";
        double[][][] out = new double[input.length][input[0].length][input[0].length];

        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                for (int k = 0; k < input[0].length; k++) {
                    out[i][j][k] = input[i][j][k];
                }
            }
        }

        return out;
    }

    public static double[][] dotProduct2D(double[][] A, int[][] B){
        assert A.length == A[0].length && B.length == B[0].length : "Input matrix not square";
        assert B.length == A.length : "Lengths not equal";

        double[][] out = new double[A.length][A.length];

        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A.length; j++) {
                out[i][j] = A[i][j] * B[i][j];
            }
        }

        return out;
    }

    public static int[][] dotProduct2D(int[][] A, int[][] B){
        assert A.length == A[0].length && B.length == B[0].length : "Input matrix not square";
        assert B.length == A.length : "Lengths not equal";

        int[][] out = new int[A.length][A.length];

        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A.length; j++) {
                out[i][j] = A[i][j] * B[i][j];
            }
        }

        return out;
    }

    public static double linear(double input[], double weights[], double bias){
        double out = 0;

        for (int i = 0; i < input.length; i++) {
            out += input[i] * weights[i];
        }
        out += bias;

        if (Double.isNaN(out))
            out = 0;

        return out;
    }

    public static double relu(double x){
        return Math.max(0, x);
    }

    /**
     * @param big - matrix to be cross correlated against
     * @param small - matrix which is cross correlated over the big
     * @param forward - if true the activation function is applied
     * @return Either the activated output of the cross correlation or the cross correlation product
     */
    public static double[][] crossCorrelate2DValid(double[][] big, double[][] small, boolean forward){

        int outLength = big.length - small.length + 1;
        double[][] out = new double[outLength][outLength];

        if (forward) {
            for (int i = 0; i < outLength; i++) {
                for (int j = 0; j < outLength; j++) {
                    out[i][j] += relu(ccValid(big, small, i, j));
                }
            }
        }

        else {
            for (int i = 0; i < outLength; i++) {
                for (int j = 0; j < outLength; j++) {
                    out[i][j] += ccValid(big, small, i, j);
                }
            }
        }

        return out;
    }

    public static double[][] convolution2DFull(double[][] big, double[][] small){

        int outLength = big.length + small.length - 1;
        double[][] out = new double[outLength][outLength];
        double[][] rot = rot180(small);
        double[][] gBig = growFull(big, small.length);


        for (int i = 0; i < outLength; i++) {
            for (int j = 0; j < outLength; j++) {
                out[j][i] = ccFull(gBig, rot, j, i);
            }
        }

        return out;
    }

    public static double[][] matrixSum2D(double[][] A, double[][] B, double[][] C){
        assert A.length == B.length && A[0].length == B[0].length : "Unequal lengths provided";
        assert A.length == C.length && A[0].length == C[0].length : "Unequal lengths provided";

        double[][] out = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                out[i][j] = A[i][j] + B[i][j] + C[i][j];
            }
        }

        return out;
    }

    public static double[][] matrixSum2D(double[][] A, double[][] B){
        assert A.length == B.length && A[0].length == B[0].length  : "Unequal lengths provided";

        double[][] out = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                out[i][j] = A[i][j] + B[i][j];
            }
        }

        return out;
    }
    
    public static double[][][] matrixSum3D(double[][][] A, double[][][] B){
        assert A.length == B.length && A[0].length == B[0].length && A[0][0].length == B[0][0].length : "Unequal lengths provided";

        int channels = A.length;
        int length = A[0].length;

        double[][][] out = new double[channels][length][length];

        for (int i = 0; i < channels; i++) {
            for (int j = 0; j < length; j++) {
                for (int k = 0; k < length; k++) {
                    out[i][j][k] = A[i][j][k] + B[i][j][k];
                }
            }
        }

        return out;
    }

    /**
     * Both main and subtract must be square and have the same length
     * @param main - matrix to be subtracted from
     * @param subtract - matrix to be scaled. Then taken from main
     * @param scale - scalar for the subtract matrix
     * @return the main matrix minus the scaled subtract matrix
     */
    public static double[][] scaleSubtract(double[][] main, double[][] subtract, double scale){
        assert main.length == subtract.length : "Unequal lengths provided";

        double[][] out = new double[main.length][main.length];
        for (int i = 0; i < main.length; i++) {
            for (int j = 0; j < main.length; j++) {
                out[i][j] = main[i][j] - (scale * subtract[i][j]);
            }
        }

        return out;
    }

    public static double[] flatten2D(double[][] input){
        assert input.length == input[0].length: "Input not square";

        int ind = 0;
        double[] out = new double[input.length*input.length];

        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input.length; j++) {
                out[ind++] = input[i][j];
            }
        }

        return out;
    }


    public static double[][][] unFlatten3D(double[] input, int channels, int length){

        double[][][] out = new double[channels][length][length];
        int ind = 0;

        for (int i = 0; i < channels; i++) {
            for (int j = 0; j < length; j++) {
                for (int k = 0; k < length; k++) {
                    out[i][k][j] = input[ind++];
                }
            }
        }
        return out;
    }

    public static double[] flatten3D(double[][][] input){
        assert input[0].length == input[0][0].length : "Unequal lengths provided";

        double[] out = new double[input.length * input[0].length * input[0].length];

        int ind = 0;
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                for (int k = 0; k < input[0].length; k++) {
                    out[ind++] = input[i][k][j];
                }
            }
        }

        return out;
    }

    public static int[][] from3Dto2D(int[][][] input){
        assert input[0].length == input[0][0].length : "Unequal lengths provided";

        int[][] out = new int[input[0].length][input[0].length];

        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                for (int k = 0; k < input[0].length; k++) {
                    if (input[i][k][j] == 1)
                        out[k][j] = i+1;
                    if (input[i][k][j] == -1)
                        out[k][j] = -i-1;
                }
            }
        }

        return out;
    }

    /**
     * @param input - vector with length = x^2 with x being an int
     * @return - 2D matrix of input vector
     */
    public static double[][] unflatten2D(double[] input){

        int length = (int) Math.sqrt(input.length);
        double[][] out = new double[length][length];
        int ind = 0;

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                out[i][j] = input[ind++];
            }
        }

        return out;
    }

    private static double ccValid(double[][] big, double[][] small, int x, int y){

        double sum = 0;

        for (int i = 0; i < small.length; i++) {
            for (int j = 0; j < small.length; j++) {
                sum += small[j][i] * big[x+j][y+i];
            }
        }

        return sum;
    }

    private static double ccFull(double[][] big, double[][] small, int x, int y){

        double out = 0;

        for (int i = 0; i < small.length; i++) {
            for (int j = 0; j < small.length; j++) {
                out += small[j][i] * big[x+j][y+i];
            }
        }

        return out;
    }

    /**
     * @param input - square matrix
     * @return - input rotated 180 degrees
     */
    public static double[][] rot180(double[][] input){

        int length = input.length;
        double[][] out = new double[length][length];
        int index = length - 1;

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++)
                out[j][i] = input[index-j][index-i];
        }

        return out;
    }

    /**
     * Method used to grow a matrix for full cross correlation. Extra indices are 0
     * @param input - matrix to be grown
     * @param smallSize - length of the matrix input is being cross correlated against
     * @return
     */
    private static double[][] growFull(double[][] input, int smallSize){

        int size = (smallSize - 1) * 2 + input.length;
        int diff = smallSize - 1;
        double[][] out = new double[size][size];

        for (int i = diff; i < input.length + diff; i++) {
            for (int j = diff; j < input.length + diff; j++) {
                out[i][j] = input[i-diff][j-diff];
            }
        }

        return out;
    }
}
