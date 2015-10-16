package net.serenitybdd.ant.util;


import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class PathProcessor {
    public String normalize(String path) {
        if ((path != null) &&  path.startsWith("classpath:")) {
            return classpath(path);
        } else {
            return path;
        }
    }

    private String classpath(String path) {
        String corePath = path.replace("classpath:","");
        try {
            URL pathUrl = Thread.currentThread().getContextClassLoader().getResource(corePath);
            if ((pathUrl == null) || Paths.get(pathUrl.toURI()) == null) {
                throw new RuntimeException("Source directory not found: " + path);
            }
            return Paths.get(Thread.currentThread().getContextClassLoader().getResource(corePath).toURI())
                        .toAbsolutePath().toString().replaceAll("%20"," ");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Invalid path: " + path);
//        return Thread.currentThread().getContextClassLoader().getResource(corePath).getPath().replaceAll("%20"," ");
    }
}
