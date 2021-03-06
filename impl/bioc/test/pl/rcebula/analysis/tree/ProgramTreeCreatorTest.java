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
package pl.rcebula.analysis.tree;

import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.lexer.TokenType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.error_report.MyFiles;

/**
 *
 * @author robert
 */
public class ProgramTreeCreatorTest
{
    
    public ProgramTreeCreatorTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
    }
    
    @After
    public void tearDown()
    {
    }

    private ErrorInfo generateErrorInfo(int lineNum, int chNum)
    {
        return new ErrorInfo(lineNum, chNum, new MyFiles.File(0, "test"));
    }
    
    @Test
    public void testHelloWorld()
    {
        System.out.println("testHelloWorld");
        
        List<Token<?>> inputTokens = new ArrayList<Token<?>>()
        {{
                    add(new Token<>(TokenType.KEYWORD, "def", generateErrorInfo(1, 1))); // 0
                    add(new Token<>(TokenType.ID, "onSTART", generateErrorInfo(1, 5))); // 1
                    add(new Token<>(TokenType.OPEN_BRACKET, null, generateErrorInfo(1, 12))); // 2
                    add(new Token<>(TokenType.CLOSE_BRACKET, null, generateErrorInfo(1, 13))); // 3
                    add(new Token<>(TokenType.ID, "PRINT", generateErrorInfo(2, 5))); // 4
                    add(new Token<>(TokenType.OPEN_BRACKET, null, generateErrorInfo(2, 10))); // 5
                    add(new Token<>(TokenType.STRING, "Hello World!\n", generateErrorInfo(2, 11))); // 6
                    add(new Token<>(TokenType.CLOSE_BRACKET, null, generateErrorInfo(2, 27))); // 7
                    add(new Token<>(TokenType.KEYWORD, "end", generateErrorInfo(3, 1))); // 8
                    add(new Token<>(TokenType.END, null, generateErrorInfo(4, 1))); // 9
        }};
        
        List<Integer> inputSteps = Arrays.asList(0, 3, 5, 9, 12, 13, 15, 19, 17, 11, 2);
        
        ProgramTree expectedProgramTree = new ProgramTree();
        UserFunction uf = new UserFunction("onSTART", generateErrorInfo(1, 5));
        Call c = new Call("PRINT", null, generateErrorInfo(2, 5));
        c.addCallParam(new ConstCallParam(inputTokens.get(6), generateErrorInfo(2, 11)));
        uf.addCall(c);
        expectedProgramTree.addUserFunction(uf);
        
        ProgramTreeCreator programTreeCreator = new ProgramTreeCreator(inputTokens, inputSteps);
        ProgramTree pt = programTreeCreator.getProgramTree();
        
        assertEquals(expectedProgramTree, pt);
    }
    
    @Test
    public void testNestedFunctionsAndParams()
    {
        System.out.println("testNestedFunctionsAndParams");
        
        List<Token<?>> inputTokens = new ArrayList<Token<?>>()
        {
            {
                add(new Token<>(TokenType.KEYWORD, "def", generateErrorInfo(1, 1))); // 0
                add(new Token<>(TokenType.ID, "onSTART", generateErrorInfo(1, 5))); // 1
                add(new Token<>(TokenType.OPEN_BRACKET, null, generateErrorInfo(1, 12))); // 2
                add(new Token<>(TokenType.CLOSE_BRACKET, null, generateErrorInfo(1, 13))); // 3

                add(new Token<>(TokenType.ID, "CALL2", generateErrorInfo(2, 5))); // 4
                add(new Token<>(TokenType.OPEN_BRACKET, null, generateErrorInfo(2, 10))); // 5

                add(new Token<>(TokenType.ID, "PRINT", generateErrorInfo(3, 9))); // 6
                add(new Token<>(TokenType.OPEN_BRACKET, null, generateErrorInfo(3, 14))); // 7
                add(new Token<>(TokenType.ID, "ADD", generateErrorInfo(3, 15))); // 8
                add(new Token<>(TokenType.OPEN_BRACKET, null, generateErrorInfo(3, 18))); // 9
                add(new Token<>(TokenType.INT, 2, generateErrorInfo(3, 19))); // 10
                add(new Token<>(TokenType.COMMA, null, generateErrorInfo(3, 20))); // 11
                add(new Token<>(TokenType.INT, 4, generateErrorInfo(3, 21))); // 12
                add(new Token<>(TokenType.CLOSE_BRACKET, null, generateErrorInfo(3, 22))); // 13
                add(new Token<>(TokenType.CLOSE_BRACKET, null, generateErrorInfo(3, 23))); // 14
                add(new Token<>(TokenType.COMMA, null, generateErrorInfo(3, 24))); // 15

                add(new Token<>(TokenType.ID, "PRINT", generateErrorInfo(4, 9))); // 16
                add(new Token<>(TokenType.OPEN_BRACKET, null, generateErrorInfo(4, 14))); // 17
                add(new Token<>(TokenType.STRING, "\nwynik", generateErrorInfo(4, 15))); // 18
                add(new Token<>(TokenType.CLOSE_BRACKET, null, generateErrorInfo(4, 22))); // 19
                add(new Token<>(TokenType.CLOSE_BRACKET, null, generateErrorInfo(4, 23))); // 20

                add(new Token<>(TokenType.KEYWORD, "end", generateErrorInfo(5, 5))); // 21
                add(new Token<>(TokenType.END, null, generateErrorInfo(6, 1))); // 22
            }
        };

        List<Integer> inputSteps = Arrays.asList(0, 3, 5, 9, 12, 13, 15, 18, 20, 13, 15, 18, 20, 13, 15,
                19, 16, 15, 19, 17, 17, 16, 15, 18, 20, 13, 15, 19, 17, 17, 11, 2);
        
        ProgramTree expectedProgramTree = new ProgramTree();
        UserFunction uf = new UserFunction("onSTART", generateErrorInfo(1, 5));
        
        Call c = new Call("CALL2", null, generateErrorInfo(2, 5));
        Call c1 = new Call("PRINT", c, generateErrorInfo(3, 9));
        c.addCallParam(c1);
        Call c11 = new Call("ADD", c1, generateErrorInfo(3, 15));
        c1.addCallParam(c11);
        c11.addCallParam(new ConstCallParam(inputTokens.get(10), generateErrorInfo(3, 19)));
        c11.addCallParam(new ConstCallParam(inputTokens.get(12), generateErrorInfo(3, 21)));
        Call c2 = new Call("PRINT", c, generateErrorInfo(4, 9));
        c.addCallParam(c2);
        c2.addCallParam(new ConstCallParam(inputTokens.get(18), generateErrorInfo(4, 15)));
        
        uf.addCall(c);
        expectedProgramTree.addUserFunction(uf);
        
        ProgramTreeCreator programTreeCreator = new ProgramTreeCreator(inputTokens, inputSteps);
        ProgramTree pt = programTreeCreator.getProgramTree();
        
        assertEquals(expectedProgramTree, pt);
    }
}
