package scripts.multibulk.rstyle;

import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskBulkProcessor;
import com.trackstudio.external.TaskMultiBulkProcessor;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import scripts.bulk.rstyle.RStyleUtil;

import java.util.List;

public class UnitFields implements TaskMultiBulkProcessor {
    @Override
    public void execute(List<SecuredTaskBean> tasks) throws GranException {
        SessionContext sc = tasks.iterator().next().getSecure();
        SecuredUserBean user = sc.getUser();
        for (SecuredTaskBean task : tasks) {
            TriggerManager.getTask().updateTask(
                    task.getId(),
                    SafeString.createSafeString(task.getShortname()),
                    SafeString.createSafeString(task.getName()),
                    SafeString.createSafeString(buildDescription(task, user)),
                    task.getBudget(),
                    task.getDeadline(),
                    task.getPriorityId(),
                    task.getParentId(),
                    task.getHandlerUserId(),
                    task.getHandlerGroupId(),
                    task.getSubmitdate(),
                    task.getUpdatedate()
            );
        }
    }

    private static final String STEP_FOR_REPEAT = "Шаги для воспроизведения";
    private static final String SUSPECT_RESULT = "Ожидаемый результат";
    private static final String FACT_RESULT = "Фактический результат";
    private static final String DESC = "Описание";

    private String buildDescription(SecuredTaskBean taskBean, SecuredUserBean user) throws GranException {
        StringBuilder sb = new StringBuilder();
        sb.append(STEP_FOR_REPEAT).append("</br>");
        sb.append(RStyleUtil.getUdfValue(taskBean.getUDFValues(), STEP_FOR_REPEAT, user)).append("</br>");
        sb.append(SUSPECT_RESULT).append("</br>");
        sb.append(RStyleUtil.getUdfValue(taskBean.getUDFValues(), SUSPECT_RESULT, user)).append("</br>");
        sb.append(FACT_RESULT).append("</br>");
        sb.append(RStyleUtil.getUdfValue(taskBean.getUDFValues(), FACT_RESULT, user)).append("</br>");
        sb.append(DESC).append("</br>");
        sb.append(taskBean.getDescription()).append("</br>");
        return sb.toString();
    }
}
