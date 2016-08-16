import java.util.ArrayList;

/**
 * Created by Cavi Cardan on 15.08.2016.
 */
public class RuleObject {
    private String ruleName;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    private ArrayList<String> ruleArgs;

    public ArrayList<String> getRuleArgs() {
        return ruleArgs;
    }

    public void setRuleArgs(ArrayList<String> ruleArgs) {
        this.ruleArgs = ruleArgs;
    }
}
