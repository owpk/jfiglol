package com.owpk.appender;

import com.owpk.Jfiglol;

import java.io.IOException;
import java.util.List;

public interface Appender {

    List<String> getResult() throws IOException;

}
