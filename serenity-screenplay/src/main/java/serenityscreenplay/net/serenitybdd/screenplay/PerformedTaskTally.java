package serenityscreenplay.net.serenitybdd.screenplay;

public class PerformedTaskTally {
        int performedTasks = 0;

        public void reset() {
            this.performedTasks = 0;
        }

        public void newTask() {
            this.performedTasks++;
        }

        public int getPerformedTaskCount() {
            return performedTasks;
        }
    }