package scripts.before_add_message;


import java.util.ArrayList;
import java.util.List;

import scripts.CommonSubscriber;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredUserBean;

public class SubscribeAfterOperation extends CommonSubscriber implements OperationTrigger {
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        ArrayList<SecuredUserBean> users = AdapterManager.getInstance().getSecuredAclAdapterManager().getUserList(message.getSecure(), message.getTaskId());
        List<String> toRemove = new ArrayList<String>();
        for (SecuredUserBean f : users) {
            toRemove.add(f.getId());
        }
        log.error(" unsubscribe : " + toRemove);
        unsubscribe(message.getTaskId(), toRemove);
        //SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(message.getSecure(), FELLOWS_UDF);
        String fellowsUDF = message.getUdfValue(FELLOWS_UDF);
        log.error(" fellowsUDF : " + fellowsUDF);
        if (fellowsUDF != null) {
            String[] value = fellowsUDF.split(";");
            if (value!=null && value.length>0)
                for (String login : value) {
                    if (login.length()>0){
                        login = login.startsWith("@") ? login.substring(1) : login;
                        SecuredUserBean f = AdapterManager.getInstance().getSecuredUserAdapterManager().findByLogin(message.getSecure(), login.trim()); //skip @
                        if (f != null) {
                            subscribe(f.getUser(), message.getTaskId());
                        }
                    }
                }
        }
        return message;
    }

}
