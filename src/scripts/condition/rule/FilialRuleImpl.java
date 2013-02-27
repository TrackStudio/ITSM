package scripts.condition.rule;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.tools.Pair;

import java.util.List;

public class FilialRuleImpl implements ConditionRule {
    @Override
    public boolean init(List<SecuredUDFValueBean> udfs) throws GranException {
        boolean result = false;
        for (SecuredUDFValueBean udf : udfs) {
            if ("Филиал".equals(udf.getCaption())) {
                Pair pair = (Pair) udf.getValue();
                if ("xx xxxxxx".equals(pair.getValue())) {
                    result = true;
                }
            }
        }
        return result;
    }

    @Override
    public String getTaskId() {
        return "1";
    }

    @Override
    public String getUserId() {
        return "1";
    }

    @Override
    public String getPrstatusId() {
        return null;
    }

    @Override
    public String getOwnerId() {
        return "1";
    }

    @Override
    public boolean isOverride() {
        return true;
    }

    @Override
    public String getOverridePrstatusId() {
        return "5";
    }
}
