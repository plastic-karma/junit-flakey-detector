package de.plastickarma.flakeyunit.json;


import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.plastickarma.flakeyunit.FlakeyTestIndicatorBuilder;
import junit.framework.AssertionFailedError;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.StringWriter;

import static de.plastickarma.flakeyunit.UnittestHelper.createFlakeyTest;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNull.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Test cases for json serialization.
 */
public class FlakeyTestcaseJsonFileSerializerTest {

    @Test public void flakeyTestSerializesToJson() throws Throwable {
        String output;
        try (final StringWriter writer = new StringWriter()) {
            FlakeyTestcaseJsonSerializer jsonSerializer = new FlakeyTestcaseJsonSerializer(writer);
            final TestRule indicator = FlakeyTestIndicatorBuilder.indicator().listener(jsonSerializer).build();

            try {
                createFlakeyTest(indicator, FlakeyTestcaseJsonFileSerializerTest.class, "flakey1").evaluate();
                fail("Exception expected");
            } catch (AssertionFailedError throwable) {
                // Expected
            }
            output = writer.toString();
        }

        assertThat(output, is(notNullValue()));
        final JsonObject jsonObject = new GsonBuilder().create().fromJson(output, JsonObject.class);

        assertThat(jsonObject.get("methodName").getAsString(), is("flakey1"));
        assertThat(jsonObject.get("className").getAsString(), is(this.getClass().getName()));
        assertThat(jsonObject
                .get("originalException")
                .getAsJsonObject()
                .get("exceptionClass")
                .getAsString(),
            is(AssertionFailedError.class.getName()));
    }
}
