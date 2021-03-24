import computation.parser.IParser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

import computation.contextfreegrammar.*;
import computation.derivation.Derivation;
import computation.derivation.Step;
import computation.parser.*;
import computation.parsetree.*;

public class CYKparser implements IParser {
    public boolean isInLanguage(ContextFreeGrammar cfg, Word w) {
        List<Rule> rules = new ArrayList<Rule>();
        rules = cfg.getRules();

        List<Variable> vars = new ArrayList<Variable>();
        for(Rule rule : rules){
            if(!vars.contains(rule.getVariable())){
                vars.add(rule.getVariable());
            }
        }
        int n = w.length();
        int r = vars.size();

        boolean[][][] table = new boolean[n][n][r];
        for(int s =0; s<n; s++){
            for(Rule rule : rules){
                if(rule.getExpansion().get(0).equals(w.get(s))){
                    int v = vars.indexOf(rule.getVariable());
                    table[0][s][v] = true;
                }
            }
        }

        for(int l=1; l<n; l++){
            for(int s=0; s<n-l+1;s++){
                for(int p=0; p<l-1;p++){
                    for(Rule rule : rules){
                        if(!rule.getExpansion().isTerminal()){
                            int a = vars.indexOf(rule.getVariable());
                            int b = vars.indexOf(rule.getExpansion().get(0));
                            int c = vars.indexOf(rule.getExpansion().get(1));
                            if (table[p][s][b] && table[l-p][s+p][c]){
                                table[l][s][a] = true;
                            }                                    
                        }
                    }
                }
            }
        }



        return false;
    }
    
    public ParseTreeNode generateParseTree(ContextFreeGrammar cfg, Word w){
        
        return null;
    }

    

}
