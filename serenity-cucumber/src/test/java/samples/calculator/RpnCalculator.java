package samples.calculator;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

public class RpnCalculator {
    private final Deque<Integer> stack = new LinkedList<>();
    private static final List<String> OPS = asList("-", "+", "*", "/");

    public void push(Object arg) {
        if (OPS.contains(arg)) {
            Integer y = stack.removeLast();
            Integer x = (stack.isEmpty() ? 0 : stack.removeLast());
            Integer val = null;
            if (arg.equals("-")) {
                val = x - y;
            } else if (arg.equals("+")) {
                val = x + y;
            } else if (arg.equals("*")) {
                val = x * y;
            } else if (arg.equals("/")) {
                val = x / y;
            }
            push(val);
        } else {
            stack.add((Integer) arg);
        }
    }

    public Integer value() {
        return stack.getLast();
    }
}
