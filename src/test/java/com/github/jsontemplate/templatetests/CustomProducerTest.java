package com.github.jsontemplate.templatetests;

import com.github.jsontemplate.JsonTemplate;
import com.github.jsontemplate.jsonbuild.JsonStringNode;
import com.github.jsontemplate.valueproducer.IValueProducer;
import com.github.jsontemplate.valueproducer.StringValueProducer;
import com.jayway.jsonpath.DocumentContext;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.github.jsontemplate.templatetests.TestUtils.parse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CustomProducerTest {

    @Test
    public void testWithNewProducer() {
        JsonTemplate jsonTemplate = new JsonTemplate("{balance:@euro(20)}").withValueProducer(new EuroProducer());
        DocumentContext document = parse(jsonTemplate);
        assertThat(document.read("$.balance", String.class), is("€20"));
    }

    @Test
    public void testWithExtendedProducer() {
        JsonTemplate jsonTemplate = new JsonTemplate("{ref:@s(GOF)}").withValueProducer(new MyStringValueProducer());
        DocumentContext document = parse(jsonTemplate);
        assertThat(document.read("$.ref", String.class), is("[GOF]"));
    }

    private static class MyStringValueProducer extends StringValueProducer {

        @Override
        public JsonStringNode produce(String value) {
            return super.produce("[" + value + "]");
        }
    }

    private static class EuroProducer implements IValueProducer<JsonStringNode> {

        @Override
        public String getTypeName() {
            return "euro";
        }

        @Override
        public JsonStringNode produce() {
            return new JsonStringNode(() -> "€" + new Random().nextInt(100));
        }

        @Override
        public JsonStringNode produce(String value) {
            return new JsonStringNode(() -> "€" + value);
        }

        @Override
        public JsonStringNode produce(List<String> valueList) {
            throw new UnsupportedOperationException();
        }

        @Override
        public JsonStringNode produce(Map<String, String> paramMap) {
            throw new UnsupportedOperationException();
        }
    }
}
