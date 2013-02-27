package scripts.before_add_message;

import com.trackstudio.app.adapter.macros.ConvertURL;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;

public class CutNodeJsAddOperation implements OperationTrigger {
    @Override
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean msg) throws GranException {
        msg.setDescription(new ConvertURL().convertNodeFromTree(msg.getDescription()));
        return msg;
    }
}
