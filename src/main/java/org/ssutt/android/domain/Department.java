/*
* Copyright 2014 Plain Solutions
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.ssutt.android.domain;

import java.io.Serializable;

public class Department implements Serializable {
    private String name;
    private String tag;

    public Department(String tag, String name) {
        this.tag = tag;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        Department that = (Department) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (tag != null ? !tag.equals(that.tag) : that.tag != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(name).append(" ").append(tag).append(" ").toString();
    }
}
