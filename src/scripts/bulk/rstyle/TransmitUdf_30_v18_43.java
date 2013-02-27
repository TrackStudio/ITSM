package scripts.bulk.rstyle;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskBulkProcessor;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TransmitUdf_30_v18_43 implements TaskBulkProcessor {
    private static final Log log = LogFactory.getLog(TransmitUdf_30_v18_43.class);

    @Override
    public SecuredTaskBean execute(SecuredTaskBean task) throws GranException {
        String fromUdf = null, toUdf = null, value = null, newValue = null;
        try {
            SessionContext sc = task.getSecure();
            SecuredUserBean root = new SecuredUserBean("1", sc);
            fromUdf = RStyleUtil.getUdfValue(root.getUDFValues(), "from", root);
            toUdf = RStyleUtil.getUdfValue(root.getUDFValues(), "to", root);
            value = RStyleUtil.getUdfValue(task.getUDFValues(), toUdf, task.getSecure().getUser());
            newValue = RStyleUtil.getUdfValue(task.getUDFValues(), fromUdf, task.getSecure().getUser());
            log.error(" toUdf="+toUdf+", value="+value+", newValue="+newValue+", fromUdf="+fromUdf);
            if (value == null || value.isEmpty()) {
                AdapterManager.getInstance().getSecuredUDFAdapterManager().setTaskUDFValueSimple(
                        sc,
                        task.getId(),
                        toUdf,
                        RStyleUtil.getUdfValue(task.getUDFValues(), fromUdf, task.getSecure().getUser())
                );
                TaskRelatedManager.getInstance().invalidateTask(task.getId());
            }
        } catch (Exception e) {
            log.error(" toUdf="+toUdf+", value="+value+", newValue="+newValue+", fromUdf="+fromUdf, e);
            throw new GranException(e);
        }
        return task;
    }
}
