/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileSorting;

/**
 *
 * @author amnwaqar
 */

import javax.swing.*;
import java.io.*;
import java.util.*;

public class FileSorter extends SwingWorker<Object,String> {
    private int limit, files;
    private File input, output;

    public FileSorter(int limit, File input, File output) {
        this.limit = limit;
        this.input = input;
        this.output = output;
    }
    
    public void split()
    {
        Queue<String> toSort = new LinkedList<>();
        File[] file;
        
        try {

            Scanner myReader = new Scanner(input);
            
            while (myReader.hasNextLine()) {
                toSort.add(myReader.nextLine());
            }
            myReader.close();
            
            files = (toSort.size() / limit) + 1;
            file = new File[files];
            
            for (int k = 0; k < files; k++){
                if (k != files - 1)
                {
                    String[] arr = new String[limit];
                    try {
                        file[k] = new File("./resources/temp" + k +".txt");
                        file[k].createNewFile();
                        
                        for (int i = 0; i < limit; i++)
                        {
                            arr[i] = toSort.poll();
                        }
                        
                        quickSort(arr, 0, limit-1);
                        try (PrintWriter myWriter = new PrintWriter(new FileOutputStream(file[k]))) {
                            for (int i = 0; i < limit; i++)
                            {
                                myWriter.println((arr[i]));
                            }
                        }
                        
                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                    }
                }
                else
                {
                    String[] arr = new String[toSort.size()];
                    
                    try {
                        file[k] = new File("./resources/temp" + k +".txt");
                        file[k].createNewFile();
                        int loop = toSort.size();
                        
                        for (int i = 0; i < loop; i++)
                        {
                            arr[i] = toSort.poll();
                        }
                        
                        quickSort(arr, 0, loop-1);
                        try (PrintWriter myWriter = new PrintWriter(new FileOutputStream(file[k]))) {
                            for (int i = 0; i < loop; i++)
                            {
                                myWriter.println((arr[i]));
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }
    }
    
    public void quickSort(String[] arr, int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex-1);
            quickSort(arr, partitionIndex+1, end);
        }
    }
    
    private int partition(String[] arr, int begin, int end) {
        String pivot = arr[end];
        int i = (begin-1);

        for (int j = begin; j < end; j++) {
         
            if (pivot.compareTo(arr[j]) > 0) {
                i++;

                String swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        String swapTemp = arr[i+1];
        arr[i+1] = arr[end];
        arr[end] = swapTemp;

        return i+1;
    }
    
    public void merge()
    {
        Queue<String> toSort = new LinkedList<>();
        File[] file = new File[files];
        
        for (int k = 0; k < files; k++){
            file[k] = new File("./resources/temp" + k +".txt");
        }
        
        for (int k = 0; k < files - 1; k++){
            
            try {
               
                Scanner myReader1 = new Scanner(file[k]);
                Scanner myReader2 = new Scanner(file[k+1]);
                
                while (myReader1.hasNextLine()) {
                    toSort.add(myReader1.nextLine());
                }
                myReader1.close();
                
                while (myReader2.hasNextLine()) {
                    toSort.add(myReader2.nextLine());
                }
                myReader2.close();
                
                String[] arr = new String[toSort.size()];
                
                for (int i = 0; i < arr.length; i++)
                {
                    arr[i] = toSort.poll();
                }
                
                file[k].delete();
                mergeSort(arr);
                
                PrintWriter myWriter = new PrintWriter(new FileOutputStream(file[k+1]));
                        
                for (String arr1 : arr) {
                    myWriter.println(arr1);
                }
                        
                myWriter.close();
                
            } catch (FileNotFoundException e) {
                System.out.println("Input file not found");
            }
        }
        
        try{
            PrintWriter myWriter = new PrintWriter(new FileOutputStream(output));
            Scanner scan = new Scanner(file[files - 1]);
                
            while (scan.hasNextLine()) {
                myWriter.println(scan.nextLine());
            }
            scan.close();
            myWriter.close();
            file[files-1].delete();
                
        } catch (FileNotFoundException e) {
            System.out.println("Output file not found");
        }
    }
    
    public void mergeSort(String[] list){  
        mergeSortSegment(list, 0, list.length);
   }
    
   private void mergeSortSegment(String[] list, int start, int end){  
        int numElements = end-start;
        if (numElements > 1)
        {  
            int middle = (start+end)/2;
            mergeSortSegment(list, start, middle);
            mergeSortSegment(list, middle, end);
         
            String[] tempList = new String[numElements];
            for (int i=0; i<numElements; i++)
            {
                tempList[i] = list[start+i];
                //System.out.println(tempList[i]);
            }
         
            int indexLeft = 0; 
            int indexRight = middle-start; 

            for (int i=0; i<numElements; i++)
            { 
                if (indexLeft<(middle-start)){  
                    if (indexRight<(end-start)){  
                        if (tempList[indexLeft].compareTo(tempList[indexRight])<0){
                            list[start+i] = tempList[indexLeft++];
                        }
                        else{
                            list[start+i] = tempList[indexRight++];
                        }
                    }
                    else{ 
                        list[start+i] = tempList[indexLeft++];
                    }
                }
                else{
                    list[start+i] = tempList[indexRight++];
                }
            }
        }
    }

    @Override
    protected Object doInBackground() throws Exception {
        
        setProgress(0);
        
        publish("...Splitting File...");
        setProgress(25);
        try {
                Thread.sleep(500);
        }catch (InterruptedException e) {}
        
        split();
        setProgress(50);
        try {
                Thread.sleep(500);
        }catch (InterruptedException e) {}
        
        publish("...Merging Files...");
        try {
                Thread.sleep(500);
        }catch (InterruptedException e) {}
        
        merge();
        setProgress(75);
        publish("...File Sorted...");
        
        try {
                Thread.sleep(500);
        }catch (InterruptedException e) {}
        
        output.deleteOnExit();
        
        return null;
    }

    @Override
    protected void done() {
        setProgress(100);
    }

    

    
    

    
    
    
    
    
}
