package scripts.task_custom_field_value;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.TimeZone;

public class ExpireTask implements TaskUDFValueScript {
    private static Log log = LogFactory.getLog(ExpireTask.class);

    @Override
    public Object calculate(SecuredTaskBean task) throws GranException {
        String result = "no";
        Calendar deadline = task.getDeadline();
        if (deadline != null) {
            log.error(" deadline : " + deadline.getTimeZone() + " " + deadline.getTimeInMillis());
            if (deadline.getTimeInMillis() < Calendar.getInstance(TimeZone.getTimeZone(task.getSecure().getUser().getTimezone())).getTimeInMillis()) {
                result = "yes";
            }
        }
        return result;
    }
}
