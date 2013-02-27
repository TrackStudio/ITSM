package scripts.instead_of_add_message;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;

import java.util.Calendar;

public class CheckEmailNotify implements OperationTrigger {

    @Override
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        SecuredMessageTriggerBean createMessage = new SecuredMessageTriggerBean(
                null,
                message.getDescription(),
                Calendar.getInstance(),
                null,
                message.getDeadline(),
                message.getBudget(),
                message.getTaskId(),
                message.getSecure().getUserId(),
                null,
                message.getPriorityId(),
                message.getTask().getHandlerUserId(),
                message.getTask().getHandlerGroupId(),
                null,
                message.getMstatusId(),
                null,
                message.getSecure(),
                message.getAtts());

        message = createMessage.create(true);

        return message;
    }
}
