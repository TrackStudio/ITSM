package scripts.instead_of_create_task.emelin;

import com.trackstudio.app.TriggerManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;

public class CashMachineCreation implements TaskTrigger {
    @Override
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        String errorCashMachine = getKey(task.getDescription());
        SecuredTaskBean appropriateTask = findAppropriateTask(errorCashMachine);
        if (appropriateTask == null) {
            task.create(true);
        } else {
            String mstatusId = null;  // This is a type of operation
            TriggerManager.getMessage().createMessage(task.getSecure().getUserId(),
                    appropriateTask.getId(),
                    mstatusId,
                    SafeString.createSafeString(task.getDescription()),
                    task.getAbudget(),
                    task.getHandlerUserId(),
                    task.getHandlerGroupId(),
                    task.getResolutionId(),
                    task.getPriorityId(),
                    task.getDeadline(),
                    task.getBudget(),
                    task.getSubmitdate());
        }
        return task;
    }

    /**
     * This method finds appropriate task by cash machine
     * @param errorCashMachine error of cash machine
     * @return task or null
     */
    private SecuredTaskBean findAppropriateTask(String errorCashMachine) {
        return null;
    }

    /**
     * This method cuts error of cash machine from description
     * @param description description
     * @return key
     */
    private String getKey(String description) {
        return null;
    }
}
