package scripts.bulk.rstyle;

import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.view.UDFValueViewText;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RStyleUtil {
    private static Log log = LogFactory.getLog(RStyleUtil.class);

    public static String getUdfValue(Map<String, SecuredUDFValueBean> udfs, String udfCaption, SecuredUserBean userBean) throws GranException {
        String value = null;
        udfCaption = udfCaption.trim();
        log.error("Try to find : [" + udfCaption + "]");
        for (String udfId : udfs.keySet()) {
            SecuredUDFValueBean udf = udfs.get(udfId);
            log.error("caption : [" + udf.getCaption() + "]");
            if (udfCaption.equals(udf.getCaption())) {
                value = new UDFValueViewText(udf).getValue(userBean);
            }
        }
        return value;
    }

    public interface Action {
        void make(SecuredTaskBean task, HashMap<String, SecuredUDFValueBean> udfs) throws GranException;
    }

    public static Action buildAction(String actionName) {
        Action action = new EmptyActionImpl();
        if ("назначение ответственного".equals(actionName)) {
            action = new HandlerActionImpl();
        } else if ("ввод одинакового комментария".equals(actionName)) {
            action = new EnterOperationActionImpl();
        } else if ("ввод одинакового значения в поле «Плановый релиз»".equals(actionName)) {
            action = new EnterUDFActionImpl();
        } else if ("Изменить состояние задач".equals(actionName)) {
            action = new ChangeStateActionImpl();
        }
        return action;
    }

    public static class HandlerActionImpl implements Action {
        @Override
        public void make(SecuredTaskBean task, HashMap<String, SecuredUDFValueBean> udfs) throws GranException {
            String userId = CSVImport.findUserIdByLogin(RStyleUtil.getUdfValue(udfs, "handler", task.getSecure().getUser()));
            TriggerManager.getTask().updateTask(
                    task.getId(),
                    SafeString.createSafeString(task.getShortname()),
                    SafeString.createSafeString(task.getName()),
                    SafeString.createSafeString(task.getDescription()),
                    task.getBudget(),
                    task.getDeadline(),
                    task.getPriorityId(),
                    task.getParentId(),
                    userId,
                    null,
                    task.getSubmitdate(),
                    task.getUpdatedate()
            );
        }
    }

    public static class EnterOperationActionImpl implements Action {
        @Override
        public void make(SecuredTaskBean task, HashMap<String, SecuredUDFValueBean> udfs) throws GranException {
            String mstatusId = CSVImport.findMessageTypeIdByName(RStyleUtil.getUdfValue(udfs, "mstatus", task.getSecure().getUser()), task.getCategory().getName());
            String text = RStyleUtil.getUdfValue(udfs, "text", task.getSecure().getUser());

            KernelManager.getMessage().createMessage(task.getSecure().getUser().getId(),
                    task.getId(),
                    mstatusId,
                    SafeString.createSafeString(text),
                    0L,
                    null,
                    null,
                    null,
                    task.getPriorityId(),
                    null,
                    null,
                    Calendar.getInstance());
        }
    }

    public static class EnterUDFActionImpl implements Action {
        @Override
        public void make(SecuredTaskBean task, HashMap<String, SecuredUDFValueBean> udfs) throws GranException {
            String toUdf = RStyleUtil.getUdfValue(udfs, "to", task.getSecure().getUser());
            log.error(" toUdf : " + toUdf);
            String value = RStyleUtil.getUdfValue(udfs, "udf_value", task.getSecure().getUser());
            log.error(" value : " + value);
            AdapterManager.getInstance().getSecuredUDFAdapterManager().setTaskUDFValueSimple(
                    task.getSecure(),
                    task.getId(),
                    toUdf,
                    value
            );
        }
    }

    public static class ChangeStateActionImpl implements Action {
        @Override
        public void make(SecuredTaskBean task, HashMap<String, SecuredUDFValueBean> udfs) throws GranException {
            String statusId = CSVImport.findStateIdByName(RStyleUtil.getUdfValue(udfs, "status", task.getSecure().getUser()), task.getCategory().getName());
            if (statusId != null) {
                KernelManager.getTask().updateTaskStatus(task.getId(), statusId);
            } else {
                throw new UserException("Статус не найден!");
            }
        }
    }

    public static class EmptyActionImpl implements Action {
        @Override
        public void make(SecuredTaskBean task, HashMap<String, SecuredUDFValueBean> udfs) throws GranException {
            throw new UserException("Unsupported action!");
        }
    }

}
