package scripts.report.bars;

import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.report.birt.list.ListReport;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredReportBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.Config;
import com.trackstudio.view.TaskViewHTML;

import java.util.*;

public class BarsReport extends ListReport {
    private final static HashMap<String, String> headerAnalyze = new HashMap<String, String>();
    private final static List<String> keyHeadAnalyze = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15");
    static {
        headerAnalyze.put("1", "№ группы");
        headerAnalyze.put("2", "Наименование группы");
        headerAnalyze.put("3", "кол-во багов ");
        headerAnalyze.put("4", "Уд.вес %");
        headerAnalyze.put("5", "Трудо-затраты, Ч/Д");
        headerAnalyze.put("6", "Закры-то (Б)");
        headerAnalyze.put("7", "Отклонен (Р)");
        headerAnalyze.put("8", "Поставка устанавливается (Б)");
        headerAnalyze.put("9", "Принят в работу (Р)");
        headerAnalyze.put("10", "Реализация (Р)");
        headerAnalyze.put("11", "Согласование Спецификации (Б)");
        headerAnalyze.put("12", "Тестируется (Б)");
        headerAnalyze.put("13", "Уточнение требований (Б)");
        headerAnalyze.put("14", "КОНТРОЛИ");
    }

    public enum Line {
        TENDER, NOT_TENDER, BUGS_WITHOUT_NUMBER, ORDER_NOT_NEED,
        SUPPORT, SUBWORKER, SUBWORKER_TENDER, SUBWORKER_NOT_TENDER,
        TOTAL_BUGS, INSPECTION
    }

    private HashMap<String, String> sizeColumnAnalyze;
    private List<String> sortAnalyze;
    private HashMap<String, HashMap<String, String>> dataAnalyze;

    /**
     * Конструктор
     *
     * @param contextPath контекст
     * @param sc          сессия пользователя
     * @param report      отчет
     * @param fv          параметры фильтрации
     * @param taskId      ID Задачи
     * @param format      формат вывода
     * @throws com.trackstudio.exception.GranException
     *          при необзодимости
     */
    public BarsReport(String contextPath, SessionContext sc, SecuredReportBean report, TaskFValue fv, String taskId, String format) throws GranException {
        super(contextPath, sc, report, fv, taskId, format);

        this.sortAnalyze = new ArrayList<String>();
        this.sizeColumnAnalyze = new HashMap<String, String>();
        for (Line line : Line.values()) {
            this.sortAnalyze.add(String.valueOf(line.hashCode()));
            this.sizeColumnAnalyze.put(String.valueOf(String.valueOf(line.hashCode())), "30px");
        }

        this.dataAnalyze = new HashMap<String, HashMap<String, String>>();
        HashMap<Line, List<SecuredTaskBean>> lineList = this.buildStatusList();
        for (Map.Entry<Line, List<SecuredTaskBean>> line : lineList.entrySet()) {
            HashMap<String, String> values = new HashMap<String, String>();
            StatusFactory.IControllable controllable = StatusFactory.getIControllable(line.getKey());
            values.put("1", controllable.getNumber());
            values.put("2", controllable.getName());
            int index = 3;
            for (String valueCell : StatusFactory.getCellValue(this.list, line.getValue())) {
                values.put(String.valueOf(index), valueCell);
                ++index;
            }
            this.dataAnalyze.put(String.valueOf(line.getKey().hashCode()), values);
        }
        this.convertNameToLink();
        this.processSorting();
    }

    /**
     * This method converts a name's column from text to link.
     * @throws GranException - unpredictable situation
     */
    private void convertNameToLink() throws GranException {
        for (Map.Entry<String, HashMap<String, String>> taskEntry : this.data().entrySet()) {
            TaskViewHTML viewer = new TaskViewHTML(new SecuredTaskBean(taskEntry.getKey(), this.sc), Config.getInstance().getSiteURL());
            if (taskEntry.getValue().get(FieldMap.TASK_NAME.getFilterKey()) != null) {
                taskEntry.getValue().put(FieldMap.TASK_NAME.getFilterKey(), viewer.getName());
            }
        }
    }

    private void processSorting() throws GranException {
        String udfName = "report_sort_" + this.nameValue();
        String valueOrder = StatusFactory.getSortingUdfValue(udfName);
        if (valueOrder != null) {
            List<String> sortingOrder = Arrays.asList(valueOrder.split(";"));
            this.keyHead = new ArrayList<String>();
            for (String columnName : sortingOrder) {
                this.keyHead.add(this.getKeyByValue(columnName));
            }
        }
    }

    private String getKeyByValue(String value) {
        for (Map.Entry<String, String> entry : this.header.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private HashMap<Line, List<SecuredTaskBean>> buildStatusList() throws GranException {
        HashMap<Line, List<SecuredTaskBean>> map = this.initRowMap();
        for (SecuredTaskBean taskBean : this.list) {
            for (Line line : StatusFactory.findAppropriateColumn(taskBean, this.name())) {
                map.get(line).add(taskBean);
            }
        }
        return map;
    }

    private HashMap<Line, List<SecuredTaskBean>> initRowMap() {
        HashMap<Line, List<SecuredTaskBean>> map = new HashMap<Line, List<SecuredTaskBean>>();
        for (Line line : Line.values()) {
            map.put(line, new ArrayList<SecuredTaskBean>());
        }
        return map;
    }

    public HashMap<String, String> sizeColumnAnalyze() {
        return this.sizeColumnAnalyze;
    }

    public HashMap<String, HashMap<String, String>> dataAnalyze() {
        return this.dataAnalyze;
    }

    public List<String> sortAnalyze() {
        return this.sortAnalyze;
    }

    public List<String> keyHeadAnalyze() {
        return keyHeadAnalyze;
    }

    public HashMap<String, String> headerAnalyze() {
        return headerAnalyze;
    }

    public String sortingType() {
        return "Сортировка:";
    }

    public String sortingTypeValue() {
        return "value";
    }

    public String groupingType() {
        return "Группировка:";
    }

    public String groupingTypeValue() {
        return "value";
    }

    @Override
    public String name() {
        return "Наименова-ние отчета:";
    }

    @Override
    public String type() {
        return "Период (\"Дата \"С\" - \"Дата \"ПО\"):";
    }

    @Override
    public String task() {
        return "Ответствен-ный:";
    }

    @Override
    public String filter() {
        return "Вид отчета:";
    }
}

