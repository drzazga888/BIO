/*
 * Copyright (C) 2016 robert
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package analysis.tree;

import java.util.Objects;

/**
 *
 * @author robert
 */
public abstract class CallParam
{
    private final Integer line;
    private final Integer chNum;

    public CallParam(Integer line, Integer chNum)
    {
        this.line = line;
        this.chNum = chNum;
    }

    public Integer getLine()
    {
        return line;
    }

    public Integer getChNum()
    {
        return chNum;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.line);
        hash = 37 * hash + Objects.hashCode(this.chNum);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final CallParam other = (CallParam)obj;
        if (!Objects.equals(this.line, other.line))
        {
            return false;
        }
        if (!Objects.equals(this.chNum, other.chNum))
        {
            return false;
        }
        return true;
    }
}