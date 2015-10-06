/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smo.photoorganizer.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides utility methods for reading and writing files
 * 
 * @author Salvador
 */
public class FileUtils {
    
    
    public static List<String> getFiles(String rootDirectory) {
        
        File[] files = new File(rootDirectory).listFiles();
        List<String> result = new ArrayList();
        for (File file : files) {            
            if (!file.isDirectory()) {
                result.add(file.getAbsolutePath());
                // System.out.println("File:" + file.getAbsolutePath());
            }
            else if (file.isDirectory()) {
                // System.out.println("Dir:" + file.getAbsolutePath());
                result.addAll(getFiles(file.getAbsolutePath()));
            }
        }        
        return result;
    }    
}
