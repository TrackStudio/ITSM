package scripts.before_edit_task.rstyle;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;

public class CommonCheckEdit_v_10_17_46 implements TaskTrigger {
    @Override
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        new CheckPlainRealiseEdit_v10_17_26().execute(task);
        new CheckRelatedTaskEdit_v30_10_11().execute(task);
        return task;
    }
}
