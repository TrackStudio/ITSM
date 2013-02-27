package scripts.report.bars;

import com.trackstudio.app.UdfValue;
import com.trackstudio.app.udf.StringValue;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class StatusFactory {
    private static final LinkedHashMap<BarsReport.Line, IControllable> controllable = new LinkedHashMap<BarsReport.Line, IControllable>();
    static {
        controllable.put(BarsReport.Line.TENDER, new TenderControl());
        controllable.put(BarsReport.Line.NOT_TENDER, new NotTenderControl());
        controllable.put(BarsReport.Line.BUGS_WITHOUT_NUMBER, new BugsWithoutNumberControl());
        controllable.put(BarsReport.Line.ORDER_NOT_NEED, new OrderNotNeedControl());
        controllable.put(BarsReport.Line.SUPPORT, new SupportControl());
        controllable.put(BarsReport.Line.SUBWORKER, new SubworkerControl());
        controllable.put(BarsReport.Line.SUBWORKER_TENDER, new SubworkerTenderControl());
        controllable.put(BarsReport.Line.SUBWORKER_NOT_TENDER, new SubworkerNotTenderControl());
        controllable.put(BarsReport.Line.TOTAL_BUGS, new TotalTaskControl());
        controllable.put(BarsReport.Line.INSPECTION, new InspectorControl());
    }

    private static final List<IValuable> valuable = new ArrayList<IValuable>();
    static {
        valuable.add(new TotalTaskValue());
        valuable.add(new WightValue());
        valuable.add(new SpentTimeValue());
        valuable.add(new ClosingTaskValue());
        valuable.add(new CancelingTaskValue());
        valuable.add(new ShippingProcessTaskValue());
        valuable.add(new TakingJobTaskValue());
        valuable.add(new ImplementingTaskValue());
        valuable.add(new VerifyingSpecificationTaskValue());
        valuable.add(new TestingTaskValue());
        valuable.add(new ImplicitlySettingTaskValue());
        valuable.add(new InspectionValue());
    }

    public interface IControllable {
        public boolean check(SecuredTaskBean task, String reportName) throws GranException;
        public String getName();
        public String getNumber();
    }

    public interface IValuable {
        public String calculateValue(List<SecuredTaskBean> total, List<SecuredTaskBean> tasks) throws GranException;
    }

    public static IControllable getIControllable(BarsReport.Line line) {
        return controllable.get(line);
    }

    public static List<BarsReport.Line> findAppropriateColumn(SecuredTaskBean taskBean, String reportName) throws GranException {
        List<BarsReport.Line> lines = new ArrayList<BarsReport.Line>();
        for (Map.Entry<BarsReport.Line, IControllable> entry : controllable.entrySet()) {
            if (entry.getValue().check(taskBean, reportName)) {
                lines.add(entry.getKey());
            }
        }
        return lines;
    }

    public static List<String> getCellValue(List<SecuredTaskBean> total, List<SecuredTaskBean> tasks) throws GranException {
        List<String> values = new ArrayList<String>();
        for (IValuable calculator : valuable) {
            values.add(calculator.calculateValue(total, tasks));
        }
        return values;
    }

    private static class TenderControl implements IControllable {
        @Override
        public boolean check(SecuredTaskBean task, String reportName) throws GranException {
            return StatusFactory.checkOrderUdf(task, reportName, "ТЕНДЕРНЫЕ ЗАКАЗЫ");
        }

        @Override
        public String getName() {
            return "ТЕНДЕРНЫЕ ЗАКАЗЫ (BANK->RB)";
        }

        @Override
        public String getNumber() {
            return "1";
        }
    }

    private static class NotTenderControl implements IControllable {
        @Override
        public boolean check(SecuredTaskBean task, String reportName) throws GranException {
            return StatusFactory.checkOrderUdf(task, reportName, "НЕТЕНДЕРНЫЕ ЗАКАЗЫ");
        }

        @Override
        public String getName() {
            return "НЕТЕНДЕРНЫЕ  ЗАКАЗЫ (BANK->RB)";
        }

        @Override
        public String getNumber() {
            return "2";
        }
    }

    private static class BugsWithoutNumberControl implements IControllable {
        @Override
        public boolean check(SecuredTaskBean task, String reportName) throws GranException {
            return StatusFactory.checkOrderUdf(task, reportName, "БАГИ БЕЗ НОМЕРА");
        }

        @Override
        public String getName() {
            return "БАГИ БЕЗ НОМЕРА  (заказы не оформлены)";
        }

        @Override
        public String getNumber() {
            return "3";
        }
    }

    private static class OrderNotNeedControl implements IControllable {
        @Override
        public boolean check(SecuredTaskBean task, String reportName) throws GranException {
            return StatusFactory.checkOrderUdf(task, reportName, "ЗАКАЗ НЕ НУЖЕН");
        }

        @Override
        public String getName() {
            return "ЗАКАЗ НЕ НУЖЕН (бесплатно)";
        }

        @Override
        public String getNumber() {
            return "4";
        }
    }

    private static class SupportControl implements IControllable {
        @Override
        public boolean check(SecuredTaskBean task, String reportName) throws GranException {
            return StatusFactory.checkOrderUdf(task, reportName, "ПОДДЕРЖКА");
        }

        @Override
        public String getName() {
            return "\"ПОДДЕРЖКА\" (бесплатно)";
        }

        @Override
        public String getNumber() {
            return "5";
        }
    }

    private static class SubworkerControl implements IControllable {
        @Override
        public boolean check(SecuredTaskBean task, String reportName) throws GranException {
            return !StatusFactory.getUdfValue(task.getUdfValuesList(), "Задание GCP").isEmpty();
        }

        @Override
        public String getName() {
            return "БАГИ В РАБОТЕ У СУБПОДРЯДЧИКА";
        }

        @Override
        public String getNumber() {
            return "6";
        }
    }

    private static class SubworkerTenderControl implements IControllable {
        @Override
        public boolean check(SecuredTaskBean task, String reportName) throws GranException {
            boolean taskGCP = !StatusFactory.getUdfValue(task.getUdfValuesList(), "Задание GCP").isEmpty();
            boolean taskOrder = StatusFactory.checkOrderUdf(task, reportName, "В Т.Ч. ПО ТЕНДЕРНЫМ ЗАКАЗАМ GCP 6.1");
            return taskGCP && taskOrder;
        }

        @Override
        public String getName() {
            return "В Т.Ч. ПО ТЕНДЕРНЫМ ЗАКАЗАМ";
        }

        @Override
        public String getNumber() {
            return "6.1";
        }
    }

    private static class SubworkerNotTenderControl implements IControllable {
        @Override
        public boolean check(SecuredTaskBean task, String reportName) throws GranException {
            boolean taskGCP = !StatusFactory.getUdfValue(task.getUdfValuesList(), "Задание GCP").isEmpty();
            boolean taskOrder = StatusFactory.checkOrderUdf(task, reportName, "В Т.Ч. ПО ТЕНДЕРНЫМ ЗАКАЗАМ GCP 6.2");
            return taskGCP && taskOrder;
        }

        @Override
        public String getName() {
            return "В Т.Ч. ПО ПРОЧИМ ЗАКАЗАМ";
        }

        @Override
        public String getNumber() {
            return "6.2";
        }
    }

    private static class TotalTaskControl implements IControllable {
        @Override
        public boolean check(SecuredTaskBean task, String reportName) {
            return true;
        }

        @Override
        public String getName() {
            return "ВСЕГО БАГОВ В TS";
        }

        @Override
        public String getNumber() {
            return "7";
        }
    }

    private static class InspectorControl implements IControllable {
        @Override
        public boolean check(SecuredTaskBean task, String reportName) {
            return true;
        }

        @Override
        public String getName() {
            return "КОНТРОЛЬ 1";
        }

        @Override
        public String getNumber() {
            return "8";
        }
    }

    private static class TotalTaskValue implements IValuable {
        @Override
        public String calculateValue(List<SecuredTaskBean> total, List<SecuredTaskBean> tasks) {
            return String.valueOf(tasks.size());
        }
    }

    private static class WightValue implements IValuable {
        @Override
        public String calculateValue(List<SecuredTaskBean> total, List<SecuredTaskBean> tasks) {
            return String.valueOf((tasks.size()/total.size())*100);
        }
    }

    private static class SpentTimeValue implements IValuable {
        @Override
        public String calculateValue(List<SecuredTaskBean> total, List<SecuredTaskBean> tasks) throws GranException {
            double result = 0d;
            for (SecuredTaskBean task : tasks) {
                Double value = (Double) getUdfValue(task, "Трудозатраты, ч/д");
                if (value != null) {
                    result += value;
                }
            }
            return String.valueOf(result);
        }
    }

    private static class ClosingTaskValue implements IValuable {
        @Override
        public String calculateValue(List<SecuredTaskBean> total, List<SecuredTaskBean> tasks) {
            return String.valueOf(countStatus(tasks, "2c918bbe314b0a4901314b730218001d"));
        }
    }

    private static long countStatus(List<SecuredTaskBean> tasks, String status) {
        long result = 0l;
        for (SecuredTaskBean task : tasks) {
            if (status.equals(task.getStatusId())) {
                ++result;
            }
        }
        return result;
    }

    private static class CancelingTaskValue implements IValuable {
        @Override
        public String calculateValue(List<SecuredTaskBean> total, List<SecuredTaskBean> tasks) {
            return String.valueOf(countStatus(tasks, "2c918bbe314b0a4901314b7302180017"));
        }
    }

    private static class ShippingProcessTaskValue implements IValuable {
        @Override
        public String calculateValue(List<SecuredTaskBean> total, List<SecuredTaskBean> tasks) {
            return String.valueOf(countStatus(tasks, "d1310e8c318f57a8013194a4efdd00e0"));
        }
    }

    private static class TakingJobTaskValue implements IValuable {
        @Override
        public String calculateValue(List<SecuredTaskBean> total, List<SecuredTaskBean> tasks) {
            return String.valueOf(countStatus(tasks, "2c918bbe314b0a4901314b7302180018"));
        }
    }

    private static class ImplementingTaskValue implements IValuable {
        @Override
        public String calculateValue(List<SecuredTaskBean> total, List<SecuredTaskBean> tasks) {
            return String.valueOf(countStatus(tasks, "2c918bbe314b0a4901314b730218001b"));
        }
    }

    private static class VerifyingSpecificationTaskValue implements IValuable {
        @Override
        public String calculateValue(List<SecuredTaskBean> total, List<SecuredTaskBean> tasks) {
            return String.valueOf(countStatus(tasks, "2c918bbe314b0a4901314b730218001a"));
        }
    }

    private static class TestingTaskValue implements IValuable {
        @Override
        public String calculateValue(List<SecuredTaskBean> total, List<SecuredTaskBean> tasks) {
            return String.valueOf(countStatus(tasks, "2c918bbe314b0a4901314b6ec5a60015"));
        }
    }

    private static class ImplicitlySettingTaskValue implements IValuable {
        @Override
        public String calculateValue(List<SecuredTaskBean> total, List<SecuredTaskBean> tasks) {
            return String.valueOf(countStatus(tasks, "2c918bbe314b0a4901314b7302180019"));
        }
    }

    private static class InspectionValue implements IValuable {
        @Override
        public String calculateValue(List<SecuredTaskBean> total, List<SecuredTaskBean> tasks) {
            return String.valueOf("0");
        }
    }

    private static List<String> getUdfValue(List<SecuredUDFValueBean> udfs, String name) throws GranException {
        List<String> values = new ArrayList<String>();
        for (SecuredUDFValueBean udf : udfs) {
            if (name.equals(udf.getCaption())) {
                Object obj = udf.getValue();
                if (obj != null) {
                    values.add(obj.toString());
                }
            }
        }
        return values;
    }

    public static String getSortingUdfValue(String udfName) throws GranException {
        UserRelatedInfo userInfo = UserRelatedManager.getInstance().find("1");
        String value = null;
        for (UdfValue udfValue : userInfo.getUDFValues()) {
            if (udfName.equals(udfValue.getCaption())) {
                value = ((StringValue) udfValue.getValueContainer()).getValue(null);
                break;
            }
        }
        return value;
    }

    public static Object getUdfValue(SecuredTaskBean task, String nameUdf) throws GranException {
        Object value = null;
        for (SecuredUDFValueBean udfValue : task.getUdfValuesList()) {
            if (udfValue.getCaption().equals(nameUdf)) {
                value = udfValue.getValue();
            }
        }
        return value;
    }

    private static boolean checkOrderUdf(SecuredTaskBean task, String reportName, String udfName) throws GranException {
        boolean result = false;
        List<String> list = StatusFactory.getUdfValue(task.getUdfValuesList(), "Заказ");
        String checkingValue = StatusFactory.getSortingUdfValue(udfName + reportName);
        if (checkingValue != null) {
            result = CollectionUtils.isSubCollection(new ArrayList<String>(Arrays.asList(checkingValue.split(";"))), list);
        }
        return result;
    }
}
