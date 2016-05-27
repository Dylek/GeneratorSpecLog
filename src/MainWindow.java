import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Dylek on 2016-05-27.
 */
public class MainWindow extends JFrame {
    private JFrame mainFrame;

    private JTextArea generatedOutput ;
    private JTextArea loadedLogicRules;
    private JFileChooser fileChooser;
    private JMenuBar menuBar;
    private JButton generateB;
    private JButton loadFileB;
    private JButton saveFileB;
    private JScrollPane rulesPanel;
    private JScrollPane outputPanel;

    public MainWindow(){
        mainFrame=new JFrame();
        JTextArea generatedOutput = new JTextArea();
        JTextArea loadedLogicRules = new JTextArea();
        generateB=new JButton("Generate");
        loadFileB=new JButton("Open File");
        saveFileB=new JButton("Save File");
        fileChooser=new JFileChooser();

        rulesPanel=new JScrollPane(loadedLogicRules);
        outputPanel=new JScrollPane(generatedOutput);

        rulesPanel.setPreferredSize(new Dimension(500,300));
        outputPanel.setPreferredSize(new Dimension(500,300));

        Container container = mainFrame.getContentPane();
        container.setLayout(new FlowLayout(FlowLayout.LEFT));

        menuBar=new JMenuBar();
        menuBar.add(loadFileB);
        menuBar.add(generateB);
        menuBar.add(saveFileB);
        mainFrame.setJMenuBar(menuBar);

        mainFrame.setSize(new Dimension(1300,600));
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
        loadedLogicRules.setText("Tu polecą wczytane zasady logiki, wiesz te całe Seq itp");
        generatedOutput.updateUI();
    }
    private void loadListeners(){
        loadFileB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal=fileChooser.showOpenDialog(mainFrame);

                if(returnVal==JFileChooser.APPROVE_OPTION) {

                    String fileName="";
                    try {
                        fileName=fileChooser.getSelectedFile().getCanonicalPath();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    if (fileName != null && fileName != "") {
                        System.out.println("Loading file");

                        JPanel conPan=new JPanel();
                        conPan.setPreferredSize(new Dimension(400, 300));
                        parseFileIntoRules(fileName);

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
                    File file=new File(String.valueOf(fileSelected));

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
                    //generatedOutput.setText("działaj do k***");
                    System.out.println(generatedOutput.getText().toString());
                    while(!generatedOutput.getText().isEmpty()) {
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
                    }
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

    private void parseFileIntoRules(String fileName){

    }

    private void generateSpecLog(){

    }

}
