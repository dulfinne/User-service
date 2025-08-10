package com.dulfinne.randomgame.userservice.util;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public final class LoggingUtils {
  private LoggingUtils() {}

  public static String getDifferencesStringForRecord(Object oldObj, Object newObj) {
    RecordComponent[] components = oldObj.getClass().getRecordComponents();

    return Arrays.stream(components)
        .map(
            component -> {
              try {
                Object oldValue = component.getAccessor().invoke(oldObj);
                Object newValue = component.getAccessor().invoke(newObj);

                if (!Objects.equals(oldValue, newValue)) {
                  return component.getName() + ": " + oldValue + " -> " + newValue;
                }
              } catch (Exception e) {
                return null;
              }
              return null;
            })
        .filter(Objects::nonNull)
        .collect(Collectors.joining("; "));
  }
}
