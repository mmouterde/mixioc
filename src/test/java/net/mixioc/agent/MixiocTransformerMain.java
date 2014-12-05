package net.mixioc.agent;

import net.mixioc.internal.MixiocTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.io.*;

public class MixiocTransformerMain {

    public static void main(String[] args) {
        buildExpexted();
        buildTransformed();
    }

    private static void buildExpexted() {
        try {
            InputStream in = MixiocTransformerMain.class.getResourceAsStream("/net/mixioc/entities/expected/TestClass.class");
            ClassReader cr = new ClassReader(in);
            ClassNode classNode = new ClassNode();
            cr.accept(classNode, 0);

            //We are done now. so dump the class
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(cw);

            writeClass(cw.toByteArray(), "asmout/net/mixioc/entities/TestClass.class", "TestClass.class");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void buildTransformed() {
        InputStream inputStream = MixiocTransformerMain.class.getResourceAsStream("/net/mixioc/entities/TestClass.class");
        try {
            ClassReader classReader = new ClassReader(inputStream);
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);
            byte[] result = MixiocTransformer.doTransform(classNode);
            writeClass(result, "asmout/net/mixioc/entities/TestClass.class", "TestClass.class");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void writeClass(byte[] data,String outputDir,String fileName){
        try {
            File outDir = new File(outputDir);
            outDir.mkdirs();
            DataOutputStream dout = new DataOutputStream(new FileOutputStream(new File(outDir, fileName)));
            dout.write(data);
            dout.flush();
            dout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
