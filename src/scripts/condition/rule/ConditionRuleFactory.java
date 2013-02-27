package scripts.condition.rule;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredUDFValueBean;

import java.util.ArrayList;
import java.util.List;

public class ConditionRuleFactory {

    private static final List<ConditionRule> RULES = new ArrayList<ConditionRule>();
    static {
        RULES.add(new FilialRuleImpl());
    }

    public static List<ConditionRule> buildConditionRules(List<SecuredUDFValueBean> udfs) throws GranException {
        List<ConditionRule> appropriateRules = new ArrayList<ConditionRule>();
        for (ConditionRule rule : RULES) {
            if (rule.init(udfs)) {
                appropriateRules.add(rule);
            }
        }
        return appropriateRules;
    }
}
