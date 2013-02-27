package scripts.after_add_message;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import scripts.util.GCUtil;

public class UpdateGCEvent implements OperationTrigger {
    @Override
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        GCUtil.updateSubtask(message.getTask());
        return message;
    }
}
