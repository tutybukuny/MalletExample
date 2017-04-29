package malletexample;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import cc.mallet.fst.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.pipe.tsf.*;
import cc.mallet.types.*;

public class TrainCRF {

    private static void print() throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(new File("st_crf.model"));
        ObjectInputStream ois = new ObjectInputStream(fis);

        CRF crf = (CRF) ois.readObject();

        InstanceList testData = new InstanceList(crf.getInputPipe());
        testData.addThruPipe(new LineGroupIterator(new BufferedReader(
                new InputStreamReader(new FileInputStream("Train.txt"))),
                Pattern.compile("^\\s*$"), true));

        for (int i = 0; i < testData.size(); i++) {
            Sequence input = (Sequence) testData.get(i).getData();
            Sequence output = crf.transduce(input);
            for (int j = 0; j < output.size(); j++) {
//                System.out.println(input.get(j).toString() + " " + output.get(j));
                System.out.println(output.get(j));
            }
            System.out.println("");
        }
    }

    public TrainCRF(String trainingFilename, String testingFilename) throws IOException {

        ArrayList<Pipe> pipes = new ArrayList<>();

        int[][] conjunctions = new int[2][];
        conjunctions[0] = new int[]{-1};
        conjunctions[1] = new int[]{1};

        pipes.add(new SimpleTaggerSentence2TokenSequence());
        pipes.add(new OffsetConjunctions(conjunctions));
        pipes.add(new FeaturesInWindow("B-", -3, 3));
//        pipes.add(new TokenTextCharSuffix("C1=", 1));
//        pipes.add(new TokenTextCharSuffix("C2=", 2));
//        pipes.add(new TokenTextCharSuffix("C3=", 3));
//        pipes.add(new RegexMatches("CAPITALIZED", Pattern.compile("^\\p{Lu}.*")));
//        pipes.add(new RegexMatches("STARTSNUMBER", Pattern.compile("^[0-9].*")));
//        pipes.add(new RegexMatches("HYPHENATED", Pattern.compile(".*\\-.*")));
//        pipes.add(new RegexMatches("DOLLARSIGN", Pattern.compile(".*\\$.*")));
//        pipes.add(new TokenFirstPosition("FIRSTTOKEN"));
        pipes.add(new TokenSequence2FeatureVectorSequence());

        Pipe pipe = new SerialPipes(pipes);

        InstanceList trainingInstances = new InstanceList(pipe);
        InstanceList testingInstances = new InstanceList(pipe);

        trainingInstances.addThruPipe(new LineGroupIterator(
                new BufferedReader(new InputStreamReader(new FileInputStream(trainingFilename), "UTF-8")),
                Pattern.compile("^\\s*$"), true));
        testingInstances.addThruPipe(new LineGroupIterator(new BufferedReader(
                new InputStreamReader(new FileInputStream(testingFilename), "UTF-8")),
                Pattern.compile("^\\s*$"), true));

        CRF crf = new CRF(pipe, null);
        //crf.addStatesForLabelsConnectedAsIn(trainingInstances);
        crf.addStatesForThreeQuarterLabelsConnectedAsIn(trainingInstances);
        crf.addStartState();

        CRFTrainerByLabelLikelihood trainer
                = new CRFTrainerByLabelLikelihood(crf);
        trainer.setGaussianPriorVariance(10.0);

        //CRFTrainerByStochasticGradient trainer = 
        //new CRFTrainerByStochasticGradient(crf, 1.0);
        //CRFTrainerByL1LabelLikelihood trainer = 
        //	new CRFTrainerByL1LabelLikelihood(crf, 0.75);
        //trainer.addEvaluator(new PerClassAccuracyEvaluator(trainingInstances, "training"));
        trainer.addEvaluator(new PerClassAccuracyEvaluator(testingInstances, "testing"));
        trainer.addEvaluator(new TokenAccuracyEvaluator(testingInstances, "testing"));
        trainer.train(trainingInstances);

        FileOutputStream fos = new FileOutputStream("st_crf.model");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(crf);
        oos.close();
        fos.close();
    }

    public static void main(String[] args) throws Exception {
//        TrainCRF trainer = new TrainCRF("Train.txt", "Test.txt");
        print();
    }

}
