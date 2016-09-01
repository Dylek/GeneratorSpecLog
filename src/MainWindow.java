import com.sun.deploy.security.ruleset.Rule;

import javax.swing.*;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dylek on 2016-05-27.
 */
public class MainWindow extends JFrame {

    final static String LOOKANDFEEL="System";
    private JFrame mainFrame;

    private JTextArea generatedOutput ;
    private JTextArea loadedLogicRules;
    private JTextArea formulaField;
    private ArrayList <String> loadedFileLines;
    private String text;

    private JFileChooser fileChooser;
    private JMenuBar menuBar;
    private JButton generateB;
    private JButton loadFileB;
    private JButton saveFileB;
    private JScrollPane rulesPanel;
    private JScrollPane outputPanel;
    private  JScrollPane formulaPane;

//WAŻNE
    /**
     * ruleAtt <Zasada,Atrybuty> np. <Seq,[f1,f2]>
     * ruleLogic <Zasada,Lokiga > np. <Seq,[f1=>f2,f1^f2]>
     */
    Hashtable<String,String[]> ruleAtt;
    Hashtable<String,String[]> ruleLogic;

    public MainWindow(){
        ruleAtt=new Hashtable<>();
        ruleLogic=new Hashtable<>();

        initLookAndFeel();
        mainFrame=new JFrame();

        generatedOutput = new JTextArea("");
        generatedOutput.setFont(new Font("Verdena",Font.BOLD,14));
        loadedLogicRules = new JTextArea("");
        loadedLogicRules.setFont(new Font("Verdena",Font.PLAIN,15));
        formulaField=new JTextArea("");
        formulaField.setFont(new Font("Verdena",Font.BOLD,14));

        generateB=new JButton("Generate");
        loadFileB=new JButton("Open File");
        saveFileB=new JButton("Save File");
        fileChooser=new JFileChooser();
        loadedFileLines=new ArrayList<String>();
        rulesPanel=new JScrollPane(loadedLogicRules);
        outputPanel=new JScrollPane(generatedOutput);
        formulaPane=new JScrollPane(formulaField);

        rulesPanel.setPreferredSize(new Dimension(500,460));
        outputPanel.setPreferredSize(new Dimension(500,460));
        formulaPane.setPreferredSize(new Dimension(1005,50));
        Container container = mainFrame.getContentPane();
        container.setLayout(new FlowLayout(FlowLayout.LEFT));

        menuBar=new JMenuBar();
        menuBar.add(loadFileB);
        menuBar.add(generateB);
        menuBar.add(saveFileB);
        mainFrame.setJMenuBar(menuBar);

        mainFrame.setSize(new Dimension(1030,600));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setTitle("Generator Specyfikacji Logicznej");
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);
        container.add(formulaPane);
        container.add(rulesPanel);

        container.add(outputPanel);

        loadListeners();

        generatedOutput.setText("Miejsce na wygenerowaną specyfikację");
        generatedOutput.updateUI();
        generatedOutput.setLineWrap(true);
        loadedLogicRules.setText("Miejsce na zasady logiki, m.in Sequence,Concurrent ");
        loadedLogicRules.setLineWrap(true);
        /**
         * po wczytaniu nie zmieniamy już zasad logiki
         */
        loadedLogicRules.setEditable(false);
        generatedOutput.setEditable(false);
        generatedOutput.updateUI();
        formulaField.setText("Miejsce na formule");
    }

    private void initLookAndFeel() {
       try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
           e.printStackTrace();
       } catch (InstantiationException e) {
           e.printStackTrace();
       } catch (ClassNotFoundException e) {
           e.printStackTrace();
       }

    }

    private void loadListeners(){
        loadFileB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal=fileChooser.showOpenDialog(mainFrame);

                if(returnVal==JFileChooser.APPROVE_OPTION) {
                    resetLoadedData();
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
                formulaField.setText(formulaField.getText().trim());
                generateSpecLog();
            }
        });


    }

    private void parseFileIntoRules(File file){
        loadedLogicRules.setText("Wczytane zasady:\n");
        System.out.println("Parsuje");
        String fileText="";

        //osobna zmienna na logike z pliku, a osobna na wyświeltanies
        try {
            Scanner scanner=new Scanner(file);
            String temp="";
            while(scanner.hasNextLine()){
                temp=scanner.nextLine();
                loadedFileLines.add(temp);
                fileText=fileText.concat(temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //wyświetlanie wczytanych reguł

        for(String s:loadedFileLines){
            loadedLogicRules.append("\n  "+s);
        }
        System.out.println("Wczytano");
        loadedLogicRules.append("\n __________________________________________________");

        //Kod odpowiedzialkny za wyłuskiwanie zasad, atrubutów i logiki
        String[] splitFirst=fileText.split("}");
        for(int i=0;i<splitFirst.length;i++){

            String rule;
            String[] attrs;
            String[] logic;
            rule=splitFirst[i].substring(0,splitFirst[i].indexOf("("));

            attrs=splitFirst[i].substring(splitFirst[i].indexOf("(")+1,splitFirst[i].indexOf(")")).split(",");
            logic=splitFirst[i].substring(splitFirst[i].indexOf("{")+1).split(",");

            ruleAtt.put(rule,attrs);
            ruleLogic.put(rule,logic);
        }


    }

    private String generateSpecLog(){
        System.out.println("Generuje");
        int formulaState=0;


        ArrayList<RuleObject> ruleSeq=parseWL(formulaField.getText());

        formulaState=checkFormulaField(formulaField.getText(),ruleSeq);
        switch(formulaState){
            case 0: generatedOutput.append("\nFormuła w porządku. Rozpoczynam generowanie.");break;
            case 1:generatedOutput.append("\nBłąd w formule!\n Jedna z podanych formuł nie istnieje.");break;
            case 2:generatedOutput.append("\nBłąd w formule!\n Jedna z podanych formuł ma błędną ilość argumentów.");break;
            case 3:generatedOutput.append("\nBłąd w formule!\n Formuła zawiera nie dozwolone znaki.");break;
            case 4:generatedOutput.append("\nBłąd w formule!\n Formuła zawiera błędne nawiasowanie.");break;
            case 5:generatedOutput.append("\nBłąd w formule!\n Formuła zawiera błędy.");break;

            default:generatedOutput.append("\nNie określony błąd. Coś poszło nie tak.");break;
        }


        String L="";
        //kolejnośc wystąpienia,(nazwa zasady, argumenty z WL)
        //działamy tylko jeśli formuła jest OK
        if(formulaState==0){

            for(int i=0;i<ruleSeq.size();i++){
               ArrayList<String> wL_arg=ruleSeq.get(i).getRuleArgs();
                String L2=getL2(ruleSeq.get(i).getRuleName(),wL_arg);

                //było wszędzie i
                for (int j=0;j<wL_arg.size();j++){
                    if(!isAtomic(wL_arg.get(j))){
                        String agg=getF_en(wL_arg.get(j))+" V "+getF_ex(wL_arg.get(j));
                        System.out.println("\nagg:"+agg);
                        System.out.println(L2+" - "+wL_arg.get(j)+" - "+agg);
                        System.out.println(L2.replace(wL_arg.get(j),agg));
                        System.out.println(L2.replaceAll(wL_arg.get(j),agg));
                        L2=L2.replace(wL_arg.get(j),agg);
                        System.out.println(L2+" - "+wL_arg.get(j)+" - "+agg);
                    }
                }
                System.out.println("L:"+L);
                if(i==0){
                    L=L2;
                }else{
                    L=L+" U "+L2;//TODO znaczkiem sumy jest U

                }
            }
            generatedOutput.setText("Dla formuly: "+formulaField.getText()+"\nWygenerowano logike:\n"+L);
        }

        return L;
    }


private ArrayList<RuleObject> parseWL(String wl) {
    ArrayList<RuleObject> parrsed=new ArrayList<RuleObject>();
    //pierwszy jest oczywisty
    RuleObject obj=new RuleObject();
    obj.setRuleName(wl.substring(0,wl.indexOf("(")));
    obj.setRuleArgs(getArgs(wl.substring(wl.indexOf("(")+1,wl.lastIndexOf(")"))));
    parrsed.add(obj);
    for(int o=0;o<obj.getRuleArgs().size();o++){
        if(!isAtomic(obj.getRuleArgs().get(o))){

            ArrayList<RuleObject> referenced=parseWL(obj.getRuleArgs().get(o));
            for(RuleObject rek: referenced){
                parrsed.add(rek);
            }
        }
    }

    return  parrsed;
}

    /**
     *
     * @param s
     * @return
     * false- dany string nie jest atomiczny
     * true- dany string jest atomiczny(czyt. nie jest zagnieżdzoną formułą)
     */
    private boolean isAtomic(String s) {
        for(String pattern: ruleAtt.keySet()){
            if(s.contains(pattern)){
                return false;
            }
        }
        return true;
    }

    /**
     *TODO checkFormulaField
     * @param text
     * @param ruleSeq
     * @return
     * 0- ok
     * 1-Jedna z podanych formuł nie istnieje
     * 2-Jedna z podanych formuł ma błędną ilość argumentów.
     * 3-Formuła zawiera nie dozwolone znaki.
     * 4-Formuła zawira błędne nawiasowanie.
     * 5-zbłakane litery
     *
     */
    private int checkFormulaField(String text, ArrayList<RuleObject> ruleSeq) {

        if(text.length()>text.substring(0,text.lastIndexOf(")")+1).length()){
            return 5;
        }
        ArrayList<String> check=new ArrayList<String>();
        for(int i=0;i<text.length();i++){
            String toCheck=text.substring(i,i+1);
            if(toCheck.equals("(")){
                check.add("(");
            }else
            if( text.substring(i,i+1).equals(")")){
                if(check.size()>0 && check.get(check.size()-1).equals("(")){
                    check.remove(check.size()-1);
                }else{
                    check.add(")");
                }
            }
        }

        if(check.size()>0){
            return 4;
        }

        Pattern pattern=Pattern.compile("[!@#$%^&*|{}/\\`]");
        Matcher mat=pattern.matcher(text);
        if(mat.find()){
            return 3;
        }

        for(RuleObject obj: ruleSeq){
            //działa
            for(String argument :obj.getRuleArgs()){
                if(isAtomic(argument) && argument.contains("(")){
                    return 1;
                }
                if(!isAtomic(argument) && argument.length()>argument.substring(0,argument.lastIndexOf(")")+1).length()){
                    return 5;
                }
            }

            //to coś nie pyka
            if(!ruleAtt.containsKey(obj.getRuleName())){
                return 1;
            }
            //to działa
            if(obj.getRuleArgs().size()!=ruleAtt.get(obj.getRuleName()).length){
                return 2;
            }
        }

        /*
         *
          * 1. Kod na sprawdzanie poprawenego nawiasowania
          * 2. Kod na s
         */


        return 0;
    }


    //tu musi zostać podane Zasada-argumernty
    private String getF_en(String nonAtomic){
        RuleObject obj=new RuleObject();
        obj.setRuleName(nonAtomic.substring(0,nonAtomic.indexOf("(")));
        obj.setRuleArgs(getArgs(nonAtomic.substring(nonAtomic.indexOf("(")+1,nonAtomic.lastIndexOf(")"))));
        //f_en zawsze jest pierwsza formula
        String f_en=ruleLogic.get(obj.getRuleName())[0];
       // ArrayList<Integer> whichArgTo=new ArrayList<Integer>();
        //zamiana każdego f1 f2 na odpowiednie argumenty

        for(int i=0;i<ruleAtt.get(obj.getRuleName()).length;i++){
            System.out.println("rule att:"+ruleAtt.get(obj.getRuleName())[i]+"\nobj args:"+obj.getRuleArgs().get(i)+"\ni:"+i);
            if(f_en.contains(ruleAtt.get(obj.getRuleName())[i]) && !isAtomic(obj.getRuleArgs().get(i))){
                //skoro czegoś nie ma to nie zrobi tego replaca
                f_en=f_en.replace(ruleAtt.get(obj.getRuleName())[i],getF_en( obj.getRuleArgs().get(i)));
            }else{
                f_en=f_en.replace(ruleAtt.get(obj.getRuleName())[i],obj.getRuleArgs().get(i));
            }

        }
        return f_en;
    }
    private Object getF_ex(String nonAtomic) {
        RuleObject obj=new RuleObject();
        obj.setRuleName(nonAtomic.substring(0,nonAtomic.indexOf("(")));
        obj.setRuleArgs(getArgs(nonAtomic.substring(nonAtomic.indexOf("(")+1,nonAtomic.lastIndexOf(")"))));
        //f_en zawsze jest pierwsza formula
        String f_ex=ruleLogic.get(obj.getRuleName())[1];
        //zamiana każdego f1 f2 na odpowiednie argumenty
        for(int i=0;i<ruleAtt.get(obj.getRuleName()).length;i++){
            if(f_ex.contains(ruleAtt.get(obj.getRuleName())[i]) && !isAtomic(obj.getRuleArgs().get(i))){
                //skoro czegoś nie ma to nie zrobi tego replaca
                f_ex=f_ex.replace(ruleAtt.get(obj.getRuleName())[i],getF_en( obj.getRuleArgs().get(i)));
            }else{
                f_ex=f_ex.replace(ruleAtt.get(obj.getRuleName())[i],obj.getRuleArgs().get(i));
            }
        }
        return f_ex;
    }

    /**
     * L2 := WL[i]0P n fWL[i]0P:fen;WL[i]0P:fexg;
     * Dla danej zasady rule, zwracamy jej logikę z podstawionymi wartościami atrybutów
     * @param rule
     * @param args
     * @return
     */
    private String getL2(String rule,ArrayList<String> args){
        String temp="";
        //od 2, ponieważ pomijamy f.en i f.ex
        for(int i=2;i<ruleLogic.get(rule).length;i++){
            //UOL=Unit of logic
            String UOL=ruleLogic.get(rule)[i];//pobieram jedną zasadę

            //dla każdego atrybutu podmieniamy odpowiednie parametry danej reguły
            for(int j=0;j<ruleAtt.get(rule).length;j++){
                UOL=UOL.replaceAll(ruleAtt.get(rule)[j],args.get(j));
            }

            if(i==2){
               temp=temp.concat(UOL);
            }else if(i>2){
                temp=temp.concat(","+UOL);
            }
        }

        return temp;
    }
    private void resetLoadedData(){
        loadedFileLines.clear();
        loadedLogicRules.removeAll();
    }

    //spradzone
    private  ArrayList<String> getArgs(String data){
        ArrayList<String> args=new ArrayList<String>();
        String temp="";
        data=data+",";
        int inside=0;
        for(int i=0;i<data.length();i++){
            //zakładam, że jest poprawne nawiasowanie
            if(inside==0 && data.substring(i,i+1).equals(",")){
                if(temp.substring(0,1).equals(",")){
                    temp=temp.substring(1);
                }
                args.add(temp);
                //System.out.println("add:"+temp);
                temp="";

            }
            else if(data.substring(i,i+1).equals("(")){
                //System.out.println("inside++");
                inside++;
            }else if(data.substring(i,i+1).equals(")")){
                //System.out.println("inside--");
                inside--;
            }

            temp=temp+data.substring(i,i+1);

        }

        return args;
    }
}
