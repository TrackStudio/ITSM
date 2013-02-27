package scripts.multibulk.rstyle;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskBulkProcessor;
import com.trackstudio.external.TaskMultiBulkProcessor;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import scripts.bulk.rstyle.RStyleUtil;

import java.util.List;

public class TransmitUdf_30_v18_43 implements TaskMultiBulkProcessor {
    private static final Log log = LogFactory.getLog(TransmitUdf_30_v18_43.class);

    @Override
    public void execute(List<SecuredTaskBean> tasks) throws GranException {
        if (!tasks.isEmpty()) {
            String fromUdf = null, toUdf = null, value = null, newValue = null;
            try {
                SessionContext sc = tasks.iterator().next().getSecure();
                SecuredUserBean user = sc.getUser();
                fromUdf = RStyleUtil.getUdfValue(user.getUDFValues(), "from", user);
                toUdf = RStyleUtil.getUdfValue(user.getUDFValues(), "to", user);
                for (SecuredTaskBean task : tasks) {
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
                }
            } catch (Exception e) {
                log.error(" toUdf="+toUdf+", value="+value+", newValue="+newValue+", fromUdf="+fromUdf, e);
                throw new GranException(e);
            }
        }
    }
}
