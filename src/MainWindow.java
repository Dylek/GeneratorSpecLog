import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Dylek on 2016-05-27.
 */
public class MainWindow extends JFrame {
    private JFrame mainFrame;

    private JTextArea generatedOutput ;
    private JTextArea loadedLogicRules;
    private ArrayList <String> loadedFileLines;

    private JFileChooser fileChooser;
    private JMenuBar menuBar;
    private JButton generateB;
    private JButton loadFileB;
    private JButton saveFileB;
    private JScrollPane rulesPanel;
    private JScrollPane outputPanel;

    public MainWindow(){
        mainFrame=new JFrame();
        generatedOutput = new JTextArea("");
        loadedLogicRules = new JTextArea("");
        generateB=new JButton("Generate");
        loadFileB=new JButton("Open File");
        saveFileB=new JButton("Save File");
        fileChooser=new JFileChooser();
        loadedFileLines=new ArrayList<String>();
        rulesPanel=new JScrollPane(loadedLogicRules);
        outputPanel=new JScrollPane(generatedOutput);

        rulesPanel.setPreferredSize(new Dimension(450,500));
        outputPanel.setPreferredSize(new Dimension(450,500));

        Container container = mainFrame.getContentPane();
        container.setLayout(new FlowLayout(FlowLayout.LEFT));

        menuBar=new JMenuBar();
        menuBar.add(loadFileB);
        menuBar.add(generateB);
        menuBar.add(saveFileB);
        mainFrame.setJMenuBar(menuBar);

        mainFrame.setSize(new Dimension(1000,600));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setTitle("Generator Specyfikacji Logicznej");
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);

        container.add(rulesPanel);
        container.add(outputPanel);

        loadListeners();

        generatedOutput.setText("tu poleci wygenerowana specyfikacja");
        generatedOutput.updateUI();
        loadedLogicRules.setText("Tu polecą wczytane zasady logiki, m.in Sequence,Concurrent ");
        generatedOutput.updateUI();
    }
    private void loadListeners(){
        loadFileB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal=fileChooser.showOpenDialog(mainFrame);

                if(returnVal==JFileChooser.APPROVE_OPTION) {

                    String fileName="";
                    File file=null;
                    try {
                        file=fileChooser.getSelectedFile();
                        fileName=fileChooser.getSelectedFile().getCanonicalPath();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    if (fileName != null && fileName != "") {
                        System.out.println("Loading file");

                        JPanel conPan=new JPanel();
                        conPan.setPreferredSize(new Dimension(400, 300));
                        parseFileIntoRules(file);

                        System.out.println("Loading complete\n Result in output Text Area");
                    }

                    fileChooser.setSelectedFile(new File(""));
                }
            }
        });
        saveFileB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal=fileChooser.showSaveDialog(mainFrame);

                if(returnVal==JFileChooser.APPROVE_OPTION){
                    File fileSelected= fileChooser.getSelectedFile();
                    File file=new File(fileSelected+".txt");

                    // creates the file
                    try {
                        file.createNewFile();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    FileWriter writer = null;
                    try {
                        writer = new FileWriter(file);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                   // System.out.println(generatedOutput.getText());
                    //while(!generatedOutput.getText().isEmpty()) {
                        // Writes the content to the file
                        try {
                            writer.write(generatedOutput.getText().toString());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        try {
                            writer.flush();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        try {
                            writer.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        fileChooser.setSelectedFile(new File(""));
                   // }
                }
            }

        });
        generateB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateSpecLog();
            }
        });
    }

    private void parseFileIntoRules(File file){
        loadedLogicRules.setText("Wczytane zasady:\n");
        System.out.println("Parsuje");

        try {
            Scanner scanner=new Scanner(file);

            while(scanner.hasNextLine()){
                loadedFileLines.add(scanner.nextLine());
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for(String s:loadedFileLines){
            loadedLogicRules.append("\n"+s);
        }
        System.out.println("Wczytano");

    }

    private void generateSpecLog(){
        System.out.println("Generuje");
        generatedOutput.setText("Wygenerowana logika:\n");
        for(String s:loadedFileLines){
            generatedOutput.append("\n"+s);
        }

    }

}