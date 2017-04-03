package XML;

import Measure.LibConst;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.hus.nlp.tagger.VietnameseMaxentTagger;

/**
 *
 * @author tutyb
 */
public class TestTag {

    private VietnameseMaxentTagger tagger = new VietnameseMaxentTagger(
            "E:\\thien\\Learning\\NLP\\Project\\Code\\vn.hus.nlp.tagger-4.2.0-bin\\resources\\models\\vtb.tagger");

    private File folder;

    public TestTag(String folderPath) {
        folder = new File(folderPath);
        run(folder);
    }

    private void run(File f) {
        File[] files = f.listFiles();

        for (File file : files) {
            if (file.getPath().contains("\\.")) {
                continue;
            }
            
            if (file.isFile()) {
                taggerRunning(file.getPath());
            } else {
                run(file);
            }
        }
    }

    private void taggerRunning(String filePath) {
        Scanner inp = new Scanner(filePath);
        String newLine = "\r\n";
        try {
            PrintWriter print = new PrintWriter(
                    filePath.replace("Tagged Questions", "Split word tagged questions"), "UTF-8");

            print.write(inp.nextLine() + newLine);
            while (inp.hasNext()) {
                String line = inp.nextLine();
                if (line.contains("root") || line.contains("test")
                        || line.contains("answer") || line.contains("base")) {
                    print.write(line + newLine);
                    continue;
                }

                String originLine = line;
                line = line.trim();
                System.out.println(line);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TestTag.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestTag.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        TestTag testTag = new TestTag("E:\\thien\\Learning\\NLP\\Project\\Data\\Tagged Questions");
    }
}
