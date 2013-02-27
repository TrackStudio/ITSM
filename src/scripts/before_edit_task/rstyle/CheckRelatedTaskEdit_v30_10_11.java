package scripts.before_edit_task.rstyle;

import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Null;
import com.trackstudio.view.UDFValueViewText;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import scripts.bulk.rstyle.RStyleUtil;

import java.util.*;

public class CheckRelatedTaskEdit_v30_10_11 implements TaskTrigger {
    public static Log log = LogFactory.getLog(CheckRelatedTaskEdit_v30_10_11.class);

    @Override
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        log.error(this.getClass().getName() + " v26.11.2012 17:52");
        String oldValue = RStyleUtil.getUdfValue(new SecuredTaskBean(task.getId(), task.getSecure()).getUDFValues(), "Плановый релиз", task.getSecure().getUser());
        String newValue = task.getUdfValue("Плановый релиз");
        boolean result = checkChangeUDF(oldValue, newValue);
        log.error("Compare fields : " + oldValue + " : " + newValue + " result : " + result);
        if (result) {
            Set<String> rTasks = new TreeSet<String>();
            boolean bindingResult = buildRelatedTasks(task, rTasks);
            log.error(" binding result : " + bindingResult + " size : " + rTasks.size());
            if (bindingResult) {
                for (String rTaskId : rTasks) {
                    SecuredTaskBean taskBean = new SecuredTaskBean(rTaskId, task.getSecure());
                    HashMap<String, String> udfs = getUdfValue(taskBean.getUDFValues(), task.getSecure());
                    udfs.put("Плановый релиз", newValue);
                    update(taskBean, udfs);
                    log.error("Set field in task id : " + rTaskId);
                }
            }
        }
        return task;
    }

    private boolean checkChangeUDF(String oldValue, String newValue) {
        return (oldValue == null && newValue != null) || (oldValue != null && !oldValue.equals(newValue));
    }

    private void update(SecuredTaskBean task, HashMap<String, String> udfs) throws GranException {
        TriggerManager.getInstance().updateTask(
                task.getSecure(),
                task.getId(),
                task.getShortname(),
                task.getName(),
                task.getDescription(),
                task.getBudget(),
                task.getDeadline(),
                task.getPriorityId(),
                task.getParentId(),
                task.getHandlerUserId(),
                task.getHandlerGroupId(),
                false,
                udfs,
                true
        );
    }

    public static HashMap<String, String> getUdfValue(Map<String, SecuredUDFValueBean> udfs, SessionContext sc) throws GranException {
        HashMap<String, String> textUdfs = new HashMap<String, String>();
        for (String udfId : udfs.keySet()) {
            SecuredUDFValueBean udf = udfs.get(udfId);
            textUdfs.put(udf.getCaption(), new UDFValueViewText(udf).getValue(sc.getUser()));
        }
        return textUdfs;
    }

    private boolean buildRelatedTasks(SecuredTaskTriggerBean task, final Set<String> rTasks) throws GranException {
        boolean result = false;
        String tasks = task.getUdfValue("Связанная задача");
        log.error(" tasks fields : " + tasks);
        if (Null.isNotNull(tasks)) {
            for (String number : tasks.split(";")) {
                String rTaskId = KernelManager.getTask().findByNumber(number);
                boolean canEdit = AdapterManager.getInstance().getSecuredTaskAdapterManager().isTaskEditable(task.getSecure(), rTaskId);
                log.error(" Can edit task : " + number + " result : " + canEdit);
                if (canEdit) {
                    SecuredTaskBean taskBean = new SecuredTaskBean(rTaskId, task.getSecure());
                    log.error(" find binding task : " + taskBean.getNumber() + " : " + taskBean.getCategory().getName());
                    if ("Системное требование".equals(taskBean.getCategory().getName().trim()) && checkTotalBindingFirstRealise(taskBean, task.getId())) {
                        rTasks.add(rTaskId);
                        result = true;
                    }
                } else {
                    throw new UserException(task.getSecure().getUser().getLogin() + " не имеет доступа к связанным задачам! : " + number, false);
                }
            }
        }
        return result;
    }

    private Set<String> getBidingTasks(SecuredTaskBean taskBean) throws GranException {
        Set<String> bidingTasks = new TreeSet<String>();
        EggBasket<String, String> rtlist = KernelManager.getIndex().getReferencedTasksForTask(taskBean.getId());
        for (Map.Entry<String, List<String>> entry : rtlist.entrySet()) {
            bidingTasks.addAll(entry.getValue());
        }
        String tasks = RStyleUtil.getUdfValue(taskBean.getUDFValues(), "Связанная задача", taskBean.getSecure().getUser());
        if (Null.isNotNull(tasks)) {
            for (String number : tasks.split(";") ) {
                String rTaskId = KernelManager.getTask().findByNumber(number);
                bidingTasks.add(rTaskId);
            }
        }
        return bidingTasks;
    }

    private boolean checkTotalBindingFirstRealise(SecuredTaskBean taskBean, String firstRealiseId) throws GranException {
        Set<String> bindTasks = new TreeSet<String>();
        for (String rTaskId :  getBidingTasks(taskBean)) {
            SecuredTaskBean bindTask = new SecuredTaskBean(rTaskId, taskBean.getSecure());
            log.error(" check binding tasks : " + bindTask.getNumber() + " : " + bindTask.getCategory().getName() + " : " + firstRealiseId);
            if ("Первичное требование".equals(bindTask.getCategory().getName().trim())) {
                bindTasks.add(bindTask.getId());
            }
        }
        boolean result = bindTasks.isEmpty() || (bindTasks.size() == 1 && bindTasks.contains(firstRealiseId));
        log.error(" tasks size : " + bindTasks + " : " +result);
        return result;
    }
}