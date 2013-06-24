package utils;

import ca.uwo.csd.ai.nlp.common.SparseVector;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.uwo.csd.ai.nlp.libsvm.ex.Instance;

/**
 * <code>DataFileReader</code> reads data files written in LibSVM format.
 * @author Syeed Ibn Faiz
 */
public class DataFileReader {
    
    public static List<Instance<SparseVector>> readDataFile(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));        
        
        ArrayList<Instance<SparseVector>> instances = new ArrayList<Instance<SparseVector>>();
        
        String line;
        int lineCount = 0;
        while ((line = reader.readLine()) != null) {
            lineCount++;
            String[] tokens = line.split("\\s+");
            if (tokens.length < 2) {                
                System.err.println("Inappropriate file format: " + fileName);
                System.err.println("Error in line " + lineCount);
                System.exit(-1);
            }
            
            double label = Double.parseDouble(tokens[0]);
            SparseVector vector = new SparseVector(tokens.length - 1);
            
            for (int i = 1; i < tokens.length; i++) {
                String[] fields = tokens[i].split(":");
                if (fields.length < 2) {
                    System.err.println("Inappropriate file format: " + fileName);
                    System.err.println("Error in line " + lineCount);
                    System.exit(-1);
                }
                int index = Integer.parseInt(fields[0]);
                double value = Double.parseDouble(fields[1]);
                vector.add(index, value);
            }
            
            instances.add(new Instance<SparseVector>(label, vector));
        }                
        reader.close();       
        return instances;
    }
}
