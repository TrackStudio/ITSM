package scripts.after_edit_task.rstyle;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.builder.TaskBuilder;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.exception.UserExceptionAfterTrigger;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;

public class CreateSystemRequirementEdit_v_10_16_00 implements TaskTrigger {
    public static Log log = LogFactory.getLog(CreateSystemRequirementEdit_v_10_16_00.class);

    @Override
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        String categoryId = CSVImport.findCategoryIdByName("Системное требование");
        String parentId = findParent(task);
        log.error("parent id " + parentId);
        if (parentId != null) {
            HashMap<String, String> udfs = buildUDF(task);
            SecuredTaskTriggerBean child = build(task, categoryId, udfs, parentId).create(false);
            log.error("new create child : " + child.getNumber());
        } else {
            throw new UserExceptionAfterTrigger("Невозможно автоматически создать системное требование!");
        }
        return task;
    }

    private String findParent(SecuredTaskTriggerBean task) throws GranException {
        String nameParent = task.getUdfValue("Подсистема");
        SecuredTaskBean parent = AdapterManager.getInstance().getSecuredFindAdapterManager().searchTaskByQuickGo(task.getSecure(), nameParent);
        if (parent != null) {
            for (SecuredTaskBean child : parent.getChildren()) {
                if ("Текущие задачи".equals(child.getName())) {
                    parent = child;
                    break;
                }
            }
        }
        return parent != null ? parent.getId() : null;
    }

    private HashMap<String, String> buildUDF(SecuredTaskTriggerBean task) throws GranException {
        HashMap<String, String> udfs = new HashMap<String, String>();
        udfs.put("Плановый релиз", task.getUdfValue("Плановый релиз"));
        udfs.put("Уведомить", task.getUdfValue("Уведомить"));
        udfs.put("Связанная задача", task.getUdfValue("Связанная задача"));
        return udfs;
    }

    private SecuredTaskTriggerBean build(SecuredTaskBean task, String categoryId, HashMap<String, String> udfValues, String parentId) throws GranException {
        TaskBuilder taskBuilder = new TaskBuilder();
        taskBuilder.setSubmitdate(task.getSubmitdate());
        taskBuilder.setUpdatedate(task.getUpdatedate());
        taskBuilder.setSc(task.getSecure());
        taskBuilder.setCategoryId(categoryId);
        taskBuilder.setShortname(task.getShortname());
        taskBuilder.setName(task.getName());
        taskBuilder.setDeadline(task.getDeadline());
        taskBuilder.setParentId(parentId);
        taskBuilder.setPriorityId(task.getPriorityId());
        taskBuilder.setHandlerUserId(task.getHandlerUserId());
        taskBuilder.setHandlerGroupId(task.getHandlerGroupId());
        taskBuilder.setResolutionId(task.getResolutionId());
        taskBuilder.setAbudget(task.getAbudget());
        taskBuilder.setBudget(task.getBudget());
        taskBuilder.setStatusId(task.getStatusId());
        taskBuilder.setSubmitterId(task.getSubmitterId());
        taskBuilder.setClosedate(task.getClosedate());
        taskBuilder.setNeedSend(false);
        taskBuilder.setCopyOrMoveOpr(false);

        taskBuilder.setUdfValues(udfValues);

        return SecuredTaskTriggerBean.build(taskBuilder, TaskBuilder.Action.CREATE);
    }
}
