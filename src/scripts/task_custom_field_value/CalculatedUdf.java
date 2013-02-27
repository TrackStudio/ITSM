package scripts.task_custom_field_value;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.tools.HibernateUtil;
import com.trackstudio.tools.Pair;

public class CalculatedUdf implements TaskUDFValueScript {
    @Override
    public Object calculate(SecuredTaskBean task) throws GranException {
        HibernateUtil hu = new HibernateUtil();
        //hu.getList(); hql query
        SecuredUDFValueBean udfValue = getUdf(task);
        Pair pair = (Pair) udfValue.getValue();
        return pair.getValue();
    }

    private SecuredUDFValueBean getUdf(SecuredTaskBean task) throws GranException {
        SecuredUDFValueBean udfValue = null;
        for (SecuredUDFValueBean udf : task.getUdfValuesList()) {
            if ("list_udf".equals(udf.getCaption())) {
                udfValue = udf;
                break;
            }
        }
        return udfValue;
    }
}
