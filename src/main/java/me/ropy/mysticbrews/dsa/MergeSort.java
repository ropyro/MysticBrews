package me.ropy.mysticbrews.dsa;

public class MergeSort {

    public static void sortHighLow(int[] arr){
        //base case
        if(arr.length <= 1) return;

        //split
        //get the middle with integer division
        int mid = arr.length/2;
        //create arry left from 0 to mid
        int[] left = new int[mid];
        //create arry right from mid to length
        int[] right = new int[arr.length-mid];
        //fill arrays
        for(int i = 0; i < arr.length; i++){
            if(i < mid){
                left[i] = arr[i];
            }else{
                right[i-mid] = arr[i];
            }
        }

        //recursive case
        sortHighLow(left);
        sortHighLow(right);

        //conquer
        int l = 0, r = 0;
        while (l < left.length || r < right.length){
            if(l == left.length && r != right.length){
                arr[l + r] = right[r];
                r++;
                continue;
            }
            if(r == right.length && l != left.length){
                arr[l + r] = left[l];
                l++;
                continue;
            }

            if(left[l] > right[r]){
                arr[r + l] = left[l];
                l++;
            }else{
                arr[r + l] = right[r];
                r++;
            }
        }
    }

    public static void sort(int[] arr){
        //base case
        if(arr.length <= 1) return;

        //split
        //get the middle with integer division
        int mid = arr.length/2;
        //create arry left from 0 to mid
        int[] left = new int[mid];
        //create arry right from mid to length
        int[] right = new int[arr.length-mid];
        //fill arrays
        for(int i = 0; i < arr.length; i++){
            if(i < mid){
                left[i] = arr[i];
            }else{
                right[i-mid] = arr[i];
            }
        }

        //recursive case
        sort(left);
        sort(right);

        //conquer
        int l = 0, r = 0;
        while (l < left.length || r < right.length){
            if(l == left.length && r != right.length){
                arr[l + r] = right[r];
                r++;
                continue;
            }
            if(r == right.length && l != left.length){
                arr[l + r] = left[l];
                l++;
                continue;
            }
            if(left[l] < right[r]){
                arr[r + l] = left[l];
                l++;
            }else{
                arr[r + l] = right[r];
                r++;
            }
        }
    }
}
