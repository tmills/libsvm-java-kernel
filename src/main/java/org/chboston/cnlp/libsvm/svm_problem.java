package org.chboston.cnlp.libsvm;

public class svm_problem<K> implements java.io.Serializable {

    public int l;
    public double[] y;
    public svm_node<K>[] x;

    public svm_problem(int l, double[] y, svm_node<K>[] x) {
        this.l = l;
        this.y = y;
        this.x = x;
    }

    public svm_problem() {
    }
    
}
