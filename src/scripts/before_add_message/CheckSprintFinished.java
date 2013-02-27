package scripts.before_add_message;

import scripts.CommonScrum;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;

import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;

public class CheckSprintFinished  extends CommonScrum implements OperationTrigger {

    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
       for (SecuredTaskBean task : message.getTask().getChildren()) {
           if (!task.getStatus().isFinish()) throw new UserException("��� ������ � ������� ������ ���� ���������");
       }
        return message;
    }
}
