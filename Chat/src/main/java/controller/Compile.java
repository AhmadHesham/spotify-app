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
            System.out.println("Compiling...");
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(
                    Arrays.asList("Chat/src/main/java/api/commands/" + map.get("className") + ".java"));
            String[] arr = {};
            Iterable<String> options = Arrays.asList(arr);
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null,
                    compilationUnits);
            boolean success = task.call();
            fileManager.close();

            System.out.println("Compiling Success State: " + success);
            File classFile = new File("Chat/src/main/java/api/commands/" + map.get("className") + ".class");
            File newClassFile = new File("Chat/src/main/resources/" + map.get("className") + ".class");
           // classFile.renameTo(newClassFile);
            //classFile.delete();
            File testFile = new File("Chat/src/main/resources/" + map.get("className") + ".class");
            System.out.println(testFile.exists());
            Class<?> currentClass = new CompileHelper().loadClass(map.get("className"));
            CommandsMap.replace(map.get("className"), currentClass);
            System.out.println("Replace Status: Success");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
