package net.serenitybdd.cucumber.util;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class BigDecimalAverageCollector implements Collector<BigDecimal, BigDecimalAverageCollector.BigDecimalAccumulator, BigDecimal> {

    private BigDecimalAverageCollector() {
    }

    public static BigDecimalAverageCollector create() {
        return new BigDecimalAverageCollector();
    }

    @Override
    public Supplier<BigDecimalAccumulator> supplier() {
        return BigDecimalAccumulator::new;
    }

    @Override
    public BiConsumer<BigDecimalAccumulator, BigDecimal> accumulator() {
        return BigDecimalAccumulator::add;
    }

    @Override
    public BinaryOperator<BigDecimalAccumulator> combiner() {
        return BigDecimalAccumulator::combine;
    }

    @Override
    public Function<BigDecimalAccumulator, BigDecimal> finisher() {
        return BigDecimalAccumulator::getAverage;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

    static class BigDecimalAccumulator {

        public BigDecimal sum = BigDecimal.ZERO;
        public BigDecimal count = BigDecimal.ZERO;

        public BigDecimalAccumulator() {
        }


        private BigDecimalAccumulator(BigDecimal sum, BigDecimal count) {
            sum.add(sum);
            count.add(count);
        }

        public static BigDecimalAccumulator accumulateWith(BigDecimal sum, BigDecimal count) {
            return new BigDecimalAccumulator(sum, count);
        }

        BigDecimal getAverage() {
            return BigDecimal.ZERO.compareTo(count) == 0 ?
                   BigDecimal.ZERO :
                   sum.divide(count, 2, BigDecimal.ROUND_HALF_UP);
        }

        BigDecimalAccumulator combine(BigDecimalAccumulator another) {
            return accumulateWith(
                sum.add(another.sum),
                count.add(another.count)
            );
        }

        void add(BigDecimal successRate) {
            count = count.add(BigDecimal.ONE);
            sum = sum.add(successRate);
        }
    }
}