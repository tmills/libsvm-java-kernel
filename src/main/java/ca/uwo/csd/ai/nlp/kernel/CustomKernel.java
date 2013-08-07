package ca.uwo.csd.ai.nlp.kernel;

import ca.uwo.csd.ai.nlp.libsvm.svm_node;

/**
 * Interface for a custom kernel function
 * @author Syeed Ibn Faiz
 */
public interface CustomKernel<DATA_TYPE> {
    double evaluate(svm_node<DATA_TYPE> x, svm_node<DATA_TYPE> y);
}
