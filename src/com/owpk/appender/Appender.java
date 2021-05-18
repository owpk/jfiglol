package com.owpk.appender;

import com.owpk.Jfiglol;

import java.io.IOException;
import java.util.List;

public interface Appender {
   default String print() {
      return String.format(Jfiglol.VERBOSE_FORMAT, "mode", getName());
   }

   List<String> getResult() throws IOException;

   String getName();
}
