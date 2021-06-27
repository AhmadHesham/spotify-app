package controller;


import java.io.File;
import java.util.Arrays;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import api.Command;
import config.STATUSCODES;

public class Compile extends Command {

    @Override
    public String authorize() throws Exception {
        return STATUSCODES.SUCCESS;
    }

    @Override
    public void execute() throws Exception {
        try {
            CompileHelper.getCompileHelper().compile(map.get("className"));
            //System.out.println("Compiling Success State: " + success);
            //File classFile = new File("Chat/src/main/java/api/commands/" + map.get("className") + ".class");
            //File newClassFile = new File("Chat/src/main/resources/" + map.get("className") + ".class");
            //classFile.renameTo(newClassFile);
            //classFile.delete();
            //File testFile = new File("Chat/src/main/resources/" + map.get("className") + ".class");
            //System.out.println(testFile.exists());
            //Class<?> currentClass = new CompileHelper().loadClass(map.get("className"));
            //api.commands.CommandsMap.replace(map.get("className"), currentClass);
            //System.out.println("Replace Status: Success");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
