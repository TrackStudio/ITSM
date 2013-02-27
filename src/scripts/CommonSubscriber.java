package scripts;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.model.Notification;
import com.trackstudio.model.Usersource;
import com.trackstudio.secured.SecuredUserBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import scripts.util.PropertiesUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.String;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class CommonSubscriber {
    public static Log log = LogFactory.getLog(CommonSubscriber.class);
    protected String FELLOWS_UDF = null;
    protected String SUBSCRIBE_FILTER_ID = null;
    protected static Properties properties = null;
    {
        properties = PropertiesUtil.getProperties("subscribe.properties");
        Enumeration enums = properties.propertyNames();
        log.error("Read properties : ");
        log.error("------------------------------------");
        while (enums.hasMoreElements()) {
            String name = (String) enums.nextElement();
            log.error(name + " : " + properties.get(name));
        }
        log.error("------------------------------------");

        FELLOWS_UDF = properties.getProperty("subscribe.udf");
        SUBSCRIBE_FILTER_ID = properties.getProperty("subscribe.filter.id");
        if (FELLOWS_UDF == null || SUBSCRIBE_FILTER_ID == null) {
            throw new IllegalStateException("Script is configured incorrectly! subscribe.udf is empty or subscribe.filter.id is empty");
        }
    }

    public CommonSubscriber() {
        if (properties != null) {
            FELLOWS_UDF = properties.getProperty("subscribe.udf");
            SUBSCRIBE_FILTER_ID = properties.getProperty("subscribe.filter.id");
        }
    }

    protected void subscribe(UserRelatedInfo user, String taskId) throws GranException {
        KernelManager.getFilter().setNotification(SafeString.createSafeString("auto"), SUBSCRIBE_FILTER_ID, user.getId(), null, taskId, user.getTemplate());
    }

    protected void unsubscribe(String taskId, List<String> toRemoveUsers) throws GranException {
        List<Notification> list = KernelManager.getFilter().getNotificationList(SUBSCRIBE_FILTER_ID, taskId);
        List<String> toRemove = new ArrayList<String>();
        for (Notification n : list) {
            Usersource userSource = KernelManager.getFind().findUsersource(n.getUser().getId());
            if (n.getTask().getId().equals(taskId)) {
                if ("auto".equals(n.getName()) && userSource.getUser() != null && toRemoveUsers.contains(userSource.getUser().getId())) {
                    toRemove.add(n.getId());
                }
            }
        }
        for (String id : toRemove) KernelManager.getFilter().deleteNotification(id);
    }
}