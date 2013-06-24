
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import utils.DataFileReader;
import ca.uwo.csd.ai.nlp.common.SparseVector;
import ca.uwo.csd.ai.nlp.kernel.KernelManager;
import ca.uwo.csd.ai.nlp.kernel.LinearKernel;
import ca.uwo.csd.ai.nlp.libsvm.svm_model;
import ca.uwo.csd.ai.nlp.libsvm.svm_parameter;
import ca.uwo.csd.ai.nlp.libsvm.ex.Instance;
import ca.uwo.csd.ai.nlp.libsvm.ex.SVMPredictor;
import ca.uwo.csd.ai.nlp.libsvm.ex.SVMTrainer;

/**
 * Demonstration of sample usage
 * @author Syeed Ibn Faiz
 */
public class Demo {
    
    public static void testLinearKernel(String[] args) throws IOException, ClassNotFoundException {
        String trainFileName = args[0];
        String testFileName = args[1];
        String outputFileName = args[2];
        
        //Read training file
        List<Instance<SparseVector>> trainingInstances = DataFileReader.readDataFile(trainFileName);        
        
        //Register kernel function
        KernelManager.setCustomKernel(new LinearKernel());        
        
        //Setup parameters
        svm_parameter param = new svm_parameter();                
        
        //Train the model
        System.out.println("Training started...");
        svm_model model = SVMTrainer.train(trainingInstances, param);
        System.out.println("Training completed.");
                
        //Save the trained model
        //SVMTrainer.saveModel(model, "a1a.model");
        //model = SVMPredictor.load_model("a1a.model");
        
        //Read test file
        List<Instance<SparseVector>> testingInstances = DataFileReader.readDataFile(testFileName);
        //Predict results
        double[] predictions = SVMPredictor.predict(testingInstances, model, true);
        writeOutputs(outputFileName, predictions);
        //SVMTrainer.doCrossValidation(trainingInstances, param, 10, true);
        //SVMTrainer.doInOrderCrossValidation(trainingInstances, param, 10, true);
    }
    
    private static void writeOutputs(String outputFileName, double[] predictions) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
        for (double p : predictions) {
            writer.write(String.format("%.0f\n", p));
        }
        writer.close();
    }
    
    private static void showUsage() {
        System.out.println("Demo training-file testing-file output-file");
    }
    
    private static boolean checkArgument(String[] args) {
        return args.length == 3;
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {        
        if (checkArgument(args)) {
            testLinearKernel(args);
        } else {
            showUsage();
        }
    }
        
}
