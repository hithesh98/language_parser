public class SampleCode {
    public boolean isInLanguage(ContextFreeGrammar cfg, Word w){
        int n = w.length();
        int derivNum = (2*n) - 1;
    
        List<Rule> rules = new ArrayList<Rule>();
        rules = cfg.getRules();
        Variable startVariable = cfg.getStartVariable();
    
        int count = 0;
        List<Word> words = new ArrayList<Word>(); 
        List<Word> words1 = new ArrayList<Word>(); 

        
        // to get the first variable
        Word word = words.get(0);
        Symbol sym = word.get(0);
        // Evaluates the variable to the rules to get expansion
        for (Rule rule : rules) {
            if (rule.getVariable().equals(sym)) {
            words.add(rule.getExpansion());
            }
            count += 1;
        }

        //   Substitution
        Word word = words.get(0).replace(0, words.get(1));

        // Find 
        Word testfind = new Word("1");
        for (Word word : words) {
        if (word.equals(testfind)){
            count += 1;
        }
        }

        // Store into dictionary
        HashMap<Integer, List<Word>> deriv = new HashMap<Integer, List<Word>>();
        deriv.put(1, words);

        
        // Complete derivation for a list of words.
        for (Word word : words ){
            for (int i = 0; i < word.length(); i++) {
            if (!word.get(i).isTerminal()){
                List<Word> der1 = new ArrayList<Word>(); 
                for (Rule rule : rules) {
                if (rule.getVariable().equals(word.get(i))) {
                    der1.add(rule.getExpansion());
                }
                }
                for (Word deri1 : der1 ){
                words1.add(word.replace(i, deri1));
                }
            }
            }
        }
}

// The working code
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import computation.contextfreegrammar.*;
import computation.parser.*;
import computation.parsetree.*;
import computation.derivation.*;

public class Parser implements IParser {
  public boolean isInLanguage(ContextFreeGrammar cfg, Word w){
    int n = w.length();
    int derivNum = (2*n) - 1;

    List<Rule> rules = new ArrayList<Rule>();
    rules = cfg.getRules();
    Variable startVariable = cfg.getStartVariable();

    int count = 0;

    // make list of list for words to be stored 
    List<List<Word>> generalList = new ArrayList<List<Word>>(); 
    for (int i=0; i < derivNum; i++) {
      ArrayList<Word> myList = new ArrayList<Word>();
      generalList.add(myList); 
    }



    List<Word> words = new ArrayList<Word>(); 
    HashMap<Integer, List<Word>> dictWord = new HashMap<Integer, List<Word>>();


    // Build a list of words derived from Start variable
      for (Rule rule : rules) {
        if (rule.getVariable() == startVariable) {
          generalList.get(count).add(rule.getExpansion());
        }
    }
    dictWord.put(count + 1, generalList.get(count));
    count += 1;

    while (count < derivNum){
      for (Word word : generalList.get(count-1) ){
        for (int i = 0; i < word.length(); i++) {
          if (!word.get(i).isTerminal()){
            List<Word> der1 = new ArrayList<Word>(); 
            for (Rule rule : rules) {
              if (rule.getVariable().equals(word.get(i))) {
                der1.add(rule.getExpansion());
              }
            }
            for (Word deriv : der1 ){
              generalList.get(count).add(word.replace(i, deriv));
            }
          }
        }
      }
      count += 1;
    }
    boolean check = false;
    for (Word word : generalList.get(count-1)) {
      if (word.equals(w)){
         check = true;
     }
    }

    return check;  
  }

  
  public ParseTreeNode generateParseTree(ContextFreeGrammar cfg, Word w) {
    // 1. Initialise neccesary variables
    int n = w.length();
    int exactDervOfWord = (2*n) - 1;
    List<Rule> rules = new ArrayList<Rule>();
    rules = cfg.getRules();
    Variable startVariable = cfg.getStartVariable();
    int numDerivations = 0;
    List<List<Word>> generalList = new ArrayList<List<Word>>();
    List<List<Word>> varList = new ArrayList<List<Word>>();


    // 2. Lists for storing derivation
      // make exact number of list for words to be stored at each derivation 
    makeLists(exactDervOfWord, generalList);

    // 3. Derivation from Start Variable
    for (Rule rule : rules) {
      if (rule.getVariable().equals(startVariable)) {
        generalList.get(numDerivations).add(rule.getExpansion());
      }
    }
    numDerivations += 1;

    // 4. 
    while (numDerivations < exactDervOfWord-n) {
      if (numDerivations == 1){
        // Stores the var only from the start expansion into varList
        List<Word> listOfVars = new ArrayList<Word>();
        for (Word word : generalList.get(numDerivations-1)){
          boolean varOnly= true;
          for (int i =0; i < word.length(); i++){
            if(word.get(i).isTerminal()){
              varOnly = false;
            }
          }
          if (varOnly){
            listOfVars.add(word);
          }
        }
        varList.add(listOfVars);
        numDerivations += 1;         
      }
      if (numDerivations > 1 && numDerivations - 1 < exactDervOfWord-n ) {
        List<List<List<Word>>> listOfExpPerDerviation = new ArrayList<List<List<Word>>>();
        for (Word word : varList.get(numDerivations-2)){
          List<List<Word>> listOfExPerWord = new ArrayList<List<Word>>();
          for(int i= 0; i <word.length(); i++){
            for(Rule rule :rules){
              List<Word> tempListofExpan = new ArrayList<Word>();
              if(rule.getVariable().equals(word.get(i))){
                if (!rule.getExpansion().isTerminal()){
                  tempListofExpan.add(word);
                  tempListofExpan.add(word.replace(i, rule.getExpansion()));
                  listOfExPerWord.add(tempListofExpan);
                }
              }
            }
          }
          listOfExpPerDerviation.add(listOfExPerWord);
        }
        numDerivations += 1;
      }
    }

    return null;
    return null;
  }
}

List<Derivation> listofDer = new ArrayList<Derivation>();
for (Word word : generalList.get(numDerivations-1)) {
  Derivation dervofWord = new Derivation(word);
  for (int i = 0; i < word.length(); i++){
    for (Rule rule: rules){
      if(!rule.getExpansion().isTerminal()){
        if(rule.getVariable().equals(word.get(i))){
          dervofWord.addStep(word, rule, i);
        }  
      }
    }
  }
  listofDer.add(dervofWord);
}
// for all the derivations in the list
for (int i =0; i < listofDer.size(); i++) {
  // for all the steps in the derivation
  for (Step step : listofDer.get(0)) {
    Derivation dervofWord = new Derivation(step.getWord());
    for (int j = 0; j < step.getWord().length(); i++){
      for (Rule rule: rules){
        if(!rule.getExpansion().isTerminal()){
          if(rule.getVariable().equals(step.getWord().get(j))){
            dervofWord.addStep(step.getWord(), rule, j);
          }  
        }
      }
      listofDer.add(dervofWord);
    }
  }
}

List<Word> stepwords = new ArrayList<Word>();
    boolean found = false;
    while(found==false){
      for (int j=0; j<3; j++){
        for(Step step : listofDerivations.get(j)){
          for (int i=0; i<step.getWord().length(); i++){
            for (Rule rule : rules){
              if (numDerivations < exactDervOfWord-n){
                if (rule.getVariable().equals(step.getWord().get(i)) && !step.getWord().get(i).isTerminal()){
                  Derivation newDerivation = new Derivation(listofDerivations.get(j));
                  newDerivation.addStep(step.getWord().replace(i, rule.getExpansion()), rule, i);
                  listofDerivations.add(newDerivation);
                  stepwords.add(step.getWord());
               }
              }
              if (numDerivations >= exactDervOfWord-n){
                if (rule.getVariable().equals(step.getWord().get(i)) && rule.getExpansion().isTerminal()){
                  Derivation newDerivation = new Derivation(listofDerivations.get(j));
                  newDerivation.addStep(step.getWord().replace(i, rule.getExpansion()), rule, i);
                  listofDerivations.add(newDerivation);
                  stepwords.add(step.getWord().replace(i, rule.getExpansion()));
                }
              }

            }
          }
        }
      }
      numDerivations +=1;
    }


    //  Working code:
    public ParseTreeNode generateParseTree(ContextFreeGrammar cfg, Word w) {
      // 1. Initialise neccesary variables
      int n = w.length();
      int exactDervOfWord = (2*n) - 1;
      List<Rule> rules = new ArrayList<Rule>();
      rules = cfg.getRules();
      Variable startVariable = cfg.getStartVariable();
      int numDerivations = 0;
      List<List<Word>> generalList = new ArrayList<List<Word>>();
  
      // 2. Lists for storing derivation
        // make exact number of list for words to be stored at each derivation 
      makeLists(exactDervOfWord, generalList);
  
      List<Derivation> listofDerivations = new ArrayList<Derivation>();
      // 3. Derivation from Start Variable
      for (Rule rule : rules) {
        if (rule.getVariable().equals(startVariable)) {
          generalList.get(numDerivations).add(rule.getExpansion());
          Derivation derivation = new Derivation(rule.getExpansion());
          listofDerivations.add(derivation);
        }
      }
      numDerivations += 1;
      int count = 1;
      List<Derivation> foundDerivation = new ArrayList<Derivation>();
      boolean found = false;
      while(found==false){
        foundBreak:
        for (int j=0; numDerivations <= exactDervOfWord ; j++){
          count = 1;
          for(Step step : listofDerivations.get(j)){
            count += 1;
            for (int i=0; i<step.getWord().length(); i++){
              for (Rule rule : rules){
                if (count <= exactDervOfWord){
                  if (rule.getVariable().equals(step.getWord().get(i))){
                    Derivation newDerivation = new Derivation(listofDerivations.get(j));
                    newDerivation.addStep(step.getWord().replace(i, rule.getExpansion()), rule, i);
                    listofDerivations.add(newDerivation);
                    if(newDerivation.getLatestWord().equals(w)){
                      found=true;
                      foundDerivation.add(newDerivation);
                      break foundBreak;
                    }
                  }
                }
              }
            }
            numDerivations = count;
          }
        }
      }
    
      







ParseTreeNode tree = new ParseTreeNode(new Variable("A0"), 
new ParseTreeNode(new Variable('Z'), new ParseTreeNode(new Terminal('0'))),
new ParseTreeNode(new Variable('B'), 
                      1new ParseTreeNode(new Variable('A'), 
                      new ParseTreeNode(new Variable('Z'), new ParseTreeNode(new Terminal('0'))), 
                      new ParseTreeNode(new Variable('Y'), new ParseTreeNode(new Terminal('1')))), 
                      new ParseTreeNode(new Variable('Y'), new ParseTreeNode(new Terminal('1')))));




    List<Word> wordsDer = new ArrayList<Word>();
    // Get variable for parseTree
    List<ParseTreeNode> listNodes = new ArrayList<ParseTreeNode>();

    Iterator<Step> iterDer = foundDerivation.get(0).iterator();  
    int ders = 0;  
    for(Step step : foundDerivation.get(0)){
      if (ders == 0 && step.getRule() != null){
        ParseTreeNode p =new ParseTreeNode(step.getRule().getVariable(), new ParseTreeNode(step.getRule().getExpansion().get(0)));
        listNodes.add(p);
      }
    }

    // 
    List<Variable> varR = new ArrayList<Variable>();
    List<Word> expR = new ArrayList<Word>();

    Iterator<Step> splitRule = foundDerivation.get(0).iterator();  
    for(Step step : foundDerivation.get(0)){
      varR.add(step.getRule().getVariable());
      expR.add(step.getRule().getExpansion());
    }



    
    List<ParseTreeNode> listOfP = new ArrayList<ParseTreeNode>();
    List<Variable> varR = new ArrayList<Variable>();
    List<Word> expR = new ArrayList<Word>();
    List<Rule> ruleR = new ArrayList<Rule>();
   
    for(int i =0; i<exactDervOfWord; i++){
      ruleR.add(foundDerivation.get(0).iterator().next().getRule());
    }  
    
    for(int i =0; i<exactDervOfWord; i++){
      Rule derRule =  foundDerivation.get(0).iterator().next().getRule();
      varR.add(derRule.getVariable());
      expR.add(derRule.getExpansion());
    }    

    int steps = 0;
    int numofDer=0;

    for (int i =0; i<=exactDervOfWord; i++){
      if (i < exactDervOfWord){
        numofDer += 1;
        steps += 1;

        // if is a terminal then parse node for it
        if(expR.get(numofDer).isTerminal()){
          ParseTreeNode ndPar = new ParseTreeNode(varR.get(numofDer), new ParseTreeNode(expR.get(numofDer).get(0)));
          listOfP.add(ndPar);
        }



        if (steps > 2){
          Symbol first = listOfP.get(-1).getSymbol();
          Symbol second = listOfP.get(-2).getSymbol();
          if (expR.get(numofDer).equals(new Word(first,second))){
            ParseTreeNode par = new ParseTreeNode(varR.get(numofDer), listOfP.get(-1), listOfP.get(-2));
            listOfP.clear();
            listOfP.add(par);
          }
          steps = 0;
        }
      }
      if (i == exactDervOfWord) {
        ParseTreeNode finished = new ParseTreeNode(varR.get(numofDer), listOfP.get(-1), listOfP.get(-2));
        listOfP.clear();
        listOfP.add(finished);
      }
    }
    listOfP.get(0).print();


    public ParseTreeNode generateParseTree(ContextFreeGrammar cfg, Word w) {
    
      // 1. Initialise neccesary variables
      int n = w.length();
      int exactDervOfWord = (2*n) - 1;
      List<Rule> rules = new ArrayList<Rule>();
      rules = cfg.getRules();
      Variable startVariable = cfg.getStartVariable();
      int numDerivations = 0;
      List<List<Word>> generalList = new ArrayList<List<Word>>();
  
      // 2. Lists for storing derivation
        // make exact number of list for words to be stored at each derivation 
      makeLists(exactDervOfWord, generalList);
  
      List<Derivation> listofDerivations = new ArrayList<Derivation>();
      // 3. Derivation from Start Variable
      for (Rule rule : rules) {
        if (rule.getVariable().equals(startVariable)) {
          generalList.get(numDerivations).add(rule.getExpansion());
          Derivation derivation = new Derivation(rule.getExpansion());
          listofDerivations.add(derivation);
        }
      }
      numDerivations += 1;
      int count = 1;
      List<Derivation> foundDerivation = new ArrayList<Derivation>();
      boolean found = false;
      foundBreak:
        while(found==false){
          for (int j=0; numDerivations <= exactDervOfWord ; j++){
            count = 1;
            for(Step step : listofDerivations.get(j)){
              count += 1;
              for (int i=0; i<step.getWord().length(); i++){
                for (Rule rule : rules){
                  if (count <= exactDervOfWord){
                    if (rule.getVariable().equals(step.getWord().get(i))){
                      Derivation newDerivation = new Derivation(listofDerivations.get(j));
                      newDerivation.addStep(step.getWord().replace(i, rule.getExpansion()), rule, i);
                      listofDerivations.add(newDerivation);
                      if(newDerivation.getLatestWord().equals(w)){
                        found=true;
                        foundDerivation.add(newDerivation);
                        break foundBreak;
                      }
                    }
                  }
                }
              }
              numDerivations = count;
            }
          }
        return null;
      }
  
      List<ParseTreeNode> listOfP = new ArrayList<ParseTreeNode>();
      List<Variable> varR = new ArrayList<Variable>();
      List<Word> expR = new ArrayList<Word>();
      List<Rule> ruleR = new ArrayList<Rule>();
     
      // Add rules to list
      Iterator<Step> splitRule = foundDerivation.get(0).iterator();
      for(int i =0; i<exactDervOfWord; i++){
        Rule derRule = splitRule.next().getRule();
        ruleR.add(derRule);
        if (derRule != null){
          varR.add(derRule.getVariable());
          expR.add(derRule.getExpansion());
        }
      }  
      
  
      int steps = 0;
      int numofDer=0;
  
      for (int i =0; i< exactDervOfWord; i++){
        numofDer += 1;
        if (numofDer < exactDervOfWord){
          steps += 1;
  
          // if is a terminal then parse node for it
          if(expR.get(numofDer-1).isTerminal()){
            ParseTreeNode ndPar = new ParseTreeNode(varR.get(numofDer-1), new ParseTreeNode(expR.get(numofDer-1).get(0)));
            listOfP.add(ndPar);
          }
  
  
  
          if (steps > 2){
            Symbol first = listOfP.get(listOfP.size()-1).getSymbol();
            Symbol second = listOfP.get(listOfP.size()-2).getSymbol();
            if (expR.get(numofDer-1).equals(new Word(first,second))){
              ParseTreeNode par = new ParseTreeNode(varR.get(numofDer-1), listOfP.get(listOfP.size()-1), listOfP.get(listOfP.size()-2));
              listOfP.clear();
              listOfP.add(par);
            }
            steps = 0;
          }
        }
        if (numofDer == exactDervOfWord) {
          ParseTreeNode finished = new ParseTreeNode(startVariable, listOfP.get(listOfP.size()-1), listOfP.get(listOfP.size()-2));
          listOfP.clear();
          listOfP.add(finished);
        }
      }
      return listOfP.get(0);
    }
  
    // Helper Functions for isInLanguage
    private static void makeLists(int derivNum, List<List<Word>> generalList ){
      for (int i=0; i < derivNum; i++) {
        ArrayList<Word> myList = new ArrayList<Word>();
        generalList.add(myList); 
      }
    }


    // CYK 
    List<List<Variable>> listofVars = new ArrayList<List<Variable>>();

    for(int i=0; i<w.length(); i++){
        List<Variable> vars = new ArrayList<>();
        for(Rule rule : rules){
            for(Symbol symp : rule.getExpansion()){
                if (symp.equals(w.get(i))){
                    vars.add(rule.getVariable());
                } 
            }
        }
        if(!vars.isEmpty()){
            listofVars.add(vars);
        }
    }

    List<List<Variable>> test = generateList(listofVars, rules);




    public List<List<Variable>> generateList(List<List<Variable>> listCYK, List<Rule> rules) {
      List<List<Variable>> result = new ArrayList<List<Variable>>();
      int count = 0;
      for(List<Variable> set : listCYK){
          count += 1;
          List<Variable> tempList = new ArrayList<Variable>();
          for(Variable var: set){
              if(count < listCYK.size()){
                  for(Variable var2 : listCYK.get(count)){
                      Word word = new Word(var,var2);
                      for (Rule rule : rules){
                          if(rule.getExpansion().equals(word)){
                              tempList.add(rule.getVariable());
                          }
                      }
                  }
              }
          }
          if(count < listCYK.size()){
              result.add(tempList);
          }
      }
      return result;
  }