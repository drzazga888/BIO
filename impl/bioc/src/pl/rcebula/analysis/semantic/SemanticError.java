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
package pl.rcebula.analysis.semantic;

/**
 *
 * @author robert
 */
public class SemanticError extends Exception
{
    public SemanticError(String message)
    {
        super(message);
    }
    
    public SemanticError(int line, int chNum, String message)
    {
        super("[line: " + line + ", character: " + chNum + "]: " + message);
    }
    
    public SemanticError(int line1, int chNum1, String message1, 
            int line2, int chNum2, String message2)
    {
        super("[line: " + line1 + ", character: " + chNum1 + "]: " + message1 + 
                "[line: " + line2 + ", character: " + chNum2 + "]: " + message2);
    }
}