package scripts.bulk.rstyle;

import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.TaskBulkProcessor;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import java.util.Calendar;

public class SetFields implements TaskBulkProcessor {
    private static final Log log = LogFactory.getLog(SetFields.class);

    @Override
    public SecuredTaskBean execute(SecuredTaskBean task) throws GranException {
        log.error(" Start action : " + SetFields.class.getName());
        SessionContext sc = task.getSecure();
        SecuredUserBean root = new SecuredUserBean("1", sc);

        String actionName = RStyleUtil.getUdfValue(root.getUDFValues(), "action", root);
        log.error(" actionName : " + actionName);
        RStyleUtil.Action action = RStyleUtil.buildAction(actionName);
        log.error(" action : " + action.getClass().getName());
        action.make(task, root.getUDFValues());
        return task;
    }
}
