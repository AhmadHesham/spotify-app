package controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class CompileHelper extends ClassLoader {
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            //InputStream in = CompileHelper.class.getResourceAsStream("Chat/src/main/resources/" + name + ".class");
            InputStream in = new FileInputStream(new File("Chat/src/main/resources/" + name + ".class"));
            byte[] buff = new byte[10000];
            int len = in.read(buff);
            return defineClass(name, buff, 0, len);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return this.getParent().loadClass(name);
    }

}
