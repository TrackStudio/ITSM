package scripts.after_edit_task;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;

public class CheckCloseDate implements TaskTrigger {
    //http://localhost:8080/TrackStudio/TaskViewAction.do?method=page&id=4028808a19512fa5011951d9cbdb0070
    @Override
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        AdapterManager.getInstance().getSecuredTaskAdapterManager().updateTask(
                task.getSecure(),
                task.getId(),
                task.getShortname(),
                task.getName(),
                task.getDescription(),
                task.getBudget(),
                task.getDeadline(),
                task.getPriorityId(),
                "4028808a19512fa5011951d9cbdb0070",
                task.getHandlerId(),
                task.getHandlerGroupId(),
                false);
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
