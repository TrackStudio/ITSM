package scripts.after_create_task.rstyle;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.builder.TaskBuilder;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserExceptionAfterTrigger;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;

public class CreateSystemRequirementCreate_10_v_16_48 implements TaskTrigger {
    public static Log log = LogFactory.getLog(CreateSystemRequirementCreate_10_v_16_48.class);

    @Override
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        String categoryId = CSVImport.findCategoryIdByName("Системное требование");
        String parentId = findParent(task);
        log.error("parent id " + parentId);
        HashMap<String, String> udfs = buildUDF(task);
        SecuredTaskTriggerBean child = build(task, categoryId, udfs, parentId).create(false);
        log.error("new create child : " + child.getNumber());
        AdapterManager.getInstance().getSecuredUDFAdapterManager().setTaskUDFValueSimple(
                task.getSecure(),
                task.getId(),
                "Связанная задача",
                task.getUdfValue("Связанная задача")+";"+child.getNumber()
        );
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
        return parent != null ? parent.getId() : task.getId();
    }

    private HashMap<String, String> buildUDF(SecuredTaskTriggerBean task) throws GranException {
        HashMap<String, String> udfs = new HashMap<String, String>();
        udfs.putAll(task.getUdfValues());
        udfs.put("Связанная задача", task.getNumber());
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