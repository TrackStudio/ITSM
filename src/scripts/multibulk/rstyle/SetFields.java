package scripts.multibulk.rstyle;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskBulkProcessor;
import com.trackstudio.external.TaskMultiBulkProcessor;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import scripts.bulk.rstyle.RStyleUtil;

import java.util.HashMap;
import java.util.List;

//import java.util.Calendar;

public class SetFields implements TaskMultiBulkProcessor {
    private static final Log log = LogFactory.getLog(SetFields.class);

    @Override
    public void execute(List<SecuredTaskBean> tasks) throws GranException {
        log.error(" Start action : " + SetFields.class.getName());
        if (!tasks.isEmpty()) {
            SessionContext sc = tasks.iterator().next().getSecure();
            SecuredUserBean user = sc.getUser();
            String actionName = RStyleUtil.getUdfValue(user.getUDFValues(), "action", user);

            log.error(" actionName : " + actionName);
            RStyleUtil.Action action = RStyleUtil.buildAction(actionName);
            log.error(" action : " + action.getClass().getName());
            HashMap<String, SecuredUDFValueBean> udfs = new HashMap<String, SecuredUDFValueBean>(user.getUDFValues());
            for (SecuredTaskBean task : tasks) {
                action.make(task, udfs);
            }
        }
    }
}

