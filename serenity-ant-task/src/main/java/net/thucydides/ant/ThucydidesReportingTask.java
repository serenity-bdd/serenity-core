package net.thucydides.ant;

import net.serenity_bdd.ant.SerenityReportingTask;
import net.serenity_bdd.ant.util.PathProcessor;
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import org.apache.tools.ant.BuildException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Use SerenityReportingTask instead.
 */
@Deprecated
public class ThucydidesReportingTask extends SerenityReportingTask {}
