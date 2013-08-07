package org.chboston.cnlp.libsvm.ex;

import java.io.IOException;
import java.util.List;

import org.chboston.cnlp.libsvm.svm;
import org.chboston.cnlp.libsvm.svm_model;
import org.chboston.cnlp.libsvm.svm_node;

/**
 *
 * @author Syeed Ibn Faiz
 * @author Tim Miller
 */
public class SVMPredictor {

    public static <K> double[] predict(List<Instance<K>> instances, svm_model model) {
        return predict(instances, model, true);
    }

//    public static <K> double[] predict(List<Instance<K>> instances, svm_model model, boolean displayResult) {
//        Instance[] array = new Instance[instances.size()];
//        array = instances.toArray(array);
//        return predict(array, model, displayResult);
//    }

    public static <K> double predict(Instance<K> instance, svm_model model, boolean displayResult) {
        return svm.svm_predict(model, new svm_node<K>(instance.getData()));
    }
    
    public static <K> double predictProbability(Instance<K> instance, svm_model model, double[] probabilities) {
        return svm.svm_predict_probability(model, new svm_node<K>(instance.getData()), probabilities);
    }    
    public static <K> double[] predict(List<Instance<K>> instances, svm_model model, boolean displayResult) {
        int total = 0;
        int correct = 0;

        int tp = 0;
        int fp = 0;
        int fn = 0;

        boolean binary = model.nr_class == 2;
        double[] predictions = new double[instances.size()];
        int count = 0;

        for (Instance<K> instance : instances) {
            double target = instance.getLabel();
            double p = svm.svm_predict(model, new svm_node<K>(instance.getData()));
            predictions[count++] = p;

            ++total;
            if (p == target) {
                correct++;
                if (target > 0) {
                    tp++;
                }
            } else if (target > 0) {
                fn++;
            } else {
                fp++;
            }
        }
        if (displayResult) {
            System.out.print("Accuracy = " + (double) correct / total * 100
                    + "% (" + correct + "/" + total + ") (classification)\n");

            if (binary) {
                double precision = (double) tp / (tp + fp);
                double recall = (double) tp / (tp + fn);
                System.out.println("Precision: " + precision);
                System.out.println("Recall: " + recall);
                System.out.println("Fscore: " + 2 * precision * recall / (precision + recall));
            }
        }
        return predictions;
    }

    public static svm_model loadModel(String filePath) throws IOException, ClassNotFoundException {
        return svm.svm_load_model(filePath);
    }
}
