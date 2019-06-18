# README #

This is a simple Junit test rule to detect flakey testcases. You have to add a method with a org.junit.Rule annotation to your test class. Potential flakey test cases will be passed to a listener for further processing.

```
@Rule
public TestRule flakeyTestcaseIndicator() {
    return FlakeyTestIndicatorBuilder.indicator()
                .listener(...)
                //...
                .retries(...)
                .build();
    }
```
