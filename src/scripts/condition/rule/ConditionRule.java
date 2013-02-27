package scripts.condition.rule;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredUDFValueBean;

import java.util.List;

public interface ConditionRule {
    public boolean init(List<SecuredUDFValueBean> udfs) throws GranException;
    public String getTaskId();
    public String getUserId();
    public String getPrstatusId();
    public String getOverridePrstatusId();
    public String getOwnerId();
    public boolean isOverride();
}
