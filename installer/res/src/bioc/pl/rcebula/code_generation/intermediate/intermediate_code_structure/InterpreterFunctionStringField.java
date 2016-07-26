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
package pl.rcebula.code_generation.intermediate.intermediate_code_structure;

import pl.rcebula.code_generation.intermediate.intermediate_code_structure.StringField;
import java.io.DataOutputStream;
import java.io.IOException;
import pl.rcebula.code.InterpreterFunction;

/**
 *
 * @author robert
 */
public class InterpreterFunctionStringField extends StringField
{
    private final InterpreterFunction interpreterFunction;

    public InterpreterFunctionStringField(InterpreterFunction interpreterFunction)
    {
        super(interpreterFunction.toString());
        this.interpreterFunction = interpreterFunction;
    }

    @Override
    public void writeToBinaryFile(DataOutputStream dos) throws IOException
    {
        dos.writeByte(interpreterFunction.getOpcode());
    }
}
