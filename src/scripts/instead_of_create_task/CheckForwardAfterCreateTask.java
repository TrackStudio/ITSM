package scripts.instead_of_create_task;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;

public class CheckForwardAfterCreateTask implements TaskTrigger {
    @Override
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        return task.create();
    }
}
