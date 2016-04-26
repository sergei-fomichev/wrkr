package edu.uml.cs.mstowell.wrkr.ml;

import android.content.Context;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.uml.cs.mstowell.wrkrlib.R;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;

/**
 * Performs simple logistic regression.
 * User: tpeng
 * Date: 6/22/12
 * Time: 11:01 PM
 *
 * @author tpeng
 * @author Matthieu Labas
 *
 * Modified by Mike Stowell for use with wrkr app
 */
public class Logistic implements Globals {

    /** the learning rate */
    private double rate;

    /** the weight to learn */
    private double[] weights;

    /** the number of iterations */
    @SuppressWarnings("all")
    private int ITERATIONS = 500;//3000;

    // need a context to find resources
    Context mContext;

    public Logistic(Context c) {
        this.rate = 0.001; //0.0001;
        weights = new double[NUM_FEATURES];
        this.mContext = c;
    }

    private static double sigmoid(double z) {
        return 1.0 / (1.0 + Math.exp(-z));
    }

    public void train(List<Instance> instances) {
        for (int n = 0; n < ITERATIONS; n++) {
            //double lik = 0.0;
            for (int i = 0; i < instances.size(); i++) {
                double[] x = instances.get(i).x;
                double predicted = classify(x);
                int label = instances.get(i).label;
                for (int j = 0; j < weights.length; j++) {
                    weights[j] = weights[j] + rate * (label - predicted) * x[j];
                }
                // not necessary for learning
                //lik += label * Math.log(classify(x)) + (1-label) * Math.log(1- classify(x));
            }
            //Log.d("wrkr", "iteration: " + n + " " + Arrays.toString(weights) + " mle: " + lik);
        }
    }

    public void setWeights(double[] w) {
        weights = w;
    }

    @SuppressWarnings("unused")
    public double[] getWeights() {
        return weights;
    }

    public double classify(double[] x) {

        double logit = .0;
        for (int i = 0; i < weights.length; i++)  {
            logit += weights[i] * x[i];
        }
        return sigmoid(logit);
    }

    public static class Instance {
        public int label;
        public double[] x;

        public Instance(int label, double[] x) {
            this.label = label;
            this.x = x;
        }
    }

    public List<Instance> readDataSet(int resource) throws FileNotFoundException {
        List<Instance> dataset = new ArrayList<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(mContext.getResources().openRawResource(resource));
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("x")) { // header line
                    continue;
                }
                String[] columns = line.split(",");

                // last column is the label
                int i = 0;
                double[] data = new double[columns.length-1];
                for (; i < columns.length-1; i++) {
                    data[i] = Double.parseDouble(columns[i]);
                }
                int label = Integer.parseInt(columns[i]);
                Instance instance = new Instance(label, data);
                dataset.add(instance);
            }
        } finally {
            if (scanner != null)
                scanner.close();
        }
        return dataset;
    }

    // run the logistic classifier to receive a set of weights from the training data
    public double[] runLogisticRegression() throws FileNotFoundException {

        // training data
        List<Instance> instances = readDataSet(R.raw.train);
        train(instances);

        return weights;
    }

    public double mean(List<Double> prob) {
        double sum = 0;
        for (Double val : prob)
            sum += val;
        return sum / prob.size();
    }
}
