package scripts.task_custom_field_value.itsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import scripts.itsm.CommonITSM;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;

public class ClientLink extends CommonITSM implements TaskUDFValueScript {
    private static final Log log = LogFactory.getLog(CommonITSM.class);

    @Override
	public Object calculate(SecuredTaskBean task) throws GranException {
        Object value = getUDFValueByCaption(task, INCIDENT_CLIENT_UDF);
        log.error("ClientLink : " + value);
        if (value != null) {
        	SecuredUserBean clientUser = AdapterManager.getInstance().getSecuredUserAdapterManager().findByName(task.getSecure(), value.toString());
        	if (clientUser!=null) {
        		List<String> clients = new ArrayList<String>();
        		clients.add(clientUser.getLogin());
        		return clients;
        	}
        }

		return null;
	}

}
