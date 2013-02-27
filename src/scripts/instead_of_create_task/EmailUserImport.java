package scripts.instead_of_create_task;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.email.change.Change;
import com.trackstudio.app.adapter.email.change.NewTaskChange;
import com.trackstudio.app.adapter.email.change.NewTaskWithAttachmentChange;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.model.MailImport;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.tools.Null;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import scripts.itsm.CommonITSM;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailUserImport implements TaskTrigger {
    private static final Log log = LogFactory.getLog(EmailUserImport.class);
    private Set<String> registerUsers = new TreeSet<String>();

    private List<String> parseEmailsCC(String desc) {
        String text = desc;
        List emails = new ArrayList();
        if (text.indexOf("CC users:<br>") != -1 && text.lastIndexOf("<br>") != -1) {
            text = text.substring(text.indexOf("CC users:<br>") + "CC users:<br>".length(), text.lastIndexOf("<br>"));
            for (String email : text.split(";<br>")) {
                email = email.substring(email.indexOf("email:") + "email:".length()).trim();
                if (email.contains(" name:")) {
                    String name = email.substring(email.indexOf(" name:") + " email".length(), email.length()).trim();
                    email = email.substring(0, email.indexOf(", name:"));
                    emails.add(email+"="+name);
                } else {
                    emails.add(email);
                }
            }
        }
        return emails;
    }

    private String parseEmailsFrom(String desc) {
        String text = desc;
        String emailPattern = "e-from:\\s*\\\"?(\\S+\\s*\\S+[^\\\"])?\\\"?\\s+(<|&lt;)?(([-a-z0-9!#$%&'*+/=?^_`{|}~]+(\\.[-a-z0-9!#$%&'*+/=?^_`{|}~]+)*)@([-a-z0-9.]+))(&gt;|>)?";
        Pattern pat = Pattern.compile(emailPattern);
        Matcher mat = pat.matcher(text);
        if (mat.find()) {
            String userName = mat.group(1);
            String userEmail = mat.group(3);
            if (userName == null) {
                userName = mat.group(4);
            }
            return userEmail + "=" + userName;
        }
        return null;
    }

    private SecuredTaskTriggerBean setSubmitter(SecuredTaskTriggerBean task, String userName, String userEmail, String mailUserId, String userStatusId) throws GranException {
        String userId = KernelManager.getUser().findUserByEmailIgnoreCase(userEmail, task.getParentId());
        String login = userEmail.substring(0, userEmail.indexOf("@"));
        if (userId == null) {
            userId = KernelManager.getUser().findByLogin(login);
            int i = 0;
            while (userId != null) {
                login = login + i;
                userId = KernelManager.getUser().findByLogin(login);
                i++;
            }
        }
        if (userId == null && checkEmail(userEmail)){
            SafeString safeName = SafeString.createSafeString(userEmail);
            userId = KernelManager.getUser().createUser(mailUserId, SafeString.createSafeString(login), safeName, userStatusId, SafeString.createSafeString(""));

            KernelManager.getUser().updateUser(
                    userId,
                    SafeString.createSafeString(login),  //SafeString login
                    safeName, //SafeString name
                    null, // SafeString tel
                    SafeString.createSafeString(userEmail), //SafeString email
                    userStatusId,   //String prstatusId
                    mailUserId, //String managerId
                    task.getSecure().getUser().getTimezone(), //String timezone
                    task.getSecure().getUser().getLocale(), //String locale
                    SafeString.createSafeString(task.getSecure().getUser().getCompany()), //SafeString company
                    null, //SafeString template
                    null, //String taskId
                    null, //Calendar expire
                    null, //SafeString preferences
                    true //boolean enabled
            );
            registerUsers.add(userId);
            task.setSubmitterId(userId);
        }
        return task;
    }

    @Override
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        System.out.println("START : ");
        MailImport mailImport = KernelManager.getFind().findMailImport(CommonITSM.getProperty("mail.import.id"));
        String desc = task.getDescription();
        String userStatusId = mailImport.getOwner().getPrstatus().getId();
        String mailUserId =  mailImport.getOwner().getId();
        if (Null.isNotNull(desc)) {
            System.out.println("Desc : " + desc);
            Set setUsers = new TreeSet();
            String users = "";
            String header = desc;
            if (desc.indexOf("\n\n") != -1) {
                header = desc.substring(0, desc.indexOf("\n\n"));
            }
            String submitter = parseEmailsFrom(header);
            List<String> emails = parseEmailsCC(desc);
            System.out.println("CC emails : " + emails);
            for (int i=0;i!=emails.size();++i) {
                String userEmail = emails.get(i);
                String userName = userEmail;
                if (emails.get(i).contains("=")) {
                    String[] arrs = emails.get(i).split("=");
                    userEmail = arrs[0];
                    userName = arrs[1];
                }
                System.out.println("Try find email : " + userEmail + " name : " + userName);
                String userId = KernelManager.getUser().findUserByEmailIgnoreCase(userEmail, task.getParentId());
                String login = userEmail.substring(0, userEmail.indexOf("@"));
                if (userId == null) {
                    userId = KernelManager.getUser().findByLogin(login);
                    int j = 0;
                    while (userId != null) {
                        login = login + j;
                        userId = KernelManager.getUser().findByLogin(login);
                        j++;
                    }
                }
                System.out.println("Try find result " + userId);
                if (userId == null && checkEmail(userEmail)) {
                    System.out.println("Try create login : " + login + " name : " + userName + " email " + userEmail + " mailUserId : " + mailUserId + " userStatusId : " + userStatusId );
                    SafeString safeName = SafeString.createSafeString(userEmail);
                    userId = KernelManager.getUser().createUser(mailUserId, SafeString.createSafeString(login), safeName, userStatusId, SafeString.createSafeString(""));

                    System.out.println("Try update login : " + login + " name : " + userName + " email " + userEmail + " mailUserId : " + mailUserId + " ");
                    KernelManager.getUser().updateUser(
                            userId,
                            SafeString.createSafeString(login),  //SafeString login
                            safeName, //SafeString name
                            null, // SafeString tel
                            SafeString.createSafeString(userEmail), //SafeString email
                            userStatusId,   //String prstatusId
                            mailUserId, //String managerId
                            task.getSecure().getUser().getTimezone(), //String timezone
                            task.getSecure().getUser().getLocale(), //String locale
                            SafeString.createSafeString(task.getSecure().getUser().getCompany()), //SafeString company
                            null, //SafeString template
                            null, //String taskId
                            null, //Calendar expire
                            null, //SafeString preferences
                            true //boolean enabled
                    );
                    registerUsers.add(userId);
                    System.out.println("Create succesfull");
                }
                if (userId != null) {
                    setUsers.add(userId);
                }
            }
            System.out.println("TEST 2");
            if (submitter != null) {
                String[] userSubbmitter = submitter.split("=");
                task = setSubmitter(task, userSubbmitter[1], userSubbmitter[0], mailUserId, userStatusId);
            }
            System.out.println("TEST 3" + setUsers);
            for (Object o : setUsers) {
                users = users + o.toString() + ";";
            }
            users = users + task.getUdfValue("CC");
            System.out.println("USER : " + users);
            task.setUdfValue("CC", users);
        }

        for (String userId : registerUsers) {
            KernelManager.getUser().forgotPassword(userId);
        }
        String newTaskId = createTask(task);
        task.setId(newTaskId);
        log.error("newTaskId : " + newTaskId + " triggerInputId : " + task.getId());
        return task;
    }

    private static boolean checkEmail(String email) {
        System.out.println(" checkEmail : " + email);
        String valueForbiddenEmails = CommonITSM.getProperty("forbidden.emails");
        List<String> forbiddenEmails = new ArrayList<String>();
        if (Null.isNotNull(valueForbiddenEmails)) {
            forbiddenEmails = Arrays.asList(valueForbiddenEmails.split(";"));
        }
        boolean result = !forbiddenEmails.contains(email);
        System.out.println(" checkEmail result : " + result);
        return result;
    }

    private static String getCorrectedEmail(String email) {
        return checkEmail(email) ? email : null;
    }

    private static List<String> getCorrectedEmail(List<String> emails) {
        List<String> correctedEmails = new ArrayList<String>();
        for (String email : emails) {
            if (checkEmail(email)) {
                correctedEmails.add(email);
            }
        }
        return correctedEmails;
    }

    private String createTask(SecuredTaskTriggerBean task) throws GranException {
        log.error("Submitter id : " + task.getSubmitterId());
        String taskId = KernelManager.getTask().createTask(task.getParentId(),
                task.getSubmitterId(),
                task.getCategoryId(),
                SafeString.createSafeString(task.getName()),
                task.getDeadline(),
                task.getSubmitdate(),
                task.getUpdatedate(),
                task.getStatusId());

        if (task.getAtts() != null && !task.getAtts().isEmpty()) {
            AdapterManager.getInstance().getSecuredAttachmentAdapterManager().createAttachment(task.getSecure(),
                    taskId, null,
                    task.getSecure().getUserId(), task.getAtts(), false);
        }

        setUdf(task, taskId);

        KernelManager.getTask().updateTask(taskId,
                SafeString.createSafeString(task.getShortname()),
                SafeString.createSafeString(task.getName()),
                SafeString.createSafeString(task.getDescription()),
                task.getBudget(), task.getDeadline(),
                task.getPriorityId(), task.getParentId(),
                task.getHandlerUserId(), task.getHandlerGroupId(),
                task.getSubmitdate(), task.getUpdatedate());

        //sendNotify(taskId, task);

        return taskId;
    }

    private void setUdf(SecuredTaskTriggerBean task, String taskId) throws GranException {
        HashMap<String, String> udfs = task.getUdfValues();
        for (Map.Entry<String, String> entry : udfs.entrySet()) {
            AdapterManager.getInstance().getSecuredUDFAdapterManager().setTaskUDFValueSimple(task.getSecure(), taskId, entry.getKey(), entry.getValue());
        }
    }

    private void sendNotify(String newId, SecuredTaskTriggerBean parent) throws GranException {
        Calendar now = new GregorianCalendar();
        now.setTimeInMillis(System.currentTimeMillis());
        Change change;
        SecuredTaskBean task = new SecuredTaskBean(newId, parent.getSecure());
        if (task.getAttachments() == null || task.getAttachments().size() == 0) {
            change = new NewTaskChange(now, parent.getSecure().getUserId(), newId);
        } else {
            List<String> attsId = new ArrayList<String>();
            for (SecuredAttachmentBean sab : task.getAttachments()) {
                attsId.add(sab.getId());
            }
            change = new NewTaskWithAttachmentChange(now, task.getSecure().getUserId(), newId, attsId);
        }
        AdapterManager.getInstance().getFilterNotifyAdapterManager().sendNotifyForTask(null, newId, task.getSubmitterId(), null, change);
    }
}
