package com.kalix.exam.manage.biz.utils;

import com.kalix.framework.core.util.ConfigUtil;

import java.util.LinkedHashMap;

public class ExamBaseConfigUtil {
    private static final String examBaseConfigName = "config.exam.base";
    private static String EXAM_ROOMS_PERSON = null;

    private static void initConfig() {
        EXAM_ROOMS_PERSON = (String) ConfigUtil.getConfigProp("EXAMROOM_PERSON", examBaseConfigName);
    }

    public static LinkedHashMap<String, String> getAllRoomPersonConfigs() {
        initConfig();
        String[] configArr = EXAM_ROOMS_PERSON.split(",");
        LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
        if (configArr != null && configArr.length > 0) {
            for (String config : configArr) {
                String[] roomAndNo = config.split("-");
                if (roomAndNo != null && roomAndNo.length > 0) {
                    configMap.put(roomAndNo[0], roomAndNo[1]);
                }
            }
        }
        return configMap;
    }
}
