import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import computation.contextfreegrammar.*;
import computation.derivation.Derivation;
import computation.derivation.Step;
import computation.parser.*;
import computation.parsetree.*;

public class Parser implements IParser {
  public boolean isInLanguage(ContextFreeGrammar cfg, Word w){
    //0. Clean inputs
    if (w.length() < 1){
      return false;
    }
    if (!cfg.isInChomskyNormalForm()){
      return false;
    }

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

    // 3. Derivation from Start Variable
      for (Rule rule : rules) {
        if (rule.getVariable().equals(startVariable)) {
          generalList.get(numDerivations).add(rule.getExpansion());
        }
    }
    numDerivations += 1;

    // 4. Derivation following Start Variable
      // Generate list of words for each dervation step 
    while (numDerivations < exactDervOfWord){
      // loop over every list of derivated words 
      for (Word word : generalList.get(numDerivations-1) ){
        // loop over every Variable in the word
        for (int i = 0; i < word.length(); i++) {
          // make a temporary list of words with Variables. Words with terminals rejected.
          if (!word.get(i).isTerminal()){
            List<Word> tempListOfDerv = new ArrayList<Word>(); 
            for (Rule rule : rules) {
              if (rule.getVariable().equals(word.get(i))) {
                tempListOfDerv.add(rule.getExpansion());
              }
            }
            // Expand Variables in the temp list and store it in the general list 
            for (Word derivWord : tempListOfDerv ){
              generalList.get(numDerivations).add(word.replace(i, derivWord));
            }
          }
        }
      }
      numDerivations += 1;
    }

    // 5. Check if the word is in the final derivation list
    boolean inLanguage = false;
    for (Word word : generalList.get(numDerivations-1)) {
      if (word.equals(w)){
         inLanguage = true;
     }
    }
    return inLanguage;  
  }
  
  public ParseTreeNode generateParseTree(ContextFreeGrammar cfg, Word w) {
    
    // 1. Initialise neccesary variables
    int n = w.length();
    int exactDervOfWord = (2*n) - 1;
    List<Rule> rules = new ArrayList<Rule>();
    rules = cfg.getRules();
    Variable startVariable = cfg.getStartVariable();
    int numDerivations = 0;

    // 2. Lists for storing derivation
      // make exact number of list for words to be stored at each derivation 
    List<Derivation> listofDerivations = new ArrayList<Derivation>();

    // 3. Derivation from Start Variable
    for (Rule rule : rules) {
      if (rule.getVariable().equals(startVariable)) {
        Derivation derivation = new Derivation(rule.getExpansion());
        listofDerivations.add(derivation);
      }
    }
    numDerivations += 1;
    int count = 1;

    // Finds the derivation sequence. If not in language it returns null
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

    // Create parse tree from derivation sequence
    List<ParseTreeNode> listOfNodes = new ArrayList<ParseTreeNode>();
    List<Variable> listOfVarW = new ArrayList<Variable>();
    List<Word> listOfExpInW = new ArrayList<Word>();
    List<Rule> listOfRuleInW = new ArrayList<Rule>();
   
    // Add rules to list
    Iterator<Step> splitRule = foundDerivation.get(0).iterator();
    for(int i =0; i<exactDervOfWord; i++){
      Rule eachRuleInDerOfW = splitRule.next().getRule();
      listOfRuleInW.add(eachRuleInDerOfW);
      if (eachRuleInDerOfW != null){
        listOfVarW.add(eachRuleInDerOfW.getVariable());
        listOfExpInW.add(eachRuleInDerOfW.getExpansion());
      }
    }  
    

    int steps = 0;
    int numofDerInW=0;

    for (int i =0; i< exactDervOfWord; i++){
      numofDerInW += 1;
      if (numofDerInW < exactDervOfWord){
        steps += 1;
        // if is a terminal then parse node for it
        if(listOfExpInW.get(numofDerInW-1).isTerminal()){
          ParseTreeNode ndPar = new ParseTreeNode(listOfVarW.get(numofDerInW-1), new ParseTreeNode(listOfExpInW.get(numofDerInW-1).get(0)));
          listOfNodes.add(ndPar);
        }
        if (steps > 2){
          Symbol first = listOfNodes.get(listOfNodes.size()-1).getSymbol();
          Symbol second = listOfNodes.get(listOfNodes.size()-2).getSymbol();
          if (listOfExpInW.get(numofDerInW-1).equals(new Word(first,second))){
            ParseTreeNode par = new ParseTreeNode(listOfVarW.get(numofDerInW-1), listOfNodes.get(listOfNodes.size()-1), listOfNodes.get(listOfNodes.size()-2));
            listOfNodes.clear();
            listOfNodes.add(par);
          }
          steps = 0;
        }
      }
      if (numofDerInW == exactDervOfWord) {
        ParseTreeNode finished = new ParseTreeNode(startVariable, listOfNodes.get(listOfNodes.size()-1), listOfNodes.get(listOfNodes.size()-2));
        listOfNodes.clear();
        listOfNodes.add(finished);
      }
    }
    return listOfNodes.get(0);
  }

  // Helper Functions for isInLanguage
  private static void makeLists(int derivNum, List<List<Word>> generalList ){
    for (int i=0; i < derivNum; i++) {
      ArrayList<Word> myList = new ArrayList<Word>();
      generalList.add(myList); 
    }
  } 
}