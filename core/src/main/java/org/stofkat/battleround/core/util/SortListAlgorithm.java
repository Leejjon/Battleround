package org.stofkat.battleround.core.util;

import java.util.ArrayList;

import org.stofkat.battleround.core.GameObject;

/**
 * I've used Nathan Nelson's sorter and made it a bit easier.
 * @see http://stackoverflow.com/questions/4191855/sorting-a-listnumber
 * 
 * @author Leejjon
 */
public class SortListAlgorithm {
    public static ArrayList<GameObject> orderGameObjectsByYCoordinate(ArrayList<GameObject> list){
    	int count = 0;
        int count2 = 1;
    	while (list.size() > 1 && count != list.size() - 1){
            if (list.get(count2).getAbsoluteY() > list.get(count).getAbsoluteY()){
                list.add(count, list.get(count2));
                list.remove(count2 + 1);
            }
            if (count2 == list.size() - 1){
                count++;
                count2 = count + 1;
            }
            else{
            	count2++;
            }
        }
        return list;
    }
}
