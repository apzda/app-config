/*
 * Copyright (C) 2023-2023 Fengz Ning (windywany@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.apzda.cloud.config.service;

import com.apzda.cloud.config.Setting;
import com.apzda.cloud.config.exception.SettingUnavailableException;
import com.google.common.base.Splitter;
import lombok.val;
import org.springframework.lang.NonNull;

/**
 * @author fengz (windywany@gmail.com)
 * @version 1.0.0
 * @since 1.0.0
 **/
public interface SettingService {

    <T extends Setting> T load(@NonNull Class<T> tClass, @NonNull String tenantId) throws SettingUnavailableException;

    default <T extends Setting> T load(@NonNull Class<T> tClass) throws SettingUnavailableException {
        return load(tClass, "0");
    }

    <T extends Setting> boolean save(@NonNull T setting, @NonNull String tenantId) throws SettingUnavailableException;

    default <T extends Setting> boolean save(@NonNull T setting) throws SettingUnavailableException {
        return save(setting, "0");
    }

    void refresh(String key);

    @SuppressWarnings("unchecked")
    static SettingMeta getSettingMeta(String key) {
        val keys = Splitter.on("@").omitEmptyStrings().trimResults().splitToList(key);
        var sClass = key;
        var tenantId = "0";
        if (keys.size() > 1) {
            sClass = keys.get(0);
            tenantId = keys.get(1);
        }
        try {
            val aClass = Class.forName(sClass);
            if (Setting.class.isAssignableFrom(aClass)) {
                return new SettingMeta((Class<? extends Setting>) aClass, sClass, tenantId);
            }
        }
        catch (ClassNotFoundException ignored) {
        }
        return new SettingMeta(null, sClass, tenantId);
    }

    record SettingMeta(Class<? extends Setting> settingClass, String clazz, String tenantId) {
        public String getSettingKey() {
            return clazz + "@" + tenantId;
        }
    }

}
