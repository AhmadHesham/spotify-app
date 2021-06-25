package api.commands;

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
            // File root = new File("/commands");
            // File sourceFile = new File(root, map.get("className"));
            // JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            // compiler.run(null, null, null, sourceFile.getPath());
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
            Iterable<? extends JavaFileObject> compilationUnits = fileManager
                    .getJavaFileObjectsFromStrings(Arrays.asList("Chat/src/main/java/api/commands/" + map.get("className") + ".java"));
            String[] arr = {"-d", "./chat/target/classes/"};
            Iterable<String> options = Arrays.asList(arr);
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, compilationUnits);
            boolean success = task.call();
            fileManager.close();
            System.out.println("Compiling Success State: " + success);

            CommandsMap.replace(map.get("className"), Class.forName("api.commands." + map.get("className")));
            System.out.println("Replace Status: Success" );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
