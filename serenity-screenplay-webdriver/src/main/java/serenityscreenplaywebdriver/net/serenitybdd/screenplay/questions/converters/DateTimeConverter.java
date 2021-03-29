package serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.converters;

import org.joda.time.DateTime;

public class DateTimeConverter implements Converter<DateTime> {
    @Override
    public DateTime convert(Object value) {
        return DateTime.parse(value.toString());
    }
}
