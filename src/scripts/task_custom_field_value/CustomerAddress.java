package scripts.task_custom_field_value;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;

/**
 * This class calculates customer's info. It takes a submitted of task
 */
public class CustomerAddress implements TaskUDFValueScript {

    /**
     * This method returns customer's info
     * @param task task
     * @return StringBuilder
     * @throws GranException for necessary
     */
    @Override
    public Object calculate(SecuredTaskBean task) throws GranException {
        StringBuilder sb = new StringBuilder();
        sb.append("ФИО : ").append(task.getSubmitter().getName()).append("\n");
        sb.append("email : ").append(task.getSubmitter().getEmailList()).append("\n");
        sb.append("телефон : ").append(task.getSubmitter().getTel()).append("\n");
        return sb.toString();
    }
}
