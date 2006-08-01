package com.thoughtworks.paranamer;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class QdoxParanamerGenerator {

    private static final String SPACE  = " ";
    private static final String NEWLINE = "\n";
    private static final String COMMA = ",";
    private static final String EMPTY = "";

    public String generate(String sourcePath) {
        StringBuffer buffer = new StringBuffer();
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(new File(sourcePath));
        JavaClass[] classes = builder.getClasses();
        Arrays.sort(classes);
        for (int i = 0; i < classes.length; i++) {
            JavaClass clazz = classes[i];
            if (!clazz.isInterface()) {
        	buffer.append(addMethods(clazz.getMethods(), clazz.getPackage() + "." + clazz.getName()));
            }
        }
        return buffer.toString();
    }

    private String addMethods(JavaMethod[] methods, String className) {
        StringBuffer buffer = new StringBuffer();
        Arrays.sort(methods);
        for (int j = 0; j < methods.length; j++) {
            JavaMethod method = methods[j];
            if (Arrays.asList(method.getModifiers()).contains("public")) {
        	buffer.append(addPublicMethod(method, className));
            }
        }
        return buffer.toString();
    }

    private String addPublicMethod(JavaMethod method, String className) {
        StringBuffer buffer = new StringBuffer();
        JavaParameter[] parms = method.getParameters();
        DocletTag[] alsoKnownAs = method.getTagsByName("previousParamNames");
        for (int k = 0; k < alsoKnownAs.length; k++) {
            String value = alsoKnownAs[k].getValue();
            buffer.append(className);
            buffer.append(SPACE);
            buffer.append((method.getName() + SPACE + value + SPACE + getTypes(parms)).trim());
            buffer.append(NEWLINE);
        }
        buffer.append(className);
        buffer.append(SPACE);
        buffer.append((method.getName() + SPACE + getParamNames(parms) + SPACE + getTypes(parms)).trim());
        buffer.append(NEWLINE);
        return buffer.toString();
    }

    private String getParamNames(JavaParameter[] parms) {
	StringBuffer buffer = new StringBuffer();
        for (int k = 0; k < parms.length; k++) {
            buffer.append(parms[k].getName());
            buffer.append(comma(k, parms.length));
        }
        return buffer.toString();
    }

    private String getTypes(JavaParameter[] parms) {
	StringBuffer buffer = new StringBuffer();
        for (int k = 0; k < parms.length; k++) {
            buffer.append(parms[k].getType());
            buffer.append(comma(k, parms.length));
        }
        return buffer.toString();
    }


    public void write(String outputPath, String parameterText) throws IOException {
        new File(outputPath + File.separator + "META-INF" + File.separator).mkdirs();
        FileWriter fileWriter = new FileWriter(outputPath + File.separator + "META-INF" + File.separator + "ParameterNames.txt");
        PrintWriter pw = new PrintWriter(fileWriter);
        pw.println("format version 1.0");
        pw.println(parameterText);
        pw.close();
    }

    private String comma(int k, int size) {
        return (k + 1 < size) ? COMMA : EMPTY;
    }


}
