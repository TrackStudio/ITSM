package scripts.bulk.rstyle;

import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskBulkProcessor;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;

public class UnitFields implements TaskBulkProcessor {
    @Override
    public SecuredTaskBean execute(SecuredTaskBean task) throws GranException {
        SessionContext sc = task.getSecure();
        SecuredUserBean root = new SecuredUserBean("1", sc);

        TriggerManager.getTask().updateTask(
                task.getId(),
                SafeString.createSafeString(task.getShortname()),
                SafeString.createSafeString(task.getName()),
                SafeString.createSafeString(buildDescription(task, root)),
                task.getBudget(),
                task.getDeadline(),
                task.getPriorityId(),
                task.getParentId(),
                task.getHandlerUserId(),
                task.getHandlerGroupId(),
                task.getSubmitdate(),
                task.getUpdatedate()
        );
        return task;
    }

    private static final String STEP_FOR_REPEAT = "Шаги для воспроизведения";
    private static final String SUSPECT_RESULT = "Ожидаемый результат";
    private static final String FACT_RESULT = "Фактический результат";
    private static final String DESC = "Описание";

    private String buildDescription(SecuredTaskBean taskBean, SecuredUserBean root) throws GranException {
        StringBuilder sb = new StringBuilder();
        sb.append(STEP_FOR_REPEAT).append("</br>");
        sb.append(RStyleUtil.getUdfValue(taskBean.getUDFValues(), STEP_FOR_REPEAT, root)).append("</br>");
        sb.append(SUSPECT_RESULT).append("</br>");
        sb.append(RStyleUtil.getUdfValue(taskBean.getUDFValues(), SUSPECT_RESULT, root)).append("</br>");
        sb.append(FACT_RESULT).append("</br>");
        sb.append(RStyleUtil.getUdfValue(taskBean.getUDFValues(), FACT_RESULT, root)).append("</br>");
        sb.append(DESC).append("</br>");
        sb.append(taskBean.getDescription()).append("</br>");
        return sb.toString();
    }
}
