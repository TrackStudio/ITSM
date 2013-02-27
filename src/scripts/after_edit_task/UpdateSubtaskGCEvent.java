package scripts.after_edit_task;

import com.trackstudio.app.google.calendar.CalendarUtil;
import com.trackstudio.app.google.calendar.GCEventImpl;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import scripts.util.GCUtil;

public class UpdateSubtaskGCEvent implements TaskTrigger {

    @Override
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        GCUtil.updateSubtask(task);
        return task;
    }
}
