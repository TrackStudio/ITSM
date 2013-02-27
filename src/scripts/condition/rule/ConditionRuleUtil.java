package scripts.condition.rule;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredUDFValueBean;

import java.util.List;

public class ConditionRuleUtil {

    public static void createACLs(List<SecuredUDFValueBean> udfs) throws GranException {
        List<ConditionRule> rules = ConditionRuleFactory.buildConditionRules(udfs);
        for (ConditionRule rule : rules) {
            createACL(rule);
        }
    }

    private static void createACL(ConditionRule rule) throws GranException {
        String aclId = KernelManager.getAcl().createAcl(rule.getTaskId(), null, rule.getUserId(), rule.getPrstatusId(), rule.getOwnerId());
        KernelManager.getAcl().updateAcl(aclId, rule.getOverridePrstatusId(), rule.isOverride());
    }
}
