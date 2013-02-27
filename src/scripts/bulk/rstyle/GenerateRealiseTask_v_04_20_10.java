package scripts.bulk.rstyle;

import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.builder.TaskBuilder;
import com.trackstudio.constants.CommonConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskBulkProcessor;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.Null;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GenerateRealiseTask_v_04_20_10 implements TaskBulkProcessor {
    private static Log log = LogFactory.getLog(GenerateRealiseTask_v_04_20_10.class);

    @Override
    public SecuredTaskBean execute(SecuredTaskBean task) throws GranException {
        try {
            SessionContext sc = task.getSecure();
            SecuredUserBean root = new SecuredUserBean("1", sc);
            String categoryName = RStyleUtil.getUdfValue(root.getUDFValues(), "category", root);
            String valuesMatches = RStyleUtil.getUdfValue(root.getUDFValues(), "udfs", root);
            String categoryId = CSVImport.findCategoryIdByName(categoryName);
            SecuredTaskTriggerBean triggerBean = build(task, categoryId, gatherUdf(root, task, buildMatches(valuesMatches))).create(false);
            for (SecuredTaskBean child : task.getChildren()) {
                log.error(" create task : #" + child.getNumber());
                AdapterManager.getInstance().getSecuredTaskAdapterManager().pasteTasks(sc, triggerBean.getId(), child.getId(), CommonConstants.COPY_RECURSIVELY);
            }

            boolean deleteOriginTask = "yes".equals(RStyleUtil.getUdfValue(root.getUDFValues(), "delete", root));
            if (deleteOriginTask) {
                TriggerManager.getTask().deleteTask(task.getId(), root.getLogin());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GranException(e);
        }
        return task;
    }

    private Map<String, String> buildMatches(String value) {
        log.error(" udfs: ["+value+"]");
        Map<String, String> map = new LinkedHashMap<String, String>();
        if (Null.isNotNull(value)) {
            for (String line : value.split("\r\n")) {
                String[] pair = line.split("=");
                map.put(pair[0], pair[1]);
            }
        }
        log.error(" map: ["+map+"]");
        return map;
    }

    private HashMap<String, String> gatherUdf(SecuredUserBean root, SecuredTaskBean task, Map<String, String> matches) throws GranException {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, SecuredUDFValueBean> udfs = task.getUDFValues();
        for (SecuredUDFValueBean valueBean : udfs.values()) {
            if (matches.containsKey(valueBean.getCaption())) {
                String udfValue = RStyleUtil.getUdfValue(task.getUDFValues(), valueBean.getCaption(), root);
                if (Null.isNotNull(udfValue)) {
                    values.put(matches.get(valueBean.getCaption()), udfValue);
                }
            }
        }
        return values;
    }

    private SecuredTaskTriggerBean build(SecuredTaskBean task, String categoryId, HashMap<String, String> udfValues) throws GranException {
        TaskBuilder taskBuilder = new TaskBuilder();
        taskBuilder.setSubmitdate(task.getSubmitdate());
        taskBuilder.setUpdatedate(task.getUpdatedate());
        taskBuilder.setSc(task.getSecure());
        taskBuilder.setCategoryId(categoryId);
        taskBuilder.setShortname(task.getShortname());
        taskBuilder.setName(task.getName());
        taskBuilder.setDeadline(task.getDeadline());
        taskBuilder.setParentId(task.getParentId());
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

        taskBuilder.setDescription(task.getDescription());

        taskBuilder.setUdfValues(udfValues);

        return SecuredTaskTriggerBean.build(taskBuilder, TaskBuilder.Action.CREATE);
    }
}
