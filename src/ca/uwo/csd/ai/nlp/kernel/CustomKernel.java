package ca.uwo.csd.ai.nlp.kernel;

import ca.uwo.csd.ai.nlp.libsvm.svm_node;

/**
 * Interface for a custom kernel function
 * @author Syeed Ibn Faiz
 */
public interface CustomKernel<K> {
    double evaluate(svm_node<K> x, svm_node<K> y);
}
