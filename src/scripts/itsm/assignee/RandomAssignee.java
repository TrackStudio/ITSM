package scripts.itsm.assignee;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Prstatus;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


public class RandomAssignee implements PeekAssigneeStrategy {
    private static Log log = LogFactory.getLog(RandomAssignee.class);

    protected ArrayList candidates;
    private SecuredUserBean handler;


    public RandomAssignee(ArrayList candidates, SecuredUserBean handler){
        this.candidates = candidates;
        this.handler = handler;

    }

    public Object peek() {
        return handler;
    }
}
