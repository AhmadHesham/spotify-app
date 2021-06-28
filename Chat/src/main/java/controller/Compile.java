package controller;

import java.io.File;
import java.util.Arrays;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;

public class Compile extends Command {

    @Override
    public String authorize() throws Exception {
        return STATUSCODES.SUCCESS;
    }

    @Override
    public void execute() throws Exception {
        try {
            String fileName = map.get("className");
            String methodName = map.get("methodName");
            String commandPrefix = "Chat/src/main/java/api/commands/";
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
            Iterable<? extends JavaFileObject> compilationUnits = fileManager
                    .getJavaFileObjectsFromStrings(Arrays.asList(commandPrefix + fileName + ".java"));
            String[] optionsArr = {};
            Iterable<String> options = Arrays.asList(optionsArr);
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null,
                    compilationUnits);
            boolean success = task.call();
            System.out.println("Compilation Status: " + success);

            new File(commandPrefix + fileName + ".class")
                    .renameTo(new File("Chat/src/main/resources/" + fileName + ".class"));
            Thread.sleep(3000);
            Class<?> loadedClass = new CompileHelper().loadClass(fileName);
            api.commands.CommandsMap.replace(methodName, loadedClass, fileName);
            ResponseHandler.handleResponse("Class " + fileName + " Compiled and Changed!", map.get("queue"), map.get("correlation_id"));
            new File("Chat/src/main/resources/" + fileName + ".class").delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
