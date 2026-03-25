package me.ropy.mysticbrews.dsa;

import java.util.Arrays;

public class HashTable<K, V> {
    private HTEntry<K, V>[] table;
    private int size;
    private double loadFactor; // result of: size / table.length (not integer division)

    public HashTable(int initialCapacity) {
        table = (HTEntry<K, V>[]) new HTEntry[initialCapacity];
        size = 0;
        loadFactor = 0;
    }

    //built int java hash code
    private int hash(K key) {
        return Math.abs(key.hashCode());
    }

    //double hash
    private int hash2(K key) {
        return Math.abs(Integer.valueOf(hash(key)).hashCode());
    }

    //create new array
    //rehash every item from our old array to our new one
    private void resize(int newCapacity){
        HTEntry<K, V>[] oldTable = Arrays.copyOf(this.table, this.table.length);
        this.table = (HTEntry<K, V>[]) new HTEntry[newCapacity];
        this.size = 0;
        this.loadFactor = 0;

        for(HTEntry<K, V> entry : oldTable){
            if(entry != null && !entry.isDeleted())
                put(entry.key, entry.value);
        }

        loadFactor = (double)size / newCapacity;
    }

    //Put (add)
    public void put(K key, V value) {
        //check for room to add
        if (loadFactor > 0.7)
            resize(table.length*2);

        //get the hashed index based on key
        int index1 = hash(key) % this.table.length;
        int index2 = 1 + (hash2(key) % (this.table.length-1));
        int startIndex = index1;
        //probe count
        int i = 0;

        //check the hashed index: if not occupied insert
        //if occupied, probe to next valid location and repeat
        //also stop if we return to the start index
        while (table[index1] != null && !table[index1].isDeleted()) {
            //if we find key again, overwrite value at that position
            if (table[index1].key.equals(key)) {
                table[index1].value = value;
                return;
            }

            //double hash probe
            i++;
            index1 = (startIndex + i*index2) % table.length;

            if (index1 == startIndex) {
                throw new Error("No empty slot");
            }
        }

        //insert into table, my new item;
        table[index1] = new HTEntry<>(key, value);
        size++;
        loadFactor = (double)size/table.length;
    }

    //Get (lookup)
    public V get(K key) {
        //get hash index based key
        int index = hash(key) % this.table.length;
        int index2 = hash2(key) % this.table.length;
        int startIndex = index;
        //probe count
        int i = 0;

        //loop: while key not found and not null -> probe to next location
        while (table[index] != null) {
            //handle key being found
            if (!table[index].isDeleted() && table[index].key.equals(key)) {
                return table[index].value;
            }

            //double hash probe
            index = (startIndex + i*index2) % table.length;
            i++;

            if (index == startIndex) {
                break;
            }
        }
        //if no key found, return failed
        return null;
    }

    //Remove (delete)
    public void remove(K key) {
        //get hash index based key
        int index = hash(key) % this.table.length;
        int index2 = hash2(key) % this.table.length;
        int startIndex = index;
        //probe count
        int i = 0;

        //loop: while value of current index is not null -> probe to next location
        while (table[index] != null) {
            //handle key being found
            if (!table[index].isDeleted() && table[index].key.equals(key)) {
                table[index].delete();
                size--;
                loadFactor = (double) size/table.length;
                return;
            }

            //double hash probe
            index = (startIndex + i*index2) % table.length;
            i++;

            if (index == startIndex) {
                break;
            }
        }
        //if no key found, fail
        throw new Error("No value found");
    }

    private static class HTEntry<K, V> {
        public K key;
        public V value;

        private boolean deleted;

        public HTEntry(K key, V value){
            this.key = key;
            this.value = value;
            deleted = false;
        }

        public void delete(){
            this.deleted = true;
        }

        public boolean isDeleted(){
            return this.deleted;
        }
    }
}
