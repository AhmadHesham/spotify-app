package controller;

import java.io.InputStream;

public class CompileHelper extends ClassLoader {

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            System.out.println("NAME: " + name);
            InputStream in = CompileHelper.class.getClassLoader().getResourceAsStream(name + ".class");
            System.out.println("IN: " + in);
            byte[] buff = new byte[10000];
            int len = in.read(buff);
            return defineClass("api.commands." + name, buff, 0, len);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.getParent().loadClass(name);

    }

    // public Class<?> loadClass2(String name) throws ClassNotFoundException {
    // try {
    // InputStream in =
    // CompileHelper.class.getClassLoader().getResourceAsStream(name + ".class");
    // System.out.println("IN: " + in);
    // byte[] buff = new byte[10000];
    // int len = in.read(buff);
    // return defineClass("api.commands." + name, buff, 0, len);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return this.getParent().loadClass(name);
    // }

}
