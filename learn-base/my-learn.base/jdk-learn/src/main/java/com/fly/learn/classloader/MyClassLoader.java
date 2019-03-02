package com.fly.learn.classloader;

import sun.misc.IOUtils;

import java.io.*;

/**
 * Created by FlyWeight on 2018/12/9.
 */
public class MyClassLoader extends ClassLoader {

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {


        try {
            InputStream ins = new FileInputStream("D:\\Program\\Projects\\idea\\github\\Learn\\learn-base\\my-learn.base" +
                    "\\jdk-learn\\target\\classes\\com\\fly\\learn\\classloader\\Person.class");
            byte[] data = inputStream2ByteArray(ins);
            return super.defineClass(null, data, 0, data.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    byte[] inputStream2ByteArray(InputStream ins) {
        byte[] buf = new byte[1024];
        byte[] data = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
            int len = 0;
            while ((len = ins.read(buf)) > -1) {
                bos.write(buf, 0, len);
            }
            data = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void main(String[] args) {
        MyClassLoader ml = new MyClassLoader();
        MyClassLoader ml2 = new MyClassLoader();
        try {
            Class clazz = ml.findClass("");
            Class clazz2 = ml2.findClass("");
            Object p = clazz.newInstance();
            Object p2 = clazz2.newInstance();
            System.out.println(p.equals(p2));
            System.out.println(p == p2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
