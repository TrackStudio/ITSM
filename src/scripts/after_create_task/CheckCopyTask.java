package scripts.after_create_task;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.constants.CommonConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;

public class CheckCopyTask implements TaskTrigger {
    @Override
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        AdapterManager.getInstance().getSecuredTaskAdapterManager().pasteTasks(
                task.getSecure(),
                task.getId(),
                task.getId(),
                CommonConstants.COPY);
        return null;
    }
}
