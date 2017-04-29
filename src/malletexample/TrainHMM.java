package malletexample;

import cc.mallet.fst.HMM;
import cc.mallet.fst.HMMTrainerByLikelihood;
import cc.mallet.fst.PerClassAccuracyEvaluator;
import cc.mallet.fst.TransducerEvaluator;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.SimpleTaggerSentence2TokenSequence;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.LineGroupIterator;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Sequence;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class TrainHMM {
    
    private static void print() throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(new File("st_hmm.model"));
        ObjectInputStream ois = new ObjectInputStream(fis);
        
        HMM hmm = (HMM) ois.readObject();
        
        InstanceList testData = new InstanceList(hmm.getInputPipe());
        testData.addThruPipe(new LineGroupIterator(new BufferedReader(
                new InputStreamReader(new FileInputStream("Test.txt"))),
                Pattern.compile("^\\s*$"), true));
        
        for (int i = 0; i < testData.size(); i++) {
            Sequence input = (Sequence) testData.get(i).getData();
            Sequence output = hmm.transduce(input);
            for (int j = 0; j < output.size(); j++) {
                System.out.println(input.get(j) + " " + output.get(j));
            }
            System.out.println("");
        }

//        hmm.print();
    }
    
    public TrainHMM(String trainingFilename, String testingFilename) throws IOException {
        
        ArrayList<Pipe> pipes = new ArrayList<>();
        
        pipes.add(new SimpleTaggerSentence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        
        Pipe pipe = new SerialPipes(pipes);
        
        InstanceList trainingInstances = new InstanceList(pipe);
        InstanceList testingInstances = new InstanceList(pipe);
        
        trainingInstances.addThruPipe(new LineGroupIterator(new BufferedReader(
                new InputStreamReader(new FileInputStream(trainingFilename))),
                Pattern.compile("^\\s*$"), true));
        testingInstances.addThruPipe(new LineGroupIterator(new BufferedReader(
                new InputStreamReader(new FileInputStream(testingFilename))),
                Pattern.compile("^\\s*$"), true));
        
        HMM hmm = new HMM(pipe, null);
//        hmm.addFullyConnectedStatesForThreeQuarterLabels(trainingInstances);
        hmm.addStatesForLabelsConnectedAsIn(trainingInstances);
//        hmm.addStatesForBiLabelsConnectedAsIn(trainingInstances);
//        hmm.addStatesForThreeQuarterLabelsConnectedAsIn(trainingInstances);

        HMMTrainerByLikelihood trainer
                = new HMMTrainerByLikelihood(hmm);
        TransducerEvaluator trainingEvaluator
                = new PerClassAccuracyEvaluator(trainingInstances, "training");
        TransducerEvaluator testingEvaluator
                = new PerClassAccuracyEvaluator(testingInstances, "testing");
//        trainer.addEvaluator(trainingEvaluator);
//        trainer.addEvaluator(testingEvaluator);
        trainer.train(trainingInstances);
//        trainer.train(trainingInstances, 10);

        trainingEvaluator.evaluate(trainer);
        testingEvaluator.evaluate(trainer);
        
        FileOutputStream fos = new FileOutputStream("st_hmm.model");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(hmm);
        oos.close();
        fos.close();
    }
    
    public static void main(String[] args) throws Exception {
        TrainHMM trainer = new TrainHMM("Train.txt", "Test.txt");
//        TrainHMM trainer = new TrainHMM("Trainv3.txt", "Testv3.txt");
//        print();
    }
    
}
