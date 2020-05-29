package net.thucydides.core.steps;

import com.google.common.eventbus.Subscribe;
import net.thucydides.core.events.TestLifecycleEvents;
import net.thucydides.core.model.DataTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExampleTables {

    private static ThreadLocal<ExampleTables> currentExampleTable = new ThreadLocal<>();

    private final List<String> headers;
    private final String title;

    private DataTable table;

    public DataTable getTable() {
        if (table == null) {
            table = DataTable.withHeaders(this.headers)
                    .andTitle(this.title).build();
        }
        return table;
    }

    ExampleTables(String title, String[] headers) {
        this.title = title;
        this.headers = Arrays.asList(headers);
    }

    public List<String> getHeaders() {
        return new ArrayList<>(headers);
    }

    public static WithHeaders useExampleTable() {
        return new ExampleTableBuilder();
    }

    public static boolean isUsingAnExampleTable() {
        return currentExampleTable.get() != null;
    }

    public static ExampleTables getCurrentExampleTable() {
        return currentExampleTable.get();
    }

    public static void clear() {
        if (currentExampleTable.get() != null) {
            currentExampleTable.get().clearTable();
        }
        currentExampleTable.remove();
    }

    private void clearTable() {
        table = null;
    }

    public void start() {
        DataTable table = DataTable.withHeaders(this.headers)
                .andTitle(this.title).build();
        System.out.println("EXAMPLE TABLES: START - " + table);
        StepEventBus.getEventBus().useExamplesFrom(table);
        StepEventBus.getEventBus().enableSoftAsserts();

        TestLifecycleEvents.register(this);
    }

    @Subscribe
    public void testFinishes(TestLifecycleEvents.TestFinished testFinished) {
        TestLifecycleEvents.unregister(this);
        clear();
    }

    public interface WithHeaders {
        OptionalFields withHeaders(String... headers);
    }

    public interface OptionalFields {
        OptionalFields andTitle(String title);

        void start();
    }

    static class ExampleTableBuilder implements WithHeaders, OptionalFields {
        private String[] headers;
        private String title;

        public OptionalFields withHeaders(String... headers) {
            this.headers = headers;
            return this;
        }

        public OptionalFields andTitle(String title) {
            this.title = title;
            return this;
        }

        public void start() {
            ExampleTables exampleTables = new ExampleTables(title, headers);
            exampleTables.start();
            currentExampleTable.set(exampleTables);
        }
    }
}