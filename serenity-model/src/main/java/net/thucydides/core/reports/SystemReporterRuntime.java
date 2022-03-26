package net.thucydides.core.reports;

public class SystemReporterRuntime implements ReporterRuntime{
    @Override
    public int availableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }
}
