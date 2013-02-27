package scripts.before_edit_task;

import java.util.ArrayList;
import java.util.List;

import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import scripts.CommonSubscriber;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.secured.SecuredUserBean;


public class SubscribeAfterEdit extends CommonSubscriber implements TaskTrigger {
    private static Log log = LogFactory.getLog(SubscribeAfterEdit.class);

    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean securedTaskTriggerBean) throws GranException {
        log.error("Start script.");
        ArrayList<SecuredUserBean> users = AdapterManager.getInstance().getSecuredAclAdapterManager().getUserList(securedTaskTriggerBean.getSecure(), securedTaskTriggerBean.getId());
        List<String> toRemove = new ArrayList<String>();
        for (SecuredUserBean f : users) {
            toRemove.add(f.getId());
        }
        log.error("unsubscribe");
        unsubscribe(securedTaskTriggerBean.getId(), toRemove);
//SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(securedTaskTriggerBean.getSecure(), FELLOWS_UDFID);

        String fellowsUDF = securedTaskTriggerBean.getUdfValue(FELLOWS_UDF);
        log.error("fellowsUDF : " + fellowsUDF);
        if (fellowsUDF != null) {
            String[] value = fellowsUDF.split(";");
            if (value!=null && value.length>0)
                for (String login : value) {
                    if (login.length()>0){
                        login = login.startsWith("@") ? login.substring(1) : login;
                        String userId = KernelManager.getUser().findByLogin(login);
                        log.error("Find user :" + (userId!=null));
                        if (userId != null) {
                            UserRelatedInfo user = UserRelatedManager.getInstance().find(userId);
                            subscribe(user, securedTaskTriggerBean.getId());
                            log.error("Add notification for :" + user.getLogin());
                        }
                    }
                }
        }
        log.error("Script finished.");
        return securedTaskTriggerBean;
    }
}
