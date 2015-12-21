/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weka;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 *
 * @author Mateusz
 */
public class WekaService {
    
    private Instances data;
    private String fileName;
    
    private WekaService(){}
    
    public WekaService(String path){
        fileName=path;
        loadData();
    }
    
    private void loadData(){
        try{
            ArffLoader loader = new ArffLoader();
            loader.setFile(new File(fileName));
            data=loader.getDataSet();
        }catch(IOException ex){
            
        }
    }
    
    public ArrayList<String> getAttributesName(){
        ArrayList<String> list= new ArrayList<>();
        for(int i=0;i<data.numAttributes();i++){
            Attribute attribut = data.attribute(i);
            list.add(attribut.name());
        }
        return list;
    }
    
    public ArrayList<Instance> getInstances(){
        ArrayList<Instance> list = new ArrayList<>();
        for(int i=0;i<data.numInstances();i++){
            list.add(data.instance(i));
        }
        return list;
    }
}
