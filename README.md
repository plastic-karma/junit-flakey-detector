# README #

This is a simple Junit test rule to detect flakey testcases. You have to add a method with a org.junit.Rule annotation to your test class. Potential flakey test cases will be propagated to a listener and from there you can do whatever you think is necessary.

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
