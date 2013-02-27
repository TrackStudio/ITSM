package scripts.report.bars;

import com.trackstudio.app.report.birt.IBuildReport;
import com.trackstudio.app.report.birt.Report;
import com.trackstudio.exception.GranException;
import com.trackstudio.securedkernel.SecuredReportAdapterManager;
import org.eclipse.birt.report.model.api.MasterPageHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;

import java.util.HashMap;

/**
 * This class is a List report.
 */
public class BarsBuilder implements IBuildReport, IBuildReport.IList {
    private Report report;
    private Parameters parameters;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws GranException {
        try {
            this.report = new BarsReport(parameters.getContext(), parameters.getSc(), parameters.getSrb(), parameters.getFv(), parameters.getTaskId(), parameters.getReportType());
            String reportType = parameters.getReportType();
            if (SecuredReportAdapterManager.RT_PDF.equalsIgnoreCase(reportType) || SecuredReportAdapterManager.RT_DOC.equalsIgnoreCase(reportType)) {
                MasterPageHandle masterPageHandle = ((ReportDesignHandle) parameters.getDesign().getDesignHandle()).findMasterPage("reportList");
                masterPageHandle.setOrientation("Landscape");
            }
        } catch (Exception e) {
            throw new GranException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Report getReport() {
        return this.report;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplate() {
       return "bars.rptdesign";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashMap<String, String> getReportParams() throws GranException {
        return this.report.initReportParameters(parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }
}
