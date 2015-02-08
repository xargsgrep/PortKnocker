/*
 *  Copyright 2014 Ahsan Rabbani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xargsgrep.portknocker.utils;

public class StringUtils
{
    public static boolean contains(String stringToCheck, String str)
    {
        return (stringToCheck == null) ? false : stringToCheck.contains(str);
    }

    // String.isEmpty only available starting API 9
    public static boolean isEmpty(String str)
    {
        return (str == null || str.length() == 0);
    }

    public static boolean isBlank(String str)
    {
        return (str == null || isEmpty(str.trim()));
    }

    public static boolean isNotBlank(String str)
    {
        return !isBlank(str);
    }
}
