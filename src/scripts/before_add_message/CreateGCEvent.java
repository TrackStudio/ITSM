package scripts.before_add_message;

import com.trackstudio.app.google.calendar.CalendarUtil;
import com.trackstudio.app.google.calendar.GCEventImpl;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;

import java.util.Calendar;

public class CreateGCEvent implements TaskTrigger {
    @Override
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {

        // I set a tasks name and number. TrackStudio will use it in title on GC - name[#number]
        GCEventImpl.Builder builder = new GCEventImpl.Builder(task.getName(), task.getNumber());

        // Then I set alias, deadline and timezone. It will be used in GC
        // We will use a default calendar. It is located in a properties file
        builder.nameOfUsagesCalendar(null).deadline(task.getDeadline()).timezone("ru");
        builder.context("Some context there");

        GCEventImpl gcEvent = new GCEventImpl(builder);

        // I create a new event in GC
        CalendarUtil.getInstance().createEvent(gcEvent);
        return task;
    }
}
