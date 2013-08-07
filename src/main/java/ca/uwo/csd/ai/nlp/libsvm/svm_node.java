package ca.uwo.csd.ai.nlp.libsvm;

public class svm_node<K> implements java.io.Serializable {

    public K data;

    public svm_node() {
    }

    public svm_node(K data) {
        this.data = data;
    }
}
