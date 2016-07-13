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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author robert
 */
public class Preprocessor
{
    public final static String startOfDirective = "#";
    public final static String includeDirectiveRegex = "^" + startOfDirective + "INCLUDE\\(\\\"(.*)\\\"\\)$";
    private final static Pattern includePattern = Pattern.compile(includeDirectiveRegex);

    private final String input;
    private final List<Path> includedPaths = new ArrayList<>();
    private Path currentPath;

    public Preprocessor(String path)
            throws IOException, PreprocessorError
    {
        Path p = Paths.get(path);
        List<String> lines = readExternalFile(p, "UTF-8", -1);

        input = linesToString(analyse(lines));
    }

    public Preprocessor(List<String> lines)
            throws PreprocessorError, IOException
    {
        input = linesToString(analyse(lines));
    }

    private List<String> analyse(List<String> lines)
            throws PreprocessorError, IOException
    {
        List<String> resultLines = new ArrayList<>();

        int it = 1;
        for (String line : lines)
        {
            // jeżeli dyrektywa preprocesora
            if (line.trim().startsWith(startOfDirective))
            {
                // ścieżka do aktualnego pliku (używana przy wypisywaniu błędów)
                String file = currentPath.toAbsolutePath().normalize().toString();
                
                // obcinamy początkowe i końcowe białe znaki
                line = line.trim();
                // sprawdzamy czy linia pasuje do wzorca
                Matcher m = includePattern.matcher(line);
                if (m.find())
                {
                    // pobieramy ścieżkę do pliku
                    String filePath = m.group(1);
                    Path p;
                    // próbujemy utworzyć obiekt Path
                    try
                    {
                        p = Paths.get(filePath);
                    }
                    catch (InvalidPathException ex)
                    {
                        String message = "Bad file path in directive: " + line;
                        throw new PreprocessorError(file, message, it);
                    }
                    Path finalPath;
                    //  jeżeli podana ścieżka jest absolutna nie robimy z nią nic
                    if (p.isAbsolute())
                    {
                        finalPath = p;
                    }
                    // inaczej tworzymy ścieżkę relatywną do aktualnej
                    else
                    {
                        finalPath = currentPath.toAbsolutePath().getParent().resolve(p);
                    }
                    // zapisujemy currentPath
                    Path tmpPath = currentPath;
                    // wczytujemy linijki z pliku i przetwarzamy rekurencyjnie
                    List<String> tmpLines = readExternalFile(finalPath, "UTF-8", it);
                    tmpLines = analyse(tmpLines);
                    
                    // dodajemy linijki do wynikowych
                    for (String tmpLine : tmpLines)
                    {
                        resultLines.add(tmpLine);
                    }
                    
                    // odtwarzamy currentPath
                    currentPath = tmpPath;
                }
                else
                {
                    String message = "Bad preprocessor directive: " + line;
                    throw new PreprocessorError(file, message, it);
                }
            }
            // inaczej
            else
            {
                resultLines.add(line);
            }
            
            ++it;
        }

        return resultLines;
    }

    public String getInput()
    {
        return input;
    }

    private String linesToString(List<String> lines)
    {
        String result = "";

        for (String str : lines)
        {
            result += str + "\n";
        }

        return result;
    }

    private List<String> readExternalFile(Path p, String encoding, int line)
            throws IOException, PreprocessorError
    {
        // sprawdzamy czy plik istnieje
        if (!Files.exists(p))
        {
            // jeżeli nie to rzucamy wyjątek
            String message = "File " + p.toAbsolutePath().normalize().toString() + " doesn't exist";
            if (currentPath != null)
            {
                String file = currentPath.toAbsolutePath().normalize().toString();
                throw new PreprocessorError(file, message, line);
            }
            else
            {
                throw new PreprocessorError(message);
            }
        }
        
        if (!checkIfSameFile(includedPaths, p))
        {
            currentPath = p;
            includedPaths.add(p);
            String filePath = p.toAbsolutePath().toString();
            InputStream is = new FileInputStream(filePath);
            return readInputStreamAsLines(is, encoding);
        }
        
        return new ArrayList<>();
    }
    
    private boolean checkIfSameFile(List<Path> paths, Path p)
            throws IOException
    {
        for (Path pp : paths)
        {
            if (Files.isSameFile(pp, p))
            {
                return true;
            }
        }
        
        return false;
    }

    private List<String> readInternalFile(String path, String encoding)
            throws IOException
    {
        InputStream is = getClass().getResourceAsStream(path);
        return readInputStreamAsLines(is, encoding);
    }

    private String readInputStreamToString(InputStream is, String encoding)
    {
        try (Scanner s = new Scanner(is, encoding))
        {
            return s.useDelimiter("\\A").hasNext() ? s.next() : "";
        }
    }

    private List<String> readInputStreamAsLines(InputStream is, String encoding)
            throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line = null;

        List<String> lines = new ArrayList<>();
        while ((line = in.readLine()) != null)
        {
            lines.add(line);
        }

        return lines;
    }
}