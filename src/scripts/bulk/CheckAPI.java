package scripts.bulk;

import com.trackstudio.app.UdfValue;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskBulkProcessor;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.secured.SecuredTaskBean;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CheckAPI implements TaskBulkProcessor {
    @Override
    public SecuredTaskBean execute(SecuredTaskBean task) throws GranException {
        TaskRelatedInfo taskInfo = TaskRelatedManager.getInstance().find(task.getId());

        List<UdfValue> udfs = taskInfo.getUDFValues();

        UdfValue ticker = null;

        for (UdfValue udf : udfs) {
            if ( udf.getCaption().equals("Бюджетный тикер")) {
                ticker = udf;
                break;
            }
        }



        if (ticker != null) {
            List<SecuredTaskBean> children = task.getChildren();
            for (SecuredTaskBean bean : children) {

                KernelManager.getUdf().setTaskUdfValue(
                        ticker.getUdfId(),
                        bean.getId(),
                        SafeString.createSafeString(ticker.getValue(null).toString()),
                        Locale.getDefault().toString(),
                        TimeZone.getDefault().toString()
                );
            }
        }
        return task;
    }
}
