package scripts.task_custom_field_value;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DurationDeadline implements TaskUDFValueScript {
    @Override
    public Object calculate(SecuredTaskBean task) throws GranException {
        Calendar deadline = task.getDeadline();
        String duration = null;
        if (deadline != null) {
            long millis = Math.abs(Calendar.getInstance(TimeZone.getTimeZone(task.getSecure().getUser().getTimezone())).getTimeInMillis() - deadline.getTimeInMillis());
            duration = String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            );
        }
        return duration;
    }
}
