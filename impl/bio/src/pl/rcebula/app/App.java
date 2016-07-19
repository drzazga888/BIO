/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.app;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import pl.rcebula.intermediate_code.IntermediateCode;
import pl.rcebula.internals.Interpreter;
import pl.rcebula.modules.BuiltinFunctions;
import pl.rcebula.tools.IProfiler;
import pl.rcebula.tools.NullProfiler;
import pl.rcebula.tools.Profiler;
import pl.rcebula.utils.Opts;
import pl.rcebula.utils.OptsError;
import pl.rcebula.utils.TimeProfiler;

/**
 *
 * @author robert
 */
public class App
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            // init logger
            initLogger();

            // time profiler
            TimeProfiler timeProfiler = new TimeProfiler(true, true);
            timeProfiler.startTotal();
            
            // Opts
            timeProfiler.start("Opts");
            Opts opts = new Opts(args);
            timeProfiler.stop();
            
            // read intermediate code
            timeProfiler.start("ReadIntermediateCode");
            IntermediateCode ic = new IntermediateCode(opts.getInputFilePath());
            timeProfiler.stop();
            
            if (opts.isDisassemble())
            {
                System.out.println("DISASSEMBLE CODE");
                System.out.println("-------------------------");
                System.out.println(ic.toStringWithLineNumbers());
            }
            
            if (opts.isRun())
            {
                IProfiler profiler = new NullProfiler();
                if (opts.isProfiler())
                {
                    profiler = new Profiler();
                }
                
                // builtin functions
                timeProfiler.start("BuiltinFunctions");
                BuiltinFunctions builtinFunctions = new BuiltinFunctions();
                timeProfiler.stop();
                // run interpreter
                Interpreter interpreter = new Interpreter(ic.getUserFunctions(), builtinFunctions, timeProfiler, profiler);
                
                // show modules time
                timeProfiler.stopTotal();
                if (opts.isTimes())
                {
                    System.out.println("TIMES");
                    System.out.println("-------------------------");
                    System.out.println(timeProfiler.toString());
                }
                
                // show profiler statistics
                if (opts.isProfiler())
                {
                    System.out.println("PROFILER");
                    System.out.println("-------------------------");
                    System.out.println(profiler.toString());
                }
            }
            else
            {
                // show modules time
                timeProfiler.stopTotal();
                if (opts.isTimes())
                {
                    System.out.println("TIMES");
                    System.out.println("-------------------------");
                    System.out.println(timeProfiler.toString());
                }
            }
        }
        catch (OptsError ex)
        {
            System.err.println("Options error: " + ex.getMessage());
        }
        catch (IOException ex)
        {
            System.err.println("IOException: " + ex.getMessage());
        }
    }

    private static void initLogger() throws IOException
    {
        Logger logger = Logger.getGlobal();

        String path = App.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf("/"));
        FileHandler fh = new FileHandler(decodedPath + "/log.txt");
        logger.addHandler(fh);
        SimpleFormatter sf = new SimpleFormatter();
        fh.setFormatter(sf);

        logger.setUseParentHandlers(false);
        logger.setLevel(Level.FINE);

        logger.info("Init logger");
    }
}
