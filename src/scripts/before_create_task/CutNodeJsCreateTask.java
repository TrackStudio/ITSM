package scripts.before_create_task;

import com.trackstudio.app.adapter.macros.ConvertURL;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;

public class CutNodeJsCreateTask implements TaskTrigger {
    @Override
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        task.setDescription(new ConvertURL().convertNodeFromTree(task.getDescription()));
        return task;
    }
}
