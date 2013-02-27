package scripts.before_edit_task.rstyle;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.secured.SecuredUserBean;
import scripts.bulk.rstyle.RStyleUtil;

public class CheckPlainRealiseEdit_v10_17_26 implements TaskTrigger {
    @Override
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        SessionContext sc = task.getSecure();
        SecuredUserBean root = new SecuredUserBean("1", sc);

        String to = RStyleUtil.getUdfValue(root.getUDFValues(), "check_plain_realise", task.getSecure().getUser());
        String regexp = RStyleUtil.getUdfValue(root.getUDFValues(), "regexp", task.getSecure().getUser());

        String value = task.getUdfValue(to);

        if (!checkValue(value, regexp)) {
            throw new UserException("Некорректные данные - "+to+" value="+value + ", regexp="+regexp, false);
        }

        return task;
    }

    private static boolean checkValue(String value, String regexp) throws UserException {
        return value != null && value.matches(regexp);
    }

    public static void main(String[] arg) throws UserException {
        System.out.println(checkValue("", "\\d+\\.\\d+\\.\\d+"));
        System.out.println(checkValue("1.2.0", "\\d+\\.\\d+\\.\\d+"));
        System.out.println(checkValue("2.12.9", "\\d+\\.\\d+\\.\\d+"));
    }
}
