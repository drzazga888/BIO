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
package pl.rcebula.preprocessor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author robert
 */
public class MyFiles
{
    private final List<File> files = new ArrayList<>();
    private int currentNum = 1;

    public MyFiles()
    {
    }
    
    public File addFile(String name)
    {
        int num = currentNum++;
        File file = new File(num, name);
        files.add(file);
        return file;
    }
    
    public File getFromNum(int num)
    {
        for (File f : files)
        {
            if (f.getNum() == num)
            {
                return f;
            }
        }
        
        return null;
    }
    
    public File getFromLine(int line)
    {
        for (File f : files)
        {
            if (f.isInTheRangeOfInterval(line))
            {
                return f;
            }
        }
        
        return null;
    }
    
    public void normalizeIntervals()
    {
        for (File f : files)
        {
            f.normalizeIntervals();
        }
    }
    
    public static class File
    {
        private final int num;
        private final String name;
        private final List<File> fromList = new ArrayList<>();
        private final List<Interval> intervals = new ArrayList<Interval>();

        public File(int num, String name)
        {
            this.num = num;
            this.name = name;
        }

        public boolean isInTheRangeOfInterval(int line)
        {
            for (Interval i : intervals)
            {
                if (line >= i.getStart() && line < i.getEnd())
                {
                    return true;
                }
            }
            
            return false;
        }
        
        public int getStartOfInterval(int line)
        {
            for (Interval i : intervals)
            {
                if (line >= i.getStart() && line < i.getEnd())
                {
                    return i.getStart();
                }
            }
            
            return -1;
        }
        
        public void normalizeIntervals()
        {
            for (int i = 0; i < intervals.size() - 1; )
            {
                Interval i1 = intervals.get(i);
                Interval i2 = intervals.get(i + 1);
                
                // jeżeli koniec i początek są takie same
                // to ustaw koniec pierwszego przedziału na koniec drugiego, a drugi usuń
                if (i1.getEnd() == i2.getStart())
                {
                    i1.setEnd(i2.getEnd());
                    intervals.remove(i2);
                }
                else
                {
                    ++i;
                }
            }
        }
        
        public void addInterval(Interval interval)
        {
            intervals.add(interval);
        }
        
        public void addFrom(File from)
        {
            fromList.add(from);
        }

        public List<File> getFromList()
        {
            return fromList;
        }
        
        public int getNum()
        {
            return num;
        }

        public String getName()
        {
            return name;
        }
        
        public static class Interval
        {
            private int start;
            private int end;

            public Interval(int start, int end)
            {
                this.start = start;
                this.end = end;
            }
            
            public int getStart()
            {
                return start;
            }

            public int getEnd()
            {
                return end;
            }

            public void setStart(int start)
            {
                this.start = start;
            }

            public void setEnd(int end)
            {
                this.end = end;
            }
        }
    }
}