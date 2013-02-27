package scripts.before_edit_task;

import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;

import com.trackstudio.exception.GranException;
import scripts.CommonScrum;

public class SetSprint extends CommonScrum implements TaskTrigger{
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean securedTaskTriggerBean) throws GranException {
                String link = securedTaskTriggerBean.getUdfValue("������");

        if (link == null || link.length() <= 0) {

            if (securedTaskTriggerBean.getParent().getCategoryId().equals(SCRUM_SRINT_CATEGORY)){
                       securedTaskTriggerBean.setUdfValue("������", (securedTaskTriggerBean.getParent().getName()+" [#"+securedTaskTriggerBean.getParent().getNumber()+"]"));
                   }
        }
        return securedTaskTriggerBean;
}
}