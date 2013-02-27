package scripts.itsm;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.tools.Null;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Common class for ITSM configuration
 */
public class CommonITSM {
    public static final Log log = LogFactory.getLog(CommonITSM.class);

    protected static Properties properties = null;

    static {
        try {
            properties = new Properties();
            log.error("Read file : " + new File("./").getAbsolutePath());
            properties.load(new FileReader("itsm.properties"));
        } catch (IOException e) {
            log.error("Error", e);
        }
    }

    public static String getProperty(String key) {
        return String.valueOf(properties.get(key));
    }

    public static final String EMAIL_PATTERN = "электронная почта:\\s*\\\"?(\\S+\\s*\\S+[^\\\"])?\\\"?\\s+(<|&lt;)?(([-A-Za-z0-9!#$%&'*+/=?^_`{|}~]+(\\.[-A-Za-z0-9!#$%&'*+/=?^_`{|}~]+)*)@([A-Za-z0-9.]+))(&gt;|>)?\\r?\\n";
    public static final String PHONE_PATTERN = "телефон:\\s*([0-9\\+\\s\\-\\(\\)]+)+\\r?\\n";
    public static final String COMPANY_PATTERN = "компания:\\s*(\\S+[\\s\\S]*?)\\r?\\n";

    protected String INCIDENT_PRODUCT_UDFID = "4028818212b7e87b0112be28559c0606";
    protected String PROBLEM_PRODUCT_UDFID = "ff80808112bf740e0112bf896aff00e0";
    protected String INCIDENT_CLIENT_UDFID2 = "ff808181341d98dd01341dea63bf0003"; // 11 права тут //ff8081812e6bb868012e6c5fe88205ae 10 права тут
    protected String INCIDENT_CLIENT_UDF = "Клиент";
    protected String INCIDENT_CLIENTLINK_UDF = "Ссылка на клиента";
    protected String INCIDENT_CLIENTLINK_UDFID = "ff8081812e6bb868012e6c5fe88205ae";

    protected String INCIDENT_FEEDBACK_OPERATION = "ff8081812ed77864012ed7984c6e00fb";

    protected String INCIDENT_CLIENTDATA_UDFID = "ff8081812e6bb868012e6bc433ce0006";
    protected String INCIDENT_EMAIL_UDF = "Электронная почта клиента";
    protected String INCIDENT_COMPANY_UDF = "Компания клиента";
    protected String INCIDENT_PHONE_UDF = "Контактный телефон";
    protected String WORKAROUND_PRODUCT_UDFID = "ff8081812ec813e8012ec823e2940045";
    protected String PRODUCT_CATEGORY_UDFID = "ff8081812e6bb868012e6c45861804a3";
    protected String CLIENT_ROLE_ID = "402881821204446701124cffdf95036d";
    protected String CLIENT_ROOT_ID = "40288182125234610112523776590001";
    protected String USERS_ID = "4028802833a09ed20133a2117fb10414";
    protected String INCIDENT_IMPACT_UDFID = "297eef002e045fd5012e046392f0004a";
    protected String INCIDENT_URGENCY_UDFID = "297eef002e045fd5012e0462c9370004";
    protected String INCIDENT_WORKFLOW = "402881821204446701122906b74502df";
    protected String FIRST_LINE_ROLE_ID = "ff8081812e5c7497012e5c8adb520096";
    protected String SECOND_LINE_ROLE_ID = "ff8081812e5c7497012e5c8b36cb011b";
    protected String THIRD_LINE_ROLE_ID = "ff8081812e5c7497012e5c8b7a9901a0";
    protected String FIRST_LINE_MANAGER_ROLE_ID = "ff8081812e5c7497012e5c8bdd430225";
    protected String SECOND_LINE_MANAGER_ROLE_ID = "ff8081812e5c7497012e5c8c0e7f02aa";
    protected String THIRD_LINE_MANAGER_ROLE_ID = "ff8081812e5c7497012e5c8c4165032f";
    protected String ESCALATE_OPERATION = "ff8081812e9fac85012e9fdea972009e";
    protected String ESCALATOR_BOT_ROLE = "ff8081812e9fac85012e9fb372780002";
    protected String WORKAROUND_CATEGORY_ID = "ff8081812e6bb868012e6bca1de1007f";
    protected String WORKAROUND_ROOT_ID = "ff8081812e6bb868012e6bc8d4b3007e";
    protected String PROBLEM_ROOT_ID = "40288182125234610112570ef3eb0038";
    protected String PROBLEM_CATEGORY_ID = "4028818212991c6e01129a46597c0095";
    protected String INCIDENT_WORKAROND_UDFID = "ff8081812e6bb868012e6bcc372a010d";
    protected String INCIDENT_TYPE_UDFID = "ff8081812e6bb868012e6c52ef830536";
    protected String PROBLEM_WORKAROND_UDFID = "ff8081812f8bd341012f8c9ec0f4027a";
    protected String WORKAROUND_FAQ_UDFID = "ff8081812ec813e8012ec823030a0004";
    protected String WORKAROUND_FAQ_YES = "ff8081812ec813e8012ec82309bc0043";
    protected String INCIDENT_RETURN_2_LINE_OPERATION = "ff8081812eb4404c012eb45f97d100d0";
    protected String INCIDENT_RETURN_3_LINE_OPERATION = "ff8081812ed77864012ed7922b4c0008";
    protected String PROBLEM_CONFIRM_OPERATION = "ff8081812fbfe1d8012fbffd65ef0006";
    protected String INCIDENT_CONFIRM_OPERATION = "ff8081812e80827a012e80ecbe8c0002";
    protected String PROBLEM_DECLINE_OPERATION = "ff8081812fbfe1d8012fc01e914f0052";
    protected String INCIDENT_DECLINE_OPERATION = "ff8081812ea037d4012ea053997c00d6";


    protected String WORKAROUND_IN_PROBLEM_OPERATION = "ff8081812f8bd341012f8c988f5a0155";
    protected String WORKAROUND_CLOSE_OPERATION = "000000002f96c5ae012f96ed9a0e0009";
    protected String PROBLEM_CLOSE_OPERATION = "ff80808112bf740e0112bf896a7d00bc";
    protected String WORKAROUND_IN_INCIDENT_OPERATION = "ff8081812ea037d4012ea04ebabb001b";
    protected String INCIDENT_RELATED_PROBLEM_UDFID = "ff8081812f06d861012f06ff59560036";
    protected String PROBLEM_DUPLICATE_UDFID = "ff8081812f90bce8012f90fa53ce0108";
    protected String PROBLEM_RFC_UDFID = "ff8081812f8bd341012f8c9e5b74022f";
    protected String RFC_ROOT_ID = "40288182125234610112571090a4003a";
    protected String PRODUCT_ROOT_ID = "4028818212044467011232a18cc202ec";
    protected String PRODUCT_CATEGORY_ID = "4028818212b7e87b0112be20285904ef";
    protected String RFC_CATEGORY_ID = "4028818212b7e87b0112b8e6af570111";
    protected String RFC_PRODUCT_UDFID = "000000002f9b2134012f9b48808700b0";
    protected String PRODUCT_DEPRECATE_OPERATION = "ff808181301ba7e801301c51bab10107";
    protected String PRODUCT_REPLACEMENT_UDFID = "ff808181301ba7e801301c477acf00be";
    protected String PRODUCT_STATE_IN_USE = "4028818212b7e87b0112be1d1f4f04ed";
    protected String INCIDENT_DEADLINE_UDFID = "ff8081812e762c70012e774f955a000b";

    protected String HD_ADMIN_ID = "40288182120444670112284899980112";

    public CommonITSM() {
        if (properties != null) {

            INCIDENT_PRODUCT_UDFID = initField(INCIDENT_PRODUCT_UDFID, "itsm.incident.udf.product");
            INCIDENT_TYPE_UDFID = initField(INCIDENT_TYPE_UDFID, "itsm.incident.udf.type");
            WORKAROUND_PRODUCT_UDFID = initField(WORKAROUND_PRODUCT_UDFID, "itsm.workaround.udf.product");
            PRODUCT_CATEGORY_UDFID = initField(PRODUCT_CATEGORY_UDFID, "itsm.product.udf.categories");
            CLIENT_ROLE_ID = initField(CLIENT_ROLE_ID, "itsm.client.role");
            CLIENT_ROOT_ID = initField(CLIENT_ROOT_ID, "itsm.client.root");

            INCIDENT_CLIENT_UDF = initField(INCIDENT_CLIENT_UDF, "itsm.incident.udf.client");
            INCIDENT_EMAIL_UDF = initField(INCIDENT_EMAIL_UDF, "itsm.incident.udf.email");
            INCIDENT_COMPANY_UDF = initField(INCIDENT_COMPANY_UDF, "itsm.incident.udf.company");
            INCIDENT_PHONE_UDF = initField(INCIDENT_PHONE_UDF, "itsm.incident.udf.phone");
            INCIDENT_CLIENTLINK_UDF = initField(INCIDENT_CLIENTLINK_UDF, "itsm.incident.udf.clientlink");

            INCIDENT_CLIENTDATA_UDFID = initField(INCIDENT_CLIENTDATA_UDFID, "itsm.incident.udf.clientdata");
            INCIDENT_IMPACT_UDFID = initField(INCIDENT_IMPACT_UDFID, "itsm.incident.udf.impact");
            INCIDENT_URGENCY_UDFID = initField(INCIDENT_URGENCY_UDFID, "itsm.incident.udf.urgency");
            INCIDENT_WORKFLOW = initField(INCIDENT_WORKFLOW, "itsm.incident.workflow");
            FIRST_LINE_ROLE_ID = initField(FIRST_LINE_ROLE_ID, "itsm.firstline.role");
            FIRST_LINE_MANAGER_ROLE_ID = initField(FIRST_LINE_MANAGER_ROLE_ID, "itsm.firstline.manager.role");
            SECOND_LINE_MANAGER_ROLE_ID = initField(SECOND_LINE_MANAGER_ROLE_ID, "itsm.secondline.manager.role");
            THIRD_LINE_MANAGER_ROLE_ID = initField(THIRD_LINE_MANAGER_ROLE_ID, "itsm.thirdline.manager.role");
            ESCALATE_OPERATION = initField(ESCALATE_OPERATION, "itsm.escalate.operation");
            SECOND_LINE_ROLE_ID = initField(SECOND_LINE_ROLE_ID, "itsm.second.role");
            THIRD_LINE_ROLE_ID = initField(THIRD_LINE_ROLE_ID, "itsm.third.role");
            ESCALATOR_BOT_ROLE = initField(ESCALATOR_BOT_ROLE, "itsm.escalator.role");
            WORKAROUND_CATEGORY_ID = initField(WORKAROUND_CATEGORY_ID, "itsm.workaround.category");
            INCIDENT_WORKAROND_UDFID = initField(INCIDENT_WORKAROND_UDFID, "itsm.incident.udf.workaround");
            PROBLEM_WORKAROND_UDFID = initField(PROBLEM_WORKAROND_UDFID, "itsm.problem.udf.workaround");
            WORKAROUND_ROOT_ID = initField(WORKAROUND_ROOT_ID, "itsm.workaround.root");
            WORKAROUND_FAQ_UDFID = initField(WORKAROUND_FAQ_UDFID, "itsm.workarond.udf.faq");
            WORKAROUND_FAQ_YES = initField(WORKAROUND_FAQ_YES, "itsm.workaround.faq.value");
            INCIDENT_RETURN_2_LINE_OPERATION = initField(INCIDENT_RETURN_2_LINE_OPERATION, "itsm.incident.return2line.operation");
            INCIDENT_RETURN_3_LINE_OPERATION = initField(INCIDENT_RETURN_3_LINE_OPERATION, "itsm.incident.return3line.operation");
            WORKAROUND_IN_PROBLEM_OPERATION = initField(WORKAROUND_IN_PROBLEM_OPERATION, "itsm.problem.workaround.operation");
            WORKAROUND_IN_INCIDENT_OPERATION = initField(WORKAROUND_IN_INCIDENT_OPERATION, "itsm.incident.workaround.operation");
            INCIDENT_RELATED_PROBLEM_UDFID = initField(INCIDENT_RELATED_PROBLEM_UDFID, "itsm.incident.udf.related.problem");
            PROBLEM_DUPLICATE_UDFID = initField(PROBLEM_DUPLICATE_UDFID, "itsm.problem.udf.duplicate");
            RFC_ROOT_ID = initField(RFC_ROOT_ID, "itsm.rfc.root");
            RFC_CATEGORY_ID = initField(RFC_CATEGORY_ID, "itsm.rfc.category");
            RFC_PRODUCT_UDFID = initField(RFC_PRODUCT_UDFID, "itsm.rfc.udf.product");
            PROBLEM_RFC_UDFID = initField(PROBLEM_RFC_UDFID, "itsm.problem.udf.rfc");
            PRODUCT_ROOT_ID = initField(PRODUCT_ROOT_ID, "itsm.product.root");
            PRODUCT_CATEGORY_ID = initField(PRODUCT_CATEGORY_ID, "itsm.product.category");
            PRODUCT_DEPRECATE_OPERATION = initField(PRODUCT_DEPRECATE_OPERATION, "itsm.product.deprecate.operation");
            PRODUCT_REPLACEMENT_UDFID = initField(PRODUCT_REPLACEMENT_UDFID, "itsm.product.udf.replacement");
            PRODUCT_STATE_IN_USE = initField(PRODUCT_STATE_IN_USE, "itsm.product.state.in_use");
            WORKAROUND_CLOSE_OPERATION = initField(WORKAROUND_CLOSE_OPERATION, "itsm.workaround.close.operation");
            PROBLEM_CLOSE_OPERATION = initField(PROBLEM_CLOSE_OPERATION, "itsm.problem.close.operation");
            INCIDENT_DEADLINE_UDFID =  initField(INCIDENT_DEADLINE_UDFID, "itsm.incident.udf.deadline");
        }
    }

    private static String initField(String field, String key) {
        String value = properties.getProperty(key);
        if (Null.isNotNull(value)) {
            field = value;
        }
        return field;
    }

    protected Object getUDFValueByCaption(SecuredTaskBean t, String caption) throws GranException{
        ArrayList<SecuredUDFValueBean> map = t.getUDFValuesList();
        for (SecuredUDFValueBean u: map){
            if (u.getCaption().equals(caption)) {
                return u.getValue();
            }
        }
        return null;
    }
    public String executeOperation(String mstatusId, SecuredTaskBean task, String text, Map<String, String> udfMap) throws GranException {
        if (AdapterManager.getInstance().getSecuredStepAdapterManager().getNextStatus(task.getSecure(), task.getId(), mstatusId)!=null)
            return  TriggerManager.getInstance().createMessage(task.getSecure(), task.getId(), mstatusId, text, null, task.getHandlerUserId(), task.getHandlerGroupId(), null, task.getPriorityId(), task.getDeadline(), task.getBudget(), udfMap!=null ? (HashMap)udfMap: null, true, null );
        else return null;
    }
    protected void setPermissionUDF(SessionContext sc, String newUDFId, String clientUdfId)
            throws GranException {
        // скопировать права с поля Клиент
        String incidentsRoot = KernelManager.getTask().findByNumber("50");
        Set<SecuredPrstatusBean> prstatusSet = new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId()));
        for (SecuredPrstatusBean spb : prstatusSet) {
            if (spb.isAllowedByACL() || spb.getUser().getSecure().allowedByACL(incidentsRoot)) {
                List<String> types = AdapterManager.getInstance().getSecuredUDFAdapterManager().getUDFRuleList(sc, spb.getId(), clientUdfId);
                KernelManager.getUdf().resetUDFRule(newUDFId, spb.getId());
                for (String type: types)
                    KernelManager.getUdf().setUDFRule(newUDFId, spb.getId(), type);
            }
        }


        // для mstatus нужно отдельно доставать
        List<String> editableIds = KernelManager.getUdf().getOperationsWhereUDFIsEditable(clientUdfId);
        List<String> viewableIds = KernelManager.getUdf().getOperationsWhereUDFIsViewable(clientUdfId);

        for (String mstatusId: editableIds)
            KernelManager.getUdf().setMstatusUDFRule(newUDFId, mstatusId, CategoryConstants.EDIT_ALL);

        for (String mstatusId: viewableIds)
            KernelManager.getUdf().setMstatusUDFRule(newUDFId, mstatusId, CategoryConstants.VIEW_ALL);
    }

    public static String getDecision(SecuredTaskBean task) throws GranException {
        StringBuffer sb = new StringBuffer();
        for (SecuredMessageBean msg : task.getMessages()) {
            if ("Решение".equals(msg.getMstatus().getName())) {
                sb.append("Решение").append("<br/>").append(msg.getDescription());
            }
        }
        return sb.toString();
    }
}
