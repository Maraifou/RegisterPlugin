package org.plugin.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UserMetaDataManager {
    private static final Map<String, Boolean> registrationStatusMap = new ConcurrentHashMap<String, Boolean>();

    public static boolean isLogIn(String  uuid) {
        return registrationStatusMap.getOrDefault(uuid, false);
    }

    public static void editStatus(String uuid, boolean status) {
        registrationStatusMap.put(uuid, status);
    }



}
