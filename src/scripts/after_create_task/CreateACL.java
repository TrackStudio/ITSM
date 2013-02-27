package scripts.after_create_task;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import scripts.condition.rule.ConditionRuleUtil;

public class CreateACL implements TaskTrigger {

    @Override
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        ConditionRuleUtil.createACLs(task.getUdfValuesList());
        return task;
    }
}