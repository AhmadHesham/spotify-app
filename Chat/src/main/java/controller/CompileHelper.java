package controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CompileHelper extends ClassLoader {
    public String folderPath;

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            List<String> filenames = new ArrayList<>();
            System.out.println("Enterd name " + name);
            try (InputStream in1 = CompileHelper.class.getClassLoader().getResourceAsStream(folderPath);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in1))) {
                String resource;

                while ((resource = br.readLine()) != null) {
                    System.out.println("Resource name: " + resource);
                    filenames.add(resource);
                }
            }
            InputStream in = CompileHelper.class.getClassLoader()
                    .getResourceAsStream(folderPath + name + ".class");
            byte[] buff = new byte[10000];
            int len = in.read(buff);
            return defineClass("api.commands." + name, buff, 0, len);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return this.getParent().loadClass(name);

    }

}
