package net.mixioc.internal;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Iterator;

public class MixiocTransformer implements ClassFileTransformer, Opcodes {

    static final Logger logger = LoggerFactory.getLogger(MixiocTransformer.class);

    private String packageFilter;

    public MixiocTransformer(String packageFilter) {
        if (packageFilter != null)
            this.packageFilter = packageFilter.replaceAll("\\.", ".");
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {

        if (className == null)
            return classfileBuffer;

        String normalizedClassName = className.replaceAll("/", ".");

        try {
            if (isFilteredMatch(normalizedClassName)) {

                logger.trace(" Processing " + normalizedClassName);
                ClassReader classReader = new ClassReader(normalizedClassName);
                ClassNode classNode = new ClassNode();
                classReader.accept(classNode, 0);

                if (isInjectionRequired(classNode)) {
                    logger.debug(normalizedClassName + " members will be injected.");
                    return doTransform(classNode);
                } else {
                    logger.trace(normalizedClassName + " has no @Inject members.");
                }
            }
        } catch (IOException e) {
            logger.error("Error while transmforming : " + className, e);
            e.printStackTrace();
        }
        return classfileBuffer;
    }

    private boolean isFilteredMatch(String className) {
        if (className == null)
            return true;
        return className.startsWith(packageFilter);
    }

    public static byte[] doTransform(ClassNode classNode) {

        //Let's move through all the methods
        for (Object aNode : classNode.methods) {
            MethodNode methodNode = (MethodNode) aNode;
            if (methodNode.name.equalsIgnoreCase("<init>")) {

                InsnList instructionList = new InsnList();

                Iterator<AbstractInsnNode> insnNodes = methodNode.instructions.iterator();
                while (insnNodes.hasNext()) {
                    AbstractInsnNode currentNode = insnNodes.next();
                    instructionList.add(currentNode);
                    if (currentNode.getOpcode() == Opcodes.INVOKESPECIAL) {
                        instructionList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/redige/util/serviceapi/ServiceManager", "inject", "(Ljava/lang/Object;)V"));
                    }
                }
                methodNode.instructions.clear();
                methodNode.instructions.add(instructionList);
            }
        }

        //We are done now. so dump the class
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(cw);
        return cw.toByteArray();
    }


    public static boolean isInjectionRequired(ClassNode classNode) {
        Iterator<FieldNode> fieldsIterator = classNode.fields.iterator();
        while (fieldsIterator.hasNext()) {
            FieldNode field = fieldsIterator.next();
            if (field.visibleAnnotations != null) {
                Iterator<AnnotationNode> annotationIterator = field.visibleAnnotations.iterator();
                while (annotationIterator.hasNext()) {
                    if (annotationIterator.next().desc.equals("Lnet/mixioc/annotation/Inject;")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
