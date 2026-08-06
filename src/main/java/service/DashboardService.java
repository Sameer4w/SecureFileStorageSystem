package com.example.securefilestoragesystem.service;

import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class DashboardService {

    public int getTotalFiles(String email){

        File folder = new File(
                System.getProperty("user.dir")
                        + File.separator
                        + "uploads"
                        + File.separator
                        + email);

        File[] files = folder.listFiles();

        if(files==null)
            return 0;

        return files.length;
    }

    public long getTotalStorage(String email){

        File folder = new File(
                System.getProperty("user.dir")
                        + File.separator
                        + "uploads"
                        + File.separator
                        + email);

        File[] files = folder.listFiles();

        long total=0;

        if(files!=null){

            for(File f:files){

                total+=f.length();

            }

        }

        return total;
    }

    public String getLastUploadedFile(String email){

        File folder = new File(
                System.getProperty("user.dir")
                        + File.separator
                        + "uploads"
                        + File.separator
                        + email);

        File[] files = folder.listFiles();

        if(files==null || files.length==0){

            return "No Files";

        }

        File latest=files[0];

        for(File f:files){

            if(f.lastModified()>latest.lastModified()){

                latest=f;

            }

        }

        return latest.getName();

    }

}