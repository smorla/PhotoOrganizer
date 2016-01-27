package com.smo.photoorganizer.utils;

import com.beust.jcommander.Parameter;

/**
 *
 * @author Esteban
 */
public class MainParameters {
    @Parameter(names = {"-source", "-s"}, description = "Full path to source directory")
    private String sourceDirectory = null;
    @Parameter(names = {"-target", "-t"} , description = "Full path to target directory")
    private String targetDirectory = null;
    @Parameter(names = {"-mode", "-m"}, description = "Simulation mode (s) or process mode (p)")
    private String mode = null;
    @Parameter(names = {"-fileop", "-fo"}, description = "Select file operation option. Move files (m) or copy files (c) from source to target")
    private String fileOpration = null;
    @Parameter(names = {"-format", "-f"}, description = "Directory format. Year/Month/Day (1) or Year/Month? (2)")
    private String directoryFormat = null;
    @Parameter(names = "-help", help = true, description = "Show usage")
    private boolean help;
    
    public MainParameters() {
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getFileOpration() {
        return fileOpration;
    }

    public void setFileOpration(String fileOpration) {
        this.fileOpration = fileOpration;
    }

    public String getDirectoryFormat() {
        return directoryFormat;
    }

    public void setDirectoryFormat(String directoryFormat) {
        this.directoryFormat = directoryFormat;
    }

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    /**
     * Returns true if every possible parameter is null. False otherwise
     * @return a Boolean
     */
    public boolean isInteractiveMode() {
        return (sourceDirectory == null && targetDirectory == null && mode == null
                && fileOpration == null && directoryFormat == null);
    }

}