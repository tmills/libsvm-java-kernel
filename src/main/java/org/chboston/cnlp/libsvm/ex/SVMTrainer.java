package org.chboston.cnlp.libsvm.ex;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.chboston.cnlp.libsvm.svm;
import org.chboston.cnlp.libsvm.svm_model;
import org.chboston.cnlp.libsvm.svm_node;
import org.chboston.cnlp.libsvm.svm_parameter;
import org.chboston.cnlp.libsvm.svm_problem;

/**
 * <code>SVMTrainer</code> performs training of an SVM.
 * @author Syeed Ibn Faiz
 * @author Tim Miller
 */
public class SVMTrainer {
    
//    private static <K> svm_problem prepareProblem(List<Instance<K>> instances) {
//        Instance<K>[] array = new Instance[instances.size()];
//        array = instances.toArray(array);
//        return prepareProblem(array);
//    }
    
    private static <K> svm_problem<K> prepareProblem(List<Instance<K>> instances) {
        return prepareProblem(instances, 0, instances.size() - 1);
    }
    
    private static <K> svm_problem<K> prepareProblem(List<Instance<K>> instances, int begin, int end) {
        svm_problem<K> prob = new svm_problem<K>();
        prob.l = (end - begin) + 1;
        prob.y = new double[prob.l];
        prob.x = new svm_node[prob.l];
        
        for (int i = begin; i <= end; i++) {
            prob.y[i-begin] = instances.get(i).getLabel();
            prob.x[i-begin] = new svm_node<K>(instances.get(i).getData());
        }
        return prob;
    }
    
    /**
     * Builds an SVM model
     * @param instances
     * @param param
     * @return 
     */
    public static <K> svm_model<K> train(List<Instance<K>> instances, svm_parameter param) {
        //prepare svm_problem
        svm_problem<K> prob = prepareProblem(instances);
        
        String error_msg = svm.svm_check_parameter(prob, param);

        if (error_msg != null) {
            System.err.print("ERROR: " + error_msg + "\n");
            System.exit(1);
        }
                
        return svm.svm_train(prob, param);
    }
    
//    public static <K> svm_model train(List<Instance<K>> instances, svm_parameter param) {
//    }
    
    /**
     * Performs N-fold cross validation
     * @param instances
     * @param param parameters
     * @param nr_fold number of folds (N)
     * @param binary whether doing binary classification
     */
    public static <K> void doCrossValidation(List<Instance<K>> instances, svm_parameter param, int nr_fold, boolean binary) {
        svm_problem<K> prob = prepareProblem(instances);
        
        int i;
        int total_correct = 0;
        double total_error = 0;
        double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
        double[] target = new double[prob.l];

        svm.svm_cross_validation(prob, param, nr_fold, target);
        if (param.svm_type == svm_parameter.EPSILON_SVR
                || param.svm_type == svm_parameter.NU_SVR) {
            for (i = 0; i < prob.l; i++) {
                double y = prob.y[i];
                double v = target[i];
                total_error += (v - y) * (v - y);
                sumv += v;
                sumy += y;
                sumvv += v * v;
                sumyy += y * y;
                sumvy += v * y;
            }
            System.out.print("Cross Validation Mean squared error = " + total_error / prob.l + "\n");
            System.out.print("Cross Validation Squared correlation coefficient = "
                    + ((prob.l * sumvy - sumv * sumy) * (prob.l * sumvy - sumv * sumy))
                    / ((prob.l * sumvv - sumv * sumv) * (prob.l * sumyy - sumy * sumy)) + "\n");
        } else {
            int tp = 0;
            int fp = 0;
            int fn = 0;
            
            for (i = 0; i < prob.l; i++) {
                if (target[i] == prob.y[i]) {
                    ++total_correct;
                    if (prob.y[i] > 0) {
                        tp++;
                    }
                } else if (prob.y[i] > 0) {
                    fn++;
                } else if (prob.y[i] < 0) {
                    fp++;
                }
            }
            System.out.print("Cross Validation Accuracy = " + 100.0 * total_correct / prob.l + "%\n");
            if (binary) {
                double precision = (double) tp / (tp + fp);
                double recall = (double) tp / (tp + fn);
                System.out.println("Precision: " + precision);
                System.out.println("Recall: " + recall);
                System.out.println("FScore: " + 2 * precision * recall / (precision + recall));
            }
        }
    }
        
    public static <K> void doInOrderCrossValidation(List<Instance<K>> instances, svm_parameter param, int nr_fold, boolean binary) {        
        int size = instances.size();
        int chunkSize = size/nr_fold;
        int begin = 0;
        int end = chunkSize - 1;
        int tp = 0;
        int fp = 0;
        int fn = 0;
        int total = 0;
        
        for (int i = 0; i < nr_fold; i++) {
            System.out.println("Iteration: " + (i+1));
            List<Instance<K>> trainingInstances = new ArrayList<Instance<K>>();
            List<Instance<K>> testingInstances = new ArrayList<Instance<K>>();
            for (int j = 0; j < size; j++) {
                if (j >= begin && j <= end) {
                    testingInstances.add(instances.get(j));
                } else {
                    trainingInstances.add(instances.get(j));
                }
            }                                    
            
            svm_model<K> trainModel = train(trainingInstances, param);
            double[] predictions = SVMPredictor.predict(testingInstances, trainModel);
            for (int k = 0; k < predictions.length; k++) {
                
                if (predictions[k] == testingInstances.get(k).getLabel()) {
                //if (Math.abs(predictions[k] - testingInstances.get(k).getLabel()) < 0.00001) {
                    if (testingInstances.get(k).getLabel() > 0) {
                        tp++;
                    }
                } else if (testingInstances.get(k).getLabel() > 0) {
                    fn++;
                } else if (testingInstances.get(k).getLabel() < 0) {
                    //System.out.println(testingInstances.get(k).getData());
                    fp++;
                }
                total++;
            }
            //update
            begin = end+1;
            end = begin + chunkSize - 1;
            if (end >= size) {
                end = size-1;
            }
        }
        
        double precision = (double) tp / (tp + fp);
        double recall = (double) tp / (tp + fn);
        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("FScore: " + 2 * precision * recall / (precision + recall));
    }
    
    public static <K> void saveModel(svm_model<K> model, String filePath) throws IOException {
        svm.svm_save_model(filePath, model);
    }
}
