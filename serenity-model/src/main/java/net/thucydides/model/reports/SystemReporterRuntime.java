package net.thucydides.model.reports;

public class SystemReporterRuntime implements ReporterRuntime{
    @Override
    public int availableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }
}
