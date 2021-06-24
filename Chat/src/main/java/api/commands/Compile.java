package api.commands;

import java.io.File;

import javax.tools.JavaCompiler;
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
            File sourceFile = new File("api.commands." + map.get("className"));
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            System.out.println();
            System.out.println("Compiled Successfully!");
            compiler.run(null, null, null, sourceFile.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
