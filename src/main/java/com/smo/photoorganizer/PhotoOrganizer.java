/**
 * Copyright 2015.
 *
 * Home photo organizer. The classification criteria used is
 * Year / Month of year
 * 
 */
package com.smo.photoorganizer;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.smo.photoorganizer.error.WrongArgumentException;
import com.smo.photoorganizer.utils.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Salvador Morla (smorla@gmail.com)
 */
public class PhotoOrganizer {
  
    // Date/Time Original(0x9003 : 36867)
    private static final int DATE_TIME_ORIGINAL = 36867;
    private static final int DATE_TIME_MODIFIED = 3;
    private static final String SIMULATION = "S";
    private static final String PROCESS    = "P";
    private static final String COPY       = "C";
    private static final String MOVE       = "M";
    private static final String YEARMONTHDAY = "1";
    private static final String YEARMONTH = "2";
    
    private String rootDirectory   = null;
    private String targetDirectory = null;
    private int    filesProcessed  = 0;
    private int    totalReadFiles  = 0;
    private List<String> filesProcessedWithError = null;
    private boolean simulation = false;
    private boolean moveFiles = false;
    private String directoryFormat;


    /**
     * Main entry point for application
     *
     * @param args the command line arguments
     * @throws Exception If file access does not work
     */
    public static void main(String[] args) throws Exception {
      
      PhotoOrganizer po = new PhotoOrganizer();

      try {
        
        System.out.println("Photo catalog organizer");
        System.out.println("-----------------------");
        
        // Check and process input parameters
        
        String sourceDir = getInput("Full path to source directory:");
        po.checkSourceDirectory(sourceDir);
        po.setRootDirectory(sourceDir);
        
        String targetDir = getInput("Full path to targetDirectory directory:");
        po.checkTargetDirectory(targetDir);
        po.setTargetDirectory(targetDir);
        
        String fileOperation = getInput("Select file operation option. Move files or copy (m | C) ").toUpperCase().equals(MOVE) ? MOVE : COPY;
        po.checkFileOperation(fileOperation);
        po.setMoveFiles(MOVE.equals(fileOperation));
        
        String option = getInput("Simulate or process? (S | p)").toUpperCase().equals(PROCESS) ? PROCESS : SIMULATION;
        po.checkOption(option);
        
        String directoryFormat = getInput("Select directory format. Year/Month/Day or Year/Month? (1 | 2)").toUpperCase().equals(YEARMONTHDAY) ? YEARMONTHDAY : YEARMONTH;
        po.checkDirectoryFormat(directoryFormat);
        po.setDirectoryFormat(directoryFormat);
        
        if (SIMULATION.equals(option)) {
            po.setSimulation(true);
        }
                
        // Run the process!
        System.out.println("Process executing...");
        po.doProcessing();
      }
      catch (Exception e) {
          System.out.println("ERROR:" + e.getMessage());          
      }
      finally {
          System.out.println("------------------------------------------------------------------------");
          System.out.println("The process has finished.");
          System.out.println("Origin: " + po.getRootDirectory());
          System.out.println("Target: " + po.getTargetDirectory());
          System.out.println("File operation option: " + (po.isMoveFiles() ? "move" : "copy"));
          System.out.println("Total read files: " + po.getTotalReadFiles());
          System.out.println("Total files processed: " + po.getFilesProcessed());
          System.out.println("Files processed with error (" + po.getFilesProcessedWithError().size() + "):");
          po.getFilesProcessedWithError().stream()
                  .forEach(System.out::println);
      }      
    }
    
    /**
     * Returns the full path (absolute) according to the date the picture was 
     * taken, the target directory specified and the picteure file name.
     * 
     * @param date
     * @param filename
     * @return The absolute path to the file where to copy the image.
     */
    private String getTargetPath(Date date, String filename) {
        
        StringBuilder s = new StringBuilder();
        
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        
        s.append(this.getTargetDirectory())
         .append(File.separator)
         .append(c.get(Calendar.YEAR))
         .append(File.separator);
        if (this.directoryFormat.equals(YEARMONTHDAY)){
         s.append(String.format("%02d", Integer.parseInt(String.valueOf(c.get(Calendar.MONTH)+1))))
          .append(File.separator)
          .append(String.format("%02d", Integer.parseInt(String.valueOf(c.get(Calendar.DAY_OF_MONTH)))));
        }else{
            s.append(String.valueOf(c.get(Calendar.YEAR)).substring(2))
             .append(String.format("%02d", Integer.parseInt(String.valueOf(c.get(Calendar.MONTH)+1))));
        } 
         s.append(File.separator)       
          .append(filename);
         
         return FileUtils.getNewPath(s.toString());         
    }
    
    
    private Date getOriginalDateTime(Metadata metadata)  {
        Date d = null;
        for (Directory directory : metadata.getDirectories()) {            
            d = directory.getDate(DATE_TIME_ORIGINAL);
            if (d != null) {
                break;
            }
        }

        return d;        
    }

    private Date getFileModifiedDateTime(Metadata metadata)  {
        Date d = null;
        for (Directory directory : metadata.getDirectories()) {            
            d = directory.getDate(DATE_TIME_MODIFIED);
            if (d != null) {
                break;
            }
        }
        return d;        
    }
    
    /**
     * Runs the procedure to achieve the re-organization of the photographic archive
     * by classifying the files by the year and month the picture was taken.
     * 
     * @throws java.io.IOException
     */
    private void doProcessing() throws IOException {
        
        System.out.println("Retrieving file set...");
        List<String> files = FileUtils.getFiles(getRootDirectory());
        System.out.println("Done. " + files.size() + " found.");
        
        System.out.println("Classification process start ...");
        for (String filename: files) {
            
            try {
                File f = new File(filename);
                Metadata metadata = ImageMetadataReader.readMetadata(f);
                
                Date d = this.getOriginalDateTime(metadata);
                
                // DEBUG - uncoment the next 2 lines to print debug info of each processed file.
                // System.out.println("File-> "+filename + " --->  Date when pic was taken: " + d);
                // print(metadata);
                // END DEBUG

                if (d == null) {
                    d = this.getFileModifiedDateTime(metadata);
                }
                String toPath = this.getTargetPath(d, f.getName());
                if (isSimulation()) {
                    System.out.println(filename+" -(simulation)-> "+toPath);
                }
                else {
                    Path from = Paths.get(f.getAbsolutePath());
                    Path to   = Paths.get(toPath);
                    
                    Files.createDirectories(to.getParent());
                    
                    if (isMoveFiles()) {
                        CopyOption[] options = new CopyOption[] {
                          StandardCopyOption.REPLACE_EXISTING,
                          StandardCopyOption.ATOMIC_MOVE
                        };
                        Files.move(from, to, options);
                    }
                    else {
                        CopyOption[] options = new CopyOption[] {
                          StandardCopyOption.REPLACE_EXISTING,
                          StandardCopyOption.COPY_ATTRIBUTES
                        };
                        Files.copy(from, to, options);
                    }
                    System.out.println(filename+" -(" + (isMoveFiles() ? "moved" : "copied") + " to)-> " + toPath);
                }
                setFilesProcessed();
            } catch (Exception e) {
                // handle exception
                getFilesProcessedWithError().add(filename);
                System.out.println("ERROR processing file " + filename + ": " + e.getMessage());
            }
        }        
        setTotalReadFiles(files.size());
    }
    
    private void checkSourceDirectory(String d) throws Exception {
        
        File f  = null;
        
        if (d == null || d.length() == 0) {
            throw new WrongArgumentException("No source directory path has been specified.");
        }
        f = new File(d);
        if (!f.exists() || !f.isDirectory()) {
            throw new WrongArgumentException("The specified source directory does not exist or it is a file but not a directory.");
        }
    }
    
    private void checkTargetDirectory(String d) throws Exception {
        if (d == null || d.length() == 0) {
            throw new WrongArgumentException("No target directory path has been specified.");
        }
    }
    
    private void checkFileOperation(String o) throws Exception {
        
        if (MOVE.equals(o) || COPY.equals(o)){
        } else {
            throw new WrongArgumentException("File operation option not valid: 'M'ove | 'C'opy");
        }
    }
    
    private void checkOption(String o) throws Exception {
        
        if (SIMULATION.equals(o) || PROCESS.equals(o)){
        } else {
            throw new WrongArgumentException("Option not valid: 'P'rocess | 'S'imulate");
        }
    }
    
    private void checkDirectoryFormat(String o) throws Exception {
        
        if (YEARMONTHDAY.equals(o) || YEARMONTH.equals(o)){
        } else {
            throw new WrongArgumentException("Directory format not valid: '1' year/month/day | '2' year/month");
        }
    }
    
    
    /**
     * Prints metadata information.
     * Just useful for debuggin and understanding the metadata structure.
     * @param metadata 
     */
    private static void print(Metadata metadata)
    {
        // A Metadata object contains multiple Directory objects
        for (Directory directory : metadata.getDirectories()) {            
            // Each Directory stores values in Tag objects
            System.out.println("DIRECTORY:" + directory.getName());
            directory.getTags().stream().forEach((tag) -> {                
                System.out.println(tag.getTagName() + "(" + tag.getTagTypeHex() + " : " + tag.getTagType() + ") - " + tag.getDescription());
            });

            // Each Directory may also contain error messages
            if (directory.hasErrors()) {
                for (String error : directory.getErrors()) {
                    System.err.println("ERROR: " + error);
                }
            }
        }
    }
    
    private static String getInput(String inputMessage) throws IOException {
        // Imprimimos mensaje
        System.out.println(inputMessage);
       
        // Esperamos la respuesta del usuario
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
       
        return br.readLine();
    }

    private void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    private String getRootDirectory() {
        return this.rootDirectory;
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public int getFilesProcessed() {
        return filesProcessed;
    }

    public void setFilesProcessed() {
        this.filesProcessed = filesProcessed + 1;
    }

    public List<String> getFilesProcessedWithError() {
        if (this.filesProcessedWithError == null) {
            this.filesProcessedWithError = new ArrayList<String>();
        }
        
        return filesProcessedWithError;
    }

    public void setFilesProcessedWithError(List<String> filesProcessedWithError) {
        this.filesProcessedWithError = filesProcessedWithError;
    }

    public boolean isSimulation() {
        return simulation;
    }

    public void setSimulation(boolean simulation) {
        this.simulation = simulation;
    }

    public int getTotalReadFiles() {
        return totalReadFiles;
    }

    public void setTotalReadFiles(int totalReadFiles) {
        this.totalReadFiles = totalReadFiles;
    }

    public boolean isMoveFiles() {
        return moveFiles;
    }

    public void setMoveFiles(boolean moveFiles) {
        this.moveFiles = moveFiles;
    }

    private void setDirectoryFormat(String directoryFormat) {
       this.directoryFormat = directoryFormat;
    }
}

