package scripts.after_create_task;

import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.google.calendar.CalendarUtil;
import com.trackstudio.app.google.calendar.GCEventImpl;
import com.trackstudio.builder.TaskBuilder;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.tools.Null;
import scripts.util.GCUtil;

import java.util.Calendar;

public class CreateSubtaskGCEvent implements TaskTrigger {
    @Override
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        String subtaskId = GCUtil.createSubtask(task);
        GCEventImpl gcEvent = GCUtil.buildEvent(new SecuredTaskBean(subtaskId, task.getSecure()));
        CalendarUtil.getInstance().createEvent(gcEvent);
        return task;
    }
}
