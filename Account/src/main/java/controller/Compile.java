package controller;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;

import javax.tools.*;
import java.util.Arrays;

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
            String type = map.get("type");
            String commandPrefix = type.equals("user") ? "Account/src/main/java/api/commands/"
                    : "Account/src/main/java/controller/";
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
            Iterable<? extends JavaFileObject> compilationUnits = fileManager
                    .getJavaFileObjectsFromStrings(Arrays.asList(commandPrefix + fileName + ".java"));
            String destination = type.equals("user") ? "Account/target/classes/" : "Account/target/classes/";
            String[] optionsArr = {"-d", destination};
            Iterable<String> options = Arrays.asList(optionsArr);
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null,
                    compilationUnits);
            boolean success = task.call();
            System.out.println("Compilation Status: " + success);

            CompileHelper helper = new CompileHelper();
            helper.folderPath = type.equals("user") ? "api/commands/" : "controller/";
            helper.packagePath = type.equals("user") ? "api.commands." : "controller.";
            Class<?> loadedClass = helper.loadClass(fileName);
            if (type.equals("user")) {
                api.commands.CommandsMap.replace(methodName, loadedClass, fileName);
            } else {
                CommandsMapController.replace(methodName, loadedClass, fileName);
            }
            ResponseHandler.handleResponse("Class " + fileName + " Compiled and Changed!", map.get("queue"),
                    map.get("correlation_id"));
            // new File("Chat/src/main/java/api/commands/" + fileName + ".class").delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
