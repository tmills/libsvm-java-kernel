package org.chboston.cnlp.libsvm.ex;

/**
 *
 * @author Syeed Ibn Faiz
 */
public class Instance<K> {
    private double label;
    private K data;

    public Instance(double label, K data) {
        this.label = label;
        this.data = data;
    }

    public K getData() {
        return data;
    }

    public void setData(K data) {
        this.data = data;
    }

    public double getLabel() {
        return label;
    }

    public void setLabel(double label) {
        this.label = label;
    }            
}
