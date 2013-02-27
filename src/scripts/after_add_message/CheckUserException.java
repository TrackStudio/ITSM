package scripts.after_add_message;

import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.exception.UserExceptionAfterTrigger;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;

public class CheckUserException implements OperationTrigger {
    @Override
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        throw new UserExceptionAfterTrigger("Check user exception");
    }
}
