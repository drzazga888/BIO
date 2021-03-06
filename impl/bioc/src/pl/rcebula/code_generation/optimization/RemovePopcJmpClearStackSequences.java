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
package pl.rcebula.code_generation.optimization;

import pl.rcebula.code.InterpreterFunction;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Label;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.LabelField;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Line;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.StringField;

/**
 *
 * @author robert
 */
public class RemovePopcJmpClearStackSequences implements IOptimizer
{
    private final IntermediateCode ic;
    private final OptimizationStatistics statistics;
    private boolean optimize = false;

    public RemovePopcJmpClearStackSequences(IntermediateCode ic, OptimizationStatistics statistics)
    {
        this.ic = ic;
        this.statistics = statistics;
        
        analyseAndRemove();
    }

    @Override
    public boolean isOptimized()
    {
        return optimize;
    }
    
    private void analyseAndRemove()
    {
        int c = 0;
        
        while (c < ic.numberOfLines())
        {
            Line line = ic.getLine(c);
            
            // komenda popc zawsze zawiera ilość usuwanych parametrów ze stosu, więc co najmniej dwa pola
            if (line.numberOfFields() > 1)
            {
                String funName = ((StringField)line.getField(0)).getStr();
                // jeżeli popc
                if (funName.equals(InterpreterFunction.POPC.toString()))
                {
                    // sprawdzamy czy linie poniżej jest komenda jmp
                    line = ic.getLine(c + 1);
                    // jmp zawsze zawiera cel skoku, czyli co najmniej dwa pola
                    if (line.numberOfFields() > 1)
                    {
                        funName = ((StringField)line.getField(0)).getStr();
                        // jeżeli JMP
                        if (funName.equals(InterpreterFunction.JMP.toString()))
                        {
                            Label label = ((LabelField)line.getField(1)).getLabel();
                            // sprawdzamy czy linijka do której skacze ten jmp to clear_stack
                            line = ic.getLine(label.getLine());
                            
                            funName = ((StringField)line.getField(0)).getStr();
                            if (funName.equals(InterpreterFunction.CLEAR_STACK.toString()))
                            {
                                // usuń linię c
                                ic.removeLine(c);
                                statistics.addPopcJmpClearStackRemoved();
                                optimize = true;
                            }
                        }
                    }
                }
            }
            
            // inkrementujemy linijkę bez względu na to czy udało się usunąć czy nie 
            // (jeżeli udało się usunąć to wiemy, że następną linijką po usuniętej było JMP, które możemy ominąć
            ++c;
        }
    }
}
