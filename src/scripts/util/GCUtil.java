package scripts.util;

import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.google.calendar.CalendarUtil;
import com.trackstudio.app.google.calendar.GCEventImpl;
import com.trackstudio.builder.TaskBuilder;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import java.util.Calendar;

public class GCUtil {
    private static SecuredTaskBean findSubtask(SecuredTaskBean task) throws GranException {
        SecuredTaskBean originTask = null;
        String motivation = (String) task.getUdfValueByName("Motivo Evento");
        for (SecuredTaskBean taskBean : task.getChildren()) {
            if (taskBean.getName().equals(motivation)) {
                originTask = taskBean;
                break;
            }
        }
        return originTask;
    }

    private static SecuredTaskTriggerBean buildSubtask(SecuredTaskBean task, SecuredTaskBean originTask) throws GranException {
        Calendar deadline = (Calendar) task.getUdfValueByName("Fecha Evento");
        String motivation = (String) task.getUdfValueByName("Motivo Evento");

        TaskBuilder taskBuilder = new TaskBuilder();
        taskBuilder.setId(originTask != null ? originTask.getId() : null);
        taskBuilder.setUpdatedate(Calendar.getInstance());
        taskBuilder.setSc(task.getSecure());
        taskBuilder.setCategoryId("2c9181e83a992975013a9a070cd70025");
        taskBuilder.setShortname(task.getShortname());
        taskBuilder.setName(motivation);
        taskBuilder.setDeadline(deadline);
        taskBuilder.setParentId(task.getId());
        taskBuilder.setNeedSend(false);
        taskBuilder.setCopyOrMoveOpr(false);
        return SecuredTaskTriggerBean.build(taskBuilder, TaskBuilder.Action.CREATE);
    }

    public static String createSubtask(SecuredTaskTriggerBean task) throws GranException {
        return TriggerManager.getInstance().createTask(buildSubtask(task, null));
    }

    public static void updateSubtask(SecuredTaskBean task) throws GranException {
        SecuredTaskBean originTask = findSubtask(task);
        buildSubtask(task, originTask).update(true);
        GCEventImpl gcEvent = GCUtil.buildEvent(originTask);
        CalendarUtil.getInstance().updateEvent(gcEvent);
    }

    public static GCEventImpl buildEvent(SecuredTaskBean task) throws GranException {
        // I set a tasks name and number. TrackStudio will use it in title on GC - name[#number]
        GCEventImpl.Builder builder = new GCEventImpl.Builder(task.getName(), task.getNumber());

        // Then I set alias, deadline and timezone. It will be used in GC
        // We will use a default calendar. It is located in a properties file
        builder.nameOfUsagesCalendar(null).deadline(task.getDeadline()).timezone("ru");
        builder.context("Some context there");

        return new GCEventImpl(builder);
    }
}
