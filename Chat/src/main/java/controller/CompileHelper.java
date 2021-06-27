package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class CompileHelper extends ClassLoader {
    private static CompileHelper compiler;

    public static CompileHelper getCompileHelper() {
        if (compiler == null) {
            compiler = new CompileHelper();
        }
        return compiler;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            // InputStream in =
            // CompileHelper.class.getResourceAsStream("Chat/src/main/resources/" + name +
            // ".class");
            // InputStream in = new FileInputStream(new
            // File("Chat/src/main/java/api/commands/" + name + ".class"));
            // InputStream in = new FileInputStream(new File("Chat/src/main/java/" + name +
            // ".class"));
            // byte[] buff = new byte[10000];
            // int len = in.read(buff);
            // return defineClass(name, buff, 0, len);
            Class<?> cls = new URLClassLoader(new URL[] { new File("Chat/src/main/java/").toURI().toURL() })
                    .loadClass(name);
            return cls;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.getParent().loadClass(name);
    }

    public void compile(String fileName) throws Exception {
        String commandPrefix = "Chat/src/main/java/api/commands/";
        String prefix = "Chat/src/main/java/";
        File classFile = new File(commandPrefix + fileName + ".java");
        File newClassFile = new File(prefix + fileName + ".java");
        classFile.renameTo(newClassFile);
        classFile.delete();
        System.out.println("Compiling");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager
                .getJavaFileObjectsFromStrings(Arrays.asList(prefix + fileName + ".java"));
        String[] optionsArr = {};
        Iterable<String> options = Arrays.asList(optionsArr);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, compilationUnits);
        boolean success = task.call();
        System.out.println("Compilation Status: " + success);

        Class<?> loadedClass = this.loadClass(fileName);
        File returnFile = new File(commandPrefix + fileName + ".java");
        newClassFile.renameTo(returnFile);
        new File(prefix + fileName + ".class").delete();
        api.commands.CommandsMap.replace(fileName, loadedClass);
    }

}
