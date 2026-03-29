package br.com.willian.services;

import jakarta.annotation.PostConstruct;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class JasperReportLoader{
    private JasperReport employeeReport;
    private JasperReport vendaReport;

    @PostConstruct
    public void init() throws Exception {

        InputStream employeeStream =
                getClass().getResourceAsStream("/templates/employee.jrxml");

        InputStream vendaStream =
                getClass().getResourceAsStream("/templates/venda.jrxml");

        employeeReport = JasperCompileManager.compileReport(employeeStream);
        vendaReport = JasperCompileManager.compileReport(vendaStream);
    }

    public JasperReport getEmployeeReport() {
        return employeeReport;
    }

    public JasperReport getVendaReport() {
        return vendaReport;
    }
}
